#!groovy

pipeline {
  options {
    timeout time: 60, unit: 'MINUTES'
  }

  agent none

  stages {
    stage('BuildAndTest') {
      parallel {
        stage('linux:openjdk-9-hotspot') {
          agent { label 'linux' }
          tools {
            jdk   'openjdk-9-hotspot'
            maven 'maven-3.5.3'
          }
          steps {
            withMaven(
              maven: 'maven-3.5.3',
              mavenLocalRepo: '.repository') {
              sh 'mvn -Denforcer.skip=true -C -e -U clean install'
            }
          }
        }
        stage('linux:openjdk-10-hotspot') {
          agent { label 'linux' }
          tools {
            jdk   'openjdk-10-hotspot'
            maven 'maven-3.5.3'
          }
          steps {
            withMaven(
              maven: 'maven-3.5.3',
              mavenLocalRepo: '.repository') {
              sh 'mvn -Denforcer.skip=true -C -e -U clean install'
            }
          }
        }
      }
    }
  }
}
