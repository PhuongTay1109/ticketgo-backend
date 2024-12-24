package com.ticketgo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiPaginationResponse extends ResponseEntity<ApiPaginationResponse.Payload> {

    public ApiPaginationResponse(HttpStatus status, String message, Object data, Pagination pagination) {
        super(new Payload(status.value(), message, data, pagination), status);
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Payload {
        private final int status;
        private final String message;
        private final Object data;
        private final Pagination pagination;

        public Payload(int status, String message, Object data, Pagination pagination) {
            this.status = status;
            this.message = message;
            this.data = data;
            this.pagination = pagination;
        }
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Pagination {
        private final int pageNumber;
        private final int pageSize;
        private final int totalPages;
        private final long totalItems;

        public Pagination(int pageNumber, int pageSize, int totalPages, long totalItems) {
            this.pageNumber = pageNumber;
            this.pageSize = pageSize;
            this.totalPages = totalPages;
            this.totalItems = totalItems;
        }
    }
}
