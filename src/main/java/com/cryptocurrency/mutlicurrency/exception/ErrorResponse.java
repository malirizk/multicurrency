package com.cryptocurrency.mutlicurrency.exception;

import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class ErrorResponse {

	private String message;
	private String description;
	private String error;
	private String transactionId;
	private Map<String, String> additionalInfo;

}
