#!/bin/sh
find . -name "*.class" | xargs rm

javac Runner.java
javac agents/*.java
javac teams/*.java
javac common/*.java
java Runner "$@"
