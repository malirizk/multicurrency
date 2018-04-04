package com.cryptocurrency.mutlicurrency.controller;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.CipherException;

import com.cryptocurrency.mutlicurrency.dto.User;
import com.cryptocurrency.mutlicurrency.model.UserDetails;
import com.cryptocurrency.mutlicurrency.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces =  MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserDetails> createUser(@RequestBody User user) throws NoSuchAlgorithmException, NoSuchProviderException,
			InvalidAlgorithmParameterException, CipherException, IOException {
		log.info("Create User : " + user);
		return new ResponseEntity<UserDetails>(userService.createUser(user), HttpStatus.OK);
	}

	@GetMapping()
	public ResponseEntity<?> test() throws NoSuchAlgorithmException, NoSuchProviderException,
			InvalidAlgorithmParameterException, CipherException, IOException {
		userService.createUser(new User());
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

}
