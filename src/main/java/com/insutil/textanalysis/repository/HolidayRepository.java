package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.Holiday;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface HolidayRepository extends R2dbcRepository<Holiday, Long> {
	@Query(
		"select * " +
		"from t_qa_holiday  " +
		"where enabled is true " +
		"and :date between start_date and end_date"
	)
	Mono<Boolean> isHolyday(LocalDate date);
}
