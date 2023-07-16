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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {
    public static void main(String[] args) {

        ExecutorService incomingRequests = Executors.newSingleThreadExecutor();
        ExecutorService responses = Executors.newSingleThreadExecutor();

        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            Selector selector = Selector.open();

            System.out.println("⏳ Litening for requests...");
            serverSocketChannel.bind(new InetSocketAddress(8080));
            serverSocketChannel.configureBlocking(false);

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                System.out.println("➿ Executing event loop");
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();

                for (SelectionKey key : selectedKeys) {
                    if (key.isValid() && key.isAcceptable()) {
                        incomingRequests.execute(() -> registerIncomingConnection(key, selector, serverSocketChannel));
                    } else if (key.isValid() && key.isReadable()) {
                        responses.execute(() -> sendResponse(key));
                    }
                }

            }
        } catch (IOException exception) {
            System.err.println("An error has ocurred while creating socket");
        } finally {
            incomingRequests.close();
            responses.close();
        }
    }

    private static void sendResponse(SelectionKey key) {

        try (SocketChannel client = (SocketChannel) key.channel()) {

            String message = "Hello World!";
            String response = String.format(
                    "HTTP/1.1 200 OK\nContent-Type: text/plain;\nContent-Length: %s\n\n%s",
                    message.getBytes().length, message);

            System.out.println("⏳ Sending response to client...");

            client.write(ByteBuffer.wrap(response.getBytes()));
        } catch (IOException exception) {
            System.err.println("An error has ocurred while sending response: ");
            System.err.println(exception.toString());
        }

    }

    private static void registerIncomingConnection(SelectionKey key, Selector selector,
            ServerSocketChannel serverSocketChannel) {
        try {
            SocketChannel socketChannel = serverSocketChannel.accept();

            if (socketChannel == null) {
                return;
            }

            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            selector.wakeup();
            System.out.println("✅ Accepted incoming request");
        } catch (IOException exception) {
            System.err.println("Error while accepting incoming request: ");
            System.err.println(exception.toString());
        }
    }
}
