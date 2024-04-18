package com.exemple.QA.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
@Entity
@Table(name = "roles")
public class Role {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	     
	    @Column(nullable = false, length = 45)
	    private String name;
	    
	    
	    public Role() { }
	     
	    public Role(String name) {
	        this.name = name;
	    }
	     
	    public Role(Long id, String name) {
	        this.id = id;
	        this.name = name;
	    }
	 
	    public Role(Long id) {
	        this.id = id;
	    }
	     
	 
	    @Override
	    public String toString() {
	        return this.name;
	    }

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	 
	
	}
