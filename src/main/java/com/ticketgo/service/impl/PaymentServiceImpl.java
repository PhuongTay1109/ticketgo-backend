package com.ticketgo.service.impl;

import com.ticketgo.config.vnpay.VNPayConfig;
import com.ticketgo.dto.BookingConfirmDTO;
import com.ticketgo.entity.Customer;
import com.ticketgo.entity.Payment;
import com.ticketgo.repository.PaymentRepository;
import com.ticketgo.request.PaymentRequest;
import com.ticketgo.service.AuthenticationService;
import com.ticketgo.service.BookingService;
import com.ticketgo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.ticketgo.constant.RedisKeys.vnPayUrlKey;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepo;
    private final AuthenticationService authService;
    private final BookingService bookingService;

    private final RedissonClient redisson;

    @Override
    @Transactional
    public String createVNPayment(PaymentRequest request) {
        if(request.getScheduleId() != null) {
            BookingConfirmDTO bookingInfo = bookingService.getBookingInfo(request.getScheduleId());
            log.info("Booking info: {}", bookingInfo);
            request.setDropoffStopId(bookingInfo.getTripInformation().getDropoffId());
            request.setPickupStopId(bookingInfo.getTripInformation().getPickupId());
            request.setTotalPrice((long) bookingInfo.getPrices().getTotalPrice());
        }

        Customer customer = authService.getAuthorizedCustomer();
        request.setCustomerId(customer.getUserId());

        long bookingId = bookingService.saveInProgressBooking(request);

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        String amount = String.valueOf(request.getTotalPrice() * 100);
        vnp_Params.put("vnp_Amount", amount);
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", bookingId + "-" + customer.getUserId() + "-" + request.getScheduleId());
        vnp_Params.put("vnp_OrderType", "250000");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);

        String locate = "vn";
        vnp_Params.put("vnp_Locale", locate);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));

        cld.add(Calendar.MINUTE, 15);
        vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

        log.info("CreateDate: {}", vnp_Params.get("vnp_CreateDate"));
        log.info("ExpireDate: {}", vnp_Params.get("vnp_ExpireDate"));


        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII)).append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (fieldNames.indexOf(fieldName) != fieldNames.size() - 1) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;


        String url = VNPayConfig.vnp_PayUrl + "?" + queryUrl;

        String cacheKey = vnPayUrlKey(customer.getUserId(), request.getScheduleId());
        RBucket<String> bucket = redisson.getBucket(cacheKey);
        bucket.set(url, 15, TimeUnit.MINUTES);

        return url;
    }

    @Override
    public void save(Payment payment) {
        paymentRepo.save(payment);
    }
}
