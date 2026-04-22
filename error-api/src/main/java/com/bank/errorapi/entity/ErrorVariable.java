package com.bank.errorapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "TB_ERROR_VARIABLE")
@IdClass(ErrorVariableId.class)
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ErrorVariable {
    @Id
    @Column(name = "error_id", length = 64)
    private String errorId;

    @Id
    @Column(name = "sequence")
    private Integer sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "error_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"variables", "correlation"})
    private CobolError cobolError;

    private String variableName;

    private String variableValue;
}
