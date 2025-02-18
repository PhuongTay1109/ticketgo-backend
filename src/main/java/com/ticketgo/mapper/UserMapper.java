package com.ticketgo.mapper;

import com.ticketgo.dto.UserDTO;
import com.ticketgo.entity.BusCompany;
import com.ticketgo.entity.Customer;
import com.ticketgo.entity.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "role", expression = "java(user.getRole().toString())")
    UserDTO toUserDTO(User user);

    default UserDTO mapUser(User user) {
        UserDTO dto = toUserDTO(user);

        if (user instanceof Customer customer) {
            dto.setFullName(customer.getFullName());
            dto.setPhoneNumber(customer.getPhoneNumber());
            dto.setDateOfBirth(customer.getDateOfBirth() != null ?
                    customer.getDateOfBirth().toString() : null);
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

