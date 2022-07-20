package study.datajpa.entity.member.projections;

import org.springframework.beans.factory.annotation.Value;

public interface MemberUserNameOnly {

    @Value("#{target.userName + ' ' + target.age}") // open projection : DB 에서 가져온 데이터를 어플리케이션 단에서 다시 처리하는 방법
    String getUserName(); // @Value 없으면 close projection

}
