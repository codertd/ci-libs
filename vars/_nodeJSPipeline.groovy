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
            stage('Initialize Docker'){
                steps {
                    script {
                        def dockerHome = tool 'docker'
                        env.PATH = "${dockerHome}:${env.PATH}"
                        println "dockerHome: ${env.PATH}"
                    }
                }
            }
            stage('Git Setup') {
                agent {
                    docker {
                        image "alpine/git"
                        args '--entrypoint='
                    }
                }
                steps {
                    script {
                        println "Setting Up"
                    }
                }
            }
            stage('Npm Setup') {
                agent {
                    docker {
                        image "node"
                        args '--entrypoint='
                    }
                }
                steps {
                    script {
                        sh "npm install"

                        sh "npm run start"
                    }
                }
            }
        }
    }
}
