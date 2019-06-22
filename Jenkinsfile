#!groovy

pipeline {
  options {
    timeout time: 60, unit: 'MINUTES'
  }

  agent none

  stages {
    stage('BuildAndTest') {
      parallel {
        stage('linux:openjdk-11-hotspot') {
          agent { label 'linux' }
          tools {
            jdk   'openjdk-11-hotspot'
            maven 'maven-3.6.0'
          }
          steps {
            withMaven(
              maven: 'maven-3.6.0',
              mavenLocalRepo: '.repository') {
              sh 'env | sort'
              sh 'mvn -Denforcer.skip=true -C -e -U clean install'
            }
          }
        }
      }
    }

    stage('Deploy') {
      when { buildingTag() }
      steps {
        withMaven(
          maven: 'maven-3.6.0',
          mavenLocalRepo: '.repository') {
          sh 'mvn -P arc7-deploy -Denforcer.skip=true -C -e deploy'
        }
      }
    }
  }

  post {
    success {
      node('mustard') {
        script {
          withMaven(
            maven: 'maven-3.6.0',
            mavenLocalRepo: '.repository') {
            withCredentials([
              usernamePassword(credentialsId: 'arc7-jms-credentials',
                               passwordVariable: 'JENKINS_JMS_PASSWORD',
                               usernameVariable: 'JENKINS_JMS_USER')]) {
              sh "./Jenkins-notify.sh success"
            }
          }
        }
      }
    }
    failure {
      node('mustard') {
        script {
          withMaven(
            maven: 'maven-3.6.0',
            mavenLocalRepo: '.repository') {
            withCredentials([
              usernamePassword(credentialsId: 'arc7-jms-credentials',
                               passwordVariable: 'JENKINS_JMS_PASSWORD',
                               usernameVariable: 'JENKINS_JMS_USER')]) {
              sh "./Jenkins-notify.sh failure"
            }
          }
        }
      }
    }
  }
}
