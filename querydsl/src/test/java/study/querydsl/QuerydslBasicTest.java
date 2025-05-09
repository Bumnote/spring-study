package study.querydsl;

import static com.querydsl.jpa.JPAExpressions.select;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDto;
import study.querydsl.dto.QMemberDto;
import study.querydsl.dto.UserDto;
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
    Member member2 = new Member("member2", 20, teamA);
    Member member3 = new Member("member3", 30, teamB);
    Member member4 = new Member("member4", 40, teamB);
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

    // when
    Member findMember = queryFactory
        .select(member)
        .from(member)
        .where(member.username.eq("member1"))
        .fetchOne();

    // then
    assertThat(findMember.getUsername()).isEqualTo("member1");
  }

  @Test
  void search() {
    // given
    Member findMember = queryFactory
        .selectFrom(member)
        .where(member.username.eq("member1")
            .and(member.age.eq(10)))
        .fetchOne();

    // when & then
    assertThat(findMember.getUsername()).isEqualTo("member1");
  }

  @Test
  void searchAndParam() {
    // given
    Member findMember = queryFactory
        .selectFrom(member)
        .where(
            member.username.eq("member1"),
            member.age.eq(10))
        .fetchOne();

    // when & then
    assertThat(findMember.getUsername()).isEqualTo("member1");
  }

  @Test
  void resultFetch() {
    List<Member> fetch = queryFactory
        .selectFrom(member)
        .fetch();

    assertThatThrownBy(() -> queryFactory
        .selectFrom(member)
        .fetchOne())
        .isInstanceOf(NonUniqueResultException.class)
        .hasMessageContaining("Query did not return a unique result");

    Member fetchFirst = queryFactory
        .selectFrom(member)
        .fetchFirst();

    QueryResults<Member> results = queryFactory
        .selectFrom(member)
        .fetchResults();

    results.getTotal();
    List<Member> content = results.getResults();

    long total = queryFactory
        .selectFrom(member)
        .fetchCount();
  }

  /*
   * 회원 정렬 순서
   * 1. 회원 나이 내림차순(desc)
   * 2. 회원 이름 오름차순(asc)
   * 단, 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
   * */
  @Test
  void sort() {

    em.persist(new Member(null, 100));
    em.persist(new Member("member5", 100));
    em.persist(new Member("member6", 100));

    List<Member> result = queryFactory
        .selectFrom(member)
        .where(member.age.eq(100))
        .orderBy(member.age.desc(), member.username.asc().nullsLast())
        .fetch();

    Member member5 = result.get(0);
    Member member6 = result.get(1);
    Member memberNull = result.get(2);

    assertThat(member5.getUsername()).isEqualTo("member5");
    assertThat(member6.getUsername()).isEqualTo("member6");
    assertThat(memberNull.getUsername()).isNull();
  }

  @Test
  void paging1() {
    List<Member> result = queryFactory
        .selectFrom(member)
        .orderBy(member.username.desc())
        .offset(1)
        .limit(2)
        .fetch();

    assertThat(result.size()).isEqualTo(2);
  }

  @Test
  void paging2() {
    QueryResults<Member> queryResults = queryFactory
        .selectFrom(member)
        .orderBy(member.username.desc())
        .offset(1)
        .limit(2)
        .fetchResults();

    assertThat(queryResults.getTotal()).isEqualTo(4);
    assertThat(queryResults.getLimit()).isEqualTo(2);
    assertThat(queryResults.getOffset()).isEqualTo(1);
    assertThat(queryResults.getResults().size()).isEqualTo(2);
  }

  @Test
  void aggregation() {
    List<Tuple> result = queryFactory
        .select(
            member.count(),
            member.age.sum(),
            member.age.avg(),
            member.age.max(),
            member.age.min()
        )
        .from(member)
        .fetch();

    Tuple tuple = result.get(0);
    assertThat(tuple.get(member.count())).isEqualTo(4);
    assertThat(tuple.get(member.age.sum())).isEqualTo(100);
    assertThat(tuple.get(member.age.avg())).isEqualTo(25);
    assertThat(tuple.get(member.age.max())).isEqualTo(40);
    assertThat(tuple.get(member.age.min())).isEqualTo(10);
  }

  /*
   * 팀 이름과 각 팀의 평균 연령을 구해라.
   * */
  @Test
  void group() {
    List<Tuple> result = queryFactory
        .select(team.name, member.age.avg())
        .from(member)
        .join(member.team, team)
        .groupBy(team.name)
        .fetch();

    Tuple teamA = result.get(0);
    Tuple teamB = result.get(1);

    assertThat(teamA.get(team.name)).isEqualTo("teamA");
    assertThat(teamA.get(member.age.avg())).isEqualTo(15);

    assertThat(teamB.get(team.name)).isEqualTo("teamB");
    assertThat(teamB.get(member.age.avg())).isEqualTo(35);
  }

  /*
   * 팀 A에 소속된 모든 회원
   * */
  @Test
  void join() {
    List<Member> result = queryFactory
        .selectFrom(member)
        .join(member.team, team)
        .where(team.name.eq("teamA"))
        .fetch();

    assertThat(result)
        .extracting("username")
        .containsExactly("member1", "member2");
  }

  /**
   * 세타 조인 회원의 이름이 팀 이름과 같은 회원 조회
   */
  @Test
  void thetaJoin() {
    em.persist(new Member("teamA"));
    em.persist(new Member("teamB"));
    em.persist(new Member("teamC"));

    List<Member> result = queryFactory
        .select(member)
        .from(member, team)
        .where(member.username.eq(team.name))
        .fetch();

    assertThat(result)
        .extracting("username")
        .containsExactly("teamA", "teamB");
  }

  /**
   * 예) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회 JPQL: select m, t from Member m left join m.team
   * t on t.name = 'teamA'
   */
  @Test
  void joinOnFiltering() {

    List<Tuple> result = queryFactory
        .select(member, team)
        .from(member)
        .leftJoin(member.team, team).on(team.name.eq("teamA"))
        .fetch();

    for (Tuple tuple : result) {
      System.out.println("tuple = " + tuple);
    }
  }

  /**
   * 연관관계 없는 엔티티 외부 조인 회원의 이름이 팀 이름과 같은 대상 외부 조인
   */
  @Test
  void joinOnNoRelation() {
    em.persist(new Member("teamA"));
    em.persist(new Member("teamB"));
    em.persist(new Member("teamC"));

    List<Tuple> result = queryFactory
        .select(member, team)
        .from(member)
        .leftJoin(team).on(member.username.eq(team.name))
        .fetch();

    for (Tuple tuple : result) {
      System.out.println("tuple = " + tuple);
    }
  }

  @PersistenceUnit
  EntityManagerFactory emf;

  @Test
  void fetchJoin() {
    em.flush();
    em.clear();

    Member findMember = queryFactory
        .selectFrom(member)
        .where(member.username.eq("member1"))
        .fetchOne();

    boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
    assertThat(loaded).as("페치 조인 미적용").isFalse();
  }

  @Test
  void fetchJoinUse() {
    em.flush();
    em.clear();

    Member findMember = queryFactory
        .selectFrom(member)
        .join(member.team, team).fetchJoin()
        .where(member.username.eq("member1"))
        .fetchOne();

    boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
    assertThat(loaded).as("페치 조인 적용").isTrue();
  }

  /**
   * 나이가 가장 많은 회원 조희
   */
  @Test
  void subQuery() {
    QMember memberSub = new QMember("memberSub");

    List<Member> result = queryFactory
        .selectFrom(member)
        .where(member.age.eq(
            select(memberSub.age.max())
                .from(memberSub)
        ))
        .fetch();

    assertThat(result).extracting("age")
        .containsExactly(40);
  }

  /**
   * 나이가 평균 이상인 회원 조희
   */
  @Test
  void subQueryGoe() {
    QMember memberSub = new QMember("memberSub");

    List<Member> result = queryFactory
        .selectFrom(member)
        .where(member.age.goe(
            select(memberSub.age.avg())
                .from(memberSub)
        ))
        .fetch();

    assertThat(result).extracting("age")
        .containsExactly(30, 40);
  }

  @Test
  void subQueryIn() {
    QMember memberSub = new QMember("memberSub");

    List<Member> result = queryFactory
        .selectFrom(member)
        .where(member.age.in(
            select(memberSub.age)
                .from(memberSub)
                .where(memberSub.age.gt(10))
        ))
        .fetch();

    assertThat(result).extracting("age")
        .containsExactly(20, 30, 40);
  }

  /**
   * 나이가 평균 이상인 회원 조희
   */
  @Test
  void selectSubQuery() {

    QMember memberSub = new QMember("memberSub");

    List<Tuple> result = queryFactory
        .select(member.username,
            select(memberSub.age.avg())
                .from(memberSub))
        .from(member)
        .fetch();

    for (Tuple tuple : result) {
      System.out.println("tuple = " + tuple);
    }
  }

  @Test
  void basicCase() {
    List<String> result = queryFactory
        .select(member.age
            .when(10).then("열살")
            .when(20).then("스무살")
            .otherwise("기타"))
        .from(member)
        .fetch();

    for (String s : result) {
      System.out.println("s = " + s);
    }
  }

  @Test
  void complexCase() {
    List<String> result = queryFactory
        .select(new CaseBuilder()
            .when(member.age.between(0, 20)).then("0~20살")
            .when(member.age.between(21, 30)).then("21~30살")
            .otherwise("기타"))
        .from(member)
        .fetch();

    for (String s : result) {
      System.out.println("s = " + s);
    }
  }

  @Test
  void constant() {
    List<Tuple> result = queryFactory
        .select(member.username, Expressions.constant("A"))
        .from(member)
        .fetch();

    for (Tuple tuple : result) {
      System.out.println("tuple = " + tuple);
    }
  }

  @Test
  void concat() {
    List<String> result = queryFactory
        .select(member.username.concat("_").concat(member.age.stringValue()))
        .from(member)
        .where(member.username.eq("member1"))
        .fetch();

    for (String s : result) {
      System.out.println("s = " + s);
    }
  }

  @Test
  void simpleProjection() {
    List<String> result = queryFactory
        .select(member.username)
        .from(member)
        .fetch();

    for (String s : result) {
      System.out.println("s = " + s);
    }
  }

  @Test
  void tupleProjection() {
    List<Tuple> result = queryFactory
        .select(member.username, member.age)
        .from(member)
        .fetch();

    for (Tuple tuple : result) {
      String username = tuple.get(member.username);
      Integer age = tuple.get(member.age);
      System.out.println("username = " + username);
      System.out.println("age = " + age);
    }
  }

  // DTO 반환 방법 -> JPQL 활용
  @Test
  void findDtoByJPQL() {
    List<MemberDto> result = em.createQuery(
        "select new study.querydsl.dto.MemberDto(m.username, m.age) from Member m",
        MemberDto.class).getResultList();

    for (MemberDto m : result) {
      System.out.println("memberDto = " + m);
    }
  }

  @Test
  void findDtoBySetter() {
    List<MemberDto> result = queryFactory
        .select(Projections.bean(MemberDto.class, member.username, member.age))
        .from(member)
        .fetch();

    for (MemberDto m : result) {
      System.out.println("memberDto = " + m);
    }
  }

  @Test
  void findDtoByField() {
    List<MemberDto> result = queryFactory
        .select(Projections.fields(MemberDto.class, member.username, member.age))
        .from(member)
        .fetch();

    for (MemberDto m : result) {
      System.out.println("memberDto = " + m);
    }
  }

  @Test
  void findDtoByConstructor() {
    List<MemberDto> result = queryFactory
        .select(Projections.constructor(MemberDto.class, member.username, member.age))
        .from(member)
        .fetch();

    for (MemberDto m : result) {
      System.out.println("memberDto = " + m);
    }
  }

  @Test
  void findUserDto() {
    QMember memberSub = new QMember("memberSub");

    List<UserDto> result = queryFactory
        .select(Projections.fields(UserDto.class,
            member.username.as("name"),
            ExpressionUtils.as(JPAExpressions
                .select(memberSub.age.max())
                .from(memberSub), "age")
        ))
        .from(member)
        .fetch();

    for (UserDto m : result) {
      System.out.println("userDto = " + m);
    }
  }

  @Test
  void findDtoByQueryProjection() {
    List<MemberDto> result = queryFactory
        .select(new QMemberDto(member.username, member.age))
        .from(member)
        .fetch();

    for (MemberDto m : result) {
      System.out.println("memberDto = " + m);
    }
  }

  @Test
  void dynamicQueryBooleanBuilder() {
    String usernameParam = "member1";
    Integer ageParam = 10;

    List<Member> result = searchMember1(usernameParam, ageParam);
    assertThat(result.size()).isEqualTo(1);
  }

  @Test
  void dynamicQueryWhereParam() {
    String usernameParam = "member1";
    Integer ageParam = 10;

    List<Member> result = searchMember2(usernameParam, ageParam);
    assertThat(result.size()).isEqualTo(1);
  }

  // 벌크 연산 이후 -> 영속성 컨텍스트를 초기화해주는 것이 좋다. DB와 데이터 정합성 문제가 발생
  @Test
  void bulkUpdate() {
    long count = queryFactory
        .update(member)
        .set(member.username, "비회원")
        .where(member.age.lt(28))
        .execute();

    // 영속성 컨텍스트 초기화
    em.flush();
    em.clear();
    List<Member> result = queryFactory
        .selectFrom(member)
        .fetch();

    for (Member m : result) {
      System.out.println("member = " + m);
    }
  }

  @Test
  void bulkAdd() {
    long count = queryFactory
        .update(member)
        .set(member.age, member.age.multiply(2))
        .execute();
  }

  @Test
  void bulkDelete() {
    long count = queryFactory
        .delete(member)
        .where(member.age.gt(18))
        .execute();
  }

  @Test
  void sqlFunction() {
    List<String> result = queryFactory
        .select(Expressions.stringTemplate("function('replace', {0}, {1}, {2})"
            , member.username, "member", "M"))
        .from(member)
        .fetch();

    for (String s : result) {
      System.out.println("s = " + s);
    }
  }

  @Test
  void sqlFunction2() {
    List<String> result = queryFactory
        .select(member.username)
        .from(member)
//        .where(member.username.eq(
//            Expressions.stringTemplate("function('lower', {0})", member.username)
//        ))
        .where(member.username.eq(member.username.lower()))
        .fetch();

    for (String s : result) {
      System.out.println("s = " + s);
    }
  }


  private List<Member> searchMember1(String usernameParam, Integer ageParam) {

    BooleanBuilder builder = new BooleanBuilder();
    if (usernameParam != null) {
      builder.and(member.username.eq(usernameParam));
    }

    if (ageParam != null) {
      builder.and(member.age.eq(ageParam));
    }

    return queryFactory
        .selectFrom(member)
        .where(builder)
        .fetch();
  }

  private List<Member> searchMember2(String usernameParam, Integer ageParam) {
    return queryFactory
        .selectFrom(member)
        .where(usernameEq(usernameParam), ageEq(ageParam))
        .fetch();
  }

  private BooleanExpression usernameEq(String usernameParam) {
    return usernameParam != null ? member.username.eq(usernameParam) : null;
  }

  private BooleanExpression ageEq(Integer ageParam) {
    return ageParam != null ? member.age.eq(ageParam) : null;
  }

  private BooleanExpression allEq(String usernameParam, Integer ageParam) {
    return usernameEq(usernameParam).and(ageEq(ageParam));
  }
}
