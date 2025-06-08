package com.gyptor.losfapp.network.UDPdiscovery;

import java.net.InetAddress;

public class Peer {
    private InetAddress address;
    private long lastSeen;

    public Peer(InetAddress address) {
        this.address = address;
        this.lastSeen = System.currentTimeMillis();
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void updateLastSeen() {
        this.lastSeen = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Peer{" +
                "IP=" + address.getHostAddress() +
                ", LastSeen=" + lastSeen +
                "}";
    }
}
