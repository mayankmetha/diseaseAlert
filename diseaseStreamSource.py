import socket
import time
from random import uniform

host = 'localhost'
port = 9998

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((host, port))
s.listen(1)

try:
    conn, addr = s.accept()
    try:
        while True:
            conn.send(bytes("{0:.2f},{1:.2f}\n".format(uniform(-180,180),uniform(-90,90)), "utf-8"))
            time.sleep(uniform(0,2))
    except:
        conn.close()
except KeyboardInterrupt:
    s.close()