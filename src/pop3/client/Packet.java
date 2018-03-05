package pop3.client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

public class Packet {
    private long length;
    private byte[] data;

    public Packet(String string) {
        this.length = string.length();
        this.data = (string + '\n').getBytes();
    }

    public void send(Socket socket) throws IOException {
        PrintStream out = new PrintStream(socket.getOutputStream());
        out.write(data);
        out.flush();
    }

    public String getData() {
        return new String(data);
    }
}
