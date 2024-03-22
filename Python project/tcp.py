import socket
 
HOST = '127.0.0.1'  # Standard loopback interfész cím (localhost)
PORT = 65432        # Figyelt port (nem privilegizált portok > 1023)
 
# Socket létrehozása
with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as server_socket:
    server_socket.bind((HOST, PORT))
    server_socket.listen()
    print(f"A szerver figyel a következőn: {HOST}:{PORT}")
 
    while True:
        # Új kapcsolat elfogadása
        conn, addr = server_socket.accept()
        with conn:
            print(f"Kapcsolódva {addr} címről")
            while True:
                data = conn.recv(1024)
                if not data:
                    break  # Nincs több adat a klienstől, kapcsolat bontása
                print(f"Fogadott adat: {data.decode()} innen: {addr}")
                response = "Válasz a szervertől.".encode()
                conn.sendall(response)