package com.cryptocurrency.mutlicurrency.dao;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.utils.Convert;

import com.cryptocurrency.mutlicurrency.contract.FirstContract;
import com.cryptocurrency.mutlicurrency.model.EthWallet;
import com.cryptocurrency.mutlicurrency.model.UserDetails;
import com.cryptocurrency.mutlicurrency.repository.EthWalletRepository;

import lombok.extern.slf4j.Slf4j;
import rx.Subscription;

@Slf4j
@Component
public class EthDAO {

	@Autowired
	private EthWalletRepository ethWalletRepository;

	@Value("${eth.hostUrl}")
	private String ethHostUrl;
	@Value("${eth.keyPath}")
	private String keysPath;
	@Value("${eth.gas.min}")
	private long minGas;
	@Value("${eth.gas.limit}")
	private BigInteger gasLimit;

	private static Web3j web3j = null;

	private static final int COUNT = 10;

	@PostConstruct
	public void init() throws IOException, InterruptedException {

		web3j = Web3j.build(new HttpService(ethHostUrl));
		Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().send();
		String clientVersion = web3ClientVersion.getWeb3ClientVersion();
		log.info("clientVersion >>> " + clientVersion);
//		toChecksumAddress("0xdbeda162c99bcbe9f716755d125cb04a49866b98");
	}

	public void createWallet(UserDetails user) throws NoSuchAlgorithmException, NoSuchProviderException,
			InvalidAlgorithmParameterException, CipherException, IOException {

		String password = user.getPassword();
		String walletFileName = WalletUtils.generateFullNewWalletFile(password, new File(keysPath));

		log.info("Create Wallet for User id");
		String[] fetchAddress = walletFileName.split("--");
		String getAddress = fetchAddress[fetchAddress.length - 1].split("\\.")[0];

		log.info("walletFileName>>>>>" + walletFileName.substring(0));
		log.info("walletFile Address>>>>>" + "0x" + getAddress);

		EthWallet ethWallet = new EthWallet();
		ethWallet.setAddress("0x" + getAddress);
		log.info("setAddress " + "0x" + getAddress);
		// userWallet.setWalletType(WalletType.TOKEN);
		ethWallet.setWalletFileName(walletFileName);

		ethWallet = ethWalletRepository.save(ethWallet);

		user.setEthWallet(ethWallet);
	}

	public BigInteger getMainWalletBalance() throws InterruptedException, ExecutionException, IOException {
		web3j.ethAccounts().send().getAccounts().forEach(ethAccount -> {
			try {
				log.info(ethAccount + " = "
						+ web3j.ethGetBalance(ethAccount, DefaultBlockParameterName.LATEST).send().getBalance());
			} catch (IOException e) {
				log.error(e.getMessage(), e.getCause());
			}
		});

		return web3j.ethGetBalance(web3j.ethAccounts().send().getAccounts().get(0), DefaultBlockParameterName.LATEST)
				.send().getBalance();
	}

	public BigInteger getBalance(EthWallet wallet) throws InterruptedException, ExecutionException, IOException {
		EthGetBalance ethGetBalance = web3j.ethGetBalance(wallet.getAddress(), DefaultBlockParameterName.LATEST).send();
		// .sendAsync().get();

		BigInteger wei = ethGetBalance.getBalance();
		return wei;
	}

