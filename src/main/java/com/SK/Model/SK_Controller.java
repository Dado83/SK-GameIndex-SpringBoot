package com.SK.Model;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class SK_Controller {

    private static final Logger LOGGER = Logger.getLogger(SK_Controller.class.getName());
    @Autowired
    GameReviewService service;
    @Autowired
    UpdateService updateService;

    @PostConstruct
    public void init() {
	LOGGER.info("inside controller");
	service.initialize();
    }

    @RequestMapping("/")
    public String index(Model model) {
	model.addAttribute("list", service.home());
	return "index";
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
