package com.example.batch.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "AGGREGATE")
public class Aggregate {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "TRX_ID", nullable = false)
    private Long trxId;
    @Column(name = "SERVER", nullable = false, length = 20)
    private String server;
}
