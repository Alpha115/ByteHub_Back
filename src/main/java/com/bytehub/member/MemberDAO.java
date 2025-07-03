package com.bytehub.member;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberDAO {
    int countById(@Param("id") String id);
    int insertMember(MemberDTO member);
}
