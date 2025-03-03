package com.ticketgo.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@JsonDeserialize(builder = AccountInfoResponse.AccountInfoResponseBuilder.class)
public class AccountInfoResponse {
    private Long id;
    private String fullName;
    private String email;
    private String imageUrl;
    private LocalDate registrationDate;
    private String status;

    @JsonPOJOBuilder(withPrefix = "")
    public static class AccountInfoResponseBuilder {}
}

