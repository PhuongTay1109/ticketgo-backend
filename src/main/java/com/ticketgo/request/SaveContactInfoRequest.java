package com.ticketgo.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SaveContactInfoRequest {
    private Long scheduleId;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
}
