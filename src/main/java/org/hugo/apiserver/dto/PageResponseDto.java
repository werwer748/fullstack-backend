package org.hugo.apiserver.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class PageResponseDto<E> { // 내려가는 값들이 조금씩 다르기 때문에 제네릭을 활용

    private List<E> dtoList;

    private List<Integer> pageNumList;

    private PageRequestDto pageRequestDto;

    private boolean prev, next;

    private int totalCount, prevPage, nextPage, totalPage, current;

    @Builder(builderMethodName = "withAll")
    public PageResponseDto(List<E> dtoList, PageRequestDto pageRequestDto, long total) {
        this.dtoList = dtoList;
        this.pageRequestDto = pageRequestDto;
        this.totalCount = (int) total;

        // 끝페이지 end
        int end = (int) (Math.ceil(pageRequestDto.getPage() / 10.0)) * 10;

        int start = end - 9;

        // 진짜로 마지막인 페이지
        int last = (int) (Math.ceil(totalCount / (double) pageRequestDto.getSize()));

        end = end > last ? last : end;

        this.prev = start > 1;
        this.next = totalCount > end * pageRequestDto.getSize();

        this.pageNumList = IntStream.rangeClosed(start, end)
                .boxed() // int => Integer
                .collect(Collectors.toList());

        this.prevPage = prev ? start - 1 : 0;

        this.nextPage = next ? end + 1 : 0;

        this.totalPage = this.pageNumList.size();

        this.current = pageRequestDto.getPage();
    }
}
