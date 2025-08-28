package br.com.alertmonitoring.scheduler;

import br.com.alertmonitoring.model.Monitor;
import br.com.alertmonitoring.repository.MonitorRepository;
import br.com.alertmonitoring.service.EmailService;
import br.com.alertmonitoring.service.MonitoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class MonitoringScheduler {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringScheduler.class);

    @Autowired
    private MonitorRepository monitorRepository;

    @Autowired
    private MonitoringService monitoringService;

    @Autowired
    private EmailService emailService;
    
    @Value("${app.alert.url-timeout-seconds}")
    private int urlTimeout;

    @Value("${app.alert.telnet-timeout-seconds}")
    private int telnetTimeout;

    @Scheduled(fixedRateString = "${app.alert.check-interval-seconds}000")
    public void monitorTargets() {
        logger.info("Iniciando verificação agendada de monitores...");
        List<String> errorMessages = new ArrayList<>();
        List<Monitor> monitors = monitorRepository.findAll();

        for (Monitor monitor : monitors) {
            boolean isOk = false;
            if ("url".equals(monitor.getType())) {
                isOk = monitoringService.verifyUrl(monitor.getAddress(), urlTimeout);
            } else if ("telnet".equals(monitor.getType())) {
                isOk = monitoringService.verifyTelnet(monitor.getAddress(), monitor.getPort(), telnetTimeout);
            }

            monitor.setLastChecked(LocalDateTime.now());
            if (isOk) {
                monitor.setStatus("OK");
            } else {
                monitor.setStatus("NOK");
                errorMessages.add("Atenção: O alvo " + monitor.getAddress() + (monitor.getPort() != null ? ":" + monitor.getPort() : "") + " não está acessível!");
            }
            monitorRepository.save(monitor);
        }

        if (!errorMessages.isEmpty()) {
            String finalMessage = String.join("\n", errorMessages);
            emailService.sendAlertEmail(finalMessage);
        }
        logger.info("Verificação agendada concluída.");
    }
}
