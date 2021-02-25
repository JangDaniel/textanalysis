package com.insutil.textanalysis.service;

import com.insutil.textanalysis.common.model.PosPair;
import com.insutil.textanalysis.common.analysis.PosTagging;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MorphemeAnalysis {
    private final PosTagging posTagging;

    public List<PosPair> analysisMorpheme(String sourceText) {
        return posTagging.tagPos(sourceText);
    }
}
