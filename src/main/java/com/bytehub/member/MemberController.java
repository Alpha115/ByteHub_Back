package com.bytehub.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private MemberService service;

    // 회원가입
    @PostMapping("/join")
    public Map<String, Object> join(@RequestBody Map<String, Object> param) {
        return service.join(param);
    }

    // 아이디 중복체크
    @GetMapping("/overlay/{id}")
    public Map<String, Object> overlay(@PathVariable String id) {
        log.info(id + " 중복체크");
        Map<String, Object> result = new HashMap<>();
        boolean success = service.overlay(id);
        result.put("use", success);
        return result;
    }
}
