package less.android.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class ConnectedClient extends Thread {
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private ChatServer server;

    public ConnectedClient(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            startListen();
        } catch (IOException e) {
            server.removeClient(this);
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startListen () throws IOException {
        while (! socket.isClosed()) {
            String data[] = inputStream.readUTF().split(":", 2);
            String command = data[0];
            String message = data[1];

            switch (command) {
                case "hello": {
                    setName(message);
                    onConnect();
                    break;
                }
                case "message": {
                    server.broadcastMessage(this, message);
                    break;
                }
            }
        }
    }

    synchronized public void sendMessage(String message) throws IOException {
        outputStream.writeUTF(message);
        outputStream.flush();
    }

    private void onConnect() {
        System.out.println("Log: " + getName() + " is registered.");
        server.broadcastMessage(this, "connected.");
    }
}
