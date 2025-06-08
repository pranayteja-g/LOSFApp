package com.gyptor.losfapp.network.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class PeerDiscoveryReceiver {
    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(8888)) {
            byte[] buffer = new byte[1024];

            System.out.println("Listening for discovery messages...");

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                InetAddress senderAddress = packet.getAddress();

                if (message.equals("LOSFApp_DISCOVER")) {
                    System.out.println("received discovery from: " + senderAddress.getHostAddress());

                    // reply back to sender
                    String reply = "LOSFAPP_RESPONSE";
                    byte[] replyBuffer = reply.getBytes();

                    DatagramPacket replyPacket = new DatagramPacket(
                            replyBuffer,
                            replyBuffer.length,
                            senderAddress,
                            packet.getPort()
                    );

                    socket.send(replyPacket);
                    System.out.println("Replied to: " + senderAddress.getHostAddress());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
