package com.bank.errorconsumer.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_COBOL_ERROR")
@Data
public class CobolError {
    @Id
    @Column(length = 64)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "correlation_id")
    private ErrorCorrelation correlation;

    private String errorCode;
    private String errorMessage;
    private String programName;
    
    private LocalDateTime timestamp;
}
