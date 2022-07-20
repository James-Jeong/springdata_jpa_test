package study.datajpa.entity.member.projections;

public interface NestedClosedProjections {

    String getUserName();
    TeamInfo getTeam();

    interface TeamInfo {

        String getName();

    }

}
