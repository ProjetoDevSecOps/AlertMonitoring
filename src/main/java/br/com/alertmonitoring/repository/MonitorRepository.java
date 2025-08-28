package br.com.alertmonitoring.repository;

import br.com.alertmonitoring.model.Monitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitorRepository extends JpaRepository<Monitor, Integer> {
    long countByStatus(String status);
}
