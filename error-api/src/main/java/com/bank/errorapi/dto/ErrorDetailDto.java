package com.bank.errorapi.dto;

import com.bank.errorapi.entity.CobolError;
import com.bank.errorapi.entity.ErrorCorrelation;
import com.bank.errorapi.entity.ErrorVariable;

import java.util.List;

public record ErrorDetailDto(
    CobolError error,
    ErrorCorrelation correlation,
    List<ErrorVariable> variables,
    List<CobolError> relatedErrors
) {}
