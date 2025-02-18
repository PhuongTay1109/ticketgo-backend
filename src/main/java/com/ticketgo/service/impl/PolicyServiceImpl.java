package com.ticketgo.service.impl;

import com.ticketgo.dto.PolicyDTO;
import com.ticketgo.mapper.PolicyMapper;
import com.ticketgo.entity.Policy;
import com.ticketgo.repository.PolicyRepository;
import com.ticketgo.service.PolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepo;

    @Override
    public List<PolicyDTO> getPolicies() {
        List<Policy> policies = policyRepo.findAll();

        return policies.stream()
                .map(PolicyMapper.INSTANCE::toPolicyDTO)
                .toList();
    }
}
