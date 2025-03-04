package com.ticketgo.service;

import com.ticketgo.request.AccountListRequest;
import com.ticketgo.response.ApiPaginationResponse;

public interface AccountService {
    void changeLockStatus(Long userId);
    ApiPaginationResponse getAccounts(AccountListRequest req);

    void delete(long id);
}
