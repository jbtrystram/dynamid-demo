from sense_hat import SenseHat
sense = SenseHat()
t=1000
color=[0,100,200]
while(True):
    for i in range(t):
        sense.set_pixel(7, 0, color)
    for i in range(t):
        sense.set_pixel(7, 0, color)
        sense.set_pixel(6, 0, color)
    for i in range(t):
        sense.set_pixel(7, 0, color)
        sense.set_pixel(6, 0, color)
        sense.set_pixel(5, 0, color)
    for i in range(t):
        sense.set_pixel(7, 0, color)
        sense.set_pixel(6, 0, color)
        sense.set_pixel(5, 0, color)
        sense.set_pixel(4, 0, color)
