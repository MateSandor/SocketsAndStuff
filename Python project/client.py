import socket
 
HOST = '127.0.0.1'  # A szerver hosztneve vagy IP címe
PORT = 65432        # A szerver által használt port
 
# Socket létrehozása
with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.connect((HOST, PORT))
    print("Kapcsolódva a szerverhez")
 
    # Adat küldése
    message = 'Helló, szerver'.encode()
    s.sendall(message)
    print("Üzenet elküldve a szervernek")
 
    # Várakozás válaszra
    data = s.recv(1024)
    print("Válasz fogadva a szervertől")
 
print(f"Fogadott adat: {data.decode()}")