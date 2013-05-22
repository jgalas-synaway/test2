package com.synaway.oneplaces.model;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.PageRequest;

import com.synaway.oneplaces.repository.SpotRepository;



@Entity
@Table(name = "users")
public class User {

	
    @Id
    @GeneratedValue(generator = "user_id", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "user_id", sequenceName = "user_id_seq", allocationSize = 1)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "login")
	private String login;
	
	@JsonIgnore
	@Column(name = "password")
	private String password;
	
	@Column(name = "role")
	private String role;
	
	@Column(name = "creation_date")
	private Date creationDate = Calendar.getInstance().getTime();
	
	@Column(name = "modification_date")
	private Date modificationDate;
	
	@JsonIgnore
	@OneToMany(mappedBy = "user")
	private List<Spot> spots;
	
	@JsonIgnore
	@OneToMany(mappedBy = "user")
	private List<UserLocation> user_locations;
	
	
	
	public User() {
		super();
	}

	public User(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	@JsonProperty("password")
	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	public List<Spot> getSpots() {
		return spots;
	}

	public void setSpots(List<Spot> spots) {
		this.spots = spots;
	}

	public List<UserLocation> getUser_locations() {
		return user_locations;
	}

	public void setUser_locations(List<UserLocation> user_locations) {
		this.user_locations = user_locations;
	}
}
