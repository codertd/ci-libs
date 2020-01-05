#!/usr/bin/env groovy

// Requires that you are calling from inside of a script{} block, or thse wont execute properly.

def call() {

    // get the repo name, its not automatically available from multibranch jenkins plugin.
    env.REPO_NAME = scm.getUserRemoteConfigs()[0].getUrl().tokenize('/').last().split("\\.")[0]

    // Setup Docker tool and credentials for docker hub
    println "Configuring Docker environment"

    // Docker account could be passed in via the pipeline params or Jenkinsfile.
    env.DOCKER_ACCOUNT = 'codertd'
    env.registry = "codertd/${env.REPO_NAME}"
    env.registryCredentials = 'dockerhub'
    env.dockerServer = "https://hub.docker.com/"

    // What the image is that will be pushed to dockerhub
    env.dockerImageFull = "${env.DOCKER_ACCOUNT}/${env.REPO_NAME}:${env.GIT_COMMIT}"

    def dockerHome = tool 'docker' // This tool is setup via Jenkins server config.
    env.PATH = "${dockerHome}:${env.PATH}"

    // See our ENV available to be used
    sh 'printenv'

    // See our source code directory.
    def execute_ls=sh(returnStdout: true, script: 'ls -alh')
    println "$execute_ls"
}
