package com.petlife.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginChoiceController {
	
	 @GetMapping("/loginChoice")
	 public String loginChoice() {
	     return "loginChoice"; // 對應 loginChoice.html
	 }

	 @GetMapping("/loginMember")
	 public String loginMember() {
	     return "loginMember"; // 對應 loginMember.html
	 }

	 @GetMapping("/loginEmp")
	 public String loginEmp() {
	     return "loginEmp"; // 對應 loginEmp.html
	 }

}
