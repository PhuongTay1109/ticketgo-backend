package com.ticketgo.repository;

import com.ticketgo.entity.Customer;
import com.ticketgo.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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

    Page<Customer> findByRole(Role role, Pageable pageable);

    @Query("""
        SELECT c
        FROM Customer c
        WHERE c.role = :role 
        AND (c.fullName LIKE %:keyword% OR c.email LIKE %:keyword%)
    """)
    Page<Customer> findByRoleAndKeyword(Role role, String keyword, Pageable pageable);

    @Query("""
        select u.userId
        from User u
        where u.isDeleted = false
    """)
    List<Long> getAllCustomerId();
}
