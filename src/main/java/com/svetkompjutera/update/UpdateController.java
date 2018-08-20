package com.svetkompjutera.update;

import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
class UpdateController {

    private static final Logger LOGGER = Logger.getLogger(UpdateController.class.getName());

    @Autowired
    private UpdateService updateService;

    @RequestMapping("/update")
    public Map<String, String> update() {
	LOGGER.info("controller update()");
	return updateService.update();
    }
}
