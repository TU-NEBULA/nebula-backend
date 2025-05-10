package com.team_nebula.nebula.domain.history.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.team_nebula.nebula.domain.history.entity.History;
import com.team_nebula.nebula.domain.user.entity.User;
import org.springframework.data.jpa.repository.Query;

public interface HistoryRepository extends JpaRepository<History, Long> {
	Page<History> findAllByUser(User user, Pageable pageable);

	@Query("""
        SELECT h FROM History h
        WHERE h.user = :user
        AND (LOWER(h.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(h.url) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """)
	Page<History> searchByKeyword(User user, String keyword, Pageable pageable);
}
