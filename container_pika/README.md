To launch rabbit container:

```
docker run -d -e RABBITMQ_NODENAME=rabbit --name rabbit -p 15672:15672 \
    -p 5672:5672 -v /rabbit/data/log:/data/log \
    -v /rabbit/data/mnesia:/data/mnesia ronnyroos/rpi-rabbitmq
```

To launch container with the test switch code and the 
rights on the GPIO and linked to a rabbit container:

```
docker run -ti --privileged --link rabbit:rabbit test_switch
```