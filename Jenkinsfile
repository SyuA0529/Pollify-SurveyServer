def DOCKER_HOST = "syua0529"
def DOCKER_IMAGE_NAME = "surveyserver"
def NAMESPACE = "survey-service"
def VERSION = "${env.BUILD_NUMBER}"

podTemplate(label: 'builder',
            containers: [
                containerTemplate(name: 'gradle', image: 'gradle:7.6.1-jdk17', command: 'cat', ttyEnabled: true),
                containerTemplate(name: 'docker', image: 'docker', command: 'cat', ttyEnabled: true),
                containerTemplate(name: 'kubectl', image: 'lachlanevenson/k8s-kubectl:v1.23.0', command: 'cat', ttyEnabled: true)
            ],
            volumes: [
                hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
            ]) {
    node('builder') {
        // clone proejct
        stage('Checkout') {
            checkout scm
        }

        // test project using gradle
        stage('Gradle Test and Build') {
            container('gradle') {
                sh "gradle clean build"
            }
        }

        // build docker image and push it to docker hub
        stage('Docker Build') {
            container('docker') {
                withCredentials([usernamePassword(
                    credentialsId: 'docker_hub_auth',
                    usernameVariable: 'USERNAME',
                    passwordVariable: 'PASSWORD'
                )]) {
                    sh "docker build -t ${DOCKER_HOST}/${DOCKER_IMAGE_NAME}:${VERSION} ."
                    sh "docker login -u ${USERNAME} -p ${PASSWORD}"
                    sh "docker push ${DOCKER_HOST}/${DOCKER_IMAGE_NAME}:${VERSION}"
                }
            }
        }

        // deploy project to kubernetes
        stage('Deploy') {
            container('kubectl') {
                withCredentials([usernamePassword(
                    credentialsId: 'docker_hub_auth',
                    usernameVariable: 'USERNAME',
                    passwordVariable: 'PASSWORD'
                )]) {
                    sh "kubectl get ns ${NAMESPACE}|| kubectl create ns ${NAMESPACE}"
                    sh """
                        sed -i "\$(grep -n 'image:' ./k8s/deployment.yaml | grep -Eo '^[^:]+')s/${DOCKER_IMAGE_NAME}/${DOCKER_IMAGE_NAME}:${VERSION}/g" ./k8s/deployment.yaml
                    """
                    sh "cat ./k8s/deployment.yaml"
                    sh "kubectl apply -f ./k8s/deployment.yaml -n ${NAMESPACE}"
                    sh "kubectl apply -f ./k8s/service.yaml -n ${NAMESPACE}"
                }
            }
        }
    }
}
