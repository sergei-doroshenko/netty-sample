package org.sdoroshenko;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;


public class PlainNioServer {

    @SuppressWarnings("squid:S2189")
    public void serve(int port) {

        try (ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
            serverChannel.configureBlocking(false);
            ServerSocket ss = serverChannel.socket();
            InetSocketAddress address = new InetSocketAddress(port);
            ss.bind(address);
            Selector selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            final ByteBuffer msg = ByteBuffer.wrap("Hi there!\r\n".getBytes());

            for (;;) {
                try {
                    selector.select();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    //handle exception
                    break;
                }
                Set<SelectionKey> readyKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = readyKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    try {
                        if (key.isAcceptable()) {
                            ServerSocketChannel server = (ServerSocketChannel) key.channel();
                            SocketChannel client = server.accept();
                            client.configureBlocking(false);
                            client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, msg.duplicate());
                            System.out.println("Accepted connection from " + client);
                        }

                        if (key.isWritable()) {
                            SocketChannel client = (SocketChannel) key.channel();
                            ByteBuffer buffer = (ByteBuffer) key.attachment();

                            while (buffer.hasRemaining()) {
                                if (client.write(buffer) == 0) {
                                    break;
                                }
                            }
//                            client.close();
                        }

                        if (key.isValid() && key.isReadable()) {
                            SocketChannel client = (SocketChannel) key.channel();
//                            ByteBuffer buffer = (ByteBuffer) key.attachment();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            int bytesRead = client.read(buffer);

                            while (bytesRead != -1) {
//                                System.out.println("Read " + bytesRead);
                                buffer.flip();

                                while(buffer.hasRemaining()){
                                    System.out.print((char) buffer.get());
                                }

                                buffer.clear();
                                bytesRead = client.read(buffer);
                            }


//                            System.out.println(new String(buffer.array()));
                            client.close();
                        }
                    } catch (IOException ex) {
                        key.cancel();
                        try {
                            key.channel().close();
                        } catch (IOException cex) {
                            // ignore on close
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        PlainNioServer plainNioServer = new PlainNioServer();
        plainNioServer.serve(9999);

        synchronized (PlainNioServer.class) {
            PlainNioServer.class.wait();
        }
    }
}
