package dku.cloudcomputing.surveyserver.service.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dku.cloudcomputing.surveyserver.entity.member.Member;
import dku.cloudcomputing.surveyserver.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaMemberService {
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${message.topic.saveMember}", groupId = "${spring.kafka.consumer.group-id}")
    public void saveMember(@Payload String memberJson) {
        try {
            log.debug(memberJson);
            Member member = objectMapper.readValue(memberJson, Member.class);
            memberRepository.save(member);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
