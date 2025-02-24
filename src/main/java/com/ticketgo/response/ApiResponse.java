package com.ticketgo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse extends ResponseEntity<ApiResponse.Payload> {

    public ApiResponse(HttpStatus status, String message, Object data) {
        super(new Payload(status.value(), message, data), status);
    }

    @Data
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Payload {
        private final int status;
        private final String message;
        private Object data;
    }

}