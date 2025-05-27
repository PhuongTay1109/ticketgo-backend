package com.ticketgo.service;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.*;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.*;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;

import static jakarta.mail.Message.RecipientType.TO;

@Slf4j
public class GmailService {

    private static final String APPLICATION_NAME = "TicketGo";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private static final List<String> SCOPES = List.of(GmailScopes.GMAIL_SEND);
    private static final String USER_ID = "user";

    /**
     * Authorizes and returns a Credential from stored tokens (or gets it the first time).
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws Exception {
        InputStream in = GmailService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) throw new FileNotFoundException("Missing: " + CREDENTIALS_FILE_PATH);

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(dataStoreFactory)
                .setAccessType("offline")
                .setApprovalPrompt("consent") // Force user to re-authorize
                .build();

        // Check if we already have tokens
        Credential credential = flow.loadCredential(USER_ID);
        if (credential != null && credential.getAccessToken() != null) {
            // Check the remaining lifetime of the access token
            Long expiresIn = credential.getExpiresInSeconds();
            if (expiresIn != null && expiresIn < 60) {
                // If less than 60 seconds remaining, automatically refresh the access token
                if (credential.refreshToken()) {
                    log.info("Access token has been refreshed.");
                } else {
                    log.info("Unable to refresh the access token.");
                }
            } else {
                log.info("Access token is still valid for: {} seconds.", expiresIn);
            }

            log.info("Access Token: {}", credential.getAccessToken());
            log.info("Refresh Token: {}", credential.getRefreshToken());
            log.info("Expires in: {}", credential.getExpiresInSeconds());

            return credential;
        }


        // FIRST TIME ONLY: Direct user to this URL
        String redirectUri = "https://ticketgo.site/oauth2callback"; // must match the one on Google Console
        AuthorizationCodeRequestUrl authUrl = flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .setApprovalPrompt("consent");
        System.out.println("Please open the following URL in your browser and authorize the application:");
        System.out.println(authUrl.build());

        // Once user authorizes, they get redirected to:
        // https://ticketgo.site/oauth2callback?code=XXXX
        // You manually copy the ?code=XXX part from browser and paste below:
        System.out.print("Paste the authorization code here: ");
        Scanner scanner = new Scanner(System.in);
        String code = scanner.nextLine();

        TokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
        credential = flow.createAndStoreCredential(tokenResponse, USER_ID);
        return credential;
    }

    /**
     * Sends an email.
     */
    public static void sendEmail(String to, String subject, String bodyHtml) throws Exception {
        log.info("[EMAIL-SERVICE] START SENDING EMAIL TO [{}] WITH SUBJECT [{}]", to, subject);

        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = getCredentials(HTTP_TRANSPORT);
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();

        MimeMessage email = createEmail(to, "me", subject, bodyHtml);
        Message message = createMessageWithEmail(email);
        service.users().messages().send("me", message).execute();

        log.info("[EMAIL-SERVICE] ✅ EMAIL SENT SUCCESSFULLY TO [{}]", to);
    }


    private static MimeMessage createEmail(String to, String from, String subject, String bodyHtml)
            throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setContent(bodyHtml, "text/html; charset=utf-8");
        return email;
    }

    private static Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encoded = Base64.getUrlEncoder().encodeToString(bytes);
        Message message = new Message();
        message.setRaw(encoded);
        return message;
    }
}
