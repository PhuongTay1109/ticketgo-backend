package com.ticketgo.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccountListRequest extends BasePageRequest {
    private String keyword;
}

