#!/usr/bin/env bash

set -e 

cd frontend-evaluation-app


mvn clean package
rm -rf ./target/original-frontend-evaluation-app-1.0-SNAPSHOT.jar
mv ./target/frontend-evaluation-app-1.0-SNAPSHOT.jar ./target/fragebogen-evaluation.jar


