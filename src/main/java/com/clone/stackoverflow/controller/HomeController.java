package com.clone.stackoverflow.controller;

import java.util.List;
import java.util.Set;

import com.clone.stackoverflow.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.clone.stackoverflow.Repository.AnswerRepository;
import com.clone.stackoverflow.Repository.QuestionRepository;
import com.clone.stackoverflow.Repository.UserRepository;
import com.clone.stackoverflow.model.Answer;
import com.clone.stackoverflow.model.Question;
import com.clone.stackoverflow.model.User;
import com.clone.stackoverflow.service.HomeService;
import org.springframework.ui.Model;


@Controller
public class HomeController {
	@Autowired
	UserRepository userRepository;
	@Autowired
	HomeService homeService;
	@Autowired
	QuestionRepository questionRepository;
	@Autowired
	AnswerRepository answerRepository;
	@Autowired
	QuestionService questionService;
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/")
	public String open() {
		return "Home";
	}
	@GetMapping("/user")
	public String UserProfile(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		User author = userRepository.getUserByUserName(username);
		model.addAttribute("author",author);
		return "UserProfile";
	}
	
	@GetMapping("/home")
	public String Home(@RequestParam(value = "start", required = false) Integer pageNo,
					   @RequestParam(required = false, value = "sort") String order,
					   Model model) {
		int pageSize = 10;
		if (pageNo == null) {
			pageNo = 1;
		}
		Page<Question> page = questionService.findPage(pageNo, pageSize, order);
		List<Question> questions = page.getContent();
		model.addAttribute("questions",questions);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("pageNo", pageNo);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("sortDir", order);
		return "HomePage";
	}
	@GetMapping("/question")
	public String QuestionPage(@RequestParam("id") Long id,Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		User author = userRepository.getUserByUserName(username);
		model.addAttribute("author",author);
		questionRepository.updateViewCount(id);
		Question question=questionRepository.findById(id).get();
		model.addAttribute("questions",question);
		List<Answer> anslist=question.getAnswers();
		model.addAttribute("anslist",anslist);
		
		return "ShowQuestion";
	}
	
	@GetMapping("/signup")
	public String SignUp() {
		return "SignUp";
	}
	
	@PostMapping("/register")
	public String SaveUserDate(
			@RequestParam("name") String name, 
			@RequestParam("email") String email,
			@RequestParam("password1") String password1,
			@RequestParam("password2") String password2) {
		User userObject = new User();
		userObject.setUsername(name);
		userObject.setEmail(email);
		//System.out.print(password1);
		//System.out.print(password2);
		if(password1.equals(password2)) {
			userObject.setPassword(bCryptPasswordEncoder.encode(password1));
			userRepository.save(userObject);
			return "redirect:/home";
			
		}else {
			return "redirect:/signup";
		}
		
		
	}
	@GetMapping("/search")
	public String Search(@RequestParam("search") String searchText,Model model) {
		Set<Question> setofQuestion= homeService.searchQuestion(searchText);
		model.addAttribute("questions",setofQuestion);
		model.addAttribute("test",true);
		//System.out.println("this is home controller");
		return "HomePage";
	}
	@PostMapping("/saveanswer")
	public String SaveAnswer(
			@RequestParam("answer") String answer,
			@RequestParam("id") Long id,Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		User author = userRepository.getUserByUserName(username);
		model.addAttribute("author",author);
		Question question=questionRepository.findById(id).get();
		Answer ansobj=new Answer();
		ansobj.setContent(answer);
		ansobj.prePersist();
		ansobj.setQuestion(question);   
		ansobj.setUser(author);
		answerRepository.save(ansobj);
		model.addAttribute("answer",ansobj);
		
		return "redirect:/question?id="+id;
	}
	
	@GetMapping("/login")
	public String LoginPage() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User author = userRepository.getUserByUserName(username);
        if(author !=null) {
        	return "redirect:/home";
        }

		return "Login";
	}
	
	

}