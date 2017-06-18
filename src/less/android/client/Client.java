package less.android.client;

import com.sun.istack.internal.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    protected String name;
    private Socket server;
    private DataInputStream receiver;
    private DataOutputStream sender;
    private Scanner scanner;
    private boolean isConnecting = false;
    volatile private boolean isWorking = true;

    public Client(String name) {
        this.name = name;
        connect();
        startScanner();
    }

    private void connect() {
        if (! isWorking || isConnecting) {
            return;
        }
        isConnecting = true;
        System.out.println("Connecting...");
        if (server != null) {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        while (isWorking) {
            try {
                server = new Socket("127.0.0.1", 7777);
                break;
            } catch (ConnectException e) {
                System.out.println("Can't connect to server.");
                System.out.println("Try to reconnect in 3 second.");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        try {
            startSender();
            startReceiver();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Connected.");
        isConnecting = false;
    }

    private void startScanner() {
        scanner = new Scanner(System.in);

        Thread senderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Lets start the chat:");
                try {
                    while (isWorking) {
                        String command = scanner.nextLine();
                        System.out.print("\n");
                        switch (command) {
                            case "exit": {
                                finish();
                                break;
                            }
                            default: {
                                say(command);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        senderThread.start();
    }

    private void startSender() throws IOException {
        if (sender != null) {
            sender.close();
        }
        sender = new DataOutputStream(server.getOutputStream());
        sayHello();
    }

    private void startReceiver() throws IOException {
        if (receiver != null) {
            receiver.close();
        }
        receiver = new DataInputStream(server.getInputStream());

        Thread receiverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isWorking) {
                    try {
                        String message = receiver.readUTF();
                        onReceive(message);
                    } catch (IOException e) {
                        connect();
                        return;
                    }
                }
            }
        });
        receiverThread.start();
    }

    private void finish() {
        isWorking = false;
        scanner.close();
        try {
            server.close();
        } catch (IOException e) {}
    }

    private void sayHello() throws IOException {
        send("hello", name);
    }

    protected void say(String message) throws IOException {
        send("message", message);
    }

    private void send(String command, String message) throws IOException {
        sender.writeUTF(String.format("%s:%s", command, message));
        sender.flush();
    }

    protected void onReceive(String message) {
        System.out.println(message);
    }
}
