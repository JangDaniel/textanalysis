package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.Product;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends R2dbcRepository<Product, Long> {
	Mono<Product> findByModelCode(String modelCode);
}
