def mainDir="."
def ecrLoginHelper="docker-credential-ecr-login"
def region="ap-northeast-2"
def ecrUrl="598552988151.dkr.ecr.ap-northeast-2.amazonaws.com"
def repository="board"
def deployHost="43.201.70.137"
def AWS_CREDENTIAL_NAME="aws-key"
pipeline {
    agent any
    stages {
        stage('Pull Codes from Github'){
            steps{
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
                        script{

                            docker.withRegistry("https://${ecrUrl}", "ecr:${region}:${AWS_CREDENTIAL_NAME}") {
                              docker.image("${ecrUrl}/${repository}:${BUILD_NUMBER}").push()
                              docker.image("${IMAGE_NAME}:latest").push()
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
        stage('Deploy to AWS EC2 VM'){
             steps{
                sshagent(credentials : ["deploy-key"]) {
                    sh "ssh -o StrictHostKeyChecking=no ubuntu@${deployHost} \
                     'aws ecr get-login-password --region ${region} | docker login --username AWS --password-stdin ${ecrUrl}/${repository}; \
                    docker run -d -p 80:8080 -t ${ecrUrl}/${repository}:${BUILD_NUMBER};'"
                }
             }
        }

    }
}
