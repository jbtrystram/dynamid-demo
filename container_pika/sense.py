#!/usr/bin/env python
import time
import pika
import datetime
import json
from sense_hat import SenseHat


#AMQP settings and connection
connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
channel = connection.channel()
channel.queue_declare(queue='temperature')

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

        channel.basic_publish(exchange='',
                          routing_key='temperature',
                       body= json.dumps(amqpMsgPayload))
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
