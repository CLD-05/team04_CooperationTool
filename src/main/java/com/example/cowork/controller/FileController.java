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
//import [User 엔티티 경로].User;

@Controller
@RequestMapping("/teams/{team_id}/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    // 1. 파일 목록 조회 (GET)
    @GetMapping
    public String fileList(@PathVariable("team_id") Long teamId, 
                           @RequestParam(value = "page", defaultValue = "0") int page, // 기본값 0페이지
                           Model model) {
        
        // 한 페이지에 10개씩 보여주도록 설정
        Pageable pageable = PageRequest.of(page, 10);
        Page<FileResponseDto> files = fileService.getTeamFiles(teamId, pageable);
        
        model.addAttribute("files", files);
        model.addAttribute("teamId", teamId);
        return "team/list";
    }

    // 2. 파일 업로드 폼 (GET)
    @GetMapping("/upload")
    public String uploadForm(@PathVariable("team_id") Long teamId, Model model) {
        model.addAttribute("teamId", teamId);
        return "team/upload";
    }

 // 3. 파일 업로드 처리 (POST)
    @PostMapping("/upload")
    public String uploadFile(@PathVariable("team_id") Long teamId,
                             @RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) { 
        
        Long dummyUploaderId = 1L; // 로그인 연동 전 테스트용 ID

        try {
            fileService.uploadFile(teamId, dummyUploaderId, file);
            // 업로드 성공 시 목록으로 이동
            return "redirect:/teams/" + teamId + "/files"; 

        } catch (RuntimeException e) { 
            // [예외 처리] 서비스에서 발생한 에러를 잡아 화면에 전달
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/teams/" + teamId + "/files/upload"; // 실패 시 폼으로 다시 이동
        }
    } 
    
//	  코드 통합 후 인증/인가 부분    
//  @PostMapping("/upload")
//  public String uploadFile(@PathVariable("team_id") Long teamId,
//                           @RequestParam("file") MultipartFile file,
//                           @AuthenticationPrincipal User user, // [수정] Object 대신 User 사용
//                           RedirectAttributes redirectAttributes) { 
//      
//      try {
//          // user.getId()를 사용하여 실제 로그인한 사용자의 ID를 넘김
//          fileService.uploadFile(teamId, user.getId(), file);
//          return "redirect:/teams/" + teamId + "/files"; 
//
//      } catch (RuntimeException e) { 
//          redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
//          return "redirect:/teams/" + teamId + "/files/upload";
//      }
//  }

    // 4. 파일 삭제 처리 (POST)
    @PostMapping("/{file_id}/delete")
    public String deleteFile(@PathVariable("team_id") Long teamId,
                             @PathVariable("file_id") Long fileId,
                             RedirectAttributes redirectAttributes) {
        try {
            // TODO: 나중에 로그인한 유저가 업로더 본인이거나 리더인지 확인하는 권한 로직 추가 필요
            fileService.deleteFile(fileId);
            // 삭제 성공 메시지
            redirectAttributes.addFlashAttribute("successMessage", "파일이 안전하게 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/teams/" + teamId + "/files";
    }

//  코드 코드 통합 후 인증/인가 부분
//  @PostMapping("/{file_id}/delete")
//  public String deleteFile(@PathVariable("team_id") Long teamId,
//                           @PathVariable("file_id") Long fileId,
//                           @AuthenticationPrincipal User user, // [수정] Object 대신 User 사용
//                           RedirectAttributes redirectAttributes) {
//      try {
//          // [수정] 현재 로그인한 사용자의 ID(user.getId())를 서비스로 전달
//          fileService.deleteFile(fileId, user.getId());
//          
//          redirectAttributes.addFlashAttribute("successMessage", "파일이 안전하게 삭제되었습니다.");
//      } catch (IllegalArgumentException | RuntimeException e) {
//          redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
//      }
//      return "redirect:/teams/" + teamId + "/files";
//  }

    // 5. 파일 다운로드 (GET) - 화면 이동 없음
    @GetMapping("/{file_id}/download")
    public Object downloadFile(
									    		@PathVariable("team_id") Long teamId,
									            @PathVariable("file_id") Long fileId,
									            RedirectAttributes redirectAttributes) {
        try {
            FileEntity fileEntity = fileService.getFileById(fileId);
            UrlResource resource = new UrlResource("file:" + fileEntity.getFilePath());
            
            // 물리적 파일이 진짜 있는지 한 번 더 체크
            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalArgumentException("실제 파일이 서버에 존재하지 않습니다.");
            }

            // 다운로드 시 한글 파일명 깨짐 방지
            String encodedFileName = UriUtils.encode(fileEntity.getFileName(), StandardCharsets.UTF_8);
            String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .body(resource);

        } catch (Exception e) {
            // [핵심] 에러 발생 시 에러 페이지로 보내지 않고, 메시지와 함께 목록으로 리다이렉트!
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/teams/" + teamId + "/files"; 
        }
    }
    

    
}
