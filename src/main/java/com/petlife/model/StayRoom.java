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

@Entity@Table(name = "StayRoom")
@Getter
@Setter
@NoArgsConstructor

public class StayRoom {

	@Id@Column(name = "room_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer roomId;
	
	@ManyToOne
	@JoinColumn(name = "room_type_id")
	private StayRoomType stayRoomType ;
	
	@Column(name = "room_status")
	private String roomStatus;
}
