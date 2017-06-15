package less.android;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    public static void main(String[] args) {
        Bot chatBot = new Bot("Harry");
        System.out.println("Log: server is started");

        try (
                ServerSocket server = new ServerSocket(7777);
                Socket socket = server.accept();
                DataInputStream serverInput = new DataInputStream(socket.getInputStream());
                DataOutputStream serverOutput = new DataOutputStream(socket.getOutputStream());
        ) {
            System.out.println("Log: server start listening");
            while (true) {
                System.out.println("Log: wait a command");
                String command = serverInput.readUTF();
                System.out.println("Log: " + command);
                switch (command) {
                    default: {
                        serverOutput.writeUTF(chatBot.answerTo(command));
                        serverOutput.flush();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
}



class Bot {
    private String name;

    public Bot(String name) {
        this.name = name;
    }

    public String answerTo(String phrase) {
        StringBuilder answer = new StringBuilder().append(name).append(": ");

        switch (phrase) {
            case "hello": {
                answer.append("Hello to you!");
                break;
            }
            case "how a u": {
                answer.append("im fine, how a u?");
                break;
            }
            default: {
                answer.append("i dont know what to sey....");
            }
        }

        return answer.toString();
    }
}
