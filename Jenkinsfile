// Pipeline completa para a aplicação AlertMonitoring - VERSÃO FINAL COM LÓGICA CORRIGIDA
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
        stage('1. Checkout from Git') {
            steps {
                echo 'Buscando código do GitHub na branch main...'
                git branch: 'main', credentialsId: GITHUB_CREDENTIALS_ID, url: 'git@github.com:ProjetoDevSecOps/AlertMonitoring.git'
            }
        }

        // ETAPA CORRIGIDA: "Envelopamos" o estágio com o ambiente do SonarQube
        stage('2. Build, Analyze & Push to Nexus') {
            steps {
                withSonarQubeEnv(SONARQUBE_SERVER) {
                    withCredentials([
                        // O token do Sonar é pego automaticamente pelo withSonarQubeEnv, mas o deixamos para o comando mvn
                        string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_AUTH_TOKEN'),
                        usernamePassword(credentialsId: NEXUS_CREDENTIALS_ID, passwordVariable: 'NEXUS_PASS', usernameVariable: 'NEXUS_USER')
                    ]) {
                        // A URL do Sonar agora é injetada pela variável de ambiente do withSonarQubeEnv
                        sh """
                            mvn clean verify sonar:sonar \
                              -Dsonar.login=${SONAR_AUTH_TOKEN} \
                              deploy -DskipTests
                        """
                    }
                }
            }
        }

        // ETAPA CORRIGIDA: O comando agora é muito mais simples
        stage('3. Wait for SonarQube Quality Gate') {
            steps {
                echo 'Aguardando resultado da análise do SonarQube...'
                timeout(time: 1, unit: 'HOURS') {
                    // Sem parâmetros extras, ele usa o contexto do 'withSonarQubeEnv' anterior
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('4. Build & Scan Docker Image') {
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

        stage('5. Push Docker Image to Nexus') {
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

        stage('6. Deploy to Kubernetes') {
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
    }

    post {
        always {
            echo 'Pipeline finalizada.'
        }
    }
}