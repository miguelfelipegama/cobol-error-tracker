package com.bank.errorapi.repository;

import com.bank.errorapi.entity.ErrorVariable;
import com.bank.errorapi.entity.ErrorVariableId;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ErrorVariableRepository implements PanacheRepositoryBase<ErrorVariable, ErrorVariableId> {
}
