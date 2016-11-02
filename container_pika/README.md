To launch activeMQ broker container:

```
docker run --name activemq -it --rm -P \
jbtrystram/activemq:latest
```

To launch container with the sensehat code and the 
rights on the GPIO and linked to a rabbit container:

```
docker run -ti --privileged --link activemq:activemq descol/rpi_pika
```
