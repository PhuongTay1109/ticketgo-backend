package com.ticketgo.service.impl;

import com.ticketgo.constant.RedisKeys;
import com.ticketgo.entity.Customer;
import com.ticketgo.entity.User;
import com.ticketgo.enums.Role;
import com.ticketgo.exception.AppException;
import com.ticketgo.repository.CustomerRepository;
import com.ticketgo.repository.UserRepository;
import com.ticketgo.request.AccountListRequest;
import com.ticketgo.response.ApiPaginationResponse;
import com.ticketgo.response.AccountInfoResponse;
import com.ticketgo.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final CustomerRepository customerRepo;
    private final UserRepository userRepo;
    private final RedissonClient redisson;

    @Override
    @Transactional
    public void changeLockStatus(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new AppException("Tài khoản không tồn tại", HttpStatus.NOT_FOUND));

        boolean currentLockedStatus = user.getIsLocked();
        String redisKey = RedisKeys.blackListUserKey;
        RList<String> blackList = redisson.getList(redisKey);

        if(currentLockedStatus) {
            blackList.add(user.getUsername());
        } else {
            blackList.remove(user.getUsername());
        }

        user.setIsLocked(!user.getIsLocked());
    }

    @Override
    @Transactional
    public ApiPaginationResponse getAccounts(AccountListRequest req) {
        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, req.buildSort());
        String keyword = req.getKeyword();
        log.info("Searching for keyword: {}", keyword);
        Page<Customer> customerPage;
        if (keyword != null && !keyword.isEmpty()) {
            customerPage = customerRepo.findByRoleAndKeyword(Role.ROLE_CUSTOMER, keyword, pageable);
        } else {
            customerPage = customerRepo.findByRole(Role.ROLE_CUSTOMER, pageable);
        }
        ApiPaginationResponse.Pagination pagination = new ApiPaginationResponse.Pagination(
                customerPage.getNumber() + 1,
                customerPage.getSize(),
                customerPage.getTotalPages(),
                customerPage.getTotalElements()
        );

        return new ApiPaginationResponse(
                HttpStatus.OK,
                "Danh sách tài khoản",
                customerPage.getContent().stream()
                        .map(customer -> AccountInfoResponse.builder()
                                .id(customer.getUserId())
                                .fullName(customer.getFullName())
                                .email(customer.getEmail())
                                .imageUrl(customer.getImageUrl())
                                .registrationDate(LocalDate.from(customer.getCreatedAt()))
                                .status(customer.getIsLocked() ? "Vô hiệu hóa" : "Đang hoạt động")
                                .build()
                        )
                        .collect(Collectors.toList()),
                pagination
        );
    }

    @Override
    @Transactional
    public void delete(long id) {
        userRepo.findById(id)
                .orElseThrow(() -> new AppException("Tài khoản không tồn tại", HttpStatus.NOT_FOUND));
        userRepo.softDelete(id);
    }
}
