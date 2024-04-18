package com.exemple.QA.repository;


import com.exemple.QA.model.Role; // Importer la classe Role à partir du bon package
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {



	
}