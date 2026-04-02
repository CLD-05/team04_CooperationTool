package com.example.cowork.controller;

import java.nio.charset.StandardCharsets;

import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import com.example.cowork.entity.FileEntity;
import com.example.cowork.file.dto.FileResponseDto;
import com.example.cowork.service.FileService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/teams/{team_id}/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    // 1. 파일 목록 조회 (GET)
    @GetMapping
    public String fileList(@PathVariable("team_id") Long teamId,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           Model model) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<FileResponseDto> files = fileService.getTeamFiles(teamId, pageable);
        model.addAttribute("files", files);
        model.addAttribute("teamId", teamId);
        return "file-form/list";
    }

    // 2. 파일 업로드 폼 (GET)
    @GetMapping("/upload")
    public String uploadForm(@PathVariable("team_id") Long teamId, Model model) {
        model.addAttribute("teamId", teamId);
        return "file-form/upload";
    }

    // 3. 파일 업로드 처리 (POST)
    @PostMapping("/upload")
    public String uploadFile(@PathVariable("team_id") Long teamId,
                             @RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        Long dummyUploaderId = 1L;
        try {
            fileService.uploadFile(teamId, dummyUploaderId, file);
            return "redirect:/teams/" + teamId + "/files";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/teams/" + teamId + "/files/upload";
        }
    }

    // 4. 파일 삭제 처리 (POST)
    @PostMapping("/{file_id}/delete")
    public String deleteFile(@PathVariable("team_id") Long teamId,
                             @PathVariable("file_id") Long fileId,
                             RedirectAttributes redirectAttributes) {
        try {
            fileService.deleteFile(fileId);
            redirectAttributes.addFlashAttribute("successMessage", "파일이 안전하게 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/teams/" + teamId + "/files";
    }

    // 5. 파일 다운로드 (GET)
    @GetMapping("/{file_id}/download")
    public Object downloadFile(@PathVariable("team_id") Long teamId,
                               @PathVariable("file_id") Long fileId,
                               RedirectAttributes redirectAttributes) {
        try {
            FileEntity fileEntity = fileService.getFileById(fileId);
            UrlResource resource = new UrlResource("file:" + fileEntity.getFilePath());
            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalArgumentException("실제 파일이 서버에 존재하지 않습니다.");
            }
            String encodedFileName = UriUtils.encode(fileEntity.getFileName(), StandardCharsets.UTF_8);
            String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .body(resource);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/teams/" + teamId + "/files";
        }
    }
}