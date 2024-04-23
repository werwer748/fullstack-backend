package org.hugo.apiserver.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@ToString
@Getter
@Builder // 엔티티상단에 빌더사용시 생성자 어노테이션을 세트로 사용해줄것
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_todo")
// 엔티티 객체는 DB라고 생각하자.
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // strategy: 키생성전략
    private Long tno; // 프라이머리키 비교시 equals, hashCode 등을 쓰기 때문에 기본타입은 쓸 수 없다.

    @Column(length = 500, nullable = false)
    private String title;

    private String content;

    private boolean complete;

    private LocalDate dueDate;

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void changeComplete(boolean complete) {
        this.complete = complete;
    }

    public void changeDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
