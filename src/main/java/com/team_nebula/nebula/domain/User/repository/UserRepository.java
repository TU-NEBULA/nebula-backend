package com.team_nebula.nebula.domain.User.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team_nebula.nebula.domain.User.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);
}