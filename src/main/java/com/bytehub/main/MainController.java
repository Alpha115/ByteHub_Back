package com.bytehub.main;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
public class MainController {

	@RequestMapping("/")
	public void home() {

		log.info("집이아닙니다");

	}

}
