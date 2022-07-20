package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.member.Member;
import study.datajpa.entity.member.Team;
import study.datajpa.entity.member.dto.MemberDto;
import study.datajpa.entity.member.projections.MemberProjection;
import study.datajpa.entity.member.projections.MemberUserNameOnly;
import study.datajpa.entity.member.projections.NestedClosedProjections;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    public void testMember() throws Exception {
        // 1) Given
        Member member = new Member("MemberA", 20, null);

        // 2) When
        Member savedMember = memberRepository.save(member);

        // 3) Then
        Member foundMember = memberRepository.findById(member.getId()).orElse(null);

        assertNotNull(foundMember);
        assertEquals(member, foundMember);
        Assertions.assertThat(member.getId()).isEqualTo(foundMember.getId());
        Assertions.assertThat(member.getUserName()).isEqualTo(foundMember.getUserName());

        List<Member> members = memberRepository.findAll(
                Sort.by(
                        Sort.Direction.DESC, "id"
                )
        );
        assertEquals(1, members.size());
        assertEquals(1, memberRepository.count());
    }

    @Test
    public void basicCRUD() throws Exception {
        Member member1 = new Member("member1", 10, null);
        Member member2 = new Member("member2", 20, null);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조획 검증
        Member findMember1 = memberRepository.findById(member1.getId()).orElse(null);
        Member findMember2 = memberRepository.findById(member2.getId()).orElse(null);
        assertNotNull(findMember1);
        assertNotNull(findMember2);
        assertEquals(member1, findMember1);
        assertEquals(member2, findMember2);

        // 리스트 조회 검증
        List<Member> members = memberRepository.findAll();
        assertEquals(members.size(), 2);

        // 카운트 검증
        long count = memberRepository.count();
        assertEquals(count, 2);

        // 업데이트 검증 (변경 감지, dirty checking)
        //findMember1.changeInfo("changed_member1", 1000);
        //findMember2.changeInfo("changed_member2", 2000);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        count = memberRepository.count();
        assertEquals(count, 0);
    }

    @Test
    public void findByUserNameAndAgeGreaterThan() throws Exception {
        // 1) Given
        Member member1 = new Member("BBB", 100, null);
        Member member2 = new Member("BBB", 200, null);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 2) When
        List<Member> members = memberRepository.findByUserNameAndAgeGreaterThan(member1.getUserName(), 150);

        // 3) Then
        assertEquals("BBB", members.get(0).getUserName());
        assertEquals(200, members.get(0).getAge());
        assertEquals(1, members.size());
    }

    @Test
    public void findUserNameList() throws Exception {
        // 1) Given
        Member member1 = new Member("AAA", 100, null);
        Member member2 = new Member("BBB", 200, null);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 2) When
        List<String> userNameList = memberRepository.findUserNameList();

        // 3) Then
        for (String s : userNameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() throws Exception {
        // 1) Given
        Team teamA = new Team("TeamA");
        teamRepository.save(teamA);

        Member member1 = new Member("BBB", 100, teamA);
        memberRepository.save(member1);

        // 2) When
        List<MemberDto> memberDtos = memberRepository.findMemberDto();

        // 3) Then
        for (MemberDto memberDto : memberDtos) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void findByNames() throws Exception {
        // 1) Given
        Member member1 = new Member("AAA", 100, null);
        Member member2 = new Member("BBB", 200, null);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 2) When
        List<Member> members = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        // 3) Then
        for (Member member : members) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void pagingTest() throws Exception {
        // 1) Given
        memberRepository.save(new Member("m1", 10, null));
        memberRepository.save(new Member("m2", 10, null));
        memberRepository.save(new Member("m3", 10, null));
        memberRepository.save(new Member("m4", 10, null));
        memberRepository.save(new Member("m5", 10, null));

        // 2) When
        int age = 10;
        int offset = 0;
        int limit = 3;

        // Page index 는 1 이 아니라 0 부터 시작한다.
        PageRequest pageRequest = PageRequest.of(
                offset, limit,
                Sort.by(Sort.Direction.DESC, "userName")
        );
        // 엔티티는 절대로 외부에 노출하면 안된다.
        // 엔티티 변경으로 인해 API 연동도 바뀌게 되므로 장애가 발생할 수 있다.
        // DTO 로 변환해야한다.
        Page<Member> memberPage = memberRepository.findByAge(age, pageRequest);
        Page<MemberDto> memberDtoPage = memberPage.map(
                member -> new MemberDto(
                        member.getId(), member.getUserName(), member.getTeamName()
                )
        );

        System.out.println("@@@@@@@@@@");
        for (Member member : memberRepository.findTop3ByAge(age)) {
            System.out.println("member = " + member);
        }
        System.out.println("@@@@@@@@@@");

        /**
         * select * from ( select member0_.member_id as member_id1_0_, member0_.age as age2_0_,
         * member0_.team_id as team_id4_0_, member0_.user_name as user_name3_0_
         * from member member0_ where member0_.age=10 order by member0_.user_name desc )
         * where rownum <= 4;
         *
         * > 난 분명히 3개까지 조회하도록 했는데 왜 4개 조회?
         * >> Slice 에서는 기본적으로 다음 페이지 존재 유무를 파악하기 위해 그 다음 엘리먼트를 하나 더 가져온다. (더보기 기능)
         * >> Page 에서는 이렇게 안하고 페이지 넘버로 구분한다.
         */
        //Slice<Member> memberPage = memberRepository.findByAge(age, pageRequest);

        // 3) Then
        List<Member> members = memberPage.getContent();
        for (Member member : members) {
            System.out.println("member = " + member);
        }

        assertEquals(3, memberPage.getSize());
        assertEquals(5, memberPage.getTotalElements());

        assertTrue(memberPage.isFirst());
        assertTrue(memberPage.hasNext());
        assertEquals(0, memberPage.getNumber());
        assertEquals(2, memberPage.getTotalPages());
    }

    @Test
    public void testBulkUpdateAge() throws Exception {
        // 1) Given
        memberRepository.save(new Member("m1", 10, null));
        memberRepository.save(new Member("m2", 19, null));
        memberRepository.save(new Member("m3", 20, null));
        memberRepository.save(new Member("m4", 21, null));
        memberRepository.save(new Member("m5", 40, null));

        // 2) When
        int resultCount = memberRepository.bulkAgePlus(20);
        //entityManager.flush();
        //entityManager.clear();

        Member member5 = memberRepository.findMemberByUserName("m5");
        /**
         * 41 살이 아니다!! 아직 영속성 컨텍스트에는 반영되지 않았다!
         * 그래서 벌크 연산 후에 반드시 영속성 컨텍스트를 플러쉬해줘야 한다.
         *
         * 벌크 연산만 하고 API 가 종료되면 문제가 없다.
         * 하지만 벌크 연산 후에 곧바로 관련 데이터를 조회하면 문제가 발생할 수 있다.
         * 그래서 영속성 컨텍스트에 반영되는지 꼭 확인이 필요하다!
         */
        System.out.println("##### member5 = " + member5);

        // 3) Then
        assertEquals(3, resultCount);
    }

    @Test
    public void findMemberLazy() throws Exception {
        // 1) Given
        // Member1 -> TeamA
        // Member2 -> TeamB
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("Member1", 10, teamA);
        Member member2 = new Member("Member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        entityManager.flush();
        entityManager.clear();

        // 2) When
        List<Member> members = memberRepository.findAll();
        //List<Member> members = memberRepository.findMemberFetchJoin();
        for (Member member : members) {
            System.out.println("member = " + member.getUserName());

            // LAZY 로딩이면 JPA 프록시 객체가 생성되는데, 페치 조인으로 가져오면 실제 클래스 객체가 생성된다.
            System.out.println("member.teamClass = " + member.getTeam().getClass());

            System.out.println("member.team = " + member.getTeam().getName());
        }

        // 3) Then

    }

    @Test
    public void queryHint() throws Exception {
        // 1) Given
        Member member = memberRepository.save(new Member("m1", 10, null));
        entityManager.flush();
        entityManager.clear();

        // 2) When
        // 수정하고 싶지 않다. 그냥 Read only 로 조회하고 싶다!
        //Member member1 = memberRepository.findById(member.getId()).orElse(null);
        //member1.changeInfo("member2", member.getAge());

        Member member1 = memberRepository.findReadOnlyByUserName(member.getUserName());
        // ReadOnly 로 가져왔기 때문에 스냅샷이 없어서 Dirty Checking 을 수행하지 않는다.
        member1.changeInfo("member2", member.getAge());

        entityManager.flush();

        // 3) Then

    }

    @Test
    public void callCustom() throws Exception {
        // 1) Given
        List<Member> members = memberRepository.findMemberCustom();

        // 2) When

        // 3) Then

    }

    @Test
    public void projectionTest() throws Exception {
        // 1) Given
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("Member1", 10, teamA);
        Member member2 = new Member("Member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        entityManager.flush();
        entityManager.clear();

        // 2) When
        List<MemberUserNameOnly> userNameOnlyList = memberRepository.findProjectionByUserName(member1.getUserName());
        List<NestedClosedProjections> nestedClosedProjectionsList = memberRepository.findNestedProjectionByUserName(member2.getUserName());

        // 3) Then
        for (MemberUserNameOnly memberUserNameOnly : userNameOnlyList) {
            System.out.println("memberUserNameOnly = " + memberUserNameOnly.getUserName());
        }

        for (NestedClosedProjections nestedClosedProjections : nestedClosedProjectionsList) {
            System.out.println("nestedClosedProjections.getUserName = " + nestedClosedProjections.getUserName());
            System.out.println("nestedClosedProjections.getTeam = " + nestedClosedProjections.getTeam());
            System.out.println("nestedClosedProjections.getTeam.getName = " + nestedClosedProjections.getTeam().getName());
        }
    }

    @Test
    public void nativeQuery() throws Exception {
        // 1) Given
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("Member1", 10, teamA);
        Member member2 = new Member("Member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        entityManager.flush();
        entityManager.clear();

        // 2) When
        Member byNativeQuery = memberRepository.findByNativeQuery(member1.getUserName());
        Page<MemberProjection> byNativeProjection = memberRepository.findByNativeProjection(PageRequest.of(1, 10));

        // 3) Then
        assertNotNull(byNativeQuery);
        System.out.println("byNativeQuery = " + byNativeQuery);

        assertNotNull(byNativeProjection);
        for (MemberProjection memberProjection : byNativeProjection) {
            System.out.println("memberProjection = " + memberProjection.getUserName());
            System.out.println("memberProjection = " + memberProjection.getTeamName());
        }
    }

}
