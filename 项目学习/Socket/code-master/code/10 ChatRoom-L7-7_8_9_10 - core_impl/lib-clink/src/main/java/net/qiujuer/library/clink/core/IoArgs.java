package net.qiujuer.library.clink.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class IoArgs { // 数据包
    private byte[] byteBuffer = new byte[256]; // 字节缓冲区
    private ByteBuffer buffer = ByteBuffer.wrap(byteBuffer); // 缓冲区

    public int read(SocketChannel channel) throws IOException { // 读取数据
        buffer.clear(); // 清空缓冲区
        return channel.read(buffer); // 读取数据
    }

    public int write(SocketChannel channel) throws IOException { // 写入数据
        return channel.write(buffer); // 写入数据
    }

    public String bufferString() { // 返回字符串
        // 丢弃换行符
        return new String(byteBuffer, 0, buffer.position() - 1); // 返回字符串
    }

    public interface IoArgsEventListener { // 事件监听器    
        void onStarted(IoArgs args); // 开始事件

        void onCompleted(IoArgs args); // 完成事件
    }
}
