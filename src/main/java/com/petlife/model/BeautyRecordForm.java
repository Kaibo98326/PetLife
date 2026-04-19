package com.petlife.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BeautyRecordForm {

    private Integer recordId;
    private Integer petId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate beautyDate;

    private String beautySlot;
    private Integer beautyId;
    private String petSize;
    private String status;
}
