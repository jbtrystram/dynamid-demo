#!/usr/bin/env python
import time
import datetime
import json
import sys
from sense_hat import SenseHat

# for AMQP connection
from proton import *
from proton.handlers import *
from proton.reactor import *


# get hostname
hostname = open('/etc/hostname').read()

# Initialise SenseHat
sense = SenseHat()


#AMQP settings and connection
class Sender(MessagingHandler):
    def __init__(self, server, address, message_body):
        super(Sender, self).__init__()

	self.server = server
        self.address = address
        self.message_body = message_body

        self.sent = False

    def on_start(self, event):
        conn = event.container.connect(self.server)
        event.container.create_sender(self.address)
        print("SENDER: Created sender for target address '{0}'".format(self.address))

    def on_sendable(self, event):
        if self.sent:
            return

        message = Message(self.message_body)
        event.sender.send(message)

        print("SENDER: Sent message '{0}'".format(self.message_body))

        event.connection.close()
        self.sent = True



def sendMessage(temp):
	amqpMsgPayload = {}
	amqpMsgPayload["timestamp"] = int((time.time()*1000))
	amqpMsgPayload["id"] = hostname[:-1]
	amqpMsgPayload["value"] = temp

	print(json.dumps(amqpMsgPayload))

        handler = Sender('rabbit', 'temperature', json.dumps(amqpMsgPayload))
        container = Container(handler)
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
