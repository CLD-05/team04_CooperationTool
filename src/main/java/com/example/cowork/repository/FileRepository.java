package com.example.cowork.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cowork.entity.FileEntity;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    
	// Pageable을 파라미터로 받도록 수정
	Page<FileEntity> findByTeamIdOrderByCreatedAtDesc(Long teamId, Pageable pageable);
}