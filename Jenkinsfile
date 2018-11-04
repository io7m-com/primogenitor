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
          sh 'mvn -P arc7-deploy -C -e deploy'
        }
      }
    }
  }
}
