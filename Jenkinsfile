#!groovy

pipeline {

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
              sh 'mvn -C -e -Denforcer.skip=true -U clean install'
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

    // Requires JMS Messaging plugin
    stage('Notify') {
      steps {
        def sendResult = sendCIMessage \
          providerName: 'default', \
          messageContent: 'some content', \
          messageProperties: 'CI_STATUS = passed', \
          messageType: 'CodeQualityChecksDone'

        echo sendResult.getMessageId()
        echo sendResult.getMessageContent()
      }
    }
  }
}

