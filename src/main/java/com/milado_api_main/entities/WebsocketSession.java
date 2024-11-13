package com.milado_api_main.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "websocketsessions", schema = "dbtogo")
public class WebsocketSession {
    @Id
    @Size(max = 255)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "userId")
    private Integer userId;

    @Column(name = "connectedAt")
    private Date connectedAt;

    @Column(name = "disconnectedAt")
    private Date disconnectedAt;

}