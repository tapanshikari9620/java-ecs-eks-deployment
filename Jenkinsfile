pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
        PATH = "${JAVA_HOME}/bin:/usr/share/maven/bin:${env.PATH}"

        ACR_LOGIN_SERVER = 'javademoacr99.azurecr.io'
        IMAGE_NAME = 'java-demo-app'
        CONTAINER_APP_NAME = 'java-demo-app'
        RESOURCE_GROUP = '1-71e9e42f-playground-sandbox'
        AZURE_LOCATION = 'eastus'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Verify Tools') {
            steps {
                sh '''
                    java --version
                    mvn --version
                    docker --version
                '''
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Build Java Application') {
            steps {
                sh 'mvn clean package -DskipTests'
                sh 'test -f target/java-demo-app.jar'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh '''
                    docker build \
                      -t ${IMAGE_NAME}:${BUILD_NUMBER} \
                      -t ${IMAGE_NAME}:latest .
                '''
            }
        }

        stage('Push Image to ACR') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'azure-acr-credentials',
                        usernameVariable: 'ACR_USERNAME',
                        passwordVariable: 'ACR_PASSWORD'
                    )
                ]) {
                    sh '''
                        set +x
                        echo "$ACR_PASSWORD" | docker login \
                          "$ACR_LOGIN_SERVER" \
                          --username "$ACR_USERNAME" \
                          --password-stdin

                        docker tag "${IMAGE_NAME}:${BUILD_NUMBER}" \
                          "${ACR_LOGIN_SERVER}/${IMAGE_NAME}:${BUILD_NUMBER}"

                        docker tag "${IMAGE_NAME}:${BUILD_NUMBER}" \
                          "${ACR_LOGIN_SERVER}/${IMAGE_NAME}:latest"

                        docker push "${ACR_LOGIN_SERVER}/${IMAGE_NAME}:${BUILD_NUMBER}"
                        docker push "${ACR_LOGIN_SERVER}/${IMAGE_NAME}:latest"

                        docker logout "$ACR_LOGIN_SERVER"
                    '''
                }
            }
        }

        stage('Deploy to Azure Container Apps') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'azure-acr-credentials',
                        usernameVariable: 'ACR_USERNAME',
                        passwordVariable: 'ACR_PASSWORD'
                    )
                ]) {
                    sh '''
                        set +x

                        # Authenticate Azure CLI using a service principal
                        # stored in Jenkins credentials.
                        az login \
                          --service-principal \
                          --username "$AZURE_CLIENT_ID" \
                          --password "$AZURE_CLIENT_SECRET" \
                          --tenant "$AZURE_TENANT_ID"

                        az account set --subscription "$AZURE_SUBSCRIPTION_ID"

                        # Update the existing Container App to the new image.
                        az containerapp update \
                          --name "$CONTAINER_APP_NAME" \
                          --resource-group "$RESOURCE_GROUP" \
                          --image "${ACR_LOGIN_SERVER}/${IMAGE_NAME}:${BUILD_NUMBER}"

                        az logout
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "Build ${BUILD_NUMBER} completed and deployment was requested."
        }

        failure {
            echo 'Pipeline failed. Review Console Output and fix the failing stage.'
        }

        always {
            sh '''
                docker image rm "${IMAGE_NAME}:${BUILD_NUMBER}" 2>/dev/null || true
                docker image rm "${IMAGE_NAME}:latest" 2>/dev/null || true
                docker image rm "${ACR_LOGIN_SERVER}/${IMAGE_NAME}:${BUILD_NUMBER}" 2>/dev/null || true
                docker image rm "${ACR_LOGIN_SERVER}/${IMAGE_NAME}:latest" 2>/dev/null || true
            '''
        }
    }
}
