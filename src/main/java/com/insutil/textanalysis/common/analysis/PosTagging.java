package com.insutil.textanalysis.common.analysis;

import com.insutil.textanalysis.common.model.PosPair;
import com.insutil.textanalysis.common.util.GetMessageComponent;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@PropertySource(value = {"classpath:analysis.properties"}, encoding = "UTF-8")
@RequiredArgsConstructor
@Component
public class PosTagging {
    @Value("${user.dic}")
    private String userDicFilePath;

    @Value("${interestedNounTags}")
    private String nounTags;

    @Value("${postPosTag}")
    private List<String> posTags;

    private final GetMessageComponent getMessageComponent;

    private Komoran komoran;

    @PostConstruct
    public void init() {
        komoran = new Komoran(DEFAULT_MODEL.FULL);
        try {
            komoran.setUserDic((new ClassPathResource(userDicFilePath)).getFile().getAbsolutePath());
        } catch(IOException e) {
            log.error(getMessageComponent.getMessage("analysis.fail.user.dic", userDicFilePath));
        }
    }

    public List<PosPair> tagPos(String text) {
        KomoranResult result = komoran.analyze(text);
        return result.getList().stream()
                .filter(pair -> !posTags.contains(pair.getFirst()))
                .map(pair -> new PosPair(pair.getFirst(), pair.getSecond()))
                .collect(Collectors.toList());
    }

    public List<String> extractNounTag(String text) {
        KomoranResult result = komoran.analyze(text);
        return result.getNouns().stream().filter(s -> !posTags.contains(s)).collect(Collectors.toList());
    }
}
