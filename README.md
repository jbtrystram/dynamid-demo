# Dynamid Demo

A simple demo built around [Vert.x](http://vertx.io/) framework to showcase the usages and goals of modern middleware.

## Architecture overview

![Architecture overview](./overviewdiagram.png)


### Distributed architecture

The aim of this setup is to showcase a fully distributed monitoring system. Every device (Raspberry-pi for example) hosts one component :
 - 1 temperature sensor
 - 1 AMQP broker
 - 1 mongoDB instance
 - 1 AMQP Vert.x connector
 
 the others verticles are distributed among devices using the Vert.x built-in clustering function.
 
 
 ## Setup
 
 ### MongoDB cluster
  To run the cluster locally (for testing), simply run 
  
  ```   
     cd mongoDBcluster
     docker-compose up 
  ```
  
  To run it on the hosts, set up DNS or edit the `/etc/hosts` file, then :
  ```
     # Run on each host : 
     docker run  -p 27017:27017 -p 28017:28017 mongo:3.0 /usr/bin/mongod --replSet rs --smallfiles --rest --httpinterface
     
     # Then on only one host
     cd mongoDBcluster/setup/
     nano mongosetup.sh #change the hostnames or write down IPs
     docker build -t mongo-setup .
     docker run --rm mongo-setup 
 ```