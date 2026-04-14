package com.petlife.model;

import java.time.LocalDate;


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

@Entity@Table(name = "Stay")
@Getter
@Setter
@NoArgsConstructor

public class Stay {


	@Id@Column(name = "stay_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer stayId;
	
	@ManyToOne
	@JoinColumn(name = "pet_id")
	private Pet pet;
	
	@ManyToOne
	@JoinColumn(name = "room_type_id")
	private StayRoomType stayRoomType;
	
	@ManyToOne
	@JoinColumn(name = "room_id")
	private StayRoom stayRoom;
	
	@Column(name = "stay_start_date")
	private LocalDate stayStartDate;
	
	@Column(name = "stay_end_date")
	private LocalDate stayEndDate;
	
	@Column(name = "stay_status")
	private String stayStatus;
	
	@Column(name = "order_price")
	private Double orderPrice;
	
	@Column(name = "stay_day")
	private Integer stayDay;
	
	@Column(name = "sum_price")
	private Double sumPrice;
	
	
	
	
	
}
