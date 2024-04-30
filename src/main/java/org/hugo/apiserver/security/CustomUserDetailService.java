package org.hugo.apiserver.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hugo.apiserver.domain.Member;
import org.hugo.apiserver.dto.MemberDto;
import org.hugo.apiserver.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service // 스프링빈을 등록해줘야함.
@Log4j2
public class CustomUserDetailService implements UserDetailsService { // 로그인을 처리할 때 동작하는 클래스

    private final MemberRepository memberRepository;

    @Override // username: 아이디에 해당하는 값 현재 프로젝트에서는 이메일
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("------------ loadUserByUsername 작동 확인 ------------" + username);

        Member member = memberRepository.getWithRoles(username);

        if (member == null) {
            throw new UsernameNotFoundException("Not Found");
        }

        MemberDto memberDto = new MemberDto(
                member.getEmail(),
                member.getPw(),
                member.getNickname(),
                member.isSocial(),
                member.getMemberRoleList()
                        .stream()
                        .map(memberRole -> memberRole.name()).collect(Collectors.toList())
        );

        log.info(memberDto); // 찍어보면 인증까지는 됨.

        return memberDto;
    }
}
