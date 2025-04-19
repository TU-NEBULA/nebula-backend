package com.team_nebula.nebula.domain.history.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team_nebula.nebula.domain.history.entity.History;
import com.team_nebula.nebula.domain.user.entity.User;

public interface HistoryRepository extends JpaRepository<History, Long> {
	List<History> findByUser(User user);
}
