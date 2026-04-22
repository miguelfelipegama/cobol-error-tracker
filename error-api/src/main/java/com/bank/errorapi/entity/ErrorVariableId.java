package com.bank.errorapi.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class ErrorVariableId implements Serializable {
    private String errorId;
    private Integer sequence;
}
