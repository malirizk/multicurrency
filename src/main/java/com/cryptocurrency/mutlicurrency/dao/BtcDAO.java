package com.cryptocurrency.mutlicurrency.dao;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.wallet.Wallet;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
//@Component
public class BtcDAO {

	private NetworkParameters netParams;

//	@PostConstruct
	public void init() {
		netParams = NetworkParameters.regTests();

		// PeerGroup.connectToLocalHost();
	}

	public void createWallet() throws IOException {
		ECKey key = new ECKey();
		Address addressFromKey = key.toAddress(netParams);
		log.info("New BTC address created : " + addressFromKey);
		
		Wallet wallet = new Wallet(netParams);
		wallet.addKey(new ECKey());
		
		File walletFile = new File("test.wallet");
		wallet.saveToFile(walletFile);
		
		// fetch the first key in the wallet directly from the keychain ArrayList
		ECKey firstKey = wallet.currentReceiveKey();
		// output key 
		log.info("First key in the wallet:\n" + firstKey);
		// and here is the whole wallet
		log.info("Complete content of the wallet:\n" + wallet);
		// we can use the hash of the public key
		// to check whether the key pair is in this wallet
		if (wallet.isPubKeyHashMine(firstKey.getPubKeyHash())) {
			log.info("Yep, that's my key.");
		} else {
			log.info("Nope, that key didn't come from this wallet.");
		}
	}
}
