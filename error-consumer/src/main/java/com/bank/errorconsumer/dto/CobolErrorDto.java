package com.bank.errorconsumer.dto;

import lombok.Data;
import java.util.List;

@Data
public class CobolErrorDto {
    private String cicsCpu;
    private String taskCode;
    private String creditObjectNumber;
    private String creditObjectType;
    private String errorCode;
    private String errorMessage;
    private String programName;
    private List<ErrorVariableDto> variables;
}
