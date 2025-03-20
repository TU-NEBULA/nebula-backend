package com.team_nebula.nebula.domain.term.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team_nebula.nebula.domain.term.entity.UserTerm;

@Repository
public interface UserTermRepository extends JpaRepository<UserTerm, Long> {
}
