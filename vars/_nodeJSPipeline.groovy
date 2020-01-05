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
                        withDockerRegistry([ credentialsId: "dockerhub", url: ""]) {

                            def customImage = docker.build(env.dockerImageFull)

                            // Push image up, and tag with latest if master.
                            customImage.push()

                            // Tag with latest if on master branch.
                            if (env.BRANCH_NAME == 'master') {
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
