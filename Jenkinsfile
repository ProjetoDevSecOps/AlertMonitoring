// Pipeline completa para a aplicação AlertMonitoring - VERSÃO CORRIGIDA
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
        GITHUB_CREDENTIALS_ID = 'GITHUB_SSH'
    }

    stages {
        stage('1. Checkout from Git') {
            steps {
                echo 'Buscando código do GitHub...'
                git branch: 'main', credentialsId: GITHUB_CREDENTIALS_ID, url: 'git@github.com:ProjetoDevSecOps/AlertMonitoring.git'
            }
        }

        stage('2. Code Analysis with SonarQube') {
            steps {
                script {
                    echo 'Analisando o código com o SonarQube...'
                    def scannerHome = tool 'SonarScanner'
                    withSonarQubeEnv(SONARQUBE_SERVER) {
                        sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=${APP_NAME} -Dsonar.sources=."
                    }
                }
            }
        }

        stage('3. Wait for SonarQube Quality Gate') {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('4. Build & Push JAR to Nexus') {
            steps {
                withCredentials([usernamePassword(credentialsId: NEXUS_CREDENTIALS_ID, passwordVariable: 'NEXUS_PASS', usernameVariable: 'NEXUS_USER')]) {
                    sh "mvn clean deploy -DskipTests"
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
                // VERSÃO CORRIGIDA COM O BLOCO 'script'
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