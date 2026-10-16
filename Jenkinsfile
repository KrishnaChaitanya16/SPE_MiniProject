pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    triggers {
        // Fired by the GitHub webhook (Poll SCM can be enabled on the job as a fallback)
        githubPush()
    }

    environment {
        IMAGE_NAME = 'krishnachaitanya1605/calculator-api'
        IMAGE_TAG  = "${env.BUILD_NUMBER}"
        // Jenkins credential ID of type "Username with password" (Docker Hub user + access token)
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub-credentials'
        // Recipient of the build status emails
        NOTIFY_EMAIL = '16krish2005@gmail.com'

        // Jenkins launched from the macOS app does not inherit the shell PATH
        PATH = "/usr/local/bin:/opt/homebrew/bin:/Applications/Docker.app/Contents/Resources/bin:${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh './mvnw -B clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh './mvnw -B test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -t ${IMAGE_NAME}:latest .'
            }
        }

        stage('Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: env.DOCKERHUB_CREDENTIALS_ID,
                                                  usernameVariable: 'DOCKERHUB_USER',
                                                  passwordVariable: 'DOCKERHUB_TOKEN')]) {
                    sh '''
                        echo "$DOCKERHUB_TOKEN" | docker login -u "$DOCKERHUB_USER" --password-stdin
                        docker push ${IMAGE_NAME}:${IMAGE_TAG}
                        docker push ${IMAGE_NAME}:latest
                    '''
                }
            }
            post {
                always {
                    sh 'docker logout || true'
                }
            }
        }

        stage('Deploy') {
            environment {
                // Jenkins itself normally owns 8080, so publish the app on 8081
                APP_PORT = '8081'
            }
            steps {
                // Idempotent: unchanged services are left running, a new image tag recreates only the replicas
                sh '''
                    docker compose up -d --remove-orphans

                    for i in $(seq 1 30); do
                        if curl -fs "http://localhost:${APP_PORT}/actuator/health" | grep -q '"status":"UP"'; then
                            echo "Deployment healthy on port ${APP_PORT}"
                            exit 0
                        fi
                        echo "Waiting for application to become healthy ($i/30)..."
                        sleep 5
                    done

                    echo "Deployment did not become healthy" >&2
                    docker compose ps
                    docker compose logs --tail=50
                    exit 1
                '''
            }
        }
    }
    post {
        success {
            mail to: env.NOTIFY_EMAIL,
                 subject: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: """Build #${env.BUILD_NUMBER} of ${env.JOB_NAME} succeeded.

Image deployed: ${env.IMAGE_NAME}:${env.IMAGE_TAG}
Build URL: ${env.BUILD_URL}
"""
        }
        failure {
            mail to: env.NOTIFY_EMAIL,
                 subject: "FAILURE: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: """Build #${env.BUILD_NUMBER} of ${env.JOB_NAME} failed.

Console output: ${env.BUILD_URL}console
"""
        }
    }
}
