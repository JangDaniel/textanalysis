package com.insutil.textanalysis.model.dto;

import lombok.*;
import org.springframework.data.r2dbc.repository.Query;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationRateData {
    private String userId;
    private String userName;
    private int allocationCount;
    private int evaluationCount;
    private String processingRate;
}
