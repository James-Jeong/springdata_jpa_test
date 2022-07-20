package study.datajpa.entity.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import study.datajpa.entity.BaseEntity;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "userName", "age"})

@NamedEntityGraph(name = "Member.all", attributeNodes = @NamedAttributeNode("team"))
public class Member extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String userName;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String userName, int age) {
        this.userName = userName;
        this.age = age;
        this.team = null;
    }

    public Member(String userName, int age, Team team) {
        changeInfo(userName, age);
        changeTeam(team);
    }

    public void changeInfo(String userName, int age) {
        this.userName = userName;
        this.age = age;
    }

    public void changeTeam(Team team) {
        if (team != null) {
            this.team = team;
            team.getMembers().add(this);
        }
    }

    public String getTeamName() {
        if (team != null) {
            return team.getName();
        }
        return null;
    }

}