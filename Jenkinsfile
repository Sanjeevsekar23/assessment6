pipeline {

    agent any

    tools {
        maven 'M3'
    }

    stages {

        stage('Checkout Git') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/Sanjeevsekar23/assessment6.git'
            }
        }

        stage('Build and Test') {
            steps {
                bat 'mvn clean test'
            }
        }

        stage('Test Results') {
            steps {
                junit 'target/surefire-reports/*.xml'
            }
        }
    }
}