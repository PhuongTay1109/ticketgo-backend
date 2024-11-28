package com.ticketgo.repository;

import com.ticketgo.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Customer c SET " +
            "c.fullName = COALESCE(:fullName, c.fullName), " +
            "c.phoneNumber = COALESCE(:phoneNumber, c.phoneNumber), " +
            "c.dateOfBirth = COALESCE(:dateOfBirth, c.dateOfBirth) " +
            "WHERE c.userId = :id")
    int updateCustomerFields(
            @Param("id") Long id,
            @Param("fullName") String fullName,
            @Param("phoneNumber") String phoneNumber,
            @Param("dateOfBirth") LocalDate dateOfBirth
    );
}
