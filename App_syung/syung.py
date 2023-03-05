import time
import sys
import pymysql
import numpy
from gpiozero import Motor
import RPi.GPIO as GPIO
import smbus
import pygame
from hx711 import HX711

motor = Motor(forward = 21, backward = 20) 
button = 27

GPIO.setup(button,GPIO.IN,GPIO.PUD_UP)
           
hx = HX711(17, 4)
hx.set_reading_format("MSB", "MSB")
hx.set_reference_unit(106)
hx.reset()
hx.tare()

def pressure(value,value2):
    address = 0x48
    A0 = 0x43
    A1 = 0x40
    bus = smbus.SMBus(1)
    bus1 = smbus.SMBus(1)
    
    bus.write_byte(address,A0)
    value = bus.read_byte(address)
    
    bus1.write_byte(address,A1)
    value2 = bus1.read_byte(address)
    return(value,value2)

def database_check():
    conn= pymysql.connect(host='192.168.35.74', user='localhost', password='', db='user_info1', charset='utf8')
    curs = conn.cursor()
    sql= "select userID from user"
    curs.execute(sql)
    rows=curs.fetchall()
    return rows

def database_update(ID,z):
    conn= pymysql.connect(host='192.168.35.74', user='localhost', password='', db='codemonster', charset='utf8')
    curs = conn.cursor()
    sql = "UPDATE user_info SET userWeight = '{}'  where  userID='{}'".format(z,ID)
    curs.execute(sql)
    conn.commit()
    conn.close()

def database_weight_check(ID):
    conn= pymysql.connect(host='192.168.35.74', user='localhost', password='', db='codemonster', charset='utf8')
    curs = conn.cursor()
    sql= "select userWeight from user_info where userID=%s"
    curs.execute(sql, ID)
    u = curs.fetchall()
    return ((u[0])[0])

def weight():
    val = hx.get_weight(5)
    print(val)     
    hx.power_down()
    hx.power_up()
    return val
    
def sound(file):
    pygame.mixer.init()
    bang = pygame.mixer.Sound(file)
    bang.play()
    
while True:
    row=database_check()
    ID = row
    print(ID)
    if (ID == ()):
        continue
    else:
        ID = ((row[0])[0])
        x =database_weight_check(ID)
        print('user weight : {}'.format(x))
        
        if x == 0:
            sound("start.wav")
            time.sleep(6)
            print("Tare done! Add weight now...")
            
            max_y = 0
            num = 0
            value=0
            value2=0
            
            while True:
                
                    temp  = GPIO.input(button)
                    if temp == 0:
                        value,value2=pressure(value,value2)
                       
                        if(value<=200) or (value2<=100):
                            sound("error.wav")
                            time.sleep(4)
                            num = 0
                            continue
                        
                        else:
                            
                            y=weight()
                        
                            if (y>max_y):
                                max_y = y
                            num += 1
                            time.sleep(1)
                        
                        if num == 5 :
                            sound("finish.wav")
                            time.sleep(5)
                            break
                        continue
                    else :
                        num = 0
                  
            x = max_y
            
        if x != None:
            z = x
            print("first weight : {}".format(x))
            print("Tare done! Add weight now...")
            
            while True:
                    ID2 = database_check()
                    if (ID2 == ()):
                        z=(z+x)/2
                        database_update(ID,z)
                        print('insert:{}'.format(z))
                        print('end')
                        motor.stop()
                        break
                    
                    temp  = GPIO.input(button)
                    if temp == 0:
                        y=weight()
                        time.sleep(0.5)
                        
                        if ((y<=x) or (y <= x+50)): # +50이 실제로는 10kg에 해당하는 무게의 값이다.
                            
                            if y > z:
                                z = y
                                print('max weight:{}'.format(z))
                            
                            motor.backward(speed=1)
                            continue
                            
                        else:
                            motor.stop()
                            sound("beep.wav")
                            time.sleep(3)
                            continue
                        
                    else:
                        motor.stop()
                        continue