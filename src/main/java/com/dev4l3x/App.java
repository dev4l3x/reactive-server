package com.dev4l3x;

import java.io.IOError;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        try (ServerSocketChannel socketChannel = ServerSocketChannel.open()) {

            System.out.println("⏱️ Litening for requests...");
            socketChannel.bind(new InetSocketAddress(8080));
            socketChannel.accept();

        } catch (IOException exception) {
            System.err.println("An error has ocurred while creating socket");
        }
    }
}