	public String transferEtherAccountToAccount(UserDetails user, BigInteger amount)
			throws TransactionException, Exception {

		EthWallet wallet = user.getEthWallet();

		BigInteger ethAccountBalance = getMainWalletBalance();

		log.info("Getting wallet ..." + wallet.getAddress() + "  and balance is "
				+ web3j.ethGetBalance(wallet.getAddress(), DefaultBlockParameterName.fromString("latest")).send()
						.getBalance());
		// Minimum gas is 21000 used for transaction
		BigInteger gasLimit = BigInteger.valueOf(minGas);
		log.info("gas:::" + gasLimit);

		BigInteger xth = new BigInteger("1000000000000000000");
		log.info(":::Transferred amount is::::" + amount);

		// Pick gas price from geth console
		BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();

		log.info("gasprice:::" + gasPrice);

		log.info("gaslimit:::" + gasLimit);

		// Calculate transaction fees for transaction
		BigInteger txnFee = gasLimit.multiply(gasPrice);
		BigInteger totalAmount = txnFee.add(amount);

		log.info("gas * price + value = " + gasLimit + " * " + gasPrice + " + " + amount + " = " + totalAmount);

		// Validate balance to perform transaction fees
		int compareResult = ethAccountBalance.compareTo(txnFee.add(amount));
		log.info("Compare result:: " + compareResult);

		log.info(":::Transferred amount is :::" + amount);
		if ((compareResult != 0 && compareResult != -1)
				&& wallet.getAddress() != web3j.ethAccounts().send().getAccounts().get(0)) {

			Credentials credentials = WalletUtils.loadCredentials(user.getPassword(),
					keysPath + wallet.getWalletFileName());
			log.info("credentials:::from address:::" + credentials.getAddress());

			log.info("--------------Wallet keystore location is " + keysPath + "/" + wallet.getWalletFileName());
			EthGetTransactionCount ethGetTransactionCount = web3j
					.ethGetTransactionCount(web3j.ethAccounts().send().getAccounts().get(0),
							DefaultBlockParameterName.LATEST)
					.sendAsync().get();
			log.info("--------------ethGetTransactionCount" + ethGetTransactionCount.getTransactionCount());

			// get the next available nonce
			BigInteger nonce = ethGetTransactionCount.getTransactionCount();
			log.info("----------nonce" + nonce);

			BigDecimal gasInDecimal = new BigDecimal(gasLimit);
			BigDecimal gaspriceInDecimal = new BigDecimal(gasPrice);

			// balance =
			// balance.subtract((gasInDecimal.multiply(gaspriceInDecimal)).divide(new
			// BigDecimal(xth)));
			log.info("::::gasInDecimal:>>>>>>>>>>>>>>>>>>>>>>>>>>>>:" + gasInDecimal + "::gaspriceInDecimal:::::::"
					+ gaspriceInDecimal);
			// log.info(":::Transferred amount is::::>>>>>>>>>>>>>>>>>>>>>" + balance);

			/*
			 * TransactionReceipt transactionReceipt = Transfer .sendFunds(web3j,
			 * credentials, wallet.getAddress(), new BigDecimal(amount), Convert.Unit.ETHER)
			 * .send();
			 * 
			 * log.info("trn recpt " + transactionReceipt.getTransactionHash() +
			 * " new balance is " + web3j.ethGetBalance(wallet.getAddress(),
			 * DefaultBlockParameterName.fromString("latest")).send() .getBalance());
			 */

			ClientTransactionManager clientTransactionManager = new ClientTransactionManager(web3j,
					web3j.ethAccounts().send().getAccounts().get(0));
			System.out.println("From address=" + clientTransactionManager.getFromAddress());

			org.web3j.tx.Transfer transfer = new org.web3j.tx.Transfer(web3j, clientTransactionManager);
			System.out.println("transfer=" + String.valueOf(transfer.getSyncThreshold()));

			RemoteCall<TransactionReceipt> rc = transfer.sendFunds(wallet.getAddress(), new BigDecimal(amount),
					Convert.Unit.ETHER);
			TransactionReceipt transactionReceipt = rc.send();

			return transactionReceipt.getTransactionHash();

		} else {
			// error insuffecient balance
			log.error("insuffecient balance");
		}

		return "";
	}

	void simpleFilterExample() throws Exception {

		Subscription subscription = web3j.blockObservable(false).subscribe(block -> {
			log.info("Sweet, block number " + block.getBlock().getNumber() + " has just been created");
		}, Throwable::printStackTrace);

		TimeUnit.MINUTES.sleep(2);
		subscription.unsubscribe();
	}

