package org.sdoroshenko;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;

import static io.netty.channel.DummyChannelHandlerContext.DUMMY_INSTANCE;

/**
 * Created by Sergei_Admin on 12.02.2018.
 */
public class ByteBufTest {

    private final static Random random = new Random();
    private static final io.netty.buffer.ByteBuf BYTE_BUF_FROM_SOMEWHERE = Unpooled.buffer(1024);
//    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();
//    private static final ChannelHandlerContext CHANNEL_HANDLER_CONTEXT_FROM_SOMEWHERE = DUMMY_INSTANCE;

    /**
     * Backing array.
     */
    @Test
    public void heapBuffer() {
        io.netty.buffer.ByteBuf heapBuf = BYTE_BUF_FROM_SOMEWHERE; //get reference form somewhere
        if (heapBuf.hasArray()) {
            byte[] array = heapBuf.array();
            int offset = heapBuf.arrayOffset() + heapBuf.readerIndex();
            int length = heapBuf.readableBytes();
            handleArray(array, offset, length);
        }
    }

    /**
     * Direct buffer data access.
     */
    @Test
    public void directBuffer() {
        ByteBuf directBuf = BYTE_BUF_FROM_SOMEWHERE; //get reference form somewhere
        if (!directBuf.hasArray()) {
            int length = directBuf.readableBytes();
            byte[] array = new byte[length];
            directBuf.getBytes(directBuf.readerIndex(), array);
            handleArray(array, 0, length);
        }
    }

    /**
     * Slice a ByteBuf.
     */
    @Test
    public void byteBufSlice() {
        Charset utf8 = Charset.forName("UTF-8");
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
        ByteBuf sliced = buf.slice(0, 15);

        System.out.println("buf: " + buf.toString(utf8));
        System.out.println("sliced: " + sliced.toString(utf8));

        buf.setByte(0, (byte)'J');

        System.out.println("buf: " + buf.toString(utf8));
        System.out.println("sliced: " + sliced.toString(utf8));

        Assert.assertEquals(sliced.getByte(0), buf.getByte(0));
    }

    /**
     * Copying a ByteBuf.
     */
    @Test
    public static void byteBufCopy() {
        Charset utf8 = Charset.forName("UTF-8");
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
        ByteBuf copy = buf.copy(0, 15);

        System.out.println("buf: " + buf.toString(utf8));
        System.out.println("copy: " + copy.toString(utf8));

        buf.setByte(0, (byte)'J');

        System.out.println("buf: " + buf.toString(utf8));
        System.out.println("copy: " + copy.toString(utf8));

        Assert.assertNotEquals(copy.getByte(0), buf.getByte(0));
    }

    /**
     * get() and set() usage.
     */
    @Test
    public void byteBufSetGet() {
        Charset utf8 = Charset.forName("UTF-8");
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);

        System.out.println("buf: " + buf.toString(utf8));
        System.out.println("buf's first char: " + (char)buf.getByte(0));

        int readerIndex = buf.readerIndex();
        int writerIndex = buf.writerIndex();
        System.out.println("readerIndex=" + readerIndex + ", writerIndex=" + writerIndex);

        buf.setByte(0, (byte)'B');

        System.out.println("buf: " + buf.toString(utf8));
        System.out.println("buf's first char: " + (char)buf.getByte(0));
        System.out.println("readerIndex=" + buf.readerIndex() + ", writerIndex=" + buf.writerIndex());

        Assert.assertEquals(buf.readerIndex(), readerIndex);
        Assert.assertEquals(buf.writerIndex(), writerIndex);
    }

    /**
     * read() and write() operations on the ByteBuf.
     */
    @Test
    public static void byteBufWriteRead() {
        Charset utf8 = Charset.forName("UTF-8");
        ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);

        System.out.println("buf: " + buf.toString(utf8));
        System.out.println("buf's first read char: " + (char)buf.readByte());

        int readerIndex = buf.readerIndex();
        int writerIndex = buf.writerIndex();
        System.out.println("readerIndex=" + readerIndex + ", writerIndex=" + writerIndex);

        buf.writeByte((byte)'?');
        System.out.println("buf: " + buf.toString(utf8));
        System.out.println("readerIndex=" + buf.readerIndex() + ", writerIndex=" + buf.writerIndex());

        Assert.assertEquals(buf.readerIndex(), readerIndex);
        Assert.assertNotEquals(buf.writerIndex(), writerIndex);
    }

    private static void handleArray(byte[] array, int offset, int len) {
        System.out.println(Arrays.toString(array));
    }
}
