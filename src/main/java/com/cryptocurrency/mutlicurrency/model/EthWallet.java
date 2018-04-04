package com.cryptocurrency.mutlicurrency.model;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table
public class EthWallet implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	private String address, walletFileName, password;
	
	private BigInteger balance;
}
