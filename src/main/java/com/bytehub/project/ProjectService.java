package com.bytehub.project;

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
	public boolean create(ProjectInsert info) {
		int row = 0;
		if (dao.insertProj(info.proj) > 0) {	// project가 들어갔다면
			row++;
			if (info.file_idx.length > 0) {	// 파일이 있고
				if (dao.insertFile(info.proj.getProject_idx(), info.file_idx) > 0) {	//files가 들어갔다면
					row++;
				}
			}
		}
		log.info("successed rows: {}", row);
		return row > 0;
	}

}
