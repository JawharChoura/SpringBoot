package com.exemple.QA.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.exemple.QA.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);
	List<User> findByRoles_Id(Long roleId);

	List<User> findAllByEmail(String email);
	List<User> findByFirstNameIgnoreCaseOrLastNameIgnoreCase(String name, String name2);
	

	
	
}