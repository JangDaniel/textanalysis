package com.insutil.textanalysis.common.model;

import lombok.Data;

@Data
public class PosPair {
    private String word;
    private String pos;

    public PosPair(String word, String pos) {
        this.word = word;
        this.pos = pos;
    }
}
