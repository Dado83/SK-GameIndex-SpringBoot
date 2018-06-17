package com.SK.Model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(value = "http://localhost:4200")
@RestController
public class SK_Controller {
	
	private static final Logger logger = Logger.getLogger(SK_Controller.class.getName());
	@Autowired
	GameReviewService service;
	@Autowired
	UpdateLogic updateLogic;
	
	
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

	
	@RequestMapping("/update")
	public void update() {
		logger.info("controller update()");
		updateLogic.update();
	}
}
