#!/usr/bin/env bash

tomcat_password="$1"

main() {
    build
    deploy_backend
    deploy_webapp
    echo "Done."
}

build() {
    ( cd backend && mvn clean package )
    ( cd webapp && npm install && npm run-script quick-war )
}

deploy_backend() {
    echo "Deploying Backend..."
    tomcat_deploy "backend/card-collection/target/backend.war" "/backend"
}

deploy_webapp() {
    echo "Deploying Webapp..."
    tomcat_deploy "webapp/ROOT.war" ""
}

tomcat_deploy() {
    echo 'got' $1 $2
    # Upload to tomcat manager and check whether output contains "OK - Deployed application"
    curl -v -XPUT --data-binary "$1" "http://tomcat:$tomcat_password@localhost:8080/manager/text/deploy?update=true&path=$2" \
        | awk 'BEGIN { e = 1 } /OK - Deployed application/ { e = 0 } { print } END { exit e }'
    echo
}

main
