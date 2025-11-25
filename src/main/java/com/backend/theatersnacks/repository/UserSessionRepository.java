package com.backend.theatersnacks.repository;

import com.backend.theatersnacks.entity.UserSession;
import com.backend.theatersnacks.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    List<UserSession> findByUser(User user);
    Optional<UserSession> findByTokenHash(String tokenHash);
    void deleteByTokenHash(String tokenHash);
}
