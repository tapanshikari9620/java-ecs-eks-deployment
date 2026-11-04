pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        AWS_REGION       = 'us-east-1'
        ECR_REPOSITORY   = 'java-demo-app'
        EKS_CLUSTER_NAME = 'java-demo-eks'
        IMAGE_NAME       = 'java-demo-app'

        // Java 17 for building your Spring Boot application
        JAVA17_HOME = '/usr/lib/jvm/java-17-amazon-corretto'

        PATH = "${JAVA17_HOME}/bin:/usr/share/maven/bin:${env.PATH}"
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
                    echo "Java version:"
                    java -version

                    echo "Maven version:"
                    mvn --version

                    echo "Docker version:"
                    docker --version

                    echo "AWS CLI version:"
                    aws --version

                    echo "kubectl version:"
                    kubectl version --client
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
                sh '''
                    mvn clean package -DskipTests
                    test -f target/java-demo-app.jar
                '''
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

        stage('Login to Amazon ECR') {
            steps {
                sh '''
                    AWS_ACCOUNT_ID=$(aws sts get-caller-identity \
                      --query Account \
                      --output text)

                    ECR_REGISTRY="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

                    aws ecr get-login-password --region "${AWS_REGION}" \
                      | docker login \
                          --username AWS \
                          --password-stdin "${ECR_REGISTRY}"
                '''
            }
        }

        stage('Push Image to ECR') {
            steps {
                sh '''
                    AWS_ACCOUNT_ID=$(aws sts get-caller-identity \
                      --query Account \
                      --output text)

                    ECR_REGISTRY="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

                    docker tag "${IMAGE_NAME}:${BUILD_NUMBER}" \
                      "${ECR_REGISTRY}/${ECR_REPOSITORY}:${BUILD_NUMBER}"

                    docker tag "${IMAGE_NAME}:${BUILD_NUMBER}" \
                      "${ECR_REGISTRY}/${ECR_REPOSITORY}:latest"

                    docker push "${ECR_REGISTRY}/${ECR_REPOSITORY}:${BUILD_NUMBER}"
                    docker push "${ECR_REGISTRY}/${ECR_REPOSITORY}:latest"
                '''
            }
        }

        stage('Configure kubectl for EKS') {
            steps {
                sh '''
                    aws eks update-kubeconfig \
                      --region "${AWS_REGION}" \
                      --name "${EKS_CLUSTER_NAME}"

                    kubectl get nodes
                '''
            }
        }

        stage('Deploy to EKS') {
            steps {
                sh '''
                    AWS_ACCOUNT_ID=$(aws sts get-caller-identity \
                      --query Account \
                      --output text)

                    ECR_REGISTRY="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
                    IMAGE_URI="${ECR_REGISTRY}/${ECR_REPOSITORY}:${BUILD_NUMBER}"

                    # Update the image in the Kubernetes Deployment
                    kubectl set image deployment/java-demo-app \
                      java-demo-app="${IMAGE_URI}" \
                      --namespace default

                    kubectl rollout status deployment/java-demo-app \
                      --namespace default \
                      --timeout=180s
                '''
            }
        }
    }

    post {
        success {
            echo "Build ${BUILD_NUMBER} completed successfully and deployed to EKS."
        }

        failure {
            echo 'Pipeline failed. Check the Console Output for the failing stage.'
        }

        always {
            sh '''
                docker image rm "${IMAGE_NAME}:${BUILD_NUMBER}" 2>/dev/null || true
                docker image rm "${IMAGE_NAME}:latest" 2>/dev/null || true
            '''
        }
    }
}
