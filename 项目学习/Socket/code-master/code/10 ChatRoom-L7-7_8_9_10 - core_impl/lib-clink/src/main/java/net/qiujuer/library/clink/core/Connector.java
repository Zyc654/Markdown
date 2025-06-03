package net.qiujuer.library.clink.core;

import net.qiujuer.library.clink.impl.SocketChannelAdapter;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.UUID;

public class Connector implements Closeable, SocketChannelAdapter.OnChannelStatusChangedListener {
    private UUID key = UUID.randomUUID(); // 唯一标识
    private SocketChannel channel; // 通道
    private Sender sender; // 发送者
    private Receiver receiver; // 接收者

    public void setup(SocketChannel socketChannel) throws IOException { // 设置
        this.channel = socketChannel; // 设置通道

        IoContext context = IoContext.get(); // 获取上下文
        SocketChannelAdapter adapter = new SocketChannelAdapter(channel, context.getIoProvider(), this); // 创建适配器

        this.sender = adapter; // 设置发送者
        this.receiver = adapter; // 设置接收者

        readNextMessage(); // 读取下一条消息
    }

    private void readNextMessage() { // 读取下一条消息
        if (receiver != null) {
            try {
                receiver.receiveAsync(echoReceiveListener); // 异步接收数据
            } catch (IOException e) {
                System.out.println("开始接收数据异常：" + e.getMessage()); // 打印异常信息
            }
        }
    }

    @Override
    public void close() throws IOException { // 重写close方法，当连接关闭时，执行
        CloseUtils.close(this); // 关闭连接
    }

    @Override // 重写onChannelClosed方法，当通道关闭时，执行
    public void onChannelClosed(SocketChannel channel) {
        System.out.println("连接已关闭：" + channel); // 打印连接已关闭信息
    }


    private IoArgs.IoArgsEventListener echoReceiveListener = new IoArgs.IoArgsEventListener() {
        @Override // 重写onStarted方法，当开始接收数据时，执行
        public void onStarted(IoArgs args) {
            // 打印开始接收数据信息
        }

        @Override // 重写onCompleted方法，当完成接收数据时，执行打印消息
        public void onCompleted(IoArgs args) {
            // 打印消息
            onReceiveNewMessage(args.bufferString());
            // 读取下一条数据
            readNextMessage(); // 读取下一条数据    
        }
    };

    protected void onReceiveNewMessage(String str) { // 接收新消息
        System.out.println(key.toString() + ":" + str); // 打印消息
    }
}
