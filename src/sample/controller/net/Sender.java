package sample.controller.net;

import java.io.IOException;

public interface Sender {

    String send(int port, String ip, byte[] data) throws IOException;
    String send(int port, String ip, Packet packet) throws IOException;
    void closeSocket();
}
