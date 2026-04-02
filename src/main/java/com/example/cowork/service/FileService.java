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
import com.example.cowork.file.dto.FileResponseDto;
import com.example.cowork.repository.FileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // 콘솔에 에러 로그를 찍기 위한 어노테이션
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    @Value("${file.dir}")
    private String fileDir;

    // 1. 파일 업로드 로직
    @Transactional
    public void uploadFile(Long teamId, Long uploaderId, MultipartFile multipartFile) {
        // [예외 처리 1] 파일이 아예 첨부되지 않은 경우 예외 발생
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("첨부된 파일이 없습니다.");
        }

        String originalFileName = multipartFile.getOriginalFilename();
        
        // [예외 처리 2] 확장자가 없는 이상한 파일 필터링
        if (originalFileName == null || !originalFileName.contains(".")) {
            throw new IllegalArgumentException("잘못된 형식의 파일입니다.");
        }

        String uuid = UUID.randomUUID().toString();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = uuid + extension;
        String fullPath = fileDir + savedFileName;

        try {
            // 물리적 폴더에 저장 시도
            multipartFile.transferTo(new File(fullPath));
        } catch (IOException e) {
            log.error("파일 저장 실패: {}", e.getMessage());
            throw new RuntimeException("서버에 파일을 저장하는 중 문제가 발생했습니다.");
        }

        // DB 저장
        FileEntity fileEntity = FileEntity.builder()
                .teamId(teamId)
                .uploaderId(uploaderId)
                .fileName(originalFileName)
                .filePath(fullPath)
                .build();
        fileRepository.save(fileEntity);
    }

    // 2. 파일 목록 조회 로직
    // Page 객체를 반환하도록 변경
    @Transactional(readOnly = true)
    public Page<FileResponseDto> getTeamFiles(Long teamId, Pageable pageable) {
        return fileRepository.findByTeamIdOrderByCreatedAtDesc(teamId, pageable)
                .map(FileResponseDto::new); // Page<FileEntity>를 Page<FileResponseDto>로 변환
    }

    // 3. 단일 파일 조회 (다운로드/삭제용)
    @Transactional(readOnly = true)
    public FileEntity getFileById(Long fileId) {
        // [예외 처리 3] DB에 없는 파일을 찾을 때 예외 발생
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 이미 삭제된 파일입니다."));
    }

    // 4. 파일 삭제 로직
    @Transactional
    public void deleteFile(Long fileId) {
        FileEntity fileEntity = getFileById(fileId);
        
        File physicalFile = new File(fileEntity.getFilePath());
        // [예외 처리 4] 물리적 파일이 실제로 존재하는지 확인 후 삭제
        if (physicalFile.exists()) {
            if (!physicalFile.delete()) {
                log.error("물리적 파일 삭제 실패: {}", fileEntity.getFilePath());
                // 파일이 삭제되지 않아도 DB에서는 지우도록 멈추지 않고 진행
            }
        } else {
            log.warn("삭제하려는 파일이 물리적 경로에 존재하지 않습니다: {}", fileEntity.getFilePath());
        }
        
        fileRepository.delete(fileEntity);
    }
    
  
//  컨트롤러에서 넘겨받은 실제 로그인 유저 ID(currentUserId)와
//  DB에 저장된 업로더 ID를 비교하여 본인 확인을 수행
//    @Transactional
//    public void deleteFile(Long fileId, Long currentUserId) {
//        FileEntity fileEntity = getFileById(fileId);
//        
//        // 본인 확인 (인가)
//        if (!fileEntity.getUploaderId().equals(currentUserId)) {
//            throw new RuntimeException("본인이 업로드한 파일만 삭제할 수 있습니다.");
//        }
//        
//        File physicalFile = new File(fileEntity.getFilePath());
//        
//        if (physicalFile.exists()) {
//            if (!physicalFile.delete()) {
//                log.error("물리적 파일 삭제 실패: {}", fileEntity.getFilePath());
//            }
//        }
//        
//        fileRepository.delete(fileEntity);
//    }
    
}