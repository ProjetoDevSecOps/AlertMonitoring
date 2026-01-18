# AlertMonitoring - DevSecOps Lab 🚀

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.10-green)
![Kubernetes](https://img.shields.io/badge/Kubernetes-On--Premise-blue)
![Jenkins](https://img.shields.io/badge/Jenkins-CI%2FCD-red)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 📄 About the Project

This project was developed as part of my **Final Academic Research Article**, demonstrating the implementation of a **DevSecOps** culture within a realistic software development lifecycle.

The application, **AlertMonitoring**, is a URL monitoring tool built with Spring Boot and Thymeleaf. It allows users to register websites and receive email alerts if a site goes down.

However, the core focus of this repository is the **infrastructure and security automation** built around the application. The goal was to simulate an On-Premise environment, configuring the entire CI/CD pipeline, Container Orchestration, and Security Gates manually. This approach allowed for a deep understanding of the "Control Plane" and "Data Plane" operations, avoiding the abstractions often found in managed cloud services.

## 📥 Inputs & 📤 Outputs

This breakdown reflects the integration between infrastructure automation and the application's business logic.

### 🛠️ DevSecOps Pipeline (Automation)
* **Inputs:**
    * **Source Code:** Java 21 / Spring Boot and K8s Manifests.
    * **Credentials:** Secrets managed by Jenkins (`NEXUS_CREDS`, `SONAR_TOKEN`, `NVD_API_KEY`).
    * **Security Feeds:** NVD vulnerability databases (via OWASP) and Trivy image vulnerability databases.
* **Outputs:**
    * **Secure Artifacts:** `.jar` file and Docker Image (Alpine-based) stored in Nexus Repository Manager.
    * **Compliance Reports:** Code coverage reports (JaCoCo) and quality analysis in SonarQube.
    * **Deployment:** Application running on Kubernetes Cluster using a Rolling Update strategy.

### 💻 AlertMonitoring (Application)
* **Inputs:**
    * **Configurations (`application.yml`):** Timeout parameters (URL/Telnet), check intervals (default 300s), and administrative credentials.
    * **Monitoring:** URL registration (HTTP) and Hosts/Ports (TCP/Telnet).
    * **Authentication:** User login managed via Spring Security 6 (In-Memory).
* **Outputs:**
    * **Operational Logs:** Logs with automatic rotation (1MB limit and 3 backups).
    * **Notifications:** Critical alerts via SMTP (email) triggered by `EmailService`.
    * **Persistence:** Monitoring data stored in H2 database (Runtime).
    * **Dashboard:** Visual interface listing the count of "OK" and "NOK" services.

## 🏗️ Architecture & DevSecOps Pipeline

The pipeline was designed to integrate security at the development level (**Shift-Left** strategy).

### Continuous Integration & Security (CI)
The `Jenkinsfile` defines a robust pipeline with the following stages:

1. **SCA (Software Composition Analysis):** Uses **OWASP Dependency-Check** to identify known vulnerabilities in project dependencies.
2. **Build & Unit Tests:** Compilation and testing using Maven.
3. **SAST (Static Application Security Testing):** Code quality and security analysis using **SonarQube**.
4. **Quality Gate:** The pipeline halts immediately if the code does not meet the strict quality metrics defined in SonarQube.
5. **Artifact Management:** Deploys the built `.jar` file to a **Nexus Repository Manager**.

### Continuous Delivery & Deployment (CD)
6. **Containerization:** Builds the Docker image based on `eclipse-temurin:21-jdk-alpine`.
7. **Container Security:** Scans the Docker image for vulnerabilities using **Trivy** (Severities `CRITICAL` and `HIGH` break the build).
8. **Registry Push:** Pushes the secure image to the Nexus Docker Registry.
9. **Kubernetes Deploy:** Updates the deployment in the K8s cluster using a Rolling Update strategy.

## 🛠️ Tech Stack

### Application
* **Language:** Java 21
* **Framework:** Spring Boot 3.4.10
* **Frontend:** Thymeleaf (Server-side rendering)
* **Database:** H2 (Runtime/Embedded)
* **Security:** Spring Security 6

### Infrastructure & Tools
* **Orchestration:** Kubernetes (K8s)
* **CI/CD:** Jenkins
* **Artifact Repository:** Nexus Repository Manager (Maven & Docker)
* **Security Scanners:** OWASP Dependency-Check, Trivy, SonarQube
* **Container:** Docker

## 📂 Project Structure

    ├── k8s/                  # Kubernetes Manifests
    │   ├── deployment.yaml   # Application Deployment (2 Replicas)
    │   └── service.yaml      # Service definition
    ├── src/                  # Source code (Spring Boot)
    ├── Dockerfile            # Docker image definition (Alpine based)
    ├── Jenkinsfile           # Groovy Pipeline script
    ├── pom.xml               # Maven dependencies & Plugins
    └── README.md             # Project documentation

## ⚙️ Configuration Notes

This project was designed for a local lab environment. To run it in your own infrastructure, you must update the references in `Jenkinsfile`, `pom.xml`, and `deployment.yaml`:

* **Nexus IP:** The project currently points to `192.168.0.124`. Update this to your Nexus server address.
* **Credentials:** The pipeline relies on Jenkins Credentials IDs (`NEXUS_CREDS`, `GITHUB_CREDS`, `SONAR_TOKEN`, `KUBE_CONFIG`, `NVD_API_KEY_ID`).

## 🚀 How to Run (Local Dev)

1. Clone the repository:

        git clone https://github.com/ProjetoDevSecOps/AlertMonitoring.git

2. Build with Maven:

        mvn clean install

3. Run the application:

        java -jar target/alert-monitoring-1.0.0.jar

4. Access via browser: `http://localhost:8080`

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
*Created by **Diogo Tavares da Silva** as part of a DevSecOps research study.*
