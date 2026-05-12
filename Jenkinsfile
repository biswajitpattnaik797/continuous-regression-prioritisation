pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo 'Building the project'
                sh 'mvn clean compile'
            }
        }

        stage('Smoke Tests') {
            steps {
                echo 'Running smoke tests'
                sh 'mvn test -Dgroups=smoke'
            }
        }

        stage('Critical Tests') {
            steps {
                echo 'Running critical tests'
                sh 'mvn test -Dgroups=critical'
            }
        }

        stage('Regression Tests') {
            steps {
                echo 'Running full regression tests'
                sh 'mvn test -Dgroups=regression'
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'target/surefire-reports/**', allowEmptyArchive: true
        }
    }
}