pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven3'
    }

    environment {
        APP_NAME              = 'AlertMonitoring'
        SONARQUBE_SERVER      = 'SonarQubeServer'
        NEXUS_DOCKER_REGISTRY = '192.168.0.124:5000'
        NEXUS_CREDENTIALS_ID  = 'NEXUS_CREDS'
        KUBE_CONFIG_ID        = 'KUBE_CONFIG'
        GITHUB_CREDENTIALS_ID = 'GITHUB_CREDS' 
    }

    stages {
        // ... Estágios 1 a 7 permanecem exatamente os mesmos ...

        stage('1. Checkout from Git') {
            steps {
                echo 'Buscando código do GitHub na branch main...'
                git branch: 'main', credentialsId: GITHUB_CREDENTIALS_ID, url: 'git@github.com:ProjetoDevSecOps/AlertMonitoring.git'
            }
        }

        stage('2. Dependency Vulnerability Check') {
            steps {
                echo 'Verificando vulnerabilidades nas dependências com OWASP Dependency-Check...'
                sh 'mvn verify -DskipTests'
            }
        }

        stage('3. Build, Analyze & Push to Nexus') {
            steps {
                withSonarQubeEnv(SONARQUBE_SERVER) {
                    withCredentials([
                        string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_AUTH_TOKEN'),
                        usernamePassword(credentialsId: NEXUS_CREDENTIALS_ID, passwordVariable: 'NEXUS_PASS', usernameVariable: 'NEXUS_USER')
                    ]) {
                        configFileProvider([configFile(fileId: 'nexus-settings', variable: 'MAVEN_SETTINGS')]) {
                            sh """
                                mvn clean verify sonar:sonar \
                                  -s ${MAVEN_SETTINGS} \
                                  -Dsonar.login=${SONAR_AUTH_TOKEN} \
                                  deploy
                             """
                        }
                    }
                }
            }
        }

        stage('4. Wait for SonarQube Quality Gate') {
            steps {
                echo 'Aguardando resultado da análise do SonarQube...'
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('5. Build & Scan Docker Image') {
            steps {
                script {
                    def appNameLower = APP_NAME.toLowerCase()
                    def imageName = "${NEXUS_DOCKER_REGISTRY}/${appNameLower}:${env.BUILD_NUMBER}"
                    echo "Construindo imagem Docker: ${imageName}"
                    docker.build(imageName)
                    echo "Escaneando a imagem em busca de vulnerabilidades com Trivy..."
                    sh "trivy image --exit-code 1 --severity CRITICAL,HIGH ${imageName}"
                }
            }
        }

        stage('6. Push Docker Image to Nexus') {
            steps {
                script {
                    echo 'Enviando imagem Docker para o registro do Nexus...'
                    docker.withRegistry("http://${NEXUS_DOCKER_REGISTRY}", NEXUS_CREDENTIALS_ID) {
                        def appNameLower = APP_NAME.toLowerCase()
                        def imageNameWithTag = "${NEXUS_DOCKER_REGISTRY}/${appNameLower}:${env.BUILD_NUMBER}"
                        docker.image(imageNameWithTag).push()
                        docker.image(imageNameWithTag).push('latest')
                    }
                }
            }
        }

        stage('7. Deploy to Kubernetes') {
            steps {
                script {
                    withKubeConfig([credentialsId: KUBE_CONFIG_ID]) {
                        def appNameLower = APP_NAME.toLowerCase()
                        def imageName = "${NEXUS_DOCKER_REGISTRY}/${appNameLower}:${env.BUILD_NUMBER}"
                        echo "Realizando deploy da imagem: ${imageName} no Kubernetes..."
                        sh "kubectl apply -f k8s/"
                        sh "kubectl set image deployment/${appNameLower} ${appNameLower}=${imageName}"
                        sh "kubectl rollout status deployment/${appNameLower}"
                    }
                }
            }
        }

        // =================================================================== //
        //   ESTÁGIO 8 ATUALIZADO COM SCAN DA IMAGEM DO ZAP                    //
        // =================================================================== //
        stage('8. Dynamic Security Scan with ZAP') {
            steps {
                script {
                    def zapImage = "zaproxy/zap-weekly" // Usando a imagem oficial e estável
                    
                    echo "Baixando a imagem do OWASP ZAP: ${zapImage}"
                    sh "docker pull ${zapImage}"
                    
                    echo "Escaneando a imagem do OWASP ZAP com Trivy..."
                    // Se a própria imagem do ZAP tiver vulnerabilidades, a pipeline falha
                    sh "trivy image --exit-code 1 --severity CRITICAL,HIGH ${zapImage}"

                    echo 'Iniciando scan de segurança dinâmico na aplicação em execução...'
                    sleep(time: 30, unit: 'SECONDS') 
                    
                    def targetUrl = "http://192.168.0.142:30080/"
                    echo "Alvo do ZAP: ${targetUrl}"

                    sh """
                        docker run --rm -v \$(pwd):/zap/wrk/:rw ${zapImage} zap-baseline.py \
                        -t ${targetUrl} \
                        -g gen.conf -r report.html
                    """
                    
                    archiveArtifacts artifacts: 'report.html', allowEmptyArchive: true
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline finalizada.'
        }
    }
}