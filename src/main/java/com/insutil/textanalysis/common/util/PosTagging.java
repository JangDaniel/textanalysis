package com.insutil.textanalysis.common.util;

import com.insutil.textanalysis.common.model.PosPair;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@PropertySource("classpath:analysis.properties")
@Component
public class PosTagging {
    @Value("${user.dic}")
    private String userDicFilePath;

    @Value("${interestedNounTags}")
    private String nounTags;

    private Komoran komoran;

    @PostConstruct
    public void init() throws IOException {
        komoran = new Komoran(DEFAULT_MODEL.FULL);
        ClassPathResource resource = new ClassPathResource(userDicFilePath);
        String path = resource.getFile().getAbsolutePath();
        komoran.setUserDic(path);

    }

    public List<PosPair> tagPos(String text) {
        KomoranResult result = komoran.analyze(text);
        return result.getList().stream()
                .map(pair -> new PosPair(pair.getFirst(), pair.getSecond()))
                .collect(Collectors.toList());
    }
}
