#!/usr/bin/env bash

main() {
    build
    deploy
    echo "Done."
}

build() {
    ( cd backend && mvn clean package )
    ( cd webapp && npm install && npm run-script quick-war )
}

deploy() {
    sudo cp webapp/ROOT.war /var/lib/tomcat8/webapps
    sudo cp backend/card-collection/target/backend.war /var/lib/tomcat8/webapps
}

main
