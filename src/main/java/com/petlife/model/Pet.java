package com.petlife.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity@Table(name = "Pet")
@Getter
@Setter
@NoArgsConstructor
public class Pet {
	@Id@Column(name = "pet_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer petId;
	
	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;
	
	@Column(name = "pet_name")
	private String petName;
	
	@Column(name = "breed")
	private String breed;
	
	@Column(name = "species")
	private String species;
	
	@Column(name = "age")
	private Integer age;
	
	@Column(name = "weight")
	private Double weight;
	
	@Column(name = "medical_history")
	private String medicalHistory;
	
	@Column(name = "pet_photo")
	private String petPhoto;
	
	@Column(name = "status")
	private String status;
	
}
