package com.ticketgo.mapper;

import com.ticketgo.request.CustomerRegistrationRequest;
import com.ticketgo.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper
public interface CustomerMapper {

    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    @Mapping(target = "password", expression = "java(passwordEncoder.encode(request.getPassword()))")
    @Mapping(target = "imageUrl", constant = "https://res.cloudinary.com/dj1h07rea/image/upload/v1728906155/sbcf-default-avatar_iovbch.webp")
    @Mapping(target = "isEnabled", constant = "false")
    @Mapping(target = "isLocked", constant = "false")
    @Mapping(target = "role", expression = "java(com.ticketgo.enums.Role.ROLE_CUSTOMER)")
    @Mapping(target = "provider", expression = "java(com.ticketgo.enums.Provider.LOCAL)")
    Customer toCustomer(CustomerRegistrationRequest request, PasswordEncoder passwordEncoder);
}

