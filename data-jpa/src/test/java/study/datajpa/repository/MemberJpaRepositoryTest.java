package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.member.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    public void testSaveMember() throws Exception {
        // 1) Given
        Member member = new Member("MemberA", 20, null);

        // 2) When
        Member savedMember = memberJpaRepository.save(member);

        // 3) Then
        Member foundMember = memberJpaRepository.findById(member.getId());

        assertNotNull(foundMember);
        assertEquals(member, foundMember);
        Assertions.assertThat(member.getId()).isEqualTo(foundMember.getId());
        Assertions.assertThat(member.getUserName()).isEqualTo(foundMember.getUserName());
    }

    @Test
    public void basicCRUD() throws Exception {
        Member member1 = new Member("member1", 10, null);
        Member member2 = new Member("member2", 20, null);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // 단건 조획 검증
        Member findMember1 = memberJpaRepository.findById(member1.getId());
        Member findMember2 = memberJpaRepository.findById(member2.getId());
        assertEquals(member1, findMember1);
        assertEquals(member2, findMember2);

        // 리스트 조회 검증
        List<Member> members = memberJpaRepository.findAll();
        assertEquals(members.size(), 2);

        // 카운트 검증
        long count = memberJpaRepository.count();
        assertEquals(count, 2);

        // 업데이트 검증 (변경 감지, dirty checking)
        //findMember1.changeInfo("changed_member1", 1000);
        //findMember2.changeInfo("changed_member2", 2000);

        // 삭제 검증
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);
        count = memberJpaRepository.count();
        assertEquals(count, 0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() throws Exception {
        // 1) Given
        Member member1 = new Member("BBB", 100, null);
        Member member2 = new Member("BBB", 200, null);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // 2) When
        List<Member> members = memberJpaRepository.findByUsernameAndAgeGreaterThan(member1.getUserName(), 150);

        // 3) Then
        assertEquals("BBB", members.get(0).getUserName());
        assertEquals(200, members.get(0).getAge());
        assertEquals(1, members.size());
    }

    @Test
    public void pagingTest() throws Exception {
        // 1) Given
        memberJpaRepository.save(new Member("m1", 10, null));
        memberJpaRepository.save(new Member("m2", 10, null));
        memberJpaRepository.save(new Member("m3", 10, null));
        memberJpaRepository.save(new Member("m4", 10, null));
        memberJpaRepository.save(new Member("m5", 10, null));

        // 2) When
        int age = 10;
        int offset = 0;
        int limit = 3;
        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        // 3) Then
        assertEquals(3, members.size());
        assertEquals(5, totalCount);
    }

    @Test
    public void testBulkUpdateAge() throws Exception {
        // 1) Given
        memberJpaRepository.save(new Member("m1", 10, null));
        memberJpaRepository.save(new Member("m2", 19, null));
        memberJpaRepository.save(new Member("m3", 20, null));
        memberJpaRepository.save(new Member("m4", 21, null));
        memberJpaRepository.save(new Member("m5", 40, null));

        // 2) When
        int resultCount = memberJpaRepository.bulkAgePlus(20);
        entityManager.flush();
        entityManager.clear();

        Member member5 = memberJpaRepository.findByUserName("m5");
        /**
         * 41 살이 아니다!! 아직 영속성 컨텍스트에는 반영되지 않았다!
         * 그래서 벌크 연산 후에 반드시 영속성 컨텍스트를 플러쉬해줘야 한다.
         */
        System.out.println("##### member5 = " + member5);

        // 3) Then
        assertEquals(3, resultCount);
    }

}