package com.gyptor.losfapp.network.UDPdiscovery;

import java.io.IOException;
import java.net.*;

public class ServerAnnouncer extends Thread{
    private final int port;
    private volatile boolean running = true;

    public ServerAnnouncer(int port){
        this.port = port;
    }

    public void stopAnnouncing(){
        running = false;
    }

    @Override
    public void run(){
        try (DatagramSocket socket = new DatagramSocket()){
            socket.setBroadcast(true);
            String localIp = InetAddress.getLocalHost().getHostAddress();
            String message = "LOSFAPP_RESPONSE";
            byte[] buffer = message.getBytes();

            while (running){
                DatagramPacket packet = new DatagramPacket(
                        buffer,
                        buffer.length,
                        InetAddress.getByName("255.255.255.255"), // Broadcast to all on LAN
                        8888 // this should match the discovery port
                );

                socket.send(packet);
                Thread.sleep(3000); // announce every 3 secs
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
