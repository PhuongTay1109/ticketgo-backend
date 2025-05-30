package com.ticketgo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerContactInfoDTO {
    private String fullName;
    private String email;
    private String phoneNumber;
}
