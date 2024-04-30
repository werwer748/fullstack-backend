package org.hugo.apiserver.dto;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;
import java.util.stream.Collectors;

public class MemberDto extends User { // 혹은 implements UserDetail

    private String email, pw, nickname;

    private boolean social;

    private List<String> roleNames = new ArrayList<>();

    // extends User: 생성자가 필요하다.
    public MemberDto(String email, String pw, String nickname, boolean social, List<String> roleNames) {
        super(
                email,
                pw,
                /**
                 * 권한 같은 경우 스프링시큐리티가 쓰는 권한으로 만들어줘야 함.
                 * 시큐리티가 쓰는 타입에 맞춰서 Dto를 만듬.
                 * 시큐리티는 권한을 객체로 가져야하기때문에 문자열로 객체를 만들어주는 SimpleGrantedAuthority를 사용함.
                 */
                roleNames.stream().map(s -> new SimpleGrantedAuthority("ROLE_" + s)).collect(Collectors.toList())
        );
        this.email = email;
        this.pw = pw;
        this.nickname = nickname;
        this.social = social;
        this.roleNames = roleNames;
    }

    // JWT 만들기
    public Map<String, Object> getClaims() {
        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("email", email);
        dataMap.put("pw", pw); // 패스워드 안주고 처리하는 방법 찾아봐야 함...
        dataMap.put("nickname", nickname);
        dataMap.put("social", social);
        dataMap.put("roleNames", roleNames);

        return dataMap;
    }

}
