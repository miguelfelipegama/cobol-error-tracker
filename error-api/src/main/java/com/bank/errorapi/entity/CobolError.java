package com.bank.errorapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "TB_COBOL_ERROR")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CobolError {
    @Id
    @Column(length = 64)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "correlation_id")
    @JsonIgnoreProperties("errors")
    private ErrorCorrelation correlation;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "error_id")
    @JsonIgnoreProperties("cobolError")
    private List<ErrorVariable> variables;

    private String errorCode;
    private String errorMessage;
    private String programName;

    private LocalDateTime timestamp;
}
