FROM resin/rpi-raspbian:jessie

RUN apt-get -q update && \
    apt-get -qy install \
        gcc \
        git \
        make \
        curl \
        python \
        python-pip \
        python-dev \
        python-pip \
        sense-hat
RUN curl http://plugwash.raspbian.org/jessietest/private/pool/main/q/qpid-proton/python-qpid-proton_0.10-2_armhf.deb -o /tmp/proton.deb
RUN curl https://archive.raspbian.org/raspbian/pool/main/o/openssl/libssl1.0.2_1.0.2j-1_armhf.deb -o /tmp/libssl.deb

RUN dpkg -i /tmp/libssl.deb
RUN dpkg -i /tmp/proton.deb



COPY ./sense.py ./app.py

CMD ["python", "app.py"]
