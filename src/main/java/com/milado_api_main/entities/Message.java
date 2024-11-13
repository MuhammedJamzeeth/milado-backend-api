package com.milado_api_main.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "messages", schema = "dbtogo")
public class Message {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "senderId")
    private Integer senderId;

    @Column(name = "recipientId")
    private Integer recipientId;

    @Lob
    @Column(name = "content")
    private String content;

}