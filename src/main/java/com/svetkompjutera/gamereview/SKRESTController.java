package com.svetkompjutera.gamereview;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.svetkompjutera.update.UpdateService;

@CrossOrigin(value = "http://localhost:4200")
//@RestController
class SKRESTController {

    private static final Logger LOGGER = Logger.getLogger(SKRESTController.class.getName());
    @Autowired
    GameReviewService service;
    @Autowired
    UpdateService updateService;

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

    @RequestMapping("/update")
    public Map<String, String> update() {
	LOGGER.info("controller update()");
	return updateService.update();
    }
}
