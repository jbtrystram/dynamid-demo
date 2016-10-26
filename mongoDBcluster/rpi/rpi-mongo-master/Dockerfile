FROM descol/rpi-mongo:1.6

RUN apt-get update && apt-get install -y curl iputils-ping

ADD mongosetup.sh /setup.sh
RUN chmod +x /setup.sh

ENTRYPOINT [ "bash", "/setup.sh" ]