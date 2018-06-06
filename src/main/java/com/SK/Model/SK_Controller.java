package com.SK.Model;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(value = "http://localhost:4200")
@RestController
public class SK_Controller {
	
	@Autowired
	GameReviewService service;
	
	
	@PostConstruct
	public void init() {
		service.initialize();
	}
	
	
	@RequestMapping("/")
	public List<GameReview> home() {
		return service.home();
	}
	
	
	@RequestMapping("/search")
	public List<GameReview> search(GameReview review) {
		return service.search(review);
	}

}
