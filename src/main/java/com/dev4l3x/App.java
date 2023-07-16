package com.dev4l3x;

import java.util.*;
import java.io.IOError;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class App {
    public static void main(String[] args) {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            Selector selector = Selector.open();

            System.out.println("⏳ Litening for requests...");
            serverSocketChannel.bind(new InetSocketAddress(8080));
            serverSocketChannel.configureBlocking(false);

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();

                for (SelectionKey key : selectedKeys) {

                    if (key.isAcceptable()) {
                        registerIncomingConnection(key, selector, serverSocketChannel);
                    }

                    if (!key.isReadable()) {
                        continue;
                    }

                    SocketChannel client = (SocketChannel) key.channel();

                    System.out.println("Incoming request!");

                    String message = "Hello World!";
                    String response = String.format(
                            "HTTP/1.1 200 OK\nContent-Type: text/plain;\nContent-Length: %s\n\n%s",
                            message.getBytes().length, message);

                    System.out.println("⏳ Sending response to client...");
                    client.write(ByteBuffer.wrap(response.getBytes()));
                    client.close();
                }

            }
        } catch (IOException exception) {
            System.err.println("An error has ocurred while creating socket");
        }
    }

    private static void registerIncomingConnection(SelectionKey key, Selector selector,
            ServerSocketChannel serverSocketChannel) {
        SocketChannel socketChannel = serverSocketChannel.accept();

        if (socketChannel == null) {
            return;
        }

        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }
}
