package org.hugo.apiserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hugo.apiserver.dto.MemberDto;
import org.hugo.apiserver.dto.MemberModifyDto;
import org.hugo.apiserver.service.MemberService;
import org.hugo.apiserver.util.JwtUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
public class SocialController {

    private final MemberService memberService;

    @GetMapping("/api/member/kakao")
    public Map<String, Object> getMemberFromKakao(String accessToken) {
        log.info("accessToken: " + accessToken);
        MemberDto memberDto = memberService.getKakaoMember(accessToken);

        Map<String, Object> claims = memberDto.getClaims();

        String jwtAccessToken = JwtUtil.generateToken(claims, 10);
        String jwtRefreshToken = JwtUtil.generateToken(claims, 60 * 24);

        claims.put("accessToken", jwtAccessToken);
        claims.put("refreshToken", jwtRefreshToken);

        return claims;
    }

    @PutMapping("/api/member/modify")
    public Map<String, String> modify(@RequestBody MemberModifyDto memberModifyDto) {
        log.info("memberModifyDto: " + memberModifyDto);

        memberService.modifyMember(memberModifyDto);

        return Map.of("result", "modified");
    }
}
