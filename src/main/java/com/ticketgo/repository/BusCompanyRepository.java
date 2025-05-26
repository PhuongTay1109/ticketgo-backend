package com.ticketgo.repository;

import com.ticketgo.entity.BusCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusCompanyRepository extends JpaRepository<BusCompany, Long> {
}
