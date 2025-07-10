package com.bytehub.project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

	private final ProjectDAO dao;

	
	@Transactional
	public boolean create(ProjectData info) {
		int row = 0;
		if (dao.insertProj(info.proj) > 0) { // project가 들어갔다면
			row++;
			dao.insertUser(info.proj.getProject_idx(), info.user_id);
			if (info.file_idx.length > 0) { // 파일이 있으면
				//파일업로드기능추가...
				dao.insertFile(info.proj.getProject_idx(), info.file_idx);
			}
		}
//		log.info("successed rows: {}", row);
		return row > 0;
	}

	
	@Transactional
	public boolean edit(ProjectData info) {
		int row = 0;
		if (dao.updateProj(info.proj) > 0) {
			row++;
			dao.deleteUser(info.proj.getProject_idx());
			dao.insertUser(info.proj.getProject_idx(), info.user_id);
			if (info.file_idx.length > 0) {
				dao.deleteFile(info.proj.getProject_idx());
				dao.insertFile(info.proj.getProject_idx(), info.file_idx);
			}
		}
		return row > 0;
	}

	
	// 누가 파일업로드 만들어줫으면좋겟덩,,,상윤님 봄
	void uploadFile() {

	}

	void removeFile() {

	}


	public Map<String, Object> detail(int idx) {
		
		Map<String, Object> map = dao.detail(idx);
		
		map.put("members", dao.members(idx));
		map.put("file_idx", dao.files(idx));
		
		return map;
	}


	// 더 좋은 방법비어ㅏㅁㅇ너;리민
	public ArrayList<Map<String, Object>> list() {
		ArrayList<Map<String, Object>> arr = dao.list();
//		arr.add(dao.members(0));
		return arr;
	}

//	@Transactional
//	public boolean delete(int project_idx) {
//		dao.deleteFile(project_idx);
//		int row = dao.delete(project_idx);
//		return row > 0;
//	}

}
