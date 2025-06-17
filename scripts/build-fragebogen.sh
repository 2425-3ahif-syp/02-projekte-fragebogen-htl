#!/usr/bin/env bash

set -e 

cd frontend
mvn clean package
rm -rf ./target/original-htl-fragebogen-application-1.0-SNAPSHOT.jar
mv ./target/htl-fragebogen-application-1.0-SNAPSHOT.jar ./target/htl-fragebogen.jar


