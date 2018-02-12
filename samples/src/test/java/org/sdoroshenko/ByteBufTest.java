package org.sdoroshenko;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Random;

/**
 * Created by Sergei_Admin on 12.02.2018.
 */
public class ByteBufTest {

    private final static Random random = new Random();
    private static final io.netty.buffer.ByteBuf BYTE_BUF_FROM_SOMEWHERE = Unpooled.buffer(1024);
    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();
    private static final ChannelHandlerContext CHANNEL_HANDLER_CONTEXT_FROM_SOMEWHERE = DUMMY_INSTANCE;

    public static void heapBuffer() {
        io.netty.buffer.ByteBuf heapBuf = BYTE_BUF_FROM_SOMEWHERE; //get reference form somewhere
        if (heapBuf.hasArray()) {
            byte[] array = heapBuf.array();
            int offset = heapBuf.arrayOffset() + heapBuf.readerIndex();
            int length = heapBuf.readableBytes();
            handleArray(array, offset, length);
        }
    }

    private static void handleArray(byte[] array, int offset, int len) {}
}
