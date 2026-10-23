
pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M3'
    }

    parameters {
        string(
            name: 'DOCKER_USERNAME',
            defaultValue: 'nethra0704',
            description: 'Enter your Docker Hub username'
        )

        string(
            name: 'IMAGE_TAG',
            defaultValue: '1.0',
            description: 'Enter the Docker image tag'
        )

        string(
            name: 'GITHUB_REPO_URL',
            defaultValue: 'https://github.com/nethra-07-git/proj1.git',
            description: 'GitHub repository URL'
        )

        string(
            name: 'BRANCH_NAME',
            defaultValue: 'main',
            description: 'Git branch to build'
        )
    }

    environment {
        DOCKER_IMAGE = "${params.DOCKER_USERNAME}/online-banking-system:${params.IMAGE_TAG}"
    }

    stages {
        stage('Checkout Repository') {
            steps {
                git branch: "${params.BRANCH_NAME}",
                    url: "${params.GITHUB_REPO_URL}"
            }
        }

        stage('Build and Test') {
            steps {
                bat '"C:\\Users\\nethr\\OneDrive\\Documents\\apache-maven-3.9.16\\bin\\mvn.cmd" clean test'
            }
        }

        stage('Package Application') {
            steps {
                bat '"C:\\Users\\nethr\\OneDrive\\Documents\\apache-maven-3.9.16\\bin\\mvn.cmd" package'
            }
        }

        stage('Build Docker Image') {
            steps {
                bat 'docker build -t %DOCKER_IMAGE% .'
            }
        }

        stage('Login to Docker Hub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-pat',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_TOKEN'
                )]) {
                    bat 'docker login -u "%DOCKER_USER%" --password-stdin'
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                bat 'docker push %DOCKER_IMAGE%'
            }
        }
    }

    post {
        success {
            echo 'Online Banking System built, tested, and pushed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check the console output.'
        }
    }
}
