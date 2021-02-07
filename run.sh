#!/bin/sh
rm teams/*.class
rm agents/*.class
rm common/*.class
rm *.class

javac Runner.java
javac agents/*.java
javac teams/*.java
javac common/*.java
# ここに --ignores-aiko を入れるとあいこ分を表示しない
# ここに --ignores-logs を入れると途中経過を表示しない
# ここに --result を入れるとリザルトを表示
java Runner "$@"