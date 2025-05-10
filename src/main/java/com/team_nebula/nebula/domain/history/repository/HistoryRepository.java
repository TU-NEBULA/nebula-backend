package com.team_nebula.nebula.domain.history.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.team_nebula.nebula.domain.history.entity.History;
import com.team_nebula.nebula.domain.user.entity.User;

public interface HistoryRepository extends JpaRepository<History, Long> {
	Page<History> findAllByUser(User user, Pageable pageable);
}
