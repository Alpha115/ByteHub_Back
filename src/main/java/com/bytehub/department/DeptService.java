package com.bytehub.department;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeptService {
	
	private final DeptDAO dao;

	public boolean deptInsert(DeptDTO dto) {
		int row = dao.deptInsert(dto);
		return row > 0 ? true : false;
	}

	public ArrayList<DeptDTO> deptList(DeptDTO dto) {
		return dao.deptList(dto);
	}

	public boolean deptUpdate(DeptDTO dto) {
		int row = dao.deptUpdate(dto);
		return row > 0 ? true : false;
	}

	public boolean deptDelete(DeptDTO dto) {
		int row = dao.deptDelete(dto);
		return row > 0 ? true : false;
	}

}
