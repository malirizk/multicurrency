package com.cryptocurrency.mutlicurrency.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class BusinessException extends Exception {

	private static final long serialVersionUID = 1L;

	private String message;
	private String code;
	private HttpStatus httpStatus;
	private Map<String, String> extraResponseData;

	public BusinessException(String message, HttpStatus httpStatus) {
		super();
		this.message = message;
		this.httpStatus = httpStatus;
	}

	public BusinessException(String code, String message) {
		super();
		this.message = message;
		this.code = code;
	}

	public BusinessException(String code, String message, HttpStatus httpStatus) {
		super();
		this.message = message;
		this.code = code;
		this.httpStatus = httpStatus;
	}

	public BusinessException(String message, String code, HttpStatus httpStatus, Map<String, String> extraResponseData) {
		super();
		this.message = message;
		this.code = code;
		this.httpStatus = httpStatus;
		this.extraResponseData = extraResponseData;
	}

}
