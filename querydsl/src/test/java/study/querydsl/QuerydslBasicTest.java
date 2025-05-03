package study.querydsl;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

@Transactional
@SpringBootTest
class QuerydslBasicTest {

  @Autowired
  EntityManager em;

  JPAQueryFactory queryFactory;


  @BeforeEach
  void before() {
    queryFactory = new JPAQueryFactory(em);

    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    em.persist(teamA);
    em.persist(teamB);

    Member member1 = new Member("member1", 10, teamA);
    Member member2 = new Member("member2", 20, teamB);
    Member member3 = new Member("member3", 30, teamB);
    Member member4 = new Member("member4", 40, teamA);
    em.persist(member1);
    em.persist(member2);
    em.persist(member3);
    em.persist(member4);
  }

  @Test
  void startJPQL() {
    // given
    String qlString = "select m from Member m "
                      + "where m.username = :username";

    Member findByJPQL = em.createQuery(qlString, Member.class)
        .setParameter("username", "member1")
        .getSingleResult();

    // when & then
    assertThat(findByJPQL.getUsername()).isEqualTo("member1");
  }

  @Test
  void startQuerydsl() {

    // given
    QMember m = new QMember("m");

    // when
    Member findMember = queryFactory
        .select(m)
        .from(m)
        .where(m.username.eq("member1"))
        .fetchOne();

    // then
    assertThat(findMember.getUsername()).isEqualTo("member1");
  }
}
