package less.android;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private String name;
    private  Socket client;

    public Client(String name) {
        this.name = name;

        try {
            client = new Socket("127.0.0.1", 7777);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Lets start the chat:");

        Thread senderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                startSender();
            }
        });

        Thread receiverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                startReceiver();
            }
        });

        senderThread.start();
        receiverThread.start();
    }

    private void startSender() {
        boolean isFinish = false;
        try (
                DataOutputStream sender = new DataOutputStream(client.getOutputStream());
                Scanner scanner = new Scanner(System.in);
        ) {
            while (! isFinish) {
                String command = scanner.nextLine();
                switch (command) {
                    case "exit": {
                        isFinish = true;
                        break;
                    }
                    default: {
                        sender.writeUTF(command);
                        sender.flush();
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }

    private void startReceiver() {
        try (
                DataInputStream receiver = new DataInputStream(client.getInputStream());
        ) {
            while (true) {
                System.out.println(receiver.readUTF());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
