#!/usr/bin/env groovy

def call() {

    // Setup Docker
    println "Configuring Docker environment"
    def dockerHome = tool 'docker' // This tool is setup via Jenkins server config.
    env.PATH = "${dockerHome}:${env.PATH}"

}
