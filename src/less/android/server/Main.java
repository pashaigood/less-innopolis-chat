package less.android.server;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Log: server is started");

            Thread serverThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    new ChatServer();
                }
            });
            serverThread.start();
            Thread.sleep(3000);
            new Bot("Harry");
//            new Bot("Merry");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
