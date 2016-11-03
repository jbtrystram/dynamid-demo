#!/usr/bin/python3

import threading
import subprocess
from subprocess import STDOUT
import os
import time
import queue
from sense_hat import SenseHat

############### S E T T I N G S #############

# Dependancies you want to install with apt-get
deps = ["update", "-y install git", "-y install curl", "-y install oracle-java8-jdk"]

# Arbitrary commands you need to run
commands = ["curl -sSL get.docker.com | sh", "git clone https://github.com/jbtrystram/dynamid-demo.git /demo", 
"usermod -aG docker pi", "export JAVA_HOME=/usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt", "rm /usr/bin/java", "rm /usr/bin/javac", "ln -s /usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt/bin/java /usr/bin/java", "ln -s /usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt/bin/javac /usr/bin/javac"]


# Docker commands 
dockers = ["pull descol/rpi_pika",
"pull ronnyroos/rpi-rabbitmq", 
"pull descol/rpi-mongo:1.6",
"pull descol/rpi-mongo:master1.6",
"stop $(docker ps -a -q)", "rm -v -f $(docker ps -a -q)",
"run -d --name activemq -p 5672:5672 -p 8161:8161 jbtrystram/activemq",
"run  -d --name mongo --restart always -p 27017:27017 -p 28017:28017 -v /demo/container_mongoDB/rpi/nodes:/nodes descol/rpi-mongo:1.6", "run --rm descol/rpi-mongo:1.6 /bin/sleep 5",
"run  --rm --name mongoConfig --link=mongo:mongo  -v /demo/container_mongoDB/rpi/nodes:/nodes descol/rpi-mongo:master1.6"
]


######################################################################

#declare a queue for interthread communication
q = queue.Queue()
threads = []

#sense hat init 
sense = SenseHat()

#get hostname
hostname = open('/etc/hostname').read()

def pixel_green_done():
    green = (0, 255, 0)
    black = [0, 0, 0]
    pixels = [green for j in range(64)]
    sense.set_pixels(pixels)
    time.sleep(0.5)
    pixels = [black for j in range(64)]


def color_pixel(px_g, px_r):
    red = [255, 0, 0]  
    black = [0, 0, 0] 
    green = (0, 255, 0)
    white = (255, 255, 255)

    pixels = [green if j < px_g else black for j in range(64)]
    for k in range(px_r):
        pixels[px_g+k] = red

    sense.set_pixels(pixels)


def install_dependencies(): 
    i=2
    q.put(i)

    # Install dependencies with apt-get
    for dep in deps:
        print("Installing "+ dep +"...")
        os.system('apt-get '+dep)
        print("[OK]")
        i = i+2
        q.put(i)

    # Run arbitrary commands
    for cmd in commands:
        print("Runnig " +cmd+ "...")
        os.system(cmd)
        print("[OK]")
        i = i+2
        q.put(i)


    #cd into the git repo and pull for last files
    os.chdir("/demo")
    os.system("git pull")
    i = i+2
    q.put(i)

    # Run docker tasks
    for dock in dockers:
        print("Runnig docker " +dock+ "...")
        os.system("docker "+dock)
        i = i+2
        q.put(i)

   
    #Run Vert.X verticles


    # Finally, launch the app
    # "docker run -d --restart always --name pika --privileged --hostname `hostname` --link activemq:activemq descol/rpi_pika"
    #i = i+2
    #q.put(i)


		

def visual_feedback():
    while True:
        item = q.get()
        if item is None:
            q.task_done()
            break
        else :
            while (q.empty()):
                color_pixel(item, 2)
                time.sleep(0.5)
                color_pixel(item, 0)
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
