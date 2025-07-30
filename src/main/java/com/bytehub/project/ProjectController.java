package com.bytehub.project;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bytehub.member.FileDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/project")
public class ProjectController {
	
	Map<String, Object> resp=null;
	private final ProjectService service;
	private final ProjectDAO dao;
	
	@Value("${spring.servlet.multipart.location}")
	private String uploadDirProp;

    // 프로젝트 상세
    @GetMapping("/detail/{idx}")
    public Map<String, Object> detail(@PathVariable int idx) {
        return service.detail(idx);
    }

    // 프로젝트 리스트
    @GetMapping("/list")
    public Map<String, Object> list() {
        Map<String, Object> resp = new HashMap<>();
        
        ArrayList<ProjectFileDTO> file_list = service.fileListByProject();
        ArrayList<ProjectEmpDTO> emp_list = service.empListByProject();
        
        resp.put("files", file_list);
        resp.put("member", emp_list);
        resp.put("list", service.list());
        return resp;
    }

    // 프로젝트 생성 (여러 멤버/파일)
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> create(
            @RequestPart("projectData") ProjectDataDTO info,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {

        Map<String, Object> resp = new HashMap<>();
        boolean success = service.create(info, files);
        resp.put("success", success);
        return resp;
    }

    // 프로젝트 수정 (여러 멤버/파일)
    @PutMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> edit(
            @RequestPart("projectData") ProjectDataDTO info,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {

        Map<String, Object> resp = new HashMap<>();
        boolean success = service.edit(info, files);
        resp.put("success", success);
        return resp;
    }
    
    @GetMapping("/download/{file_idx}")
    public ResponseEntity<Resource> download(@PathVariable int file_idx) throws Exception {
        // 파일 정보 조회 (DB)
        FileDTO file = dao.selectFileById(file_idx);
        String filePath = uploadDirProp + "/" + file.getNew_filename();

        Path path = Paths.get(filePath);
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("파일이 존재하지 않습니다.");
        }

        // 한글 파일명 인코딩
        String encodedName = URLEncoder.encode(file.getOri_filename(), StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedName + "\"")
                .body(resource);
    }
    
    @PostMapping("/delete")
    public Map<String, Object> projectDelete(@RequestBody ProjectDTO dto){
        Map<String, Object> resp = new HashMap<>();
        boolean success = service.projectDelete(dto);
        resp.put("success", success);
    	return resp;
    }
}
