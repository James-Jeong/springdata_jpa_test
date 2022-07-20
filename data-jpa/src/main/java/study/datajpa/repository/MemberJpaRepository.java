package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.member.Member;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberJpaRepository {

    private final EntityManager entityManager;

    public Member save(Member member) {
        entityManager.persist(member);
        return member;
    }

    public void delete(Member member) {
        entityManager.remove(member);
    }

    public Member findById(Long id) {
        return entityManager.find(Member.class, id);
    }

    public List<Member> findAll() {
        return entityManager.createQuery(
                "select m from Member m",
                Member.class
        ).getResultList();
    }

    public long count() {
        return entityManager.createQuery(
                "select count(m) from Member m",
                Long.class
        ).getSingleResult();
    }

    public Member findByUserName(String userName) {
        return entityManager.createQuery(
                "select m from Member m where m.userName = :username",
                        Member.class
                )
                .setParameter("username", userName)
                .getSingleResult();
    }

    public List<Member> findByUsernameAndAgeGreaterThan(String userName, int age) {
        return entityManager.createQuery(
                "select m from Member m where m.userName = :username and m.age > :age",
                Member.class
                )
                .setParameter("username", userName)
                .setParameter("age", age)
                .getResultList();
    }

    public List<Member> findByPage(int age, int offset, int limit) {
        return entityManager.createQuery(
                "select m from Member m where m.age = :age order by m.userName desc",
                Member.class
                )
                .setParameter("age", age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public long totalCount(int age) {
        return entityManager.createQuery(
                "select count(m) from Member m where m.age = :age",
                Long.class
                )
                .setParameter("age", age)
                .getSingleResult();
    }

    public int bulkAgePlus(int age) {
        return entityManager.createQuery(
                "update Member m set m.age = m.age + 1" +
                        "where m.age >= :age"
                )
                .setParameter("age", age)
                .executeUpdate();
    }

}
