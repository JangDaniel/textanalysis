package com.insutil.textanalysis.service;

import com.insutil.textanalysis.analysis.SentenceManager;
import com.insutil.textanalysis.common.analysis.PosTagging;
import com.insutil.textanalysis.common.model.PosPair;
import com.insutil.textanalysis.model.*;
import com.insutil.textanalysis.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.security.InvalidParameterException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MorphemeAnalysis {
    private final SentenceManager sentenceManager;

    private final PosTagging posTagging;

    private final SttContentsRepository sttContentsRepository;

    private final CodeRepository codeRepository;

    private final SttSentencesRepository sttSentencesRepository;

    private final ProductRepository productRepository;

    private final ScriptCriteriaRepository scriptCriteriaRepository;

    private final ScriptDetailRepository scriptDetailRepository;

    private final ScriptDetailMainWordRepository scriptDetailMainWordRepository;

    private final ContractObjectRepository contractObjectRepository;

    private final ScriptMatchRepository scriptMatchRepository;

    private long codeWithBeforeMorphemeAnalysis;

    private final String objectPatternString = "\\$\\{[A-Z]\\d{5}\\}";
    private final Pattern objectPattern = Pattern.compile(objectPatternString);

    private final DecimalFormat scoreForm = new DecimalFormat("#.##");


    @PostConstruct
    public void init() {
        codeRepository.findEnabledCodeByCodeId("BM")
                .doOnNext(t -> {
                    codeWithBeforeMorphemeAnalysis = t.getId();
                }).subscribe();
    }

    public List<PosPair> extractSentenceAndAnalysisMorpheme(String sourceText) {
        return posTagging.tagPos(sourceText);
    }

    public Flux<STTContents> getMorphemeAnalysisTargetWithCallDate(final String callDate) {
        return sttContentsRepository
                .findSTTContentsByCallDateAndStateCode(callDate, codeWithBeforeMorphemeAnalysis);
    }

    private boolean checkInputParameter(final Object obj, final String str) throws InvalidParameterException {
        if (Objects.isNull(obj) || !StringUtils.hasLength(str)) {
            log.warn("It's not acceptable data...so, return!!");
            throw new InvalidParameterException();
        }
        return true;
    }

    // STT ????????? ???????????? ?????? ??? ????????? ??????(?????? ??????) ?????? T_TA_STT_SENTENCES??? ??????
    public List<SttSentences> extractSentenceAndAnalysisMorpheme(STTContents data) throws InvalidParameterException {
        checkInputParameter(data, data.getSttText());

        log.info("working ... {}", data.getId());

        // ????????? STT?????????
        // 1. ????????? ?????? ?????? ?????? \n
        // 2. ?????? ?????? ??????(>20)
        // 3. ???????????? ???????????? ????????? ?????? ??? ??????
        // ??? ????????? ???????????? ??????
        List<String> agentSentences = sentenceManager.extractAgentSentence(data.getSttText());

        // Map<'????????? ??????', '????????? ??????.....'
        Map<String, String> morphemeAnalysisResultList = getMorphemeAnalysisResultList(agentSentences);

        if(morphemeAnalysisResultList.size() < 1) {
            throw new InvalidParameterException();
        }
        // ?????? map??? ????????? T_TA_STT_SENTENCES ??? ?????? ??? ????????? ??????
        return extractedSttSentences(data, morphemeAnalysisResultList);
    }

    private List<SttSentences> extractedSttSentences(final STTContents data, final Map<String, String> morphemeAnalysisResultList) {
        List<SttSentences> sttSentencesList = new ArrayList<>();
        for (Map.Entry<String, String> elem : morphemeAnalysisResultList.entrySet()) {
            SttSentences sttSentences = new SttSentences();
            sttSentences.setSttId(data.getId());
            sttSentences.setContractNo(data.getContractNo());
            sttSentences.setCallDate(data.getCallDate());
            sttSentences.setUnitSentence(elem.getKey());
            sttSentences.setMorpheme(elem.getValue());
            sttSentencesList.add(sttSentences);
            log.info("{}, {}", elem.getKey(), elem.getValue());
        }
        return sttSentencesList;
    }

    private Map<String, String> getMorphemeAnalysisResultList(List<String> agentSentences) {
        Map<String, String> morphemeAnalysisResultList = agentSentences.stream()
                .collect(Collectors.toMap(s -> s,
                        s -> sentenceManager.extractNoun(s),
                        (existingKey, newKey) -> existingKey));
        return morphemeAnalysisResultList;
    }

    public Flux<SttSentences> saveSentenceData(final List<SttSentences> sttSentences) {
        return sttSentencesRepository.saveAll(sttSentences);
    }

    public int preparedAnalysisWithDate(final String callDate) {
        // t_ta_stt_sentences callDate ?????? ??????
        log.info("is going to delete result data in t_ta_stt_sentences with specific date = {}", callDate);
        return sttSentencesRepository.deleteSttSentencesByCallDate(callDate)
                .subscribeOn(Schedulers.parallel()).block();
    }

    public Flux<SttSentences> analysisMorphemeWithSpecificContractNo(final String contractNo) {
        return sttContentsRepository.findSTTContentsByContractNo(contractNo)
                .flatMap(data -> Flux.just(extractSentenceAndAnalysisMorpheme(data)))
                .flatMap(data -> saveSentenceData(data));
    }

    // 1. ?????? ??????, ?????? ??????????????? STT ????????? ??????
    // 2. ????????? ????????? ?????? ?????? ????????? just skip,,return
    public Flux<ScriptMatch> analysisSimilarity(final String callDate, final String contractNo) {
        log.info("is going to analysis similarity with {}, {}", contractNo, callDate);

        List<ScriptDetail> scriptDetails = getScriptDetails(contractNo);

        // ?????? ??????????????? ?????? ??????
        List<ContractObject> contractObjectList = extractContractObject(callDate, contractNo);

        // ?????? ???????????? ?????? ?????? : ?????? ?????? ?????? replace ex) $???????????? => 378200
        // ScriptDetail ??? postObjectProcessingScript ??? ?????? ????????? ???????????? ?????? ?????? ????????? ??????
        // ScriptDetail ??? ${M~}???????????? ContractObject??? object_name??? ???????????? ????????? ????????? object_value??? ??????
        objectReplaceWithCustomerSttAndAnalysisMorpheme(scriptDetails, contractObjectList);

        List<SttSentences> sttSentencesList = analysisSimilarity(callDate, contractNo, scriptDetails);

        List<ScriptMatch> scriptMatchList = new ArrayList<>();

        for (SttSentences s : sttSentencesList) {
            if(s.getSimilarityScorePairList().size() > 0) {
                Optional<SimilarityScorePair> similarityMaxScore
                        = s.getSimilarityScorePairList().stream().sorted().findFirst();
                similarityMaxScore.ifPresent(data -> {
                    saveTaSttScriptMatch(scriptMatchList, s.getId(), data.getId(), data.getValue(), scriptDetails);
                });

            }

            /*SimilarityScorePair sim = similarityScorePair.isPresent() ? similarityScorePair.get() : new SimilarityScorePair();
            System.out.println(s.getUnitSentence() + " " + sim + " " + getDetailScript(sim.getId(), scriptDetails));*/
        }
        return scriptMatchRepository.saveAll(scriptMatchList);
    }

    private void saveTaSttScriptMatch(List<ScriptMatch> scriptMatchList, long sttSentenceId, long scriptDetailId, float score, List<ScriptDetail> scriptDetails) {
        ScriptMatch scriptMatch = new ScriptMatch();
        try {
            ScriptDetail scriptDetail = scriptDetails.stream().filter(data -> data.getId() == scriptDetailId).findFirst().orElseThrow();
            scriptMatch.setScriptDetailId(scriptDetailId);
            if(StringUtils.hasText(scriptDetail.getObjectReplacedScript()))
                scriptMatch.setMappedScript(scriptDetail.getObjectReplacedScript());
            else
                scriptMatch.setMappedScript(scriptDetail.getScript());
            scriptMatch.setSttSentenceId(sttSentenceId);
            scriptMatch.setSimilarityScore(score);
            log.info("{}", scriptMatch);
            scriptMatchList.add(scriptMatch);
        } catch(Exception e) {

        }

    }

    private List<ScriptDetail> getScriptDetails(String contractNo) {
        // 1. ??????????????? ?????? ????????? ????????? ?????????.
        // 2. ????????? ????????? ?????? ID??? ?????????.
        // 3. ?????? ???????????? ?????? ?????? ???????????? ?????? ????????? ?????????.
        // 4. ???????????? ?????? ???????????? ???????????? ????????? ????????? ?????????.
        // 4-1.  ????????? ??????????????? ????????? ?????? ?????? ????????? ??????
        // ??????. ?????? ???????????? ????????? ?????? ??????
        return sttContentsRepository.findSimilarityCode(contractNo)        // 1.
                .flatMap(productRepository::findByModelCode)  // 2.
                .flatMapMany(d -> scriptCriteriaRepository.findAllByEnabledIsTrueAndProductId(d.getId()))
                .collectList()
                .flatMapMany(l -> {
                    List<Long> ids = l.stream().map(data -> data.getId())
                            .collect(Collectors.toList());
                    return scriptDetailRepository.findAllByCriterionId(ids);
                }) // 4.
                .map(data -> data.withMainWordsFlux(scriptDetailMainWordRepository.findAllByScriptDetailIdAndEnabledIsTrue(data.getId()))) // 4-1
                .collectList()
                .block();
    }

    private String getDetailScript(long id, List<ScriptDetail> scriptDetails) {
        Optional<ScriptDetail> scriptDetail = scriptDetails.stream().filter(d -> d.getCriterionId() == id).findFirst();
        if (scriptDetail.isPresent())
            if (StringUtils.hasText(scriptDetail.get().getObjectReplacedScript()))
                return scriptDetail.get().getObjectReplacedScript();
            else
                return scriptDetail.get().getScript();
        return "";
    }

    private List<SttSentences> analysisSimilarity(String callDate, String contractNo, List<ScriptDetail> scriptDetails) {

        return sttSentencesRepository
                .findSttSentencesByCallDateAndContractNo(callDate, contractNo).collectList().block()
                .stream()
                .parallel()
                .peek(stt -> stt.setSimilarityScorePairList(checkWordSimilarity(stt, scriptDetails)))
                .collect(Collectors.toList());

/*
        sttSentencesList.stream().parallel()
                .forEach(stt -> stt.setSimilarityScorePairList(checkWordSimilarity(stt, scriptDetails)));

        return sttSentencesList;*/
    }

    public List<SimilarityScorePair> checkWordSimilarity(SttSentences sentences, List<ScriptDetail> scriptDetails) {

        // ?????? ????????? ?????? ??? STT ??????????????? ???????????? ???????????? ?????????
        // ??????, ??????, ?????????, ??????, ?????????.....
        List<String> sttNouns = Arrays.asList(sentences.getMorpheme().split(","));

        List<SimilarityScorePair> similarityScorePairs = new ArrayList<>();

        // ?????? ????????? ?????? ???????????? ??????..
        for (ScriptDetail scriptDetail : scriptDetails) {
            List<String> scriptNouns = Arrays.asList(scriptDetail.getMorpheme().split(","));
            int count = 0;

            // ??? ?????? ??????????????? ?????? ?????? ??????....
            List<ScriptDetailMainWord> scriptDetailMainWordList =
                    scriptDetail.getMainWordsFlux().collectList().block();

            for(String word : sttNouns) {
                if(scriptNouns.contains(word))
                    count++;
            }
            // STT ????????? ????????? ?????? ???????????? ?????? ??????????????? ????????? ???????????? count ??????...
            /*for (String word : scriptNouns) {
                if (sttNouns.contains(word))
                    count++;
            }
            */

            SimilarityScorePair similarityScorePair = new SimilarityScorePair();
            similarityScorePair.setId(scriptDetail.getCriterionId());
            similarityScorePair.setValue(getScoreWithMainWord(count, sttNouns, scriptDetailMainWordList));
            if(similarityScorePair.getValue() >= 0.5)
                similarityScorePairs.add(similarityScorePair);
        }
        return similarityScorePairs;
    }

    private float getScoreWithMainWord(int count, List<String> sttNouns, List<ScriptDetailMainWord> scriptDetailMainWordList) {
        float value = Float.valueOf(scoreForm.format(count / Float.valueOf(sttNouns.size())));
        for(ScriptDetailMainWord mainWord : scriptDetailMainWordList) {
            if(sttNouns.contains(mainWord.getWord())) {
                value += mainWord.getWeight() / 100;
            }
        }
        return value;
    }
    private void objectReplaceWithCustomerSttAndAnalysisMorpheme(List<ScriptDetail> scriptDetailList, List<ContractObject> contractObjectList) {
        scriptDetailList.stream()
                .filter(data -> !StringUtils.hasText(data.getMorpheme()) && StringUtils.hasText(data.getScript()))
                .peek(data -> {
                    String script = data.getScript();
                    Matcher matcher = objectPattern.matcher(script);
                    while (matcher.find()) {
                        String targetObjectCode = matcher.group().substring(2, 8);
                        String replaceString = getObjectValue(contractObjectList, targetObjectCode);
                        String matchableString = "[$]\\{" + targetObjectCode + "\\}";
                        script = script.replaceAll(matchableString, replaceString);
                    }
                    data.setObjectReplacedScript(script);
                }).forEach(data -> {
            data.setMorpheme(sentenceManager.extractNoun(data.getObjectReplacedScript()));
        });
    }

    private String getObjectValue(List<ContractObject> contractObjectList, String targetObjectCode) {

        Optional<ContractObject> contractObjectOptional =
                contractObjectList.stream().filter(data -> targetObjectCode.equals(data.getObjectCode())).findFirst();

        return contractObjectOptional.isPresent() ? contractObjectOptional.get().getObjectValue() : "";
    }

    public List<ContractObject> extractContractObject(final String callDate, final String contractNo) {
        log.info("is going to extract contract object with {}, {}", contractNo, callDate);

        return sttContentsRepository.findSTTContentsByContractNo(contractNo)
                .take(1)
                .flatMap(data -> contractObjectRepository.findContractObjectBySttContentsId(data.getId()))
                .collectList()
                .block();
    }

}
