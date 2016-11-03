#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt
rm /usr/bin/java && rm /usr/bin/javac 
ln -s /usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt/bin/java /usr/bin/java 
ln -s /usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt/bin/javac /usr/bin/javac

cd /demo/
sleep 15
java -jar /demo/fatjars/demo-dynamid-Receiver-fat.jar -cluster -cluster-host 10.45.0.52
