package br.com.alertmonitoring.model;

// CORREÇÃO: As importações agora usam 'jakarta.persistence'
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Monitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String type; // "url" ou "telnet"

    @Column(nullable = false)
    private String address;

    private Integer port;

    private LocalDateTime lastChecked;

    private String status; // "OK" ou "NOK"

    // Getters e Setters (continuam os mesmos)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Integer getPort() { return port; }
    public void setPort(Integer port) { this.port = port; }
    public LocalDateTime getLastChecked() { return lastChecked; }
    public void setLastChecked(LocalDateTime lastChecked) { this.lastChecked = lastChecked; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}