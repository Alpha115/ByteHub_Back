package com.bytehub.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JwtToken {
	private static SecretKey pri_key = null;

	public static SecretKey getPri_key() {
		return pri_key;
	}

	public static void setPri_key() {
		JwtToken.pri_key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	}

	// 토큰생성
	public static String getToken(Map<String, Object> map) {

		return Jwts.builder().setHeaderParam("alg", "HS256").setHeaderParam("typ", "JWT").setClaims(map)
				.setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 30)))
				.signWith(pri_key).compact();
	}

	// 오버로드! 키, 갚으로 하나의 데이터만 만들 경우를 대비했대
	public static String getToken(String key, Object value) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(key, value);
		return getToken(map);
	}

	// 토큰검증(payload 읽기)
	public static Map<String, Object> readToken(String token) {
		Map<String, Object> result = new HashMap<String, Object>();
		Claims claims = Jwts.parserBuilder().setSigningKey(pri_key).build().parseClaimsJws(token).getBody();
		
		for (String key : claims.keySet()) {
			result.put(key, claims.get(key));
		}
		
		return result;
	}

}
