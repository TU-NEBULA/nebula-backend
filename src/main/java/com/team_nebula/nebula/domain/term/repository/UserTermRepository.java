package com.team_nebula.nebula.domain.term.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team_nebula.nebula.domain.term.entity.UserTerm;
import com.team_nebula.nebula.domain.user.entity.User;

@Repository
public interface UserTermRepository extends JpaRepository<UserTerm, Long> {
	List<UserTerm> findByUser(User user);

}
