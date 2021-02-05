#!/bin/sh
cd src/
rm teams/*.class
rm agents/*.class
rm common/*.class
rm *.class

javac Runner.java
javac agents/*.java
javac teams/*.java
javac common/*.java
# ここに --ignores-aiko を入れるとあいこ分を表示しない
java Runner