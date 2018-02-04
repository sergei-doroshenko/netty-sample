package org.sdoroshenko;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by Sergei_Admin on 04.02.2018.
 */
public class EchoClientHandler2 extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("Client received: " + in.toString(CharsetUtil.UTF_8));
        sendMessage(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private void sendMessage(ChannelHandlerContext ctx) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            for(;;) {
                String text = in.readLine() + "\r\n";
                ctx.channel().writeAndFlush(text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
