pipeline {
    agent any
    environment {
        TIME_ZONE = 'Asia/Seoul'
        PROFILE = 'local'
        AWS_CREDENTIAL_NAME = 'aws-key'
        DEPLOY_CREDENTIAL_NAMAE = 'deploy-ssh-key'
        REGION = 'ap-northeast-2'
        ECR_PATH = '339713037008.dkr.ecr.ap-northeast-2.amazonaws.com'
        IMAGE_NAME = '339713037008.dkr.ecr.ap-northeast-2.amazonaws.com/board'
        DEPLOY_Host = '13.209.83.40'
    }
    stages {
        stage('Pull Codes from Github') {
            steps {
                checkout scm
            }
        }
        stage('Build Codes by Gradle') {
            steps {
                sh "C:/Program\\ Files/Git/bin/bash.exe -c './gradlew clean build'"
            }
        }
        stage('Dockerizing Project by Dockerfile') {
            steps {
                sh "C:/Program\\ Files/Git/bin/bash.exe -c 'docker build -t $IMAGE_NAME:$BUILD_NUMBER . && docker tag $IMAGE_NAME:$BUILD_NUMBER $IMAGE_NAME:latest'"
            }
        }
        stage('Upload to AWS ECR') {
            steps {
                script {
                    docker.withRegistry("https://$ECR_PATH", "ecr:$REGION:$AWS_CREDENTIAL_NAME") {
                        docker.image("$IMAGE_NAME:$BUILD_NUMBER").push()
                        docker.image("$IMAGE_NAME:latest").push()
                    }
                }
            }
        }
        stage('Deploy to AWS EC2 VM') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'deploy-ssh-key', keyFileVariable: 'privateKey')]) {
                    script {
                        sh '''
                            C:/Program\\ Files/Git/bin/bash.exe -c '
                            echo "Private key path: $privateKey"
                            chmod 600 "$privateKey" || echo "Failed to chmod"
                            ls -l "$privateKey"
                            eval $(ssh-agent -s)
                            ssh-add "$privateKey" || echo "Failed to add identity"
                            ssh-add -l || echo "Failed to list identities after adding"
                            ssh -o StrictHostKeyChecking=no ubuntu@$DEPLOY_Host <<EOF
                                aws ecr get-login-password --region $REGION | docker login --username AWS --password-stdin $ECR_PATH
                                docker run -d -p 80:8080 -t $IMAGE_NAME:${BUILD_NUMBER}
EOF
                            '
                        '''
                    }
                }
            }
        }
    }
}
