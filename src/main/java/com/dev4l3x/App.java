package com.dev4l3x;

import java.io.IOError;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        try (ServerSocketChannel socketChannel = ServerSocketChannel.open()) {

            System.out.println("⏳ Litening for requests...");
            socketChannel.bind(new InetSocketAddress(8080));
            while (true) {
                SocketChannel client = socketChannel.accept();

                System.out.println("Incoming request!");

                String message = "Hello World!";
                String response = String.format("HTTP/1.1 200 OK\nContent-Type: text/plain;\nContent-Length: %s\n\n%s",
                        message.getBytes().length, message);

                System.out.println("⏳ Sending response to client...");
                client.write(ByteBuffer.wrap(response.getBytes()));
                client.close();
            }

        } catch (IOException exception) {
            System.err.println("An error has ocurred while creating socket");
        }
    }
}
