// Pipeline completa para a aplicação AlertMonitoring - VERSÃO FINAL CORRIGIDA 2
pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven3'
    }

    environment {
        APP_NAME              = 'AlertMonitoring'
        SONARQUBE_SERVER      = 'SonarQubeServer' // Este é o nome que demos no "Configure System"
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

        stage('2. Build, Analyze & Push to Nexus') {
            steps {
                withCredentials([
                    string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_AUTH_TOKEN'),
                    usernamePassword(credentialsId: NEXUS_CREDENTIALS_ID, passwordVariable: 'NEXUS_PASS', usernameVariable: 'NEXUS_USER')
                ]) {
                    // A variável de ambiente ${env.SONAR_HOST_URL} é injetada automaticamente pelo Jenkins
                    sh """
                        mvn clean verify sonar:sonar \
                          -Dsonar.host.url=${env.SONAR_HOST_URL} \
                          -Dsonar.login=${SONAR_AUTH_TOKEN} \
                          deploy -DskipTests
                    """
                }
            }
        }

        stage('3. Wait for SonarQube Quality Gate') {
            steps {
                echo 'Aguardando resultado da análise do SonarQube...'
                timeout(time: 1, unit: 'HOURS') {
                    // CORREÇÃO: Usando o parâmetro correto 'installationName'
                    waitForQualityGate abortPipeline: true, installationName: SONARQUBE_SERVER
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