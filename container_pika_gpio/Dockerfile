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
        python-pika \
        sense-hat
RUN pip install rpi.gpio