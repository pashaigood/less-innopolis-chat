package less.android.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {
    private ArrayList<ConnectedClient> clients = new ArrayList<>();

    public ChatServer() {
        ServerSocket server = null;
        try {
            server = new ServerSocket(7777);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {
                addClient(server.accept());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastMessage(ConnectedClient emitter, String message) {
        String namedMessage = String.format("%s: %s", emitter.getName(), message);
        for (ConnectedClient client: clients) {
            if (! client.equals(emitter)) {
                try {
                    client.sendMessage(namedMessage);
                } catch (IOException e) {
                    System.err.println(String.format("Cannot send message to %s", client.getName()));
                }
            }
        }
    }

    private void addClient(Socket clientSocked) {
        System.out.println("Log: client is connected.");
        ChatServer server = this;
        ConnectedClient client = new ConnectedClient(clientSocked, server);
        clients.add(client);
        client.start();
    }

    public void removeClient(ConnectedClient client) {
        System.out.println(String.format("Log: client \"%s\" is disconnected.", client.getName()));
        synchronized (this) {
            clients.remove(client);
        }
        broadcastMessage(client, "is disconnected.");
    }
}


