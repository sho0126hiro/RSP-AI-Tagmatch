rm teams/*.class
rm agents/*.class
rm common/*.class
rm *.class

javac Runner.java
javac agents/*.java
javac teams/*.java
javac common/*.java
java Runner