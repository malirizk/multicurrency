package com.cryptocurrency.mutlicurrency.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.exceptions.TransactionException;

import com.cryptocurrency.mutlicurrency.dao.EthDAO;
import com.cryptocurrency.mutlicurrency.model.EthWallet;
import com.cryptocurrency.mutlicurrency.model.UserDetails;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WalletService {

	@Autowired
	private EthDAO ethDAO;
	
	
	public EthWallet getEthWalletBalance(EthWallet ethWallet) throws InterruptedException, ExecutionException, IOException {
		ethWallet.setBalance(ethDAO.getBalance(ethWallet));
		
		return ethWallet;
	}
	
	public String loadFund(UserDetails user, BigInteger amount) throws TransactionException, Exception {
		return ethDAO.transferEtherAccountToAccount(user, amount);
	}
	
	public void getMainWalletBalance() throws InterruptedException, ExecutionException, IOException {
		ethDAO.getMainWalletBalance();
	}
	
	public void executeContract(UserDetails user, BigInteger amount) throws Exception {
		ethDAO.callFirstContract(user, amount);
	}
}
