#!/usr/bin/python3

import threading
import subprocess
from subprocess import STDOUT
import os
import time
import queue
from sense_hat import SenseHat

############### S E T T I N G S #############

# Dependancies you want to install with apt-get install
deps = ["docker", "maven", "git"]

# Arbitrary commands you need to run
commands = ["git pull https://github.com/jbtrystram/dynamid-demo.git", "cd dynamid-demo"]

# Docker commands 
dockers = ["build -t sensor-app container-pika/.",
"pull ronnyroos/rpi-rabbitmq", 
"pull descol/rpi-mongo:1.6",
"pull descol/rpi-mongo:master1.6",
"run -d -e RABBITMQ_NODENAME=rabbit --name rabbit -p 15672:15672 -p 5672:5672 -v /rabbit/data/log:/data/log -v /rabbit/data/mnesia:/data/mnesia ronnyroos/rpi-rabbitmq",
"run  -d --name mongo -p 27017:27017 -p 28017:28017 -v $PWD/nodes:/nodes -v mongodb:/mongodb descol/rpi-mongo:1.6" 
"run  -p 27017:27017 -p 28017:28017 -v $PWD/nodes:/nodes --rm descol/rpi-mongo:master1.6"]


######################################################################

#declare a queue for interthread communication
q = queue.Queue()
threads = []

#sense hat init 
sense = SenseHat()

def pixel_green_done():
    green = (0, 255, 0)
    black = [0, 0, 0]
    pixels = [green for j in range(64)]
    sense.set_pixels(pixels)
    time.sleep(0.5)
    pixels = [black for j in range(64)]
    time.sleep(0.2)
    pixels = [green for j in range(64)]



def color_pixel(i):
    red = [255, 0, 0]  
    black = [0, 0, 0] 
    green = (0, 255, 0)
    white = (255, 255, 255)

    pixels = [red if j < i else black for j in range(64)]
    sense.set_pixels(pixels)


def install_dependencies(): 
    i=0
    q.put(i)

    # Install dependencies with apt-get
    for dep in deps:
        print("Installing "+ dep +"...")
        os.system('apt-get install -y '+dep)
        print("[OK]")
        i = i+3
        q.put(i)

    # Run arbitrary commands
    for cmd in commands:
        print("Runnig " +cmd+ "...")
        os.system(cmd)
        print("[OK]")
        i = i+2
        q.put(i)

     # Run docker tasks
     for dock in dockers:
        os.system("docker "+cmd)
        i = i+2
        q.put(i)


    # Finally, launch the app
    # docker run --privileged --link rabbit:rabbit sensor-app



		

def visual_feedback():
    while True:
        item = q.get()
        if item is None:
            q.task_done()
            break
        else :
            while (q.empty()):
                color_pixel(item)
                time.sleep(0.5)
                color_pixel(item-1)
                time.sleep(0.5)
            q.task_done()
	
	

# Program start from here
if __name__ == '__main__': 


    #threads settings
    q = queue.Queue()
    threads = []

    dependencies_thread = threading.Thread(target=install_dependencies)
    visual_thread = threading.Thread(target=visual_feedback)
    
    visual_thread.start()
    print("Started visualisation thread")
    dependencies_thread.start()
    print("Started installation thread")
    threads.append(visual_thread)
    threads.append(dependencies_thread)

    dependencies_thread.join()
    print("All dependencies are installed")
    q.put(None)
    q.join()
    	
    # stop visual_thread
    for t in threads:
        t.join()
    print("Finished")
    pixel_green_done()
