package sample.controller.net;

import java.io.IOException;

public interface Receiver{

    void createSocket(int port) throws IOException;
    void closeSocket();
    void changePort(int newPort) throws IOException;
    byte[] receive() throws IOException;

    static String byteArray2String(byte[] bytes) {
        return new String(bytes, 0, bytes.length);
    }

}
