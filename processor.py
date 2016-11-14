from sense_hat import SenseHat
sense = SenseHat()
t=1000
color=[76, 153, 0]
while(True):
    for i in range(t):
        sense.set_pixel(0, 0,color)
    for i in range(t):
        sense.set_pixel(0, 0, color)
        sense.set_pixel(1, 0, color)
    for i in range(t):
        sense.set_pixel(0, 0, color)
        sense.set_pixel(1, 0, color)
        sense.set_pixel(2, 0, color)
    for i in range(t):
        sense.set_pixel(0, 0, color)
        sense.set_pixel(1, 0, color)
        sense.set_pixel(2, 0, color)
        sense.set_pixel(3, 0, color)
