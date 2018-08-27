package com.svetkompjutera.update;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class UpdateController {

    private static final Logger LOGGER = Logger.getLogger(UpdateController.class.getName());

    @Autowired
    private UpdateService updateService;

    @GetMapping("/update")
    public String update(Model model) {
	LOGGER.info("controller update()");
	model.addAttribute("update", updateService.update());
	return "update";
    }
}
