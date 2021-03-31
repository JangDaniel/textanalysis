package com.insutil.textanalysis.repository;

import com.insutil.textanalysis.model.Holiday;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface HolidayRepository extends R2dbcRepository<Holiday, Long> {
}
