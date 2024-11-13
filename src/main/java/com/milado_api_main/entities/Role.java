package com.milado_api_main.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "roles", schema = "dbtogo")
public class Role {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name")
    private String name;


  

}