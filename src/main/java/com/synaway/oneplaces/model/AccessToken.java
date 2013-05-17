package com.synaway.oneplaces.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
@Table(name = "access_token")
public class AccessToken {

	@JsonIgnore
	@Id
    @GeneratedValue(generator = "access_token_id", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "access_token_id", sequenceName = "access_token_id_seq", allocationSize = 1)
	@Column(name = "id")
	private Long id;
	
	@JsonIgnore
	@ManyToOne
    @JoinColumn(name="user_id")
    private User user;
	
    private String token;
    
    private Date expire;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getExpire() {
		return expire;
	}

	public void setExpire(Date expire) {
		this.expire = expire;
	}

	@Override
	public String toString() {
		return "AccessToken [id=" + id + ", user=" + user + ", token=" + token + ", expire=" + expire + "]";
	}
}
