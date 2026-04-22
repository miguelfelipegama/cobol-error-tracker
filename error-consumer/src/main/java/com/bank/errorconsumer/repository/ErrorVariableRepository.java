package com.bank.errorconsumer.repository;

import com.bank.errorconsumer.entity.ErrorVariable;
import com.bank.errorconsumer.entity.ErrorVariableId;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ErrorVariableRepository implements PanacheRepositoryBase<ErrorVariable, ErrorVariableId> {
}
