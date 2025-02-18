package com.ticketgo.service.impl;

import com.ticketgo.dto.AmenityDTO;
import com.ticketgo.mapper.AmenityMapper;
import com.ticketgo.entity.Amenity;
import com.ticketgo.repository.AmenityRepository;
import com.ticketgo.service.AmenityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AmenityServiceImpl implements AmenityService {

    private final AmenityRepository amenityRepo;

    @Override
    public List<AmenityDTO> getAmenities() {
        List<Amenity> amenities = amenityRepo.findAll();
        return amenities.stream()
                .map(AmenityMapper.INSTANCE::toAmenityDTO)
                .toList();
    }
}
