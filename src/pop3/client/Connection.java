package pop3.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Une connexion permet de faire une requête vers le serveur et attend la réponse.
 */
public class Connection extends Observable implements Runnable {

    private Queue<String> strings;
    private String address;
    private int port;
    private volatile boolean run;

    public Connection(String address, int port) {
        this.address = address;
        this.port = port;
        this.strings = new ConcurrentLinkedQueue<>();
        this.run = true;
    }

    @Override
    public synchronized void run() {
        try {
            InetAddress serveur = InetAddress.getByName(this.address);
            Socket socket = new Socket(serveur, port);
            PrintStream output = new PrintStream(socket.getOutputStream());
            while (this.run) {
                while (this.strings.size() > 0) {
                    String s = this.strings.poll();
                    output.write((s + '\n').getBytes());
                    output.flush();
                    System.out.println(s);
                }
                wait();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void setRun(boolean run) {
        this.run = run;
    }

    public synchronized void addString(String s) {
        this.strings.add(s);
        notify();
    }
}