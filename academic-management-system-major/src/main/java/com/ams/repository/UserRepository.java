package com.ams.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ams.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	// this  help us to find user by mail 
	Optional<User> findByEmail (String email);
	
	// help  find student who need an approval 
 	boolean existsByEmail(String email);
 	List<User> findByRoleAndStatus(String role, String status);
}
