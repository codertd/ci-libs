/* Node Js pipeline for building node apps. */

def call(body) {

    def pipelineParams = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipeline {
        // agent any
        agent any
        options {
            buildDiscarder(logRotator(daysToKeepStr: '1', numToKeepStr: '10'))
            disableConcurrentBuilds()
        }
        environment {
            TEST = "${body.test}"
        }
        stages {
            stage('Setup') {
                agent {
                    docker {
                        image "alpine/git"
                        args '--entrypoint='
                    }
                }
                steps {
                    script {
                        setupEnvironmentAndTools()
                    }
                }
            }
            stage('Unit Tests') {
                agent {
                    docker {
                        image "node"
                        args '--entrypoint='
                    }
                }
                steps {
                    script {
                        // Setup node
                        sh "npm install"

                        // Execute unit tests
                        sh "npm run testAll"
                    }
                }
            }
            stage('Build Docker image and publish'){
                steps {
                    script {
                        // sh 'docker build -t react-app:latest -f Dockerfile --no-cache .'

                        docker.withRegistry("${env.dockerServer}", "${env.registryCredentials}") {
                            def customImage = docker.build("${repoName}:${env.BUILD_ID}")

                            // Push image up, and tag with latest if master.
                            customImage.push()

                            if (${env.BRANCH_NAME} == 'master') {
                                println 'Master branch, tagging with latest'
                                customImage.push('latest')
                            }
                        }
                    }
                }
            }
        }
    }
}
