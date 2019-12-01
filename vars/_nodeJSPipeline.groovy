/* Node Js pipeline for building node apps. */

def call(body) {

    def pipelineParams = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipeline {
        agent none
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
                        def dockerHome = tool 'myDocker'
                        env.PATH = "${dockerHome}/bin:${env.PATH}"
                    }
                }
            }
            stage('Setup') {
                agent {
                    docker {
                        image "alpine/git"
                    }
                }
                steps {
                    script {
                        println "Setting up!"
                    }
                }
            }
        }
    }
}