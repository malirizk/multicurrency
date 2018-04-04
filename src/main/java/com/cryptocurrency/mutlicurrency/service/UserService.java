package com.cryptocurrency.mutlicurrency.service;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.CipherException;

import com.cryptocurrency.mutlicurrency.dao.EthDAO;
import com.cryptocurrency.mutlicurrency.dto.User;
import com.cryptocurrency.mutlicurrency.model.UserDetails;
import com.cryptocurrency.mutlicurrency.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	@Autowired
	private EthDAO ethDAO;
	
	@Autowired
	private UserRepository userRepository;
	
	public UserDetails createUser(User user) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, CipherException, IOException {
		UserDetails userDetails	= new UserDetails();
		userDetails.setEmail(user.getEmail());
		// userDetails.setPassword(PasswordEnoderDecoder.EncryptText(password));
		userDetails.setPassword(user.getPassword());
		userDetails = userRepository.save(userDetails);
		log.info("After Create User : {}", userDetails);
		
		ethDAO.createWallet(userDetails);
		
		userDetails = userRepository.save(userDetails);
		return userDetails;
	}
	
	
	public Optional<UserDetails> findUserById(Long id) {
		return userRepository.findById(id);
	}
	
}
