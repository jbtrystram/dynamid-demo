FROM descol/rpi-mongo:1.7

RUN apt-get update && \
    apt-get install -y curl \
    iputils-ping

COPY mongosetup.sh /setup.sh
RUN chmod +x /setup.sh

ENTRYPOINT [ "bash", "/setup.sh" ]