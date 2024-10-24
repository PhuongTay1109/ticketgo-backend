package com.ticketgo.mapper;

import com.ticketgo.dto.UserDTO;
import com.ticketgo.model.BusCompany;
import com.ticketgo.model.Customer;
import com.ticketgo.model.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "role", expression = "java(user.getRole().toString())")
    UserDTO toUserDTO(User user);

    default UserDTO mapUser(User user) {
        UserDTO dto = toUserDTO(user);

        if (user instanceof Customer customer) {
            dto.setFullName(customer.getFullName());
            dto.setPhoneNumber(customer.getPhoneNumber());
            dto.setDateOfBirth(customer.getDateOfBirth().toString());
        }

        if (user instanceof BusCompany busCompany) {
            dto.setBusCompanyName(busCompany.getBusCompanyName());
            dto.setContactEmail(busCompany.getContactEmail());
            dto.setContactPhone(busCompany.getContactPhone());
            dto.setAddress(busCompany.getAddress());
            dto.setDescription(busCompany.getDescription());
        }
        return dto;
    }
}

