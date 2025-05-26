package com.ticketgo.service;

import com.ticketgo.dto.BusCompanyDto;

public interface BusCompanyService {
    BusCompanyDto getBusCompanyById(Long id);
    void updateBusCompany(Long id, BusCompanyDto updatedInfo);
}
