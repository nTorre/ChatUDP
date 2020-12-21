/**
 * Classe inviante
 */

package sample.model;

import java.io.IOException;
import java.net.*;

public class Sender {

    private DatagramSocket socket;

    public Sender(DatagramSocket socket){

        this.socket = socket;

    }

    public void send(int port, String ip, String msg){

        DatagramPacket packet;

        //address e port del destinatario
        InetAddress address = null;
        try {
            address = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            System.out.println("Errore"+ e.getLocalizedMessage());
        }

        //messaggio

        byte[] buf = msg.getBytes();

        //"imbustiamo" il messaggio
        packet = new DatagramPacket(buf, buf.length, address, port);

        //spedizione
        try {
            socket.send(packet);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Datagramma inviato");


    }

    public void closeSocket() {

        socket.close();

    }

    public void setSocket(DatagramSocket datagramSocket){
        this.socket=datagramSocket;
    }

}
