package net.qiujuer.library.clink.core;

import net.qiujuer.library.clink.box.BytesReceivePacket;
import net.qiujuer.library.clink.box.FileReceivePacket;
import net.qiujuer.library.clink.box.StringReceivePacket;
import net.qiujuer.library.clink.box.StringSendPacket;
import net.qiujuer.library.clink.impl.SocketChannelAdapter;
import net.qiujuer.library.clink.impl.async.AsyncReceiveDispatcher;
import net.qiujuer.library.clink.impl.async.AsyncSendDispatcher;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.UUID;

public abstract class Connector implements Closeable, SocketChannelAdapter.OnChannelStatusChangedListener { // 连接器
    protected UUID key = UUID.randomUUID(); // 键
    private SocketChannel channel; // 通道
    private Sender sender; // 发送器  IoArg
    private Receiver receiver; // 接收器 IoArg
    private SendDispatcher sendDispatcher; // 发送调度器
    private ReceiveDispatcher receiveDispatcher; // 接收调度器


    public void setup(SocketChannel socketChannel) throws IOException { // 设置
        this.channel = socketChannel; // 设置通道

        IoContext context = IoContext.get(); // 获取上下文
        SocketChannelAdapter adapter = new SocketChannelAdapter(channel, context.getIoProvider(), this); // 创建适配器

        this.sender = adapter; // 设置发送器
        this.receiver = adapter; // 设置接收器

        sendDispatcher = new AsyncSendDispatcher(sender); // 创建发送调度器
        receiveDispatcher = new AsyncReceiveDispatcher(receiver, receivePacketCallback); // 创建接收调度器

        // 启动接收
        receiveDispatcher.start();
    }


    public void send(String msg) { // 发送
        SendPacket packet = new StringSendPacket(msg); // 创建发送包
        sendDispatcher.send(packet); // 发送
    }

    public void send(SendPacket packet) { // 发送
        sendDispatcher.send(packet); // 发送
    }

    @Override
    public void close() throws IOException { // 关闭
        receiveDispatcher.close(); // 关闭接收调度器
        sendDispatcher.close(); // 关闭发送调度器
        sender.close(); // 关闭发送器
        receiver.close(); // 关闭接收器
        channel.close(); // 关闭通道
    }

    @Override
    public void onChannelClosed(SocketChannel channel) { // 通道关闭

    }


    protected void onReceivedPacket(ReceivePacket packet) { // 接收包
        System.out.println(key.toString() + ":[New Packet]-Type:" + packet.type() + ", Length:" + packet.length); // 打印接收包
    }

    protected abstract File createNewReceiveFile(); // 创建新的接收文件

    // 接收包回调
    private ReceiveDispatcher.ReceivePacketCallback receivePacketCallback 
        = new ReceiveDispatcher.ReceivePacketCallback() { // 接收包回调
        @Override // 重写父类方法
        public ReceivePacket<?, ?> onArrivedNewPacket(byte type, long length) {
            switch (type) {
                case Packet.TYPE_MEMORY_BYTES: 
                    return new BytesReceivePacket(length); // 创建字节数组接收包
                case Packet.TYPE_MEMORY_STRING:
                    return new StringReceivePacket(length); // 创建字符串接收包
                case Packet.TYPE_STREAM_FILE:
                    return new FileReceivePacket(length, createNewReceiveFile()); // 创建文件接收包
                case Packet.TYPE_STREAM_DIRECT:
                    return new BytesReceivePacket(length); // 创建字节数组接收包
                default:
                    throw new UnsupportedOperationException("Unsupported packet type:" + type);
            }
        }

        @Override
        public void onReceivePacketCompleted(ReceivePacket packet) { // 接收包完成
            onReceivedPacket(packet); // 接收包
        }
    };


}
