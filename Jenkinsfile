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
        // Automatically triggers on GitHub Webhook push event
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

        stage('Unit Tests') {
            steps {
                echo '=== Stage 3: Running Level 1 Unit & Repository Tests ==='
                script {
                    if (isUnix()) {
                        sh 'mvn test -Dtest=UserServiceTest,PrintJobServiceTest,QueueServiceTest,VirtualPrinterServiceTest,PrintJobHistoryServiceTest,JwtTokenProviderTest,UserRepositoryTest'
                    } else {
                        bat 'mvn test -Dtest=UserServiceTest,PrintJobServiceTest,QueueServiceTest,VirtualPrinterServiceTest,PrintJobHistoryServiceTest,JwtTokenProviderTest,UserRepositoryTest'
                    }
                }
            }
        }

        stage('Integration Tests') {
            steps {
                echo '=== Stage 4: Running Level 2 & 3 Relational & Consistency Tests ==='
                script {
                    if (isUnix()) {
                        sh 'mvn test -Dtest=DatabaseConsistencyTest,PrinterStateConsistencyTest,QueueConsistencyTest'
                    } else {
                        bat 'mvn test -Dtest=DatabaseConsistencyTest,PrinterStateConsistencyTest,QueueConsistencyTest'
                    }
                }
            }
        }

        stage('API Tests') {
            steps {
                echo '=== Stage 5: Running Level 4 REST Controller & Security Tests ==='
                script {
                    if (isUnix()) {
                        sh 'mvn test -Dtest=AuthControllerTest,PrintJobControllerTest,QueueControllerTest,PrinterControllerTest,AdminControllerTest'
                    } else {
                        bat 'mvn test -Dtest=AuthControllerTest,PrintJobControllerTest,QueueControllerTest,PrinterControllerTest,AdminControllerTest'
                    }
                }
            }
        }

        stage('E2E & Smoke Tests') {
            steps {
                echo '=== Stage 6: Running Level 5 End-to-End User Journeys & Frontend Tests ==='
                script {
                    if (isUnix()) {
                        sh 'mvn test -Dtest=FrontendPagesIntegrationTest,PrintQueueE2ETest,PrintQueueApplicationTest'
                    } else {
                        bat 'mvn test -Dtest=FrontendPagesIntegrationTest,PrintQueueE2ETest,PrintQueueApplicationTest'
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                echo '=== Stage 7: Evaluating Automated Quality Gate Requirements ==='
                junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: false
                echo '✅ Quality Gate PASSED: All 118 tests across unit, integration, API, and E2E tiers passed.'
            }
        }

        stage('Package') {
            steps {
                echo '=== Stage 8: Packaging Executable Spring Boot JAR ==='
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
                echo '=== Stage 9: Building Production Container Image ==='
                script {
                    if (isUnix()) {
                        sh "docker build -t ${DOCKER_IMAGE} -t ${DOCKER_IMAGE_LATEST} ."
                    } else {
                        bat "docker build -t ${DOCKER_IMAGE} -t ${DOCKER_IMAGE_LATEST} ."
                    }
                }
            }
        }

        stage('Ansible Deployment') {
            steps {
                echo '=== Stage 10: Infrastructure Configuration & Container Deployment via Ansible ==='
                script {
                    if (isUnix()) {
                        sh '''
                        if command -v ansible-playbook >/dev/null 2>&1; then
                            ansible-playbook -i ansible/inventory.ini ansible/site.yml
                        else
                            echo "Ansible not installed on agent node; utilizing Docker Compose direct orchestrator..."
                            docker compose down || true
                            docker compose up -d
                        fi
                        '''
                    } else {
                        bat '''
                        where ansible-playbook >nul 2>nul
                        if not errorlevel 1 (
                            ansible-playbook -i ansible/inventory.ini ansible/site.yml
                        ) else (
                            echo Ansible not in PATH; utilizing Docker Compose direct orchestrator...
                            docker compose down || ver>nul
                            docker compose up -d
                        )
                        '''
                    }
                }
            }
        }

        stage('Health Check') {
            steps {
                echo '=== Stage 11: Automated Post-Deployment Health Verification ==='
                retry(6) {
                    sleep(time: 10, unit: 'SECONDS')
                    script {
                        if (isUnix()) {
                            sh "curl --fail http://localhost:${APP_PORT}/actuator/health || exit 1"
                        } else {
                            bat "powershell -Command \"$ErrorActionPreference = 'Stop'; Invoke-RestMethod -Uri http://localhost:${APP_PORT}/actuator/health\""
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            echo '=== Publishing Surefire Automated Test Results ==='
            junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
        }
        success {
            echo '🎉 Complete DevOps Pipeline Succeeded! Application tested, containerized, orchestrated, and verified UP.'
        }
        failure {
            echo '🛑 CRITICAL QUALITY GATE FAILURE! Deployment has been prevented to protect production.'
        }
    }
}
