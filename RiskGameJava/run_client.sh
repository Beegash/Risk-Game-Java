#!/bin/bash
cd "$(dirname "$0")"
mvn exec:java -Dexec.mainClass="client.ClientMain" 
