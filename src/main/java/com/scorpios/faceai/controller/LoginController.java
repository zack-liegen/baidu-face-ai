package com.scorpios.faceai.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/page")
public class LoginController {
	@RequestMapping(value = "/login")
	public String login() {
		return "loginPage";
	}
	@RequestMapping(value = "/register")
	public String register() {
		return "registerPage";
	}
	@RequestMapping(value = "/home")
	public String home() {
		return "menuPage";
	}
	@RequestMapping(value = "/userInfo")
	public String userInfo() {
		return "userInfo";
	}
	@RequestMapping(value = "/menu")
	public String menu() {
		return "menuPage";
	}

}
