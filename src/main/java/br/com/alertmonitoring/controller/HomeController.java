package br.com.alertmonitoring.controller;

import br.com.alertmonitoring.repository.MonitorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // LINHA NOVA: Adiciona a ferramenta de logging
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private MonitorRepository monitorRepository;

    @GetMapping("/")
    public String index(Model model) {
        logger.info(">>> MÉTODO INDEX FOI CHAMADO! Servindo a página index.html...");
        model.addAttribute("ok_count", monitorRepository.countByStatus("OK"));
        model.addAttribute("nok_count", monitorRepository.countByStatus("NOK"));
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        // LINHA NOVA: A mensagem do nosso "detetive"
        logger.info(">>> MÉTODO LOGIN FOI CHAMADO! Servindo a página login.html...");
        return "login";
    }
}