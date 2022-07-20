package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.member.Member;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * MemberRepository + Impl
 * 이름 규칙을 맞춰야 Spring data 에서 맞춰서 호출해준다.
 * [JpaRepository 상속받은 인터페이스 이름] + Impl
 *
 * 1. 커맨드와 쿼리 분리
 * 2. 비즈니스 로직이 핵심인 것과 핵심이 아닌 것을 분리
 * 3. 라이프 사이클에 따른 기능과 역할에 따라 코드 분리
 */
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberCustomRepository {

    private final EntityManager entityManager;

    @Override
    public List<Member> findMemberCustom() {
        return entityManager.createQuery(
                "select m from Member m",
                Member.class
        ).getResultList();
    }

}
