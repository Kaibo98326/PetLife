package com.petlife.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;


@Entity
@Table(name = "EmployeeRole")
public class EmployeeRole {

    @EmbeddedId
    private EmployeeRoleId id;

    @ManyToOne
    @MapsId("empId")
    @JoinColumn(name = "emp_id")
    private Employee employee;

    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private Role role;

    public EmployeeRole() {}

    public EmployeeRole(EmployeeRoleId id, Employee employee, Role role) {
        this.id = id;
        this.employee = employee;
        this.role = role;
    }
 // ✅ Getter/Setter
    public EmployeeRoleId getId() {
        return id;
    }

    public void setId(EmployeeRoleId id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }


}


