package com.bank.errorconsumer.repository;

import com.bank.errorconsumer.entity.CobolError;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CobolErrorRepository implements PanacheRepositoryBase<CobolError, String> {
}
