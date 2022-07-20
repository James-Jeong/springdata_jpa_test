package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.member.Team;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TeamJpaRepository {

    private final EntityManager entityManager;

    public Team save(Team team) {
        entityManager.persist(team);
        return team;
    }

    public void delete(Team team) {
        entityManager.remove(team);
    }

    public Team findById(Long id) {
        return entityManager.find(Team.class, id);
    }

    public List<Team> findAll() {
        return entityManager.createQuery(
                "select t from Team t",
                Team.class
        ).getResultList();
    }

    public long count() {
        return entityManager.createQuery(
                "select count(t) from Team t",
                Long.class
        ).getSingleResult();
    }

}
