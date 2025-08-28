package br.com.alertmonitoring.controller;

import br.com.alertmonitoring.model.Monitor;
import br.com.alertmonitoring.repository.MonitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/monitor")
public class MonitorController {

    @Autowired
    private MonitorRepository monitorRepository;

    @GetMapping("/list")
    public String monitorList(Model model) {
        model.addAttribute("monitors", monitorRepository.findAll());
        return "monitor_list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("monitor", new Monitor());
        return "add_monitor";
    }

    @PostMapping("/add")
    public String addMonitor(Monitor monitor, RedirectAttributes redirectAttributes) {
        monitorRepository.save(monitor);
        redirectAttributes.addFlashAttribute("message", "Monitoramento adicionado com sucesso!");
        return "redirect:/monitor/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Monitor monitor = monitorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID do monitor inválido:" + id));
        model.addAttribute("monitor", monitor);
        return "edit_monitor";
    }

    @PostMapping("/edit/{id}")
    public String editMonitor(@PathVariable("id") Integer id, Monitor monitor, RedirectAttributes redirectAttributes) {
        monitorRepository.save(monitor);
        redirectAttributes.addFlashAttribute("message", "Monitoramento atualizado com sucesso!");
        return "redirect:/monitor/list";
    }

    @PostMapping("/delete/{id}")
    public String deleteMonitor(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        monitorRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Monitoramento excluído com sucesso!");
        return "redirect:/monitor/list";
    }
}
