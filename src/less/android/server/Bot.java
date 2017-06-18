package less.android.server;

import less.android.client.Client;

import java.io.IOException;

class Bot extends Client {
    public Bot(String name) {
        super(name);
    }

    @Override
    protected void onReceive(String message) {
        try {
            String[] data = message.split(":", 2);
            String from = data[0];
            String phrase = data[1].trim();
            String answer = answerTo(from, phrase);
            if (answer != null) {
                say(answer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String answerTo(String from, String phrase) {
        boolean isForMe = phrase.contains(name);

        switch (phrase) {
            case "hello": {
                return String.format("%s, hello to you! ", from);
            }
            case "how a u": {
                return String.format("im fine, %s, and how a u?", from);
            }
            default: {
                if (isForMe) {
                    return String.format("%s i don't know what to say...", from);
                }
            }
        }

        return null;
    }
}
