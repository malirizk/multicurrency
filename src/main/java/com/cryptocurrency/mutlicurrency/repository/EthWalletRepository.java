package com.cryptocurrency.mutlicurrency.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cryptocurrency.mutlicurrency.model.EthWallet;

@Repository
public interface EthWalletRepository extends JpaRepository<EthWallet, Long>  {

}
