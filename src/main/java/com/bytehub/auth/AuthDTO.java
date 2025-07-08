package com.bytehub.auth;

import lombok.Data;

@Data
public class AuthDTO {
	private String user_id;
	private String access_type;	// 구분자
	private int access_idx;
	private String auth;	// 'read' or 'write' only
}
