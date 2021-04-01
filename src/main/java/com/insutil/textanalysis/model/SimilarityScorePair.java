package com.insutil.textanalysis.model;

import lombok.Data;

@Data
public class SimilarityScorePair implements Comparable<SimilarityScorePair> {
    private Long id;
    private Float value;

    @Override
    public int compareTo(SimilarityScorePair o) {
        return Double.compare(o.getValue(), this.getValue());
    }

    /*@Override
    public int compareTo(SimilarityScorePair<T, T1> o) {
        return Double.compare((Double)this.value, (Double)o.value);
    }*/
}
