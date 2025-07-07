package com.bytehub.member;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Map;

@Mapper
public interface MemberDAO {
    int countById(@Param("id") String id);
    int insertMember(MemberDTO member);
    MemberDTO getMemberById(String user_id);
    int login(Map<String, String> info);
    String findUserId(@Param("name") String name, @Param("email") String email);
    int updatePassword(@Param("user_id") String user_id, @Param("password") String password);
    int updateMember(MemberDTO member);
}
