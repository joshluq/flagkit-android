pipeline {
    agent none

    environment {
        // URLs internas de la red Docker
        NEXUS_URL = "http://nexus:8081/repository/android-releases/"
        SONAR_HOST = "http://sonarqube:9000"
    }

    stages {
        stage('Compilar y Analizar') {
            agent {
                docker {
                    image 'mobiledevops/android-sdk-image:34.0.0-jdk17'
                    args '-v /root/.gradle:/root/.gradle'
                }
            }
            steps {
                script {
                    sh 'chmod +x ./gradlew'
                    withSonarQubeEnv('SonarQube') {
                        sh './gradlew clean assembleDebug sonar \
                            -Dsonar.projectKey=es.joshluq.flagkit \
                            -Dsonar.projectName="Flagkit"'
                    }
                }
            }
        }

        stage('Publicar a Nexus') {

            agent {
                docker {
                    image 'mobiledevops/android-sdk-image:34.0.0-jdk17'
                }
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexus-credentials', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASSWORD')]) {
                    sh './gradlew publishReleasePublicationToMyNexusRepository'
                }
            }
        }
    }
}