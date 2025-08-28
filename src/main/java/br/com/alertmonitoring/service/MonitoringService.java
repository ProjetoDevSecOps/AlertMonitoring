package br.com.alertmonitoring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.net.InetSocketAddress;
import java.net.Socket;

@Service
public class MonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringService.class);
    private final RestTemplate restTemplate = new RestTemplate();

    public boolean verifyUrl(String url, int timeoutSeconds) {
        try {
            return restTemplate.getForEntity(url, String.class).getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            logger.error("Erro ao verificar URL {}: {}", url, e.getMessage());
            return false;
        }
    }

    public boolean verifyTelnet(String host, int port, int timeoutSeconds) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutSeconds * 1000);
            return true;
        } catch (Exception e) {
            logger.error("Erro ao verificar Telnet {}:{}: {}", host, port, e.getMessage());
            return false;
        }
    }
}
