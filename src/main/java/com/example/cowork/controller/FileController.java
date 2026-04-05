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
import com.example.cowork.entity.Team;
import com.example.cowork.dto.file.FileResponseDto;
import com.example.cowork.service.FileService;
import com.example.cowork.service.TeamMemberService;
import com.example.cowork.service.TeamService;
import com.example.cowork.type.MemberRoleType;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/teams/{team_id}/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final TeamMemberService teamMemberService;
    private final TeamService teamService;

    // 1. 파일 목록 조회
    @GetMapping
    public String fileList(@PathVariable("team_id") Long teamId,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           HttpSession session,
                           Model model) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/api/user/login";

        Pageable pageable = PageRequest.of(page, 10);
        Page<FileResponseDto> files = fileService.getTeamFiles(teamId, pageable);

        boolean isLeader = false;
        try {
            isLeader = teamMemberService.getMemberInfo(teamId, userId).getRole() == MemberRoleType.LEADER;
        } catch (Exception ignored) {}

        Team team = teamService.getTeamById(teamId);

        model.addAttribute("files", files);
        model.addAttribute("teamId", teamId);
        model.addAttribute("teamName", team.getName());
        model.addAttribute("teamDescription",
                team.getDescription() != null ? team.getDescription() : "프로젝트 자료 및 일정 관리");
        model.addAttribute("isLeader", isLeader);
        return "file-form/list";
    }

    // 2. 파일 업로드
    @PostMapping("/upload")
    public String uploadFile(@PathVariable("team_id") Long teamId,
                             @RequestParam("file") MultipartFile file,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        Long uploaderId = (Long) session.getAttribute("userId");
        if (uploaderId == null) return "redirect:/api/user/login";

        try {
            fileService.uploadFile(teamId, uploaderId, file);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/teams/" + teamId + "/files";
    }

    // 3. 파일 삭제
    @PostMapping("/{file_id}/delete")
    public String deleteFile(@PathVariable("team_id") Long teamId,
                             @PathVariable("file_id") Long fileId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/api/user/login";

        try {
            fileService.deleteFile(fileId);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/teams/" + teamId + "/files";
    }

    // 4. 파일 다운로드
    @GetMapping("/{file_id}/download")
    public ResponseEntity<Object> downloadFile(@PathVariable("team_id") Long teamId,
                                               @PathVariable("file_id") Long fileId,
                                               HttpSession session,
                                               RedirectAttributes redirectAttributes) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(302)
                    .header(HttpHeaders.LOCATION, "/api/user/login")
                    .build();
        }

        try {
            FileEntity fileEntity = fileService.getFileById(fileId);
            UrlResource resource = new UrlResource("file:" + fileEntity.getFilePath());
            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalArgumentException("실제 파일이 서버에 존재하지 않습니다.");
            }
            String encodedFileName = UriUtils.encode(fileEntity.getFileName(), StandardCharsets.UTF_8);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + encodedFileName + "\"")
                    .body(resource);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return ResponseEntity.status(302)
                    .header(HttpHeaders.LOCATION, "/teams/" + teamId + "/files")
                    .build();
        }
    }
}