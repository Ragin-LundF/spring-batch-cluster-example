package com.example.batch.models;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(
        name = "TRANSACTIONS",
        indexes = {
                @Index(name = "IDX_LABEL", columnList = "LABEL")
        }
)
public class Transactions {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "PURPOSE", nullable = false, length = 255)
    private String purpose;
    @Column(name = "LABEL", nullable = false, length = 50)
    private String label;
    @Column(name = "AMOUNT", nullable = false)
    private BigDecimal amount;
}
