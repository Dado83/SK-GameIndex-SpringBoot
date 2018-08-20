package com.svetkompjutera.gamereview;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin(value = "http://localhost:4200")
class SKRESTController {

    private static final Logger LOGGER = Logger.getLogger(SKRESTController.class.getName());
    @Autowired
    GameReviewService service;

    @PostConstruct
    public void init() {
	LOGGER.info("inside RESTcontroller");
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
