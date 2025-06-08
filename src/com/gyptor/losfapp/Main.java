package com.gyptor.losfapp;

import com.gyptor.losfapp.network.UDPdiscovery.Peer;
import com.gyptor.losfapp.network.UDPdiscovery.PeerDiscoveryService;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
//        // copy-pasting files using Buffered streams
//        Path sc = Paths.get("D:\\videos\\K.G.F Chapter 2.mkv");
//        Path ds = Paths.get("D:\\copiedFiles").resolve(sc.getFileName());
//        FileCopyPasteUtil fcp = new FileCopyPasteUtil();
//        fcp.copyPaste(sc, ds);

        PeerDiscoveryService discoveryService = new PeerDiscoveryService();
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n --- LOSFApp menu ---");
            System.out.println("1. Discover peers");
            System.out.println("2. show discovered peers");
            System.out.println("0. exit");
            System.out.println("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    discoveryService.broadcastPing();
                    break;
                case "2":
                    List<Peer> peers = discoveryService.getDiscoveredPeers();
                    if (peers.isEmpty()) {
                        System.out.println("No peers discovered yet.");
                    } else {
                        peers.forEach(System.out::println);
                    }
                    break;
                case "0":
                    discoveryService.stop();
                    exit = true;
                    break;
                default:
                    System.out.println("invalid option");
            }
        }
        scanner.close();
    }
}