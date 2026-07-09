pipeline{
    agent any

    tools{
        jdk 'jdk21'
        maven 'maven3'
    }

    stages{
        stage('clone code'){
            steps{
                checkout scm
            }
        }

        stage('build application'){
            steps{
                sh 'mvn clean package'
            }
        }

        // stage('test'){
        //     steps{
        //         sh 'mvn test'
        //     }
        // }

        stage('build docker image'){
            steps{
                sh 'docker build -t todo-app . '
            }
        }

        stage('deploy container'){
            steps{
                sh '''
                docker stop todo-app-container || true
                docker rm todo-app-container || true
                docker run -d -p 8081:8081 --name todo-app-container todo-app
                '''
            }
        }
    }

    post{

        always{
            sh 'docker image prune -f || true ' 
        }
    }
}