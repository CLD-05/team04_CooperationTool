package com.example.cowork.service;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

@Service
public class PagingService {

    private static final int PAGE_LENGTH = 5;

    public List<Integer> getPagingNumbers(int pageNumber, int totalPages) {
        int currentBlock = (pageNumber - 1) / PAGE_LENGTH;
        int startPage = currentBlock * PAGE_LENGTH + 1;
        int endPage = Math.min(startPage + PAGE_LENGTH - 1, totalPages);

        return IntStream.rangeClosed(startPage, endPage).boxed().toList();
    }
}