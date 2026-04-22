package com.bank.errorconsumer.repository;

import com.bank.errorconsumer.entity.ErrorCorrelation;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class ErrorCorrelationRepository implements PanacheRepositoryBase<ErrorCorrelation, UUID> {
    
    public ErrorCorrelation findByContext(String cicsCpu, String taskCode, String creditObjectNumber, String creditObjectType) {
        return find("cicsCpu = ?1 and taskCode = ?2 and creditObjectNumber = ?3 and creditObjectType = ?4", 
                cicsCpu, taskCode, creditObjectNumber, creditObjectType).firstResult();
    }
}
