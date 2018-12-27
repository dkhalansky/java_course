package ru.ifmo.ct.khalansky.coursework.client;
import java.net.*;
import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {

        String serverHost = args[0];
        short serverPort = Short.parseShort(args[1]);

/*
        ClientFacade.runClients(
            InetAddress.getByName(serverHost),
            serverPort, 1000, 20, 10000, 100);
*/

        List<Integer> list = new ArrayList<>();
        for (int i = 2; i < args.length; ++i) {
            list.add(Integer.parseInt(args[i]));
        }

        for (Integer i : list) {
            System.out.print(i);
            System.out.print(" ");
        }
        System.out.println("");

        try (ListSender sender = new ListSender(new Socket(serverHost, serverPort))) {

            for (int k = 0; k < 3; ++k) {

                System.out.println("Sending a request");
                sender.accept(list);
                System.out.println("Received a response");

            }

        }

        for (Integer i : list) {
            System.out.print(i);
            System.out.print(" ");
        }
        System.out.println("");

        System.out.println("Multiple queries done");

        /*

        try (ListSender sender1 = new ListSender(new Socket(serverHost, serverPort));
            ListSender sender2 = new ListSender(new Socket(serverHost, serverPort));
        ) {

            for (int k = 0; k < 3; ++k) {

                System.out.println("Sending a request (1)");
                sender1.accept(list);
                System.out.println("Received a response (1)");

                System.out.println("Sending a request (2)");
                sender2.accept(list);
                System.out.println("Received a response (2)");

            }

        }
    */

    }

}
