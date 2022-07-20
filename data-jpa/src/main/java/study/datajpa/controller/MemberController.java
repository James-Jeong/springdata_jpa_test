package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.entity.member.Member;
import study.datajpa.entity.member.dto.MemberDto;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }

    // Parameter 에 PK 가 들어간 경우 도메인 컨버터 기능 사용 가능하다.
    @GetMapping("/members1/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).orElse(null);
        if (member == null) { return ""; }
        return member.getUserName();
    }

    // 도메인 컨버터로 인해 자동으로 객체가 주입된다.
    // Repository 를 사용해서 영속성 컨텍스트에서 엔티티를 찾는다.
    // 이 기능은 데이터 조회용으로만 사용해야 한다. 절대로 수정하면 안된다!!
    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        if (member == null) { return ""; }
        return member.getUserName();
    }

    /**
     * PageRequest 구현체 클래스가 Page 번호를 파라미터로 전달받아 Pageble 인터페이스 기반으로 동작하도록 해준다.
     * <p>
     * localhost:8080/members?page=0&sort=id,desc
     * localhost:8080/members?page=0&sort=id,desc&sort=userName,asc
     *
     * 절대로 엔티티를 그대로 반환하면 안된다. 무조건 DTO 사용하기!!
     */
    @GetMapping("/members")
    public Page<MemberDto> memberList(@PageableDefault(size = 5) Pageable pageable) {
        Page<Member> memberPage = memberRepository.findAll(pageable);
        return memberPage.map(
                member -> new MemberDto(
                        member.getId(), member.getUserName(), member.getTeamName()
                )
        );
    }

}
