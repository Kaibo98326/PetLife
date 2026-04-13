package com.petlife.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRoleId implements Serializable {
    @Column(name = "emp_id")
    private Integer empId;

    @Column(name = "role_id")
    private Integer roleId;
}


