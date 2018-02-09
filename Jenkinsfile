node {
	checkout scm
	sh './gradlew :1.12.2:setupCiWorkspace :1.12.2:clean :1.12.2:build'
	archive '1.12.2/build/libs/*jar'
}