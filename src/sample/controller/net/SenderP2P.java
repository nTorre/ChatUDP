package sample.controller.net;

import sample.model.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class SenderP2P implements Sender{

    private final DatagramSocket socket;

    public SenderP2P(DatagramSocket socket){
        this.socket = socket;
    }

    public SenderP2P() throws SocketException {
        socket = new DatagramSocket();
    }

    @Override
    public String send(int port, String destIP, byte[] data) throws IOException {
        InetAddress destAdd = InetAddress.getByName(destIP);
        DatagramPacket packet = new DatagramPacket(data, data.length, destAdd, port);
        socket.send(packet);

        return String.valueOf(System.currentTimeMillis());
    }

    @Override
    public String  send(int port, String ip, Packet packet) throws IOException {
        return send(port, ip, packet.getBytes());
    }

    @Override
    public void closeSocket() {
        socket.close();
    }


}
