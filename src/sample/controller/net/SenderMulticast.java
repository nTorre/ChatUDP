package sample.controller.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class SenderMulticast implements Sender{

    private final MulticastSocket multicastSocket;

    public SenderMulticast(MulticastSocket multicastSocket){
        this.multicastSocket = multicastSocket;
    }

    public SenderMulticast() throws IOException {
            multicastSocket = new MulticastSocket();
    }

    @Override
    public String send(int port, String groupIP, byte[] data) throws IOException {
        InetAddress groupAdd = InetAddress.getByName(groupIP);
        DatagramPacket packet = new DatagramPacket(data, data.length, groupAdd, port);
        multicastSocket.send(packet);
        return String.valueOf(System.currentTimeMillis());
    }

    @Override
    public String send(int port, String groupIP, String msg) throws IOException {
        return send(port, groupIP, msg.getBytes());
    }

    @Override
    public void closeSocket() {
        multicastSocket.close();
    }


}
