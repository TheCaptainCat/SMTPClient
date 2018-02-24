package pop3.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Observable;

public class Receiver extends Observable implements Runnable {

    private boolean run;
    private Socket socket;

    public Receiver(Socket socket) {
        this.run = true;
        this.socket = socket;
    }

    public synchronized void stop() {
        this.run = false;
        notify();
    }

    @Override
    public void run() {
        try {
            while (this.run) {
                BufferedReader buf = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String input = buf.readLine();

                if (input != null) {
                    setChanged();
                    notifyObservers(input);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
