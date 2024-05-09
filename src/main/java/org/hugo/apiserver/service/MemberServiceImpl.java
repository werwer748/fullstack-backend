package org.hugo.apiserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hugo.apiserver.domain.Member;
import org.hugo.apiserver.domain.MemberRole;
import org.hugo.apiserver.dto.MemberDto;
import org.hugo.apiserver.dto.MemberModifyDto;
import org.hugo.apiserver.repository.MemberRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberDto getKakaoMember(String accessToken) {
        // accessToken을 이용해서 사용자 정보를 카카오로부터 가져온다.
        String nickname = getEmailFromKakaoAccessToken(accessToken);
        // DB의 회원정보 체크
        Optional<Member> result = memberRepository.findById(nickname);

        if (result.isPresent()) {
            // 중복되는 멤버가 있는경우
            MemberDto memberDto = entityToDto(result.get());
            log.info("서비스 이용중인 멤버가 존재::: " + memberDto);
            return memberDto;
        }

        Member socialMember = makeSocialMember(nickname);
        memberRepository.save(socialMember);

        return entityToDto(socialMember);
    }

    @Override
    public void modifyMember(MemberModifyDto memberModifyDto) {
        Optional<Member> result = memberRepository.findById(memberModifyDto.getEmail());

        Member member = result.orElseThrow();

        member.changeNickname(memberModifyDto.getNickname());
        member.changeSocial(false);
        member.changePw(passwordEncoder.encode(memberModifyDto.getPw()));

        memberRepository.save(member);
    }

    // 닉네임으로 유저 정보 생성
    private Member makeSocialMember(String email) {
        String tempPassword = makeTempPassword();

        log.info("tempPassword: " + tempPassword);
        Member member = Member.builder()
                .email(email)
                .pw(passwordEncoder.encode(tempPassword))
                .nickname("Social Member")
                .social(true)
                .build();

        member.addRole(MemberRole.USER);

        return member;
    }

    private String getEmailFromKakaoAccessToken(String accessToken) {
        String kakaoGetUserUrl = "https://kapi.kakao.com/v2/user/me";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoGetUserUrl).build();

        ResponseEntity<LinkedHashMap> response = restTemplate
                .exchange(uriBuilder.toUri(), HttpMethod.GET, entity, LinkedHashMap.class);

        log.info("------ get kakao access token ------");
        log.info(response);

        LinkedHashMap<String, LinkedHashMap> bodyMap = response.getBody();

        LinkedHashMap<String, String> kakaoAccount = bodyMap.get("properties");
        log.info(kakaoAccount);

        String nickname = kakaoAccount.get("nickname");
        log.info(nickname);
        log.info("------ ---------------------- ------");

        return nickname;
    }

    private String makeTempPassword() {
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < 10; i++) {
            buffer.append((char) ((int)(Math.random() * 55) + 65));
        }
        return buffer.toString();
    }
}
