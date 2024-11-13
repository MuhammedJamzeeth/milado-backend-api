package com.milado_api_main.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "websocketmessages", schema = "dbtogo")
public class WebsocketMessage {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @Column(name = "sessionId")
    private String sessionId;

    @Lob
    @Column(name = "direction")
    private String direction;

    @Lob
    @Column(name = "content")
    private String content;

    @Column(name = "sentAt")
    private Date sentAt;

}