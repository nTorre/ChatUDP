package sample.controller.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ReceiverMulticast implements Receiver{

    private MulticastSocket multicastSocket;

    public ReceiverMulticast(int port) throws IOException {
        multicastSocket = new MulticastSocket(port);
    }

    public ReceiverMulticast(int port, String groupIP) throws IOException {
        multicastSocket = new MulticastSocket(port);
        joinGrop(groupIP);
    }

    @Override
    public void createSocket(int port) throws IOException {
        multicastSocket = new MulticastSocket(port);
    }

    public void createSocket(int port, String groupIP) throws IOException {
        createSocket(port);
        joinGrop(groupIP);
    }

    @Override
    public void changePort(int newPort) throws IOException {
        closeSocket();
        createSocket(newPort);
    }

    public void joinGrop(String groupIP) throws IOException {
        multicastSocket.joinGroup(InetAddress.getByName(groupIP));
    }

    public void leaveGroup(String groupIP) throws IOException {
        multicastSocket.leaveGroup(InetAddress.getByName(groupIP));
    }

    public void changeGroup(String oldIP, String newIP) throws IOException {
        leaveGroup(oldIP);
        joinGrop(newIP);
    }

    @Override
    public void closeSocket() {
        multicastSocket.close();
    }

    @Override
    public byte[] receive() throws IOException {
        System.err.println("Waiting");

        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        multicastSocket.receive(packet);

        return packet.getData();
    }

    // FIXME: 22/12/2020  da rimuovere, scopo di test
    public DatagramPacket receiveDP() throws IOException {
        System.err.println("Waiting");

        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        multicastSocket.receive(packet);

        return packet;
    }


    public boolean isClosed(){
        return multicastSocket.isClosed();
    }
}
