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
          sh 'mvn -P arc7-deploy -Denforcer.skip=true -C -e deploy'
        }
      }
    }
  }

  post {
    always {
      node('mustard') {
        script {
          withMaven(
            maven: 'maven-3.6.0',
            mavenLocalRepo: '.repository') {
            sh 'mvn -U org.apache.maven.plugins:maven-dependency-plugin:3.1.1:get -DgroupId=com.io7m.jsay -DartifactId=com.io7m.jsay -Dversion=0.0.1 -Dclassifier=main -Dtransitive=false'
            sh 'cp .repository/com/io7m/jsay/com.io7m.jsay/0.0.1/com.io7m.jsay-0.0.1-main.jar jsay.jar'
            sh 'java -jar jsay.jar'
          }
        }
      }
    }
  }
}
