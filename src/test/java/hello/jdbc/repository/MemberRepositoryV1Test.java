package hello.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.domain.Member;
import hello.jdbc.service.MemberServiceV1;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class MemberRepositoryV1Test {
    private MemberRepositoryV1 memberRepository;

    @BeforeEach
    void beforeEach() throws Exception {
        // 기본 DriverManager -  항상 새로운 커넥션 획득
        // DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        // 커넥션 풀링: HikariPoxyConnection -> JdbcConnection
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);

        memberRepository = new MemberRepositoryV1(dataSource);
    }

    @Test
    void crud() throws SQLException {
        Member member = new Member("memberV4", 10000);
        memberRepository.save(member);

        // findById
        Member findMember = memberRepository.findById(member.getMemberId());
        log.info("findMemberId={}", findMember);

        log.info("member == findMember {}", member == findMember);
        log.info("member equals findMember {}", member.equals(findMember));
        assertThat(findMember).isEqualTo(member);

        // update money: 10000 -> 20000
        memberRepository.update(member.getMemberId(), 20000);
        Member updateMember = memberRepository.findById(member.getMemberId());
        assertThat(updateMember.getMoney()).isEqualTo(20000);

        // delete
        memberRepository.delete(member.getMemberId());
        assertThatThrownBy(() -> memberRepository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);
    }
}