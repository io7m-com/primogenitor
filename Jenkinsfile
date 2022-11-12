#!groovy

pipeline {
  options {
    timeout time: 60, unit: 'MINUTES'
  }

  agent none

  stages {
    stage('BuildAndTest') {
      parallel {
        stage('linux:openjdk-17-hotspot') {
          agent { label 'linux' }
          tools {
            jdk   'openjdk-17-hotspot'
            maven 'maven-3.8.3'
          }
          steps {
            withMaven(
              maven: 'maven-3.8.3',
              mavenLocalRepo: '.repository') {
              sh 'env | sort'
              sh 'mvn -Denforcer.skip=true -Dbnd.baseline.skip=true -C -e -U clean install'
            }
          }
        }
      }
    }

    stage('Deploy') {
      when { buildingTag() }
      steps {
        withMaven(
          maven: 'maven-3.8.3',
          mavenLocalRepo: '.repository') {
          sh 'mvn -P arc7-deploy -Denforcer.skip=true -Dbnd.baseline.skip=true -C -e deploy'
        }
      }
    }
  }

  post {
    success {
      node('jenkinsnode01-mq') {
        script {
          withMaven(
            maven: 'maven-3.8.3',
            mavenLocalRepo: '.repository') {
            withCredentials([
              usernamePassword(
                credentialsId: 'arc7-jms-credentials',
                passwordVariable: 'JENKINS_JMS_PASSWORD',
                usernameVariable: 'JENKINS_JMS_USER'
              ),
              file(
                credentialsId: 'arc7-jenkins-trust-store',
                variable: 'JENKINS_JMS_TRUST_STORE'
              ),
              string(
                credentialsId: 'arc7-jenkins-trust-store-password',
                variable: 'JENKINS_JMS_TRUST_STORE_PASSWORD'
              )
            ]) {
              sh "./Jenkins-notify.sh success"
            }
          }
        }
      }
    }

    failure {
      node('jenkinsnode01-mq') {
        script {
          withMaven(
            maven: 'maven-3.8.3',
            mavenLocalRepo: '.repository') {
            withCredentials([
              usernamePassword(
                credentialsId: 'arc7-jms-credentials',
                passwordVariable: 'JENKINS_JMS_PASSWORD',
                usernameVariable: 'JENKINS_JMS_USER'
              ),
              file(
                credentialsId: 'arc7-jenkins-trust-store',
                variable: 'JENKINS_JMS_TRUST_STORE'
              ),
              string(
                credentialsId: 'arc7-jenkins-trust-store-password',
                variable: 'JENKINS_JMS_TRUST_STORE_PASSWORD'
              )
            ]) {
              sh "./Jenkins-notify.sh success"
            }
          }
        }
      }
    }
  }
}
