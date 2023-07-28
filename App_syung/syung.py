import time
import sys
import pymysql
import numpy
from gpiozero import Motor
import RPi.GPIO as GPIO
import smbus
import pygame
from hx711 import HX711


# GPIO 및 무게 압력 센서 세팅
motor = Motor(forward = 21, backward = 20) 
button = 27

GPIO.setup(button,GPIO.IN,GPIO.PUD_UP)
           
hx = HX711(17, 4)
hx.set_reading_format("MSB", "MSB")
hx.set_reference_unit(106)
hx.reset()
hx.tare()

# 압력 센서 작동 코드
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

# 모든 userID 반환
def database_check():
    conn= pymysql.connect(host='192.168.35.74', user='localhost', password='', db='user_info1', charset='utf8')
    curs = conn.cursor()
    sql= "select userID from user"
    curs.execute(sql)
    rows=curs.fetchall()
    return rows

# 사용자 무게 update
def database_update(ID,z):
    conn= pymysql.connect(host='192.168.35.74', user='localhost', password='', db='codemonster', charset='utf8')
    curs = conn.cursor()
    sql = "UPDATE user_info SET userWeight = '{}'  where  userID='{}'".format(z,ID)
    curs.execute(sql)
    conn.commit()
    conn.close()

# ID값에 해당하는 사용자 몸무게 값 받아오기
def database_weight_check(ID):
    conn= pymysql.connect(host='192.168.35.74', user='localhost', password='', db='codemonster', charset='utf8')
    curs = conn.cursor()
    sql= "select userWeight from user_info where userID=%s"
    curs.execute(sql, ID)
    u = curs.fetchall()
    return ((u[0])[0])

# 무게 측정 및 반환
def weight():
    val = hx.get_weight(5)
    print(val)     
    hx.power_down()
    hx.power_up()
    return val
    
# 소리 출력
def sound(file):
    pygame.mixer.init()
    bang = pygame.mixer.Sound(file)
    bang.play()
    
while True:
    # ID 값을 살펴보다가 누군가가 사용하면 ELSE문, 사용자가 없으면 무한 반복으로 DB에 조회
    row=database_check()
    ID = row
    print(ID)
    if (ID == ()):
        continue
    else:
        ID = ((row[0])[0])
        x =database_weight_check(ID)
        print('user weight : {}'.format(x))
        # 최초 사용자일 경우
        if x == 0:
            sound("start.wav")
            time.sleep(6)
            print("Tare done! Add weight now...")
            
            max_y = 0
            num = 0
            value=0
            value2=0
            
            # 무게를 측정한다 / 5회 측정 후 가장 높은 사용자 몸무게 측정
            while True:
                
                    temp  = GPIO.input(button)
                    # 사용자가 운행을 시작했을 경우
                    if temp == 0:
                        value,value2=pressure(value,value2)
                        # 정해진 발판에 발을 올리지 않았을 경우. 정해진 발 위치로 발을 옮기라는 경고 방송
                        if(value<=200) or (value2<=100):
                            sound("error.wav")
                            time.sleep(4)
                            num = 0
                            continue
                        # 정해진 발판에 발을 올렸을 경우. 무게 측정
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
        
        # 최초 이용자가 아닐 경우
        if x != None:
            z = x
            print("first weight : {}".format(x))
            print("Tare done! Add weight now...")
            
            while True:
                    ID2 = database_check() 
                    # 사용자의 ID를 계속 조회하다가 사용자가 종료할 경우
                    if (ID2 == ()):
                        z=(z+x)/2
                        # 사용자의 현재 탑승했을 때의 최고 몸무게와 기존 DB에 있는 몸무게의 평균을 구해 DB의 몸무게 값으로 UPDATE
                        database_update(ID,z)
                        print('insert:{}'.format(z))
                        print('end')
                        motor.stop()
                        break
                    
                    temp  = GPIO.input(button)
                    # 사용자가 운행중인 경우
                    if temp == 0:
                        y=weight()
                        time.sleep(0.5)
                        
                        # 무게를 바탕으로 1인 탑승일 경우
                        if ((y<=x) or (y <= x+50)): # +50이 실제로는 10kg에 해당하는 무게의 값이다.
                            
                            # Z 값 UPDATE
                            if y > z:
                                z = y
                                print('max weight:{}'.format(z))
                            
                            motor.backward(speed=1)
                            continue
                        
                        # 2인 탑승으로 판단될 경우, 킥보드 정지 및 경고 방송
                        else:
                            motor.stop()
                            sound("beep.wav")
                            time.sleep(3)
                            continue

                    # 사용자가 운행을 종료할 경우
                    else:
                        motor.stop()
                        continue
