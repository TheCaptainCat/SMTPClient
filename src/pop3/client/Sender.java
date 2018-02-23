package pop3.client;

import java.io.IOException;
import java.net.Socket;
import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Sender extends Observable implements Runnable {

    private Socket socket;
    private Queue<Packet> packets;
    private boolean run;

    public Sender(Socket socket) {
        this.socket = socket;
        this.packets = new ConcurrentLinkedQueue<>();
        this.run = true;
    }

    public synchronized void sendPacket(Packet packet) {
        packets.add(packet);
        notify();
    }

    public synchronized void stop() {
        run = false;
        notify();
    }

    @Override
    public synchronized void run() {
        try {
            while (this.run) {
                while (packets.size() > 0) {
                    try {
                        packets.poll().send(socket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}