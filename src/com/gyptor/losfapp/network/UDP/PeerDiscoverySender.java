package com.gyptor.losfapp.network.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class PeerDiscoverySender {
    public static void main(String[] args) {

        try {
            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);

            String message = "LOSFApp_DISCOVER";
            byte[] buffer = message.getBytes();

            // send the broadcase address on port 8888
            DatagramPacket packet = new DatagramPacket(
                    buffer,
                    buffer.length,
                    InetAddress.getByName("255.255.255.255"),
                    8888
            );

            socket.send(packet);
            System.out.println("Discovery message sent!");

            socket.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
