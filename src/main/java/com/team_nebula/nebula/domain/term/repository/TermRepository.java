package com.team_nebula.nebula.domain.term.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team_nebula.nebula.domain.term.entity.Term;
import com.team_nebula.nebula.domain.term.entity.TermType;

@Repository
public interface TermRepository extends JpaRepository<Term, Long> {
	List<Term> findByTermType(TermType termType);
}
