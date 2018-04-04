package com.cryptocurrency.mutlicurrency.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cryptocurrency.mutlicurrency.model.UserDetails;

@Repository
public interface UserRepository extends JpaRepository<UserDetails, Long>  {

}
