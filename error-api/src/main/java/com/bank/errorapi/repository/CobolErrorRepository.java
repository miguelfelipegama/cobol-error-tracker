package com.bank.errorapi.repository;

import com.bank.errorapi.entity.CobolError;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CobolErrorRepository implements PanacheRepositoryBase<CobolError, String> {
}
