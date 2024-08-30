pipeline {
    agent any
    environment {
        TIME_ZONE = 'Asia/Seoul'
        PROFILE = 'local'
        AWS_CREDENTIAL_NAME = 'aws-key'
        DEPLOY_CREDENTIAL_NAME = 'deploy-ssh-key'
        REGION = 'ap-northeast-2'
        ECR_PATH = '339713037008.dkr.ecr.ap-northeast-2.amazonaws.com'
        IMAGE_NAME = '339713037008.dkr.ecr.ap-northeast-2.amazonaws.com/board'
        DEPLOY_Host = '15.165.75.201'
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
        stage('dockerizing project by dockerfile') {
            steps {
                sh "C:/Program\\ Files/Git/bin/bash.exe -c 'docker build -t $IMAGE_NAME:$BUILD_NUMBER . && docker tag $IMAGE_NAME:$BUILD_NUMBER $IMAGE_NAME:latest'"
            }
        }
        stage('upload aws ECR') {
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
                        // Git Bash를 사용하여 권한을 설정하고 SSH 명령어를 실행
                        sh '''
                            C:/Program\\ Files/Git/bin/bash.exe -c '
                            echo "Private key path: $privateKey"
                            chmod 600 "$privateKey" || echo "Failed to chmod"
                            ls -l "$privateKey"
                            eval $(ssh-agent -s)
                            ssh-add "$privateKey" || echo "Failed to add identity"
                            ssh-add -l || echo "Failed to list identities after adding"
                            ssh -v -o IdentitiesOnly=yes -o StrictHostKeyChecking=no ubuntu@$DEPLOY_Host << EOF
                            aws ecr get-login-password --region $REGION | docker login --username AWS --password-stdin $ECR_PATH
                            docker pull $IMAGE_NAME:$BUILD_NUMBER
                            docker rm -f existing_container || true
                            docker run -d -p 80:8080 --name existing_container $IMAGE_NAME:${BUILD_NUMBER}
                            EOF
                            '
                        '''
                    }
                }
            }
        }
    }
}
