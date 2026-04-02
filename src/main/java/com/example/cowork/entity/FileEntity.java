package com.example.cowork.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "File")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // [협업 안내] 현재는 결합도를 낮추기 위해 ID값만 저장합니다.
    // 추후 Team, User 파트가 완성되면 @ManyToOne으로 변경하여 연관관계를 맺을 수 있습니다.
    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "uploader_id", nullable = false)
    private Long uploaderId; // 기획서의 '이메일 기록'은 이 ID를 통해 User 테이블을 조회하여 가져옵니다.

    @Column(name = "file_name", nullable = false)
    private String fileName; // 원본 파일명

    @Column(name = "file_path", length = 512, nullable = false)
    private String filePath; // 맥북/서버에 저장된 실제 경로

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public FileEntity(Long teamId, Long uploaderId, String fileName, String filePath) {
        this.teamId = teamId;
        this.uploaderId = uploaderId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.createdAt = LocalDateTime.now(); 
    }
}