package com.cryptocurrency.mutlicurrency.controller;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.exceptions.TransactionException;

import com.cryptocurrency.mutlicurrency.dto.TransferDTO;
import com.cryptocurrency.mutlicurrency.model.EthWallet;
import com.cryptocurrency.mutlicurrency.model.UserDetails;
import com.cryptocurrency.mutlicurrency.service.UserService;
import com.cryptocurrency.mutlicurrency.service.WalletService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class WalletController {

	@Autowired
	private UserService userService;

	@Autowired
	private WalletService walletService;

	@GetMapping(value = "/{userId}/wallets/{walletType}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<EthWallet> getWallet(@PathVariable(value = "userId") Long userId,
			@PathVariable(value = "walletType", required = false) String walletType)
			throws InterruptedException, ExecutionException, IOException {
		UserDetails user = userService.findUserById(userId).get();

		return new ResponseEntity<EthWallet>(walletService.getEthWalletBalance(user.getEthWallet()), HttpStatus.OK);
	}

	@GetMapping(value = "/main/{walletType}/wallet")
	public ResponseEntity<?> getMainWalletBalance(
			@PathVariable(value = "walletType", required = false) String walletType)
			throws InterruptedException, ExecutionException, IOException {
		walletService.getMainWalletBalance();
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
	@PostMapping(value = "/{userId}/wallets/{walletType}/load/fund")
	public ResponseEntity<String> loadFund(@PathVariable(value = "userId") Long userId,
			@RequestBody TransferDTO transferDTO) throws TransactionException, Exception {
		UserDetails user = userService.findUserById(userId).get();
		return  new ResponseEntity<String>(walletService.loadFund(user, transferDTO.getAmount()), HttpStatus.OK);
	}
	
	@PostMapping(value = "/{userId}/wallets/{walletType}/contract")
	public ResponseEntity<?> executeContract(@PathVariable(value = "userId") Long userId,
			@RequestBody TransferDTO transferDTO) throws TransactionException, Exception {
		UserDetails user = userService.findUserById(userId).get();
		walletService.executeContract(user, transferDTO.getAmount());
		return  new ResponseEntity<Void>(HttpStatus.OK);
	}

	@PostMapping(value = "/{userId}/wallets/{walletType}/transactions", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public void transfer(@RequestBody TransferDTO transferDTO) {

	}
}
