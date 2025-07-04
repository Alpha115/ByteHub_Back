package com.bytehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bytehub.utils.JwtUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class ByteHubBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(ByteHubBackApplication.class, args);
		godHelpUs();
		
		if(JwtUtils.getPri_key()==null) {
			JwtUtils.setPri_key();
			log.info("JWT Key 발행: {}", JwtUtils.getPri_key());
		}
	}

	static void godHelpUs() {
		System.out.println("//                   _ooOoo_");
		System.out.println("//                  o8888888o");
		System.out.println("//                  88\" . \"88");
		System.out.println("//                  (| -_- |)");
		System.out.println("//                  O\\  =  /O");
		System.out.println("//               ____/`---'\\____");
		System.out.println("//             .'  \\\\|     |//  `.");
		System.out.println("//            /  \\\\|||  :  |||//  \\");
		System.out.println("//           /  _||||| -:- |||||-  \\");
		System.out.println("//           |   | \\\\\\  -  /// |   |");
		System.out.println("//           | \\_|  ''\\---/''  |   |");
		System.out.println("//           \\  .-\\__  `-`  ___/-. /");
		System.out.println("//         ___`. .'  /--.--\\  `. . __");
		System.out.println("//      .\"\" '<  `.___\\_<|>_/___.'  >'\"\".");
		System.out.println("//     | | :  `- \\`.;`\\ _ /`;.`/ - ` : | |");
		System.out.println("//     \\  \\ `-.   \\_ __\\ /__ _/   .-` /  /");
		System.out.println("//======`-.____`-.___\\_____/___.-`____.-'======");
		System.out.println("//                   `=---='");
		System.out.println("//");
		System.out.println("//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		System.out.println("//          佛祖保佑           永无BUG");
		System.out.println("//         God Bless        Never Crash");
	}

}
