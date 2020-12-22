package sample.controller.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ReceiverP2P implements Receiver{

    private DatagramSocket socket;

    public ReceiverP2P(int port) throws SocketException {
        socket = new DatagramSocket(port);
    }

    @Override
    public void createSocket(int port) throws IOException {
        socket = new DatagramSocket(port);
    }

    @Override
    public void changePort(int newPort) throws IOException {
        closeSocket();
        createSocket(newPort);
    }

    @Override
    public void closeSocket() {
        socket.close();
    }

    @Override
    public byte[] receive() throws IOException {
        System.err.println("Waiting");

        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        return packet.getData();
    }

    // FIXME: 22/12/2020  da rimuovere, scopo di test
    public DatagramPacket receiveDP() throws IOException {
        System.err.println("Waiting");

        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        return packet;
    }

    public boolean isClosed(){
        return socket.isClosed();
    }



}
