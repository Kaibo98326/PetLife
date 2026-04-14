package com.petlife.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity@Table(name = "StayRoomType")
@Getter
@Setter
@NoArgsConstructor

public class StayRoomType {

	@Id@Column(name = "room_type_id")
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer roomTypeId;
	
	@Column(name = "room_name")
	private String roomName;
	
	@Column(name = "room_price")
	private Double roomPrice;
	
	@Column(name = "room_description")
	private String roomDescription;
	
	@Column(name = "capacity")
	private Integer capacity;
	
}
