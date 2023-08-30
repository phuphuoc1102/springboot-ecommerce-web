package com.ecommerce.library.model;

import java.util.Collection;
import java.util.List;

import jakarta.persistence.*;
import org.hibernate.annotations.ManyToAny;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="admins")
public class Admin {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name="admin_id")
	private Long id;
	private String firstName;
	private String lastName;
	private String username;
	private String password;
	@Lob
	@Column(columnDefinition = "MEDIUMBLOB")
	private String image;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "admin_roles",joinColumns = @JoinColumn(name="admin_id",referencedColumnName = "admin_id"),
	inverseJoinColumns = @JoinColumn(name="role_id",referencedColumnName = "role_id")
			)
	private Collection<Role> roles;
	@OneToMany(mappedBy = "admin")
	private List<Activity> activityList;

}
