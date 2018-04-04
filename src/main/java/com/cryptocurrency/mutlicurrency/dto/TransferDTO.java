package com.cryptocurrency.mutlicurrency.dto;

import java.math.BigInteger;

import lombok.Data;

@Data
public class TransferDTO {

	private String address;
	private BigInteger amount;
}
