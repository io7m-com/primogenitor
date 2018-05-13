#!groovy

pipeline {
  options {
    timeout time: 60, unit: 'MINUTES'
  }

  agent none

  stages {
    stage('Build') {
      parallel {
        stage('macOS:openjdk-9-hotspot') {
          agent { label 'macOS' }
          tools {
            jdk   'openjdk-9-hotspot'
            maven 'maven-3.5.3'
          }
          steps {
            sh 'mvn -X -Denforcer.skip=true clean install'
          }
        }
        stage('macOS:openjdk-10-hotspot') {
          agent { label 'macOS' }
          tools {
            jdk   'openjdk-10-hotspot'
            maven 'maven-3.5.3'
          }
          steps {
            sh 'mvn -X -Denforcer.skip=true clean install'
          }
        }

        stage('linux:openjdk-9-hotspot') {
          agent { label 'linux' }
          tools {
            jdk   'openjdk-9-hotspot'
            maven 'maven-3.5.3'
          }
          steps {
            sh 'mvn -X -Denforcer.skip=true clean install'
          }
        }
        stage('linux:openjdk-10-hotspot') {
          agent { label 'linux' }
          tools {
            jdk   'openjdk-10-hotspot'
            maven 'maven-3.5.3'
          }
          steps {
            sh 'mvn -X -Denforcer.skip=true clean install'
          }
        }
        stage('linux:openjdk-11-hotspot') {
          agent { label 'linux' }
          tools {
            jdk   'openjdk-11-hotspot'
            maven 'maven-3.5.3'
          }
          steps {
            sh 'mvn -X -Denforcer.skip=true clean install'
          }
        }
      }
    }
  }
}
