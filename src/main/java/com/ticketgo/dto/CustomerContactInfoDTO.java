package com.ticketgo.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerContactInfoDTO {
    private String fullName;
    private String email;
    private String phoneNumber;
}
