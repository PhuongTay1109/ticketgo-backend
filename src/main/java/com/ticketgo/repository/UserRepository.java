package com.ticketgo.repository;

import com.ticketgo.enums.Role;
import com.ticketgo.entity.User;
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
}
