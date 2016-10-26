FROM mongo:3.0

RUN apt-get update && apt-get install -y curl

ADD mongosetup.sh /setup.sh
RUN chmod +x /setup.sh

ENTRYPOINT [ "bash", "/setup.sh" ]
