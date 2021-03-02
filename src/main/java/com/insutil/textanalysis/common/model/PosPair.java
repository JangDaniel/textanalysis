package com.insutil.textanalysis.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PosPair {
    private String word;
    private String pos;
}
