package sample.controller.net;

import sample.controller.Controller;
import sample.controller.net.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

public class SocketManager {

    private static ReceiverMulticast receiverMulticast;
    private static SenderMulticast senderMulticast;
    private static ReceiverP2P receiverP2P;
    private static SenderP2P senderP2P;


    private int port = 50000;
    private String activeType;

    public SocketManager() {
    }

    public void sendText(int port, String destIP, Packet packet){
        try {
            setServiceType();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(activeType.equals("p2p")){
            try {
                senderP2P.send(port, destIP, packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                senderMulticast.send(port, destIP, packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String receiveText(){
        try {
            setServiceType();
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] data = new byte[0];

        if(activeType.equals("p2p")){
            try {
                data = receiverP2P.receive();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                data = receiverMulticast.receive();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new String(data, 0, data.length);
    }

    // FIXME: 22/12/2020 da rimuovere per miglior separazione MVC
    public DatagramPacket receive() {
        try {
            setServiceType();
        } catch (IOException e) {
            //e.printStackTrace();
        }

        DatagramPacket pkt = null;

        if (activeType.equals("p2p")) {
            try {
                pkt = receiverP2P.receiveDP();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                pkt = receiverMulticast.receiveDP();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return pkt;
    }

    private void switchToP2P() throws SocketException {
        //se i multicast sono attivi li chiudo
        if(!receiverMulticast.isClosed()){
            receiverMulticast.closeSocket();
            senderMulticast.closeSocket();
        }

        receiverP2P = new ReceiverP2P(port);
        senderP2P = new SenderP2P();
    }

    private void switchToMulticast() throws IOException {
        //se i p2p sono attivi li chiudo
        if(!receiverP2P.isClosed()){
            receiverP2P.closeSocket();
            senderP2P.closeSocket();
        }

        receiverMulticast = new ReceiverMulticast(port);
        senderMulticast = new SenderMulticast();
    }

    private void setServiceType() throws IOException {
        String requiredType = Controller.getActiveChatType();
        if( !activeType.equals(requiredType) ){
            if(requiredType.equals("p2p")){
                switchToP2P();
            }else{
                switchToMulticast();
            }
        }
        activeType = requiredType;
    }

    public int getPort(){
        return port;
    }

    public void close(){
        if(receiverMulticast!=null) receiverMulticast.closeSocket();
        if(senderMulticast!=null) senderMulticast.closeSocket();
        if(receiverP2P!=null) receiverP2P.closeSocket();
        if(senderP2P!=null) senderP2P.closeSocket();
    }

    public void changePort(int newPort){
        try{
            if(receiverMulticast!=null) receiverMulticast.changePort(newPort);
            if(receiverP2P!=null) receiverP2P.changePort(newPort);
            this.port = newPort;
        }catch (IOException e){
            e.printStackTrace();
        }
    }


}
