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
        DEPLOY_Host = '3.34.43.120'
    }
    stages {
        stage('Pull Codes from Github') {
            steps {
                checkout scm
            }
        }
        stage('Build Codes by Gradle') {
            steps {
                sh "./gradlew clean build"
            }
        }
        stage('dockerizing project by dockerfile') {
            steps {
                sh '''
                   docker build -t $IMAGE_NAME:$BUILD_NUMBER .
                   docker tag $IMAGE_NAME:$BUILD_NUMBER $IMAGE_NAME:latest
                   '''
            }
            post {
                success {
                    echo 'success dockerizing project'
                }
                failure {
                    error 'fail dockerizing project' // exit pipeline
                }
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
            post {
                success {
                    echo 'success upload image'
                }
                failure {
                    error 'fail upload image' // exit pipeline
                }
            }
        }
        stage('Deploy to AWS EC2 VM') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'deploy-ssh-key', keyFileVariable: 'privateKey')]) {
                    script {
                        // Windows에서 권한을 설정하기 위한 icacls 명령어 실행
                        bat '''
                            echo Setting file permissions on Windows...
                            icacls "%privateKey%" /inheritance:r /grant:r "SYSTEM:(R)"
                        '''

                        // SSH 에이전트를 시작하고 키를 추가하는 작업
                        bat '''
                            echo Starting SSH agent...
                            set SSH_AUTH_SOCK=\\.\pipe\openssh-ssh-agent
                            ssh-agent.cmd
                            echo Adding SSH key to agent...
                            ssh-add "%privateKey%" || echo "Failed to add identity"
                            echo Connecting via SSH...
                            ssh -o IdentitiesOnly=yes -o StrictHostKeyChecking=no ubuntu@$DEPLOY_Host << EOF
                            aws ecr get-login-password --region $REGION | docker login --username AWS --password-stdin $ECR_PATH
                            docker pull $IMAGE_NAME:$BUILD_NUMBER
                            docker rm -f existing_container || true
                            docker run -d -p 80:8080 --name existing_container $IMAGE_NAME:${BUILD_NUMBER}
                            EOF
                        '''
                    }
                }
            }
        }
    }
}
