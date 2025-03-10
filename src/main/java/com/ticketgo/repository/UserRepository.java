package com.ticketgo.repository;

import com.ticketgo.entity.User;
import com.ticketgo.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE BusCompany b SET b.role = :role WHERE b.email = :email")
    void updateRoleByEmail(@Param("role") Role role, @Param("email") String email);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE User u
        SET u.isDeleted = true
        WHERE u.userId = :id
    """)
    void softDelete(Long id);
}
