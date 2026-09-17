pipeline {
    agent any

    tools {
        jdk 'Java21'
        maven 'Maven3'
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
    }

    triggers {
        // Trigger on GitHub Webhook push payload
        githubPush()
        // Lab fallback: Periodic SCM polling for offline/isolated lab networks
        pollSCM('H/2 * * * *')
    }

    environment {
        APP_NAME = 'digital-print-queue'
        DOCKER_IMAGE = "digital-print-queue:${env.BUILD_NUMBER}"
        DOCKER_IMAGE_LATEST = "digital-print-queue:latest"
        APP_PORT = '8080'
    }

    stages {
        stage('Checkout') {
            steps {
                echo '=== Stage 1: Checkout Source Code from VCS ==='
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo '=== Stage 2: Compiling Application & Resolving Dependencies ==='
                script {
                    if (isUnix()) {
                        sh 'mvn clean compile -DskipTests'
                    } else {
                        bat 'mvn clean compile -DskipTests'
                    }
                }
            }
        }

        stage('Unit Test') {
            steps {
                echo '=== Stage 3: Running Level 1 & 2 Unit and Repository Tests ==='
                script {
                    if (isUnix()) {
                        sh 'mvn test -Dtest=UserServiceTest,PrintJobServiceTest,QueueServiceTest,VirtualPrinterServiceTest,PrintJobHistoryServiceTest,JwtTokenProviderTest,UserRepositoryTest'
                    } else {
                        bat 'mvn test -Dtest=UserServiceTest,PrintJobServiceTest,QueueServiceTest,VirtualPrinterServiceTest,PrintJobHistoryServiceTest,JwtTokenProviderTest,UserRepositoryTest'
                    }
                }
            }
        }

        stage('Integration Test') {
            steps {
                echo '=== Stage 4: Running Level 3, 4, 5 API, Consistency, and E2E Tests ==='
                script {
                    if (isUnix()) {
                        sh 'mvn test -Dtest=RegressionTestSuite'
                    } else {
                        bat 'mvn test -Dtest=RegressionTestSuite'
                    }
                }
            }
        }

        stage('Package') {
            steps {
                echo '=== Stage 5: Packaging Executable Spring Boot JAR ==='
                script {
                    if (isUnix()) {
                        sh 'mvn package -DskipTests'
                    } else {
                        bat 'mvn package -DskipTests'
                    }
                }
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true, allowEmptyArchive: false
            }
        }

        stage('Docker Build') {
            steps {
                echo '=== Stage 6: Building Container Image ==='
                script {
                    if (isUnix()) {
                        sh "docker build -t ${DOCKER_IMAGE} -t ${DOCKER_IMAGE_LATEST} ."
                    } else {
                        bat "docker build -t ${DOCKER_IMAGE} -t ${DOCKER_IMAGE_LATEST} ."
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                echo '=== Stage 7: Deploying Container Instance ==='
                script {
                    if (isUnix()) {
                        sh 'docker compose down || true'
                        sh 'docker compose up -d'
                    } else {
                        bat 'docker compose down || ver>nul'
                        bat 'docker compose up -d'
                    }
                }
            }
        }

        stage('Health Check') {
            steps {
                echo '=== Stage 8: Automated Post-Deployment Health Verification ==='
                sleep(time: 10, unit: 'SECONDS')
                script {
                    if (isUnix()) {
                        sh "curl --fail --retry 5 --retry-delay 5 http://localhost:${APP_PORT}/actuator/health || exit 1"
                    } else {
                        bat "powershell -Command \"Invoke-RestMethod -Uri http://localhost:${APP_PORT}/actuator/health\""
                    }
                }
            }
        }
    }

    post {
        always {
            echo '=== Publishing Surefire Automated Test Results ==='
            junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
            cleanWs deleteDirs: true, notFailBuild: true, patterns: [[pattern: 'uploads/**', type: 'EXCLUDE']]
        }
        success {
            echo '🎉 Pipeline Succeeded! Application tested, containerized, and deployed successfully.'
        }
        failure {
            echo '❌ Pipeline FAILED! Deployment has been stopped due to critical quality gate failure.'
        }
    }
}
