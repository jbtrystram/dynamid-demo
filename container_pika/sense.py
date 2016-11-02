#!/usr/bin/env python
import time
import datetime
import json
import sys
from sense_hat import SenseHat
#Proton import for AMQP
from __future__ import print_function, unicode_literals
from proton import Message
from proton.handlers import MessagingHandler
from proton.reactor import Container

class AmqpSender(MessagingHandler):
    def __init__(self, server, address, body):
        super(HelloWorld, self).__init__()
        self.server = server
        self.address = address
        self.body = body

    def on_start(self, event):
        conn = event.container.connect(self.server)
        event.container.create_receiver(conn, self.address)
        event.container.create_sender(conn, self.address)

    def on_sendable(self, event):
        event.sender.send(Message(body=self.body))
        event.sender.close()
        event.connection.close()


# get hostname
hostname = open('/etc/hostname').read()

# Initialise SenseHat
sense = SenseHat()


def sendMessage(temp):
        amqpMsgPayload = {}
        amqpMsgPayload["timestamp"] = int((time.time()*1000))
        amqpMsgPayload["id"] = hostname[:-1]
        amqpMsgPayload["value"] = temp

        print(json.dumps(amqpMsgPayload))
        Container(AmqpSender("activemq:5672", "temperature", json.dumps(amqpMsgPayload))).run()
        print 'Message sent to AMQP server'


def loop():
    red = (255, 0, 0)
    blue = (0, 0, 255)

    while True:
       temp = sense.temp
       sense.show_message("%.1f" %temp + "C", 0.07)	
       sendMessage(temp)

def destroy():
        X = [255, 0, 0]  # Red
        O = [255, 255, 255]  # Black

        cross = [
        X, O, O, O, O, O, O, X,
        O, X, O, O, O, O, X, O,
        O, O, X, O, O, X, O, O,
        O, O, O, X, X, O, O, O,
        O, O, O, X, X, O, O, O,
        O, O, X, O, O, X, O, O,
        O, X, O, O, O, O, X, O,
        X, O, O, O, O, O, O, X
        ]

        sense.set_pixels(cross)
    
        connection.close()


if __name__ == '__main__': # Program start from here
    try:
        loop()
    except KeyboardInterrupt: # When 'Ctrl+C' is pressed, the child program destroy() will be executed.
        destroy()
