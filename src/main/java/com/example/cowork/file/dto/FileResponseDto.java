package com.example.cowork.file.dto;

import com.example.cowork.entity.FileEntity;

import lombok.Getter;

@Getter
public class FileResponseDto {
    private Long id;
    private Long uploaderId; 
    private String fileName;
    private String createdAt;

    public FileResponseDto(FileEntity entity) {
        this.id = entity.getId();
        this.uploaderId = entity.getUploaderId();
        this.fileName = entity.getFileName();
        // 화면에 보기 좋게 "2026-03-31 15:30" 형식으로 문자열 자르기
        this.createdAt = entity.getCreatedAt().toString().replace("T", " ").substring(0, 16);
    }
}