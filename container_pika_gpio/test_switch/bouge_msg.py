#!/usr/bin/env python
import RPi.GPIO as GPIO
import time
import pika
import datetime

VibratePin = 11
Gpin = 12
Rpin = 13
tmp = 0

connection = pika.BlockingConnection(pika.ConnectionParameters(
        host='rabbit'))
channel = connection.channel()

channel.queue_declare(queue='switch_state')



def setup():
    GPIO.setmode(GPIO.BOARD) # Numbers GPIOs by physical location
    GPIO.setup(Gpin, GPIO.OUT) # Set Green Led Pin mode to output
    GPIO.setup(Rpin, GPIO.OUT) # Set Red Led Pin mode to output
    GPIO.setup(VibratePin, GPIO.IN, pull_up_down=GPIO.PUD_UP) # Set BtnPin's mode is input, and pull up to high level(3.3V)
def Led(x):
    if x == 0:
        GPIO.output(Rpin, 1)
        GPIO.output(Gpin, 0)
    if x == 1:
        GPIO.output(Rpin, 0)
        GPIO.output(Gpin, 1)

def sendMessage(x):
    if x:
        channel.basic_publish(exchange='',
                          routing_key='switch_state',
                          body='The door is open at ' + datetime.datetime.strftime(datetime.datetime.now(), '%Y-%m-%d %H:%M:%S'))
        print ' [ON] Message sent to Rabbitmq'
    else:
        channel.basic_publish(exchange='',
                          routing_key='switch_state',
                          body='The door is closed at ' + datetime.datetime.strftime(datetime.datetime.now(), '%Y-%m-%d %H:%M:%S'))
        print ' [OFF] Message sent to Rabbitmq'
def loop():
    state = 0
    entree= 0
    while True:
        entree = GPIO.input(VibratePin)
        # print entree
        if entree:
            state = state + 1
        if state > 1:
            state = 0
        Led(state)
        sendMessage(entree)
        time.sleep(0.1)
def destroy():
    GPIO.output(Gpin, GPIO.HIGH) # Green led off
    GPIO.output(Rpin, GPIO.HIGH) # Red led off
    GPIO.cleanup() # Release resource
    connection.close()
if __name__ == '__main__': # Program start from here
    setup()
    try:
        loop()
    except KeyboardInterrupt: # When 'Ctrl+C' is pressed, the child program destroy() will be executed.
        destroy()
