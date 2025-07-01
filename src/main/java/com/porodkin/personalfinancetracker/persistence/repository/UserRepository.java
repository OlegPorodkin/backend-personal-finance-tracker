package com.porodkin.personalfinancetracker.persistence.repository;

import com.porodkin.personalfinancetracker.persistence.entity.FintrackerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<FintrackerUser,Long> {
    Optional<FintrackerUser> findByEmail(String email);
}
