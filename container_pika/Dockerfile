FROM resin/rpi-raspbian:jessie

RUN apt-get -q update && \
    apt-get -qy install \
        gcc \
        git \
        make \
        python \
        python-pip \
        python-dev \
        python-pip \
	python-qpid-proton \
        python-qpid-proton-docs \
        sense-hat

COPY ./sense.py ./app.py

CMD ["python", "app.py"]
