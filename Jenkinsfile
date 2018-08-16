pipeline {
    agent any
	stages {
	    stage("init") {
	        steps {
	            sh 'git submodule update --init --recursive'
	        }
	    }
	    stage("1.7.10") {
	        steps {
	            sh './gradlew :1.7.10:setupCiWorkspace'
	            sh './gradlew :1.7.10:clean'
	            sh './gradlew :1.7.10:build'
	            archiveArtifacts artifacts: '1.7.10/build/libs/*jar'
	        }
	    }
	    stage("1.9.4") {
	        steps {
	            sh './gradlew :1.9.4:setupCiWorkspace'
	            sh './gradlew :1.9.4:clean'
	            sh './gradlew :1.9.4:build'
	            archiveArtifacts artifacts: '1.9.4/build/libs/*jar'
	        }
	    }
	    stage("1.12.2") {
	        steps {
	            sh './gradlew :1.12.2:setupCiWorkspace'
	            sh './gradlew :1.12.2:clean'
	            sh './gradlew :1.12.2:build'
	            archiveArtifacts artifacts: '1.12.2/build/libs/*jar'
	        }
	    }
	}
}