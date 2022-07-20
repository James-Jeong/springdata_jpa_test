package study.datajpa.entity.member;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

@SpringBootTest
@Transactional
class MemberTest {

    @Autowired
    MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void testEntity() throws Exception {
        // 1) Given
        Team teamA = new Team("TeamA");
        entityManager.persist(teamA);
        Team teamB = new Team("TeamB");
        entityManager.persist(teamB);

        Member memberA  = new Member("MemberA", 10, teamA);
        Member memberB  = new Member("MemberB", 15, teamA);
        entityManager.persist(memberA);
        entityManager.persist(memberB);
        Member memberC  = new Member("MemberC", 20, teamB);
        Member memberD  = new Member("MemberD", 25, teamB);
        entityManager.persist(memberC);
        entityManager.persist(memberD);

        entityManager.flush();
        entityManager.clear();

        Member member1 = entityManager.find(Member.class, memberA.getId());
        System.out.println("member1 = " + member1);

        // 2) When
        List<Member> members = entityManager.createQuery(
                "select m from Member m",
                Member.class
        ).getResultList();

        // 3) Then
        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member.getTeam() = " + member.getTeam());
        }
    }

    @Test
    public void jpaEventBaseEntity() throws Exception {
        // 1) Given
        Member memberA  = new Member("MemberA", 10);
        memberRepository.save(memberA); // @PrePersist

        Thread.sleep(1000);
        memberA.changeInfo("MemberB", memberA.getAge());

        entityManager.flush();
        entityManager.clear();

        // 2) When
        Member findMember = memberRepository.findById(memberA.getId()).orElse(null);

        // 3) Then
        Assertions.assertNotNull(findMember);
        System.out.println("findMember.getCreateDateTime = " + findMember.getCreateDateTime());
        System.out.println("findMember.getCreatedBy = " + findMember.getCreatedBy());
        System.out.println("findMember.getUpdateDateTime = " + findMember.getLastModifiedDateTime());
        System.out.println("findMember.getLastModifiedBy = " + findMember.getLastModifiedBy());
    }

}
