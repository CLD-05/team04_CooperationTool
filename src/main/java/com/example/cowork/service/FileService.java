package com.example.cowork.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.cowork.entity.FileEntity;
import com.example.cowork.dto.file.FileResponseDto;
import com.example.cowork.repository.FileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    @Value("${file.dir}")
    private String fileDir;

    // 1. 파일 업로드
    @Transactional
    public void uploadFile(Long teamId, Long uploaderId, MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("첨부된 파일이 없습니다.");
        }

        String originalFileName = multipartFile.getOriginalFilename();

        if (originalFileName == null || !originalFileName.contains(".")) {
            throw new IllegalArgumentException("잘못된 형식의 파일입니다.");
        }

        String uuid = UUID.randomUUID().toString();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = uuid + extension;
        String fullPath = fileDir + savedFileName;

        try {
            multipartFile.transferTo(new File(fullPath));
        } catch (IOException e) {
            log.error("파일 저장 실패: {}", e.getMessage());
            throw new RuntimeException("서버에 파일을 저장하는 중 문제가 발생했습니다.");
        }

        FileEntity fileEntity = FileEntity.builder()
                .teamId(teamId)
                .uploaderId(uploaderId)
                .fileName(originalFileName)
                .filePath(fullPath)
                .build();
        fileRepository.save(fileEntity);
    }

    // 2. 파일 목록 조회
    @Transactional(readOnly = true)
    public Page<FileResponseDto> getTeamFiles(Long teamId, Pageable pageable) {
        return fileRepository.findByTeamIdOrderByCreatedAtDesc(teamId, pageable)
                .map(FileResponseDto::new);
    }

    // 3. 단일 파일 조회
    @Transactional(readOnly = true)
    public FileEntity getFileById(Long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 이미 삭제된 파일입니다."));
    }

    // 4. 파일 삭제
    @Transactional
    public void deleteFile(Long fileId) {
        FileEntity fileEntity = getFileById(fileId);

        File physicalFile = new File(fileEntity.getFilePath());
        if (physicalFile.exists()) {
            if (!physicalFile.delete()) {
                log.error("물리적 파일 삭제 실패: {}", fileEntity.getFilePath());
            }
        } else {
            log.warn("삭제하려는 파일이 물리적 경로에 존재하지 않습니다: {}", fileEntity.getFilePath());
        }

        fileRepository.delete(fileEntity);
    }
}