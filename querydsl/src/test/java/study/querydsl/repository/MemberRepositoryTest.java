package study.querydsl.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

@Transactional
@SpringBootTest
class MemberRepositoryTest {

  @Autowired
  EntityManager em;

  @Autowired
  MemberRepository memberRepository;

  @Test
  void basicTest() {
    Member member = new Member("member1", 10);
    memberRepository.save(member);

    Member findMember = memberRepository.findById(member.getId()).get();
    assertThat(findMember).isEqualTo(member);

    List<Member> result1 = memberRepository.findAll();
    assertThat(result1).containsExactly(member);

    List<Member> result2 = memberRepository.findByUsername("member1");
    assertThat(result2).containsExactly(member);
  }

  @Test
  void querydslPredicateExecutorTest() {
    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    em.persist(teamA);
    em.persist(teamB);

    Member member1 = new Member("member1", 10, teamA);
    Member member2 = new Member("member2", 20, teamA);
    Member member3 = new Member("member3", 30, teamB);
    Member member4 = new Member("member4", 40, teamB);
    em.persist(member1);
    em.persist(member2);
    em.persist(member3);
    em.persist(member4);

    QMember member = QMember.member;
    Iterable<Member> result = memberRepository.findAll(
        member.age.between(10, 40).and(member.username.eq("member1")));

    for (Member m : result) {
      System.out.println("member = " + m);
    }

  }


}