package com.bytehub.department;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;

import com.bytehub.member.MemberDTO;

@Mapper
public interface DeptDAO {

	int deptInsert(DeptDTO dto);

	ArrayList<DeptDTO> deptList(DeptDTO dto);

	int deptUpdate(DeptDTO dto);

	int deptDelete(DeptDTO dto);

	ArrayList<MemberDTO> memberList(MemberDTO dto);

}
