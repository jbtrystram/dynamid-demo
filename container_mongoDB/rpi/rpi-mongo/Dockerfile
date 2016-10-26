FROM resin/rpi-raspbian:jessie

RUN mkdir -p /mongodb/data && mkdir -p /mongodb/conf
RUN apt-get update && apt-get install mongodb-server

VOLUME /mongodb
EXPOSE 27017 28017
COPY run.sh /
ENTRYPOINT ["/run.sh"]
CMD ["mongod", "--replSet", "rs", "--smallfiles", "--rest","--journal","--dbpath", "/mongodb/data"]