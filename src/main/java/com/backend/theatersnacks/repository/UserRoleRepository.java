package com.backend.theatersnacks.repository;

import com.backend.theatersnacks.entity.UserRole;
import com.backend.theatersnacks.entity.User;
import com.backend.theatersnacks.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUser(User user);
    List<UserRole> findByRole(Role role);
    Optional<UserRole> findByUserAndRole(User user, Role role);
}
