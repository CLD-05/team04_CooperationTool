package com.example.cowork.dto.file;

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
        this.createdAt = entity.getCreatedAt().toString().replace("T", " ").substring(0, 16);
    }
}