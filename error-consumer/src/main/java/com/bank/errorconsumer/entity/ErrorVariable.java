package com.bank.errorconsumer.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "TB_ERROR_VARIABLE")
@IdClass(ErrorVariableId.class)
@Data
public class ErrorVariable {
    @Id
    @Column(name = "error_id", length = 64)
    private String errorId;

    @Id
    @Column(name = "sequence")
    private Integer sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "error_id", insertable = false, updatable = false)
    private CobolError cobolError;

    private String variableName;
    
    private String variableValue;
}
