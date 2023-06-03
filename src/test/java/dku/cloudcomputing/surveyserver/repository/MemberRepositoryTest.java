package dku.cloudcomputing.surveyserver.repository;

import dku.cloudcomputing.surveyserver.entity.member.Member;
import dku.cloudcomputing.surveyserver.exception.member.NoSuchMemberException;
import dku.cloudcomputing.surveyserver.repository.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase
class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName(value = "존재하는 회원을 이메일로 검색 성공")
    void findByEmailSuccessTest() {
        Member member = new Member(1L, "test@test", "test", "tester");
        memberRepository.save(member);

        Member findMember = memberRepository.findByEmail("test@test")
                .orElseThrow(NoSuchMemberException::new);

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getEmail()).isEqualTo(member.getEmail());
        assertThat(findMember.getNickname()).isEqualTo(member.getNickname());
        assertThat(findMember.getPassword()).isEqualTo(member.getPassword());
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 이메일 검색시 에러")
    void findByEmailFailTest() {
        Optional<Member> findMember = memberRepository.findByEmail("test@test");
        assertThatThrownBy(() -> findMember.orElseThrow(NoSuchMemberException::new))
                .isInstanceOf(NoSuchMemberException.class);
    }
}