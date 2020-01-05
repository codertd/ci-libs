#!/usr/bin/env groovy

// Requires that you are calling from inside of a script{} block, or thse wont execute properly.

def call() {

    // Setup Docker
    println "Configuring Docker environment"
    def dockerHome = tool 'docker' // This tool is setup via Jenkins server config.
    env.PATH = "${dockerHome}:${env.PATH}"

    // Make sure the source code is available so we can Dockerize.
    // checkout scm

    def execute_state=sh(returnStdout: true, script: 'ls -alh')
    println "$execute_state"

}
