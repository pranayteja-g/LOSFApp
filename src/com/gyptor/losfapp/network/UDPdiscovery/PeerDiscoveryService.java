package com.gyptor.losfapp.network.UDPdiscovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class PeerDiscoveryService {
    private final int port = 8888;
    private final String DISCOVERY_MSG = "LOSFAPP_DISCOVER";
    private final String RESPONSE_MSG = "LOSFAPP_RESPONSE";
    private final Map<String, Peer> discoveredPeers = new ConcurrentHashMap<>();
    private final String selfIp;
    private final boolean DEBUG = false; // change to true when needed
    private boolean running = true;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public PeerDiscoveryService() {
        this.selfIp = getLocalIpAddress();
        System.out.println("[DISCOVERY] Local IP: " + selfIp);
        startReceiver();
        startperiodicBroadcaster();
    }

    private void startReceiver() {
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(port)) {
                byte[] buffer = new byte[1024];
                System.out.println("[DISCOVERY] Listening on port " + port);

                while (running) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String msg = new String(packet.getData(), 0, packet.getLength());
                    InetAddress senderAddress = packet.getAddress();
                    String senderIp = senderAddress.getHostAddress();

                    if (msg.equals(DISCOVERY_MSG)) {
                        if (DEBUG) System.out.println("[DISCOVERY] Ping received from " + senderIp);

                        // Discover peer from DISCOVERY message
                        if (!senderIp.equals(selfIp)) {
                            discoveredPeers.putIfAbsent(senderIp, new Peer(senderAddress));
                            discoveredPeers.get(senderIp).updateLastSeen();
                            if (DEBUG) System.out.println("[DISCOVERY] Found peer: " + senderIp);
                        } else {
                            if (DEBUG) System.out.println("[DISCOVERY] Ignored self: " + senderIp);
                        }

                        // Send back RESPONSE
                        byte[] replyBytes = RESPONSE_MSG.getBytes();
                        DatagramPacket replyPacket = new DatagramPacket(
                                replyBytes,
                                replyBytes.length,
                                senderAddress,
                                port
                        );
                        socket.send(replyPacket);

                    } else if (msg.equals(RESPONSE_MSG)) {
                        if (!senderIp.equals(selfIp)) {
                            discoveredPeers.putIfAbsent(senderIp, new Peer(senderAddress));
                            discoveredPeers.get(senderIp).updateLastSeen();
                            if (DEBUG) System.out.println("[DISCOVERY] Found peer: " + senderIp);
                        } else {
                            if (DEBUG) System.out.println("[DISCOVERY] Ignored self: " + senderIp);
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("[DISCOVERY] Error: " + e.getMessage(), e);
            }
        }).start();
    }

    public void broadcastPing() {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            byte[] msgBytes = DISCOVERY_MSG.getBytes();
            DatagramPacket packet = new DatagramPacket(
                    msgBytes, msgBytes.length,
                    InetAddress.getByName("255.255.255.255"), port
            );
            socket.send(packet);
            if (DEBUG) System.out.println("[DISCOVERY] Broadcast ping sent!");
        } catch (IOException e) {
            throw new RuntimeException("[DISCOVERY] Broadcast error: " + e.getMessage(), e);
        }
    }

    private void startperiodicBroadcaster() {
        scheduler.scheduleAtFixedRate(() -> {
            if (running){
                broadcastPing();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public List<Peer> getDiscoveredPeers() {
        return new ArrayList<>(discoveredPeers.values());
    }

    public void stop() {
        running = false;
        scheduler.shutdownNow();
    }

    private String getLocalIpAddress() {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }
}
