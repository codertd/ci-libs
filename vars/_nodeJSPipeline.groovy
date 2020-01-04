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
                        def dockerHome = tool 'docker' // This tool is setup via Jenkins server config.
                        env.PATH = "${dockerHome}:${env.PATH}"
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
            stage('Unit Tests') {
                agent {
                    docker {
                        image "node"
                        args '--entrypoint='
                    }
                }
                steps {
                    script {
                        sh "npm install"

                        sh "npm run testAll"
                    }
                }
            }
        }
    }
}
