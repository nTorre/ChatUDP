/**
 * Classe ricevitrice
 */

package sample.model;

import sample.controller.Controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Receiver {

    private DatagramSocket socket;

    public void createSocket(int port) {
        //socket del mittente
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    public void createSocket() {
        //socket del mittente
        try {
            socket = new DatagramSocket(50000);
        } catch (SocketException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    public void changePort(int port){


        //socket.close();
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    public DatagramPacket receive(Controller controller) {
        controller.start();
        byte[] buf = new byte[256];
        //pacchetto vuoto per la ricezione
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {

            /*
             * metodo bloccante che inizializza l'oggetto packet
             * (con il messaggio e l'indirizzo del mittente)
             */
            System.out.println("in attesa di un datagramma ...");
            if (!socket.isClosed())
                socket.receive(packet);
        } catch(SocketException e) {
            if(!socket.isClosed()) {

            }
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return packet;
    }

    public void closeSocket() {

        socket.close();

    }

    public DatagramSocket getSocket(){
        return socket;
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }
}
