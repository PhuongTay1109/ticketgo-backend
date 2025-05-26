package com.ticketgo.service.impl;

import com.ticketgo.dto.BusCompanyDto;
import com.ticketgo.entity.BusCompany;
import com.ticketgo.repository.BusCompanyRepository;
import com.ticketgo.service.BusCompanyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusCompanyServiceImpl implements BusCompanyService {

    private final BusCompanyRepository busCompanyRepository;

    @Override
    public BusCompanyDto getBusCompanyById(Long id) {
        BusCompany busCompany = busCompanyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bus company not found"));

        return mapToDto(busCompany);
    }

    @Override
    public void updateBusCompany(Long id, BusCompanyDto updatedInfo) {
        BusCompany busCompany = busCompanyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bus company not found"));

        if (updatedInfo.getBusCompanyName() != null) {
            busCompany.setBusCompanyName(updatedInfo.getBusCompanyName());
        }
        if (updatedInfo.getContactEmail() != null) {
            busCompany.setContactEmail(updatedInfo.getContactEmail());
        }
        if (updatedInfo.getContactPhone() != null) {
            busCompany.setContactPhone(updatedInfo.getContactPhone());
        }
        if (updatedInfo.getAddress() != null) {
            busCompany.setAddress(updatedInfo.getAddress());
        }
        if (updatedInfo.getDescription() != null) {
            busCompany.setDescription(updatedInfo.getDescription());
        }
        if (updatedInfo.getBannerUrl() != null) {
            busCompany.setBannerUrl(updatedInfo.getBannerUrl());
        }
        if (updatedInfo.getBusinessRegistrationAddress() != null) {
            busCompany.setBusinessRegistrationAddress(updatedInfo.getBusinessRegistrationAddress());
        }
        if (updatedInfo.getBusinessLicenseNumber() != null) {
            busCompany.setBusinessLicenseNumber(updatedInfo.getBusinessLicenseNumber());
        }
        if (updatedInfo.getLicenseIssuer() != null) {
            busCompany.setLicenseIssuer(updatedInfo.getLicenseIssuer());
        }
        if (updatedInfo.getLicenseIssueDate() != null) {
            busCompany.setLicenseIssueDate(updatedInfo.getLicenseIssueDate());
        }

        busCompanyRepository.save(busCompany);
    }


    private BusCompanyDto mapToDto(BusCompany company) {
        return BusCompanyDto.builder()
                .busCompanyName(company.getBusCompanyName())
                .contactEmail(company.getContactEmail())
                .contactPhone(company.getContactPhone())
                .address(company.getAddress())
                .description(company.getDescription())
                .bannerUrl(company.getBannerUrl())
                .businessRegistrationAddress(company.getBusinessRegistrationAddress())
                .businessLicenseNumber(company.getBusinessLicenseNumber())
                .licenseIssuer(company.getLicenseIssuer())
                .licenseIssueDate(company.getLicenseIssueDate())
                .build();
    }
}
