
pipeline {
    agent any

    parameters {
        string(
            name: 'DOCKER_USERNAME',
            defaultValue: 'nethra0704',
            description: 'Docker Hub username'
        )

        string(
            name: 'IMAGE_TAG',
            defaultValue: '1.0',
            description: 'Docker image tag'
        )

        string(
            name: 'GITHUB_REPO_URL',
            defaultValue: 'https://github.com/nethra-07-git/proj1.git',
            description: 'GitHub repository URL'
        )

        string(
            name: 'BRANCH_NAME',
            defaultValue: 'main',
            description: 'Git branch name'
        )
    }

    environment {
        MAVEN_HOME = 'C:\\Users\\nethr\\OneDrive\\Documents\\apache-maven-3.9.16'
        DOCKER_EXE = 'C:\\Users\\nethr\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe'
        DOCKER_IMAGE = "${params.DOCKER_USERNAME}/online-banking-system:${params.IMAGE_TAG}"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: "${params.BRANCH_NAME}",
                    url: "${params.GITHUB_REPO_URL}"
            }
        }

        stage('Build and Test') {
            steps {
                bat '"%MAVEN_HOME%\\bin\\mvn.cmd" clean test'
            }
        }

        stage('Package Application') {
            steps {
                bat '"%MAVEN_HOME%\\bin\\mvn.cmd" package'
            }
        }

        stage('Build Docker Image') {
            steps {
                bat '"%DOCKER_EXE%" build -t %DOCKER_IMAGE% .'
            }
        }

        stage('Login to Docker Hub') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-cred',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_TOKEN'
                    )
                ]) {
                    powershell '''
                        $secureToken = ConvertTo-SecureString $env:DOCKER_TOKEN -AsPlainText -Force
                        $credential = New-Object System.Management.Automation.PSCredential($env:DOCKER_USER, $secureToken)
                        $password = $credential.GetNetworkCredential().Password

                        $password | & $env:DOCKER_EXE login --username $env:DOCKER_USER --password-stdin

                        if ($LASTEXITCODE -ne 0) {
                            exit 1
                        }
                    '''
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                bat '"%DOCKER_EXE%" push %DOCKER_IMAGE%'
            }
        }
    }

    post {
        success {
            echo 'Online Banking System built, tested, and pushed successfully!'
        }

        failure {
            echo 'Pipeline failed. Check the Console Output.'
        }
    }
}
