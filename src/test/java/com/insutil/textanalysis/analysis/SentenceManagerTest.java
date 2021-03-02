package com.insutil.textanalysis.analysis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.InvalidParameterException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SentenceManagerTest {

    @Autowired
    SentenceManager sentenceManager;

    @Test
    public void patternMatchingTest() {
        String str = "A:[‡9‡]아 네 확인 먼저 해 드릴 건데요 차주 분 주민번호 가 어떻게 되십니까";
        String newStr = str.replaceFirst("^A:\\[‡\\d+‡\\]", "");
        System.out.println(newStr);
    }

    @Test
    public void extractAgentSentenceTest() {

        try {
            List<String> agentSentences = sentenceManager.extractAgentSentence("A:[‡1‡]감사 합니다 - - 입니다 무엇 을 도와 드릴까요\n" +
                    "Q:[‡4‡]아 예 여보세요 그 자동차 보험 을 하나 가입 을 할려고 하는데요\n" +
                    "A:[‡9‡]아 네 확인 먼저 해 드릴 건데요 차주 분 주민번호 가 어떻게 되십니까\n" +
                    "Q:[‡13‡]네 팔 십 일 년 팔 월 이 십 일 이요\n" +
                    "A:[‡16‡]뒷자리 어떻게 되세요\n" +
                    "Q:[‡18‡]예 공 일 공 변경 합니다\n" +
                    "A:[‡20‡]일 공 일 구 아 둘 둘 삼 이요 아 - 황성훈 인 되시는 건가요\n" +
                    "M:[‡26‡]예\n" +
                    "A:[‡27‡]- 한 번 확인 해 보겠습니다 음 예 저희 쪽 에는 지금 자동차 보험 가입 되는 건 없으신데 고객 님 명의 로 구입 되시는 건가요 - 좀 배상 은 - 명의 셨거든요 그럼\n" +
                    "M:[‡40‡]고객 님\n" +
                    "A:[‡41‡]- 그럼 산출 해 드릴 건데요 산출 이 한 동의 받고 안내 좀 바로 해 드릴 게요 예 계약\n" +
                    "Q:[‡46‡]없잖아 일당 은 데 이거 지금 그 보험 뭐 한 일 이 맞는 거죠\n" +
                    "A:[‡54‡]상담 과 실손 담보 중복 확인 을 위해서 결정 위해 정보 동의 녹음 필요한 거구요 동의 하더라도 추후 중지 요청 가능 하며 거부 시 정상적 인 상담 이 어려우세요 저희 고객 님 의 주민번호 차량 정보 교통 법규 위반 개인 정보 등 한국 신용 정보원 보험개발원 등 에 삼 개월 간 수집 활용 조회 하고 일 년 간 보유 해서 보험료 확인 드립니다 동의 하시죠 고객님 예 그럼 명의 는 고객 님 앞 으로 해서 저희 가 보험료 확인 해 드릴 거구요 예 지금 차량 중고차 는 번호 알고 계시죠 네 몇 번 이세요\n" +
                    "Q:[‡82‡]-\n" +
                    "A:[‡83‡]예 예 십 육 일 오 육 오 삼 삼 번 십 칠 년 식 의 카니발 맞으세요 고객 님 구입 은 오 육 한 이 백 프로 스텔스 오토 정도 끝 으로 확인 되시고 - 이십니다 고객님 개발원 이 확인 이 구요 예 그날 이고 블랙박스 라든가 앞 뒤 보조 범퍼 라든가 추가적 부속품 따로 장착 - 거 있으세요\n" +
                    "Q:[‡102‡]어 블랙박스 는\n" +
                    "A:[‡105‡]아 그럼 - 중고 시세 로 보는 거라서 예 탈부착 된 거를 차 를 받으 신 거죠 예 얼마 짜린지 혹시 들으신 게 있으세요\n" +
                    "Q:[‡114‡]아 그거 는 뭐 -\n" +
                    "A:[‡115‡]앞 뒤 로 다 있으세요\n" +
                    "Q:[‡118‡]그게 다 있는 거\n" +
                    "A:[‡119‡]아 그럼 보통 삼 십 만 원 선 에서 감가 하시는 조건 으로 해 드리면 이 십 이 만 원 선 정도 나오던데 - 정도 를 받은 예 운전 은 고객님 이 어느 분 이 운전 하세요\n" +
                    "Q:[‡130‡]전화 갈 거예요\n" +
                    "A:[‡131‡]고객님 많으세요 예 그럼 저번 에 고객 님 저희 배상 을 시 앞 으로 하셨을 때 고객님 자차 까지 다 넣었던 조건 으로 해서 예 키로수 는 후할인 으로 하셨던 내용 이거든요 조건 은 똑같이 한 번 봐 드릴까요 고객님 예 그럼 대물 은 고객님 일 억 정도 로 해 놓으셨던 는데 그냥 똑같이 확인 드림 될까요\n" +
                    "Q:[‡147‡]-\n" +
                    "A:[‡148‡]예 그럼 잠시 만요 그 혹시 만 칠 세 가 십 일 세 이하 자녀 는 없으신가요 고객님 아 있으세요 아 그럼 혹시 주민번호 아세요 자녀 분 이요\n" +
                    "Q:[‡158‡]어 만약 을 차량 인데\n" +
                    "M:[‡161‡]안내 분 이\n" +
                    "Q:[‡164‡]어 그 어 음 칠 년 아 이 천 십 팔 년\n" +
                    "A:[‡178‡]뒷자리 어떻게 되세요\n" +
                    "Q:[‡181‡]어 삼 공\n" +
                    "M:[‡184‡]네\n" +
                    "A:[‡214‡]삼 공 칠 공 칠 일 삼 아드님 이름 이 어떻게 되세요 - 주부 어린 이요\n" +
                    "Q:[‡220‡]네\n" +
                    "A:[‡221‡]아 그럼 제가 사모님 은 등록 어린이 에 예 법정 대리인 - 주민번호 - 에 동의 하시는 거죠 고객 님\n" +
                    "Q:[‡226‡]네\n" +
                    "A:[‡227‡]- 저희 가 알 이 - 아이 할인 까지 다 적용 하고 있구요 고객 님 잠시 만 확인 해 볼게요 예 운전 혼자 하시는 걸로 해서 키즈 특약 이제 넣어드리고 있습니다 그 옛날 에 가입 경력률 이나 이런 거 다 큰 - 있거든요 잠시만 기다려 음 보험 가입 은 좀 올해 고객 님 예전 부터 하셨긴 했는데\n" +
                    "M:[‡256‡]안 하신지 때 돼\n" +
                    "A:[‡256‡]- 그래서 - 삼 년 이 지나시면 백 프로 부터 시작 을 해요 고객 님 그 다시 한 번 확인 을 해 드리고 있구요\n" +
                    "Q:[‡264‡]네 네\n" +
                    "A:[‡265‡]- 주소 할인 이 주민번호 확인 되셨구요 아 - 원래 보험료 가 그래도 유리 하실 발행 되시는데 칠 십 오 만 팔 천 오 백 구 십 원 에서요 블랙박스 사진 은 들어오면은 - 추가 네 기준 으로 삼 만 칠 천 이 백 이 십 원 할인 되니까 칠 십 이 만 천 삼 백 칠 십 원 에서요 고객님 키로수 는 적게 타면 이천 부터 만 오천 구간 까지도 환급 을 받으실 수가 있으시거든요 을 해 드리면 되실까요 그럼 결제 는 예전 에 보니까 카드 결제 신한 카드 로 이용 을 하셨던 근데 요거 는 어떤 카드 로 등록 을 해 드릴까요\n" +
                    "M:[‡298‡]예\n" +
                    "A:[‡298‡]하시는 카드 번호 저장 하지 않아서 번호 랑 유효 기간 좀 불러 세요 고객님\n" +
                    "Q:[‡314‡]육 이 팔 아 공 공 공\n" +
                    "A:[‡317‡]네 사 공 공 공\n" +
                    "Q:[‡319‡]이 넘을 - - 육 칠 팔 칠\n" +
                    "A:[‡329‡]예 그럼 할부 도 육 개월 이 무이자 로 되시는데 일시불 아까 해보다가요\n" +
                    "Q:[‡333‡]육 십 오 일 날\n" +
                    "A:[‡334‡]이 십 라고 키로수 적게 타면 돌려받는 거라 계좌 번호 고객님 - - 등록 을 해 드리거든요 유지 는 받아 불러주세요\n" +
                    "Q:[‡349‡]삼 일 오\n" +
                    "A:[‡352‡]삼 팔 뭐 이게 은행 이 어디 거예요 신한 은행 예 그리고 비상 연락 망 으로 가족 분 연락처 있으시면 하나 불러주시겠어요\n" +
                    "Q:[‡360‡]공 일 공\n" +
                    "M:[‡361‡]예\n" +
                    "Q:[‡362‡]구 삼 육 공\n" +
                    "M:[‡363‡]삼 육 구 요 아 아 구좌 만 육 공 예 하나 는 칠 번 이 천 십 구 년\n" +
                    "A:[‡372‡]고객 님 어 이 월 십 구 일 관리 고 - 녹음 처리 해 드릴 거고 들으시고 틀린 부분 있으면 말씀 해 주시구요\n" +
                    "M:[‡377‡]계약 사항 을\n" +
                    "A:[‡378‡]직접 보실 수 있도록 몇 개\n" +
                    "M:[‡380‡]그 보내드릴 거니까 예 회신 하게\n" +
                    "A:[‡382‡]시면 되시는 거고 동의 - 고객 님\n" +
                    "M:[‡384‡]두 개 -\n" +
                    "A:[‡384‡]- 대부분 은 팔 십 일 년 고객님 팔 월 이 십 이 일 에 이게 일 공 일 구 둘 둘 삼 번 에 저희 황성훈 님 이 되실 겁니다\n" +
                    "M:[‡391‡]증권 약관\n" +
                    "A:[‡392‡]바뀌신 제도 만기 - 요런 거는 문자 모바일 이메일 쪽 으로 바로 보실 수 있도록 등록 을 해 드릴 거구요 - 고객님 이메일 등록 되어 있으신 게 잠깐 만요 다음 에 조금 이미 그래서\n" +
                    "M:[‡402‡]합니다\n" +
                    "A:[‡404‡]고객님 이메일 이 등록 되어 있으신 게 지금 에이치 에스 에이치 공 공 칠 공 공 골뱅이 한메일 되어 있으십니다 이용 하시는 거죠\n" +
                    "M:[‡411‡]네\n" +
                    "A:[‡412‡]정비 쿠폰북 스피드 메이트 이용 건 으로 손실 - 받을 수 있도록 문자 넣어 드릴 거니까 가입 후 에 보내드리는 고객 - 메시지 확인 해 주시면 되구요\n" +
                    "M:[‡419‡]아 주소지 는 서울 관악 구 십 년 도 사 십 팔 막 이 십 구 이 백 일 호 예림 대략적 으로 되어 있으십니다 동의 하시는 거죠 이거\n" +
                    "Q:[‡426‡]이사 갔는데 저\n" +
                    "M:[‡427‡]아 그래요\n" +
                    "A:[‡428‡]주소지 바꿔드릴\n" +
                    "Q:[‡432‡]많았고 칠년 동 구 십 일 다시 삼 십 일 일 사 층 사 층 예 -\n" +
                    "A:[‡444‡]예 그냥 이렇게 되나\n" +
                    "M:[‡445‡]해야 돼요\n" +
                    "A:[‡447‡]- 사십팔 가게 십 팔 이렇게 나오거든요\n" +
                    "M:[‡450‡]그 때 는 저희 가\n" +
                    "A:[‡451‡]이쪽 으로 등록\n" +
                    "M:[‡453‡]잠시 만요 변경 이\n" +
                    "A:[‡454‡]그리고\n" +
                    "M:[‡454‡]입니다 잠시 기다리세요\n" +
                    "A:[‡469‡]고객님 아까 말씀 드린 것처럼 제가 주소지 는 변경 해 드렸구요 고객님\n" +
                    "M:[‡474‡]고객 님 계약\n" +
                    "A:[‡475‡]쪽 을 조회 상 보실 수 있도록 지금 카톡 으로 문자 넣어 드릴 거거든요 해약 해 주시면 되시는 거라 발송 한 번 해 볼 게요 끊지 말고 한 번 받으 시겠어요\n" +
                    "Q:[‡483‡]네\n" +
                    "M:[‡497‡]그 누르셔가지고 쪽 읽어보시고 밑 에 제일 끝 에는 동의 모두 하셔서 대출 하게 십니까\n" +
                    "A:[‡503‡]되십니다\n" +
                    "M:[‡510‡]아까 기다릴게요 고객 님 예 고객 님 내용 계약 내용 확인 하셨다는 - 잘 들어 왔고 해당 내용 요약 된 내용 이구요 기타 자세한 사항 은 최대 로 보내 드리는 문자 메시지 통해서 언제 - 확인 가능 하고 중요 사항 만 빠르게 좀 읽어 드릴 게요 고객님 차량 은 이 천 십 칠 년 사 월 사 일 최초 등록 되신 이 천 십 칠 년 식 으로 올뉴 카니발 이 점 이 뒤 에 구 인승 으로 프레스티지 오토\n" +
                    "A:[‡532‡]그 에어컨 과 파워 스티어링 에이비에스 에어백\n" +
                    "M:[‡533‡]그쪽 에 돈 을\n" +
                    "A:[‡534‡]- 다 있는 거구요\n" +
                    "M:[‡535‡]특별히 앞 으로 부족\n" +
                    "A:[‡536‡]한 - - 추가 로 부속품 더 다섯 배\n" +
                    "M:[‡538‡]- 블랙박스 이 십 이 만 원 가입 으로 해서\n" +
                    "A:[‡540‡]아 이 천 사 백 구 십 육 만 원 한도 내 에서 보험 처리 가 되시는 거구요 예 자차 보상 시 고객님 부담 하시는 자기 부담금 은 실제 발생 금액 이 십 프로 로 최저 이 십 에서 최고 오 십 만 원 까지시고 최저 이 십 만 원 기본 부담 이 되시는 거구요 보험사 에 알리지 않은 추가 부속품 과 일부 부분품 의 도난 으로 인한 손해 보장 보험 처리 가 되지가 않습니다 긴급 출동 만 삼 천 칠 백 팔 십 원 포함 되어 있구요 예 무보험 차 상해 담보 는 여러 건 을 가입 을 해도 각 보험사 에서 금액 을 나누어 비례 보상 하므로 중복 으로 지급 되지 않습니다만 보장 은 또는 확대 가 돼서 넉넉 하게 보장 받을 수가 있구요 또한 한 대 - 차량 을 가입 하는 경우 계약 을 해지 또는 변경 으로 해서 보장 받지 못하는 경우 도 있어서 추가 로 가입 을 해 드렸습니다 동의 하시는 거죠 고객 님 그리고 저희 가 차량 키로수 는 후할인 으루 해서 적용 해 드릴 거라 약정 주행 거리 만 오 천 키로 이시고 기간 이 종료 되면 실제 운행한 주행 거리 확인 후 납입한 보험료 일부 를 약정률 에 따라 알려주신 계좌 로 환급 을 해 드릴 거구요 차량 번호판 과 최종 주행 거리 정보 는 보험 종료일 로부터 전후 일 개월 이내 에 등록 을 하시면 되는데 보내주시는 사진 은 최초 및 최종 주행 거리 정보 로 처리 가 되는 거구요 보험 기간 중 에 타고 바뀌는 경우 에는 칠 일 안 에 대체 전후 차량 의 주행 거리 송부 해 주시면 되는 거고 기간 내 사진 등록 되지 않거나 연간 환산 주행 거리 약정 주행 거리 를 초과 하는 경우 에는 환급 받는 보험 료 따로 없다 라고 보시면 되시는 겁니다 저희 고객 님은 가입 - 휴대폰 에 카톡 문자 로 번호판 이랑 계기판 이랑 블랙박스 달려 있는 내부 자리 있잖아요 그 사진 세 장 만 첨부 해 주시면은 원래 금액 칠 십 오 만 팔 천 오 백 구 십 원 에서 블랙박스 오늘 들어오면 삼 만 칠 천 이 백 이 십 원 이 바로 차감 이 돼서요 칠 십 이 만 천 삼 백 칠 십 원 으로 정리 가 될 거거든요 잊지 마시고 바쁘시더라도 꼭 부탁 드리구요 고객님 예 본 동의 는 선택 사항 으로 거절 가능 하시고 동의 하더라도 추후 중지 요청 가능 하십니다 당사 상품 안내 에서 삼 년 간 정보 수집 활용 해 전화 나 문자 이메일 우편 으로 연락 드립니다 동의 하시구요 예 감사 드리고 주민번호 같은 고유 식별 정보 및 질병 상해 정보 말씀 드린 대로 처리 해 드립니다 동의 하시구요 마지막 계약 상담 과 실손 담보 중복 확인 및 인수여부 결정 에 정보 동의 녹음 필요 하구요 동의 에도 추후 중지 요청 가능 하며 거부 시 정상적 인 상담 어려우신데 주민번호 질병 상해 정보 를 수집 활용 하고 가입 하신 보험 계약 및 보험금 지급 정보 를 한국 신용 정보원 보험개발원 등 에 삼 개월 간 조회 하여 일 년 간 보유 해서 확인 드립니다 동의 하시는 거죠 예 주민번호 같은 고유 식별 정보 및 질병 상해 정보 말씀 드린 대로 말씀 으로 처리 해 드리는데 동의 하시는 거구요 예 지금 까지 말씀 을 상태 혹시 이해 하시기 어려운 부분 이나 뭐 궁금한 사항 있으신 건가요 고객 님\n" +
                    "Q:[‡675‡]예\n" +
                    "A:[‡676‡]음 네\n" +
                    "Q:[‡678‡]보험 가입 증명서 는 음 메일 로 보내주나요\n" +
                    "A:[‡682‡]증권 약관 은 지금 메일 쪽 으로 바로 문자 함께 넣어드릴 건데 오늘 이 전화 로 가실 거예요\n" +
                    "Q:[‡688‡]어 목요일 날\n" +
                    "A:[‡690‡]예 그럼\n" +
                    "M:[‡691‡]더 가능\n" +
                    "A:[‡691‡]하구요 - 가셔서 예 고객님 보험 가입 했다 라고 주민번호 주시면 전산 으로 바로 뜨게 끔 처리 해 드릴 거거든요\n" +
                    "Q:[‡706‡]그 가입 영수증 뭐 그 팩스 로 한 번 보내 달라고 하거든요\n" +
                    "A:[‡710‡]그럼 팩스 번호 주시면 제가 그냥 - 게요 고객님\n" +
                    "Q:[‡713‡]아 그\n" +
                    "A:[‡715‡]네\n" +
                    "Q:[‡715‡]예 공 오 공 이 -\n" +
                    "A:[‡718‡]공 오 공\n" +
                    "M:[‡719‡]예 동 구 공\n" +
                    "A:[‡724‡]칠 공 이 - 구 에요\n" +
                    "Q:[‡726‡]칠 공 이요\n" +
                    "A:[‡727‡]칠\n" +
                    "Q:[‡728‡]그니까\n" +
                    "A:[‡729‡]- 공 사 이 공 육 공 에 예 칠 공 오 둘 맞으세요\n" +
                    "Q:[‡733‡]네\n" +
                    "A:[‡734‡]예 알겠습니다 그럼 제가 가입 증명서 그쪽 으로 바로 팩스 넣어 놓을 거구요 고객님 께도 문자 바로 넣어 드릴 게요 고객님 네 감사 합니다 들 안전 전화 세요 였습니다 네");

            agentSentences.forEach(System.out::println);
        } catch (InvalidParameterException e) {

        }
    }

    @Test
    public void extractAgentSentenceAndExtractNounsTest() {

        try {
            List<String> agentSentences = sentenceManager.extractAgentSentence("A:[‡1‡]감사 합니다 - - 입니다 무엇 을 도와 드릴까요\n" +
                    "Q:[‡4‡]아 예 여보세요 그 자동차 보험 을 하나 가입 을 할려고 하는데요\n" +
                    "A:[‡9‡]아 네 확인 먼저 해 드릴 건데요 차주 분 주민번호 가 어떻게 되십니까\n" +
                    "Q:[‡13‡]네 팔 십 일 년 팔 월 이 십 일 이요\n" +
                    "A:[‡16‡]뒷자리 어떻게 되세요\n" +
                    "Q:[‡18‡]예 공 일 공 변경 합니다\n" +
                    "A:[‡20‡]일 공 일 구 아 둘 둘 삼 이요 아 - 황성훈 인 되시는 건가요\n" +
                    "M:[‡26‡]예\n" +
                    "A:[‡27‡]- 한 번 확인 해 보겠습니다 음 예 저희 쪽 에는 지금 자동차 보험 가입 되는 건 없으신데 고객 님 명의 로 구입 되시는 건가요 - 좀 배상 은 - 명의 셨거든요 그럼\n" +
                    "M:[‡40‡]고객 님\n" +
                    "A:[‡41‡]- 그럼 산출 해 드릴 건데요 산출 이 한 동의 받고 안내 좀 바로 해 드릴 게요 예 계약\n" +
                    "Q:[‡46‡]없잖아 일당 은 데 이거 지금 그 보험 뭐 한 일 이 맞는 거죠\n" +
                    "A:[‡54‡]상담 과 실손 담보 중복 확인 을 위해서 결정 위해 정보 동의 녹음 필요한 거구요 동의 하더라도 추후 중지 요청 가능 하며 거부 시 정상적 인 상담 이 어려우세요 저희 고객 님 의 주민번호 차량 정보 교통 법규 위반 개인 정보 등 한국 신용 정보원 보험개발원 등 에 삼 개월 간 수집 활용 조회 하고 일 년 간 보유 해서 보험료 확인 드립니다 동의 하시죠 고객님 예 그럼 명의 는 고객 님 앞 으로 해서 저희 가 보험료 확인 해 드릴 거구요 예 지금 차량 중고차 는 번호 알고 계시죠 네 몇 번 이세요\n" +
                    "Q:[‡82‡]-\n" +
                    "A:[‡83‡]예 예 십 육 일 오 육 오 삼 삼 번 십 칠 년 식 의 카니발 맞으세요 고객 님 구입 은 오 육 한 이 백 프로 스텔스 오토 정도 끝 으로 확인 되시고 - 이십니다 고객님 개발원 이 확인 이 구요 예 그날 이고 블랙박스 라든가 앞 뒤 보조 범퍼 라든가 추가적 부속품 따로 장착 - 거 있으세요\n" +
                    "Q:[‡102‡]어 블랙박스 는\n" +
                    "A:[‡105‡]아 그럼 - 중고 시세 로 보는 거라서 예 탈부착 된 거를 차 를 받으 신 거죠 예 얼마 짜린지 혹시 들으신 게 있으세요\n" +
                    "Q:[‡114‡]아 그거 는 뭐 -\n" +
                    "A:[‡115‡]앞 뒤 로 다 있으세요\n" +
                    "Q:[‡118‡]그게 다 있는 거\n" +
                    "A:[‡119‡]아 그럼 보통 삼 십 만 원 선 에서 감가 하시는 조건 으로 해 드리면 이 십 이 만 원 선 정도 나오던데 - 정도 를 받은 예 운전 은 고객님 이 어느 분 이 운전 하세요\n" +
                    "Q:[‡130‡]전화 갈 거예요\n" +
                    "A:[‡131‡]고객님 많으세요 예 그럼 저번 에 고객 님 저희 배상 을 시 앞 으로 하셨을 때 고객님 자차 까지 다 넣었던 조건 으로 해서 예 키로수 는 후할인 으로 하셨던 내용 이거든요 조건 은 똑같이 한 번 봐 드릴까요 고객님 예 그럼 대물 은 고객님 일 억 정도 로 해 놓으셨던 는데 그냥 똑같이 확인 드림 될까요\n" +
                    "Q:[‡147‡]-\n" +
                    "A:[‡148‡]예 그럼 잠시 만요 그 혹시 만 칠 세 가 십 일 세 이하 자녀 는 없으신가요 고객님 아 있으세요 아 그럼 혹시 주민번호 아세요 자녀 분 이요\n" +
                    "Q:[‡158‡]어 만약 을 차량 인데\n" +
                    "M:[‡161‡]안내 분 이\n" +
                    "Q:[‡164‡]어 그 어 음 칠 년 아 이 천 십 팔 년\n" +
                    "A:[‡178‡]뒷자리 어떻게 되세요\n" +
                    "Q:[‡181‡]어 삼 공\n" +
                    "M:[‡184‡]네\n" +
                    "A:[‡214‡]삼 공 칠 공 칠 일 삼 아드님 이름 이 어떻게 되세요 - 주부 어린 이요\n" +
                    "Q:[‡220‡]네\n" +
                    "A:[‡221‡]아 그럼 제가 사모님 은 등록 어린이 에 예 법정 대리인 - 주민번호 - 에 동의 하시는 거죠 고객 님\n" +
                    "Q:[‡226‡]네\n" +
                    "A:[‡227‡]- 저희 가 알 이 - 아이 할인 까지 다 적용 하고 있구요 고객 님 잠시 만 확인 해 볼게요 예 운전 혼자 하시는 걸로 해서 키즈 특약 이제 넣어드리고 있습니다 그 옛날 에 가입 경력률 이나 이런 거 다 큰 - 있거든요 잠시만 기다려 음 보험 가입 은 좀 올해 고객 님 예전 부터 하셨긴 했는데\n" +
                    "M:[‡256‡]안 하신지 때 돼\n" +
                    "A:[‡256‡]- 그래서 - 삼 년 이 지나시면 백 프로 부터 시작 을 해요 고객 님 그 다시 한 번 확인 을 해 드리고 있구요\n" +
                    "Q:[‡264‡]네 네\n" +
                    "A:[‡265‡]- 주소 할인 이 주민번호 확인 되셨구요 아 - 원래 보험료 가 그래도 유리 하실 발행 되시는데 칠 십 오 만 팔 천 오 백 구 십 원 에서요 블랙박스 사진 은 들어오면은 - 추가 네 기준 으로 삼 만 칠 천 이 백 이 십 원 할인 되니까 칠 십 이 만 천 삼 백 칠 십 원 에서요 고객님 키로수 는 적게 타면 이천 부터 만 오천 구간 까지도 환급 을 받으실 수가 있으시거든요 을 해 드리면 되실까요 그럼 결제 는 예전 에 보니까 카드 결제 신한 카드 로 이용 을 하셨던 근데 요거 는 어떤 카드 로 등록 을 해 드릴까요\n" +
                    "M:[‡298‡]예\n" +
                    "A:[‡298‡]하시는 카드 번호 저장 하지 않아서 번호 랑 유효 기간 좀 불러 세요 고객님\n" +
                    "Q:[‡314‡]육 이 팔 아 공 공 공\n" +
                    "A:[‡317‡]네 사 공 공 공\n" +
                    "Q:[‡319‡]이 넘을 - - 육 칠 팔 칠\n" +
                    "A:[‡329‡]예 그럼 할부 도 육 개월 이 무이자 로 되시는데 일시불 아까 해보다가요\n" +
                    "Q:[‡333‡]육 십 오 일 날\n" +
                    "A:[‡334‡]이 십 라고 키로수 적게 타면 돌려받는 거라 계좌 번호 고객님 - - 등록 을 해 드리거든요 유지 는 받아 불러주세요\n" +
                    "Q:[‡349‡]삼 일 오\n" +
                    "A:[‡352‡]삼 팔 뭐 이게 은행 이 어디 거예요 신한 은행 예 그리고 비상 연락 망 으로 가족 분 연락처 있으시면 하나 불러주시겠어요\n" +
                    "Q:[‡360‡]공 일 공\n" +
                    "M:[‡361‡]예\n" +
                    "Q:[‡362‡]구 삼 육 공\n" +
                    "M:[‡363‡]삼 육 구 요 아 아 구좌 만 육 공 예 하나 는 칠 번 이 천 십 구 년\n" +
                    "A:[‡372‡]고객 님 어 이 월 십 구 일 관리 고 - 녹음 처리 해 드릴 거고 들으시고 틀린 부분 있으면 말씀 해 주시구요\n" +
                    "M:[‡377‡]계약 사항 을\n" +
                    "A:[‡378‡]직접 보실 수 있도록 몇 개\n" +
                    "M:[‡380‡]그 보내드릴 거니까 예 회신 하게\n" +
                    "A:[‡382‡]시면 되시는 거고 동의 - 고객 님\n" +
                    "M:[‡384‡]두 개 -\n" +
                    "A:[‡384‡]- 대부분 은 팔 십 일 년 고객님 팔 월 이 십 이 일 에 이게 일 공 일 구 둘 둘 삼 번 에 저희 황성훈 님 이 되실 겁니다\n" +
                    "M:[‡391‡]증권 약관\n" +
                    "A:[‡392‡]바뀌신 제도 만기 - 요런 거는 문자 모바일 이메일 쪽 으로 바로 보실 수 있도록 등록 을 해 드릴 거구요 - 고객님 이메일 등록 되어 있으신 게 잠깐 만요 다음 에 조금 이미 그래서\n" +
                    "M:[‡402‡]합니다\n" +
                    "A:[‡404‡]고객님 이메일 이 등록 되어 있으신 게 지금 에이치 에스 에이치 공 공 칠 공 공 골뱅이 한메일 되어 있으십니다 이용 하시는 거죠\n" +
                    "M:[‡411‡]네\n" +
                    "A:[‡412‡]정비 쿠폰북 스피드 메이트 이용 건 으로 손실 - 받을 수 있도록 문자 넣어 드릴 거니까 가입 후 에 보내드리는 고객 - 메시지 확인 해 주시면 되구요\n" +
                    "M:[‡419‡]아 주소지 는 서울 관악 구 십 년 도 사 십 팔 막 이 십 구 이 백 일 호 예림 대략적 으로 되어 있으십니다 동의 하시는 거죠 이거\n" +
                    "Q:[‡426‡]이사 갔는데 저\n" +
                    "M:[‡427‡]아 그래요\n" +
                    "A:[‡428‡]주소지 바꿔드릴\n" +
                    "Q:[‡432‡]많았고 칠년 동 구 십 일 다시 삼 십 일 일 사 층 사 층 예 -\n" +
                    "A:[‡444‡]예 그냥 이렇게 되나\n" +
                    "M:[‡445‡]해야 돼요\n" +
                    "A:[‡447‡]- 사십팔 가게 십 팔 이렇게 나오거든요\n" +
                    "M:[‡450‡]그 때 는 저희 가\n" +
                    "A:[‡451‡]이쪽 으로 등록\n" +
                    "M:[‡453‡]잠시 만요 변경 이\n" +
                    "A:[‡454‡]그리고\n" +
                    "M:[‡454‡]입니다 잠시 기다리세요\n" +
                    "A:[‡469‡]고객님 아까 말씀 드린 것처럼 제가 주소지 는 변경 해 드렸구요 고객님\n" +
                    "M:[‡474‡]고객 님 계약\n" +
                    "A:[‡475‡]쪽 을 조회 상 보실 수 있도록 지금 카톡 으로 문자 넣어 드릴 거거든요 해약 해 주시면 되시는 거라 발송 한 번 해 볼 게요 끊지 말고 한 번 받으 시겠어요\n" +
                    "Q:[‡483‡]네\n" +
                    "M:[‡497‡]그 누르셔가지고 쪽 읽어보시고 밑 에 제일 끝 에는 동의 모두 하셔서 대출 하게 십니까\n" +
                    "A:[‡503‡]되십니다\n" +
                    "M:[‡510‡]아까 기다릴게요 고객 님 예 고객 님 내용 계약 내용 확인 하셨다는 - 잘 들어 왔고 해당 내용 요약 된 내용 이구요 기타 자세한 사항 은 최대 로 보내 드리는 문자 메시지 통해서 언제 - 확인 가능 하고 중요 사항 만 빠르게 좀 읽어 드릴 게요 고객님 차량 은 이 천 십 칠 년 사 월 사 일 최초 등록 되신 이 천 십 칠 년 식 으로 올뉴 카니발 이 점 이 뒤 에 구 인승 으로 프레스티지 오토\n" +
                    "A:[‡532‡]그 에어컨 과 파워 스티어링 에이비에스 에어백\n" +
                    "M:[‡533‡]그쪽 에 돈 을\n" +
                    "A:[‡534‡]- 다 있는 거구요\n" +
                    "M:[‡535‡]특별히 앞 으로 부족\n" +
                    "A:[‡536‡]한 - - 추가 로 부속품 더 다섯 배\n" +
                    "M:[‡538‡]- 블랙박스 이 십 이 만 원 가입 으로 해서\n" +
                    "A:[‡540‡]아 이 천 사 백 구 십 육 만 원 한도 내 에서 보험 처리 가 되시는 거구요 예 자차 보상 시 고객님 부담 하시는 자기 부담금 은 실제 발생 금액 이 십 프로 로 최저 이 십 에서 최고 오 십 만 원 까지시고 최저 이 십 만 원 기본 부담 이 되시는 거구요 보험사 에 알리지 않은 추가 부속품 과 일부 부분품 의 도난 으로 인한 손해 보장 보험 처리 가 되지가 않습니다 긴급 출동 만 삼 천 칠 백 팔 십 원 포함 되어 있구요 예 무보험 차 상해 담보 는 여러 건 을 가입 을 해도 각 보험사 에서 금액 을 나누어 비례 보상 하므로 중복 으로 지급 되지 않습니다만 보장 은 또는 확대 가 돼서 넉넉 하게 보장 받을 수가 있구요 또한 한 대 - 차량 을 가입 하는 경우 계약 을 해지 또는 변경 으로 해서 보장 받지 못하는 경우 도 있어서 추가 로 가입 을 해 드렸습니다 동의 하시는 거죠 고객 님 그리고 저희 가 차량 키로수 는 후할인 으루 해서 적용 해 드릴 거라 약정 주행 거리 만 오 천 키로 이시고 기간 이 종료 되면 실제 운행한 주행 거리 확인 후 납입한 보험료 일부 를 약정률 에 따라 알려주신 계좌 로 환급 을 해 드릴 거구요 차량 번호판 과 최종 주행 거리 정보 는 보험 종료일 로부터 전후 일 개월 이내 에 등록 을 하시면 되는데 보내주시는 사진 은 최초 및 최종 주행 거리 정보 로 처리 가 되는 거구요 보험 기간 중 에 타고 바뀌는 경우 에는 칠 일 안 에 대체 전후 차량 의 주행 거리 송부 해 주시면 되는 거고 기간 내 사진 등록 되지 않거나 연간 환산 주행 거리 약정 주행 거리 를 초과 하는 경우 에는 환급 받는 보험 료 따로 없다 라고 보시면 되시는 겁니다 저희 고객 님은 가입 - 휴대폰 에 카톡 문자 로 번호판 이랑 계기판 이랑 블랙박스 달려 있는 내부 자리 있잖아요 그 사진 세 장 만 첨부 해 주시면은 원래 금액 칠 십 오 만 팔 천 오 백 구 십 원 에서 블랙박스 오늘 들어오면 삼 만 칠 천 이 백 이 십 원 이 바로 차감 이 돼서요 칠 십 이 만 천 삼 백 칠 십 원 으로 정리 가 될 거거든요 잊지 마시고 바쁘시더라도 꼭 부탁 드리구요 고객님 예 본 동의 는 선택 사항 으로 거절 가능 하시고 동의 하더라도 추후 중지 요청 가능 하십니다 당사 상품 안내 에서 삼 년 간 정보 수집 활용 해 전화 나 문자 이메일 우편 으로 연락 드립니다 동의 하시구요 예 감사 드리고 주민번호 같은 고유 식별 정보 및 질병 상해 정보 말씀 드린 대로 처리 해 드립니다 동의 하시구요 마지막 계약 상담 과 실손 담보 중복 확인 및 인수여부 결정 에 정보 동의 녹음 필요 하구요 동의 에도 추후 중지 요청 가능 하며 거부 시 정상적 인 상담 어려우신데 주민번호 질병 상해 정보 를 수집 활용 하고 가입 하신 보험 계약 및 보험금 지급 정보 를 한국 신용 정보원 보험개발원 등 에 삼 개월 간 조회 하여 일 년 간 보유 해서 확인 드립니다 동의 하시는 거죠 예 주민번호 같은 고유 식별 정보 및 질병 상해 정보 말씀 드린 대로 말씀 으로 처리 해 드리는데 동의 하시는 거구요 예 지금 까지 말씀 을 상태 혹시 이해 하시기 어려운 부분 이나 뭐 궁금한 사항 있으신 건가요 고객 님\n" +
                    "Q:[‡675‡]예\n" +
                    "A:[‡676‡]음 네\n" +
                    "Q:[‡678‡]보험 가입 증명서 는 음 메일 로 보내주나요\n" +
                    "A:[‡682‡]증권 약관 은 지금 메일 쪽 으로 바로 문자 함께 넣어드릴 건데 오늘 이 전화 로 가실 거예요\n" +
                    "Q:[‡688‡]어 목요일 날\n" +
                    "A:[‡690‡]예 그럼\n" +
                    "M:[‡691‡]더 가능\n" +
                    "A:[‡691‡]하구요 - 가셔서 예 고객님 보험 가입 했다 라고 주민번호 주시면 전산 으로 바로 뜨게 끔 처리 해 드릴 거거든요\n" +
                    "Q:[‡706‡]그 가입 영수증 뭐 그 팩스 로 한 번 보내 달라고 하거든요\n" +
                    "A:[‡710‡]그럼 팩스 번호 주시면 제가 그냥 - 게요 고객님\n" +
                    "Q:[‡713‡]아 그\n" +
                    "A:[‡715‡]네\n" +
                    "Q:[‡715‡]예 공 오 공 이 -\n" +
                    "A:[‡718‡]공 오 공\n" +
                    "M:[‡719‡]예 동 구 공\n" +
                    "A:[‡724‡]칠 공 이 - 구 에요\n" +
                    "Q:[‡726‡]칠 공 이요\n" +
                    "A:[‡727‡]칠\n" +
                    "Q:[‡728‡]그니까\n" +
                    "A:[‡729‡]- 공 사 이 공 육 공 에 예 칠 공 오 둘 맞으세요\n" +
                    "Q:[‡733‡]네\n" +
                    "A:[‡734‡]예 알겠습니다 그럼 제가 가입 증명서 그쪽 으로 바로 팩스 넣어 놓을 거구요 고객님 께도 문자 바로 넣어 드릴 게요 고객님 네 감사 합니다 들 안전 전화 세요 였습니다 네");
            agentSentences.stream()
                    .map(s -> sentenceManager.extractNoun(s))
                    .forEach(System.out::println);

        } catch (InvalidParameterException e) {

        }
    }
}