	void blockInfoExample() throws Exception {
		CountDownLatch countDownLatch = new CountDownLatch(COUNT);

		log.info("Waiting for " + COUNT + " transactions...");
		Subscription subscription = web3j.blockObservable(true).take(COUNT).subscribe(ethBlock -> {
			EthBlock.Block block = ethBlock.getBlock();
			LocalDateTime timestamp = Instant.ofEpochSecond(block.getTimestamp().longValueExact())
					.atZone(ZoneId.of("UTC")).toLocalDateTime();
			int transactionCount = block.getTransactions().size();
			String hash = block.getHash();
			String parentHash = block.getParentHash();

			log.info(timestamp + " " + "Tx count: " + transactionCount + ", " + "Hash: " + hash + ", " + "Parent hash: "
					+ parentHash);
			countDownLatch.countDown();
		}, Throwable::printStackTrace);

		countDownLatch.await(10, TimeUnit.MINUTES);
		subscription.unsubscribe();
	}

	void countingEtherExample() throws Exception {
		CountDownLatch countDownLatch = new CountDownLatch(1);

		log.info("Waiting for " + COUNT + " transactions...");
		rx.Observable<BigInteger> transactionValue = web3j.transactionObservable().take(COUNT)
				.map(Transaction::getValue).reduce(BigInteger.ZERO, BigInteger::add);

		Subscription subscription = transactionValue.subscribe(total -> {
			BigDecimal value = new BigDecimal(total);
			log.info("Transaction value: " + Convert.fromWei(value, Convert.Unit.ETHER) + " Ether (" + value + " Wei)");
			countDownLatch.countDown();
		}, Throwable::printStackTrace);

		countDownLatch.await(10, TimeUnit.MINUTES);
		subscription.unsubscribe();
	}
	
	public void toChecksumAddress (String address) {
		String checksumAddress = "0x";
		address = address.toLowerCase().replace("0x", "");
		
		char[] caseMap = web3j.web3Sha3(address).toString().substring(0, 40).toCharArray();
		char[] addressChars = address .toCharArray();
		
		for (int i = 0; i < address.length(); i++ ) {  
	        if (caseMap[i] == '1') {
	          checksumAddress += String.valueOf(addressChars[i]).toUpperCase();
	        } else {
	            checksumAddress += addressChars[i];
	        }
	    }
		
		log.info(">>>>>>>>>> checksum : " + address + " ::: " + checksumAddress);
	}
	
	
	public void callFirstContract(UserDetails user, BigInteger amount) throws Exception {
		Credentials credentials = WalletUtils.loadCredentials(user.getPassword(),
				keysPath + user.getEthWallet().getWalletFileName());
		BigInteger gasprice = web3j.ethGasPrice().send().getGasPrice();
		FirstContract contract = FirstContract.deploy(web3j, credentials, gasprice, gasLimit).send();
		log.info("X Balance ::: " + contract.getBalance().send());
		TransactionReceipt transactionReceipt = contract.transfer(amount).send();
		log.info("Transaction  ID  ::: " + transactionReceipt.getTransactionHash());
		log.info("X Balance ::: " + contract.getBalance().send());
	}
	
	private void saveTxn() throws InterruptedException {
//		http://api.etherscan.io/api?module=account&action=txlist&address=0xde0b295669a9fd93d5f28d9ec85e40f4cb697bae&startblock=0&endblock=100000000&sort=asc&apikey=YourApiKeyToken
		CountDownLatch countDownLatch = new CountDownLatch(1);
        Web3j web3j = Web3j.build(new HttpService());
        web3j.catchUpToLatestAndSubscribeToNewTransactionsObservable(DefaultBlockParameterName.EARLIEST)
                .filter(tx -> tx.getFrom().equals("0x<address>"))
                .subscribe(
                        tx -> System.out.println(tx.getValue()),
                        Throwable::printStackTrace,
                        countDownLatch::countDown);
        Thread.sleep(TimeUnit.MINUTES.toMillis(1));
	}
	
	/*public void findTransactionsByAddress() {
		web3j.ethGetTransactionByBlockNumberAndIndex(defaultBlockParameter, transactionIndex)
	}*/
}
