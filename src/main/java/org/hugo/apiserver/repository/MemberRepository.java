package org.hugo.apiserver.repository;

import org.hugo.apiserver.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, String> {

    @EntityGraph(attributePaths = {"memberRoleList"}) // Role도 가져오기 위해서 - 여러개쓰고싶으면 {}에 추가로
    @Query("select m from Member m where m.email = :email")
    Member getWithRoles(@Param("email") String email);
}
