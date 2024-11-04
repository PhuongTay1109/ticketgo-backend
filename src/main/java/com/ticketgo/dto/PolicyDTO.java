package com.ticketgo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PolicyDTO {
    private String policyType;
    private String policyContent;
}
