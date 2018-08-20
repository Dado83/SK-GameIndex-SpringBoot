package com.svetkompjutera.gamereview;

import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.svetkompjutera.update.UpdateService;

@Controller
class SKController {

    private static final Logger LOGGER = Logger.getLogger(SKController.class.getName());
    @Autowired
    GameReviewService service;
    @Autowired
    UpdateService updateService;

    @PostConstruct
    public void init() {
	LOGGER.info("inside controller");
	service.initialize();
    }

    @GetMapping("/")
    public String index(Model model) {
	model.addAttribute("list", service.home());
	model.addAttribute("review", new GameReview());
	model.addAttribute("numberOfGames", service.home().size());
	return "index";
    }

    @PostMapping("/search")
    public String search(Model model, @ModelAttribute GameReview review) {
	model.addAttribute("results", service.search(review));
	model.addAttribute("numberOfGames", service.search(review).size());
	return "searchResult";
    }

    @RequestMapping("/update")
    public Map<String, String> update() {
	LOGGER.info("controller update()");
	return updateService.update();
    }
}
