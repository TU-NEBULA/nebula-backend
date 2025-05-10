package com.team_nebula.nebula.domain.history.entity;

import com.team_nebula.nebula.domain.user.entity.User;
import com.team_nebula.nebula.global.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "histories")
public class History extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "last_visit_time", nullable = false)
	private Double lastVisitTime;

	@Column(name = "titlte", nullable = false)
	private String title;

	@Column(name = "typed_count", nullable = false)
	private Long typedCount;

	@Column(name = "url", nullable = false)
	private String url;

	@Column(name = "visit_count", nullable = false)
	private Long visitCount;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Builder
	public History(Double lastVisitTime, String title, Long typedCount, String url, Long visitCount, User user) {
		this.lastVisitTime = lastVisitTime;
		this.title = title;
		this.typedCount = typedCount;
		this.url = url;
		this.visitCount = visitCount;
		this.user = user;
	}
}
