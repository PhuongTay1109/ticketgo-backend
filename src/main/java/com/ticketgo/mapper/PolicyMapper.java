package com.ticketgo.mapper;

import com.ticketgo.dto.PolicyDTO;
import com.ticketgo.entity.Policy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PolicyMapper {

    PolicyMapper INSTANCE = Mappers.getMapper(PolicyMapper.class);

    @Mapping(target = "policyType", source = "policy.policyType")
    @Mapping(target = "policyContent", source = "policy.policyContent")
    PolicyDTO toPolicyDTO(Policy policy);
}
