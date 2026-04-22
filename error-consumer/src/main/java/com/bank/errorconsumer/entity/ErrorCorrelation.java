package com.bank.errorconsumer.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_ERROR_CORRELATION")
@Data
public class ErrorCorrelation {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    private String cicsCpu;
    private String taskCode;
    private String creditObjectNumber;
    private String creditObjectType;
    
    private LocalDateTime createdAt;
}
