package com.shop.user.entity;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true, length = 120)
	private String email;

	@Column(nullable = false, length = 120)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private UserRole role = UserRole.USER;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private UserStatus status = UserStatus.ACTIVE;

	@Column(name = "disabled_at")
	private LocalDateTime disabledAt;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, optional = true)
	private UserProfile profile;

	// helper để đồng bộ 2 chiều
	public void setProfile(UserProfile p) {
		this.profile = p;
		if (p != null)
			p.setUser(this);
	}

	/*
	 * @MapsId = Profile không tự có id, nó ăn theo User.id.
	 * 
	 * cascade = ALL = persist User → persist Profile tự động.
	 * 
	 * orphanRemoval = true = set profile = null → row profile bị xóa.
	 */
}
