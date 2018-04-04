package com.cryptocurrency.mutlicurrency.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;

import lombok.Data;

@Data
@Entity
@Table
public class UserDetails implements Serializable {

	@Id
	@GeneratedValue
	private Long id;
	@Email
	private String email;

	private String password;

	@OneToOne
	private EthWallet ethWallet;
}
