## Setup MongoDB for Raspberry PI
 
 ### MongoDB cluster

  To run it on the Raspberrys, create a file `nodes` with the list of 
   hosts in the cluster, this file will be copied in `/etc/hosts` file 
   of each container, then :
  ```
     # Run on each host : 
     docker run  -d --name mongo -p 27017:27017 -p 28017:28017 -v $PWD/nodes:/nodes -v mongodb:/mongodb descol/rpi-mongo:1.6 
     
     # Then on only one host
     docker run -v $PWD/nodes:/nodes --rm descol/rpi-mongo:master1.6
 ```
