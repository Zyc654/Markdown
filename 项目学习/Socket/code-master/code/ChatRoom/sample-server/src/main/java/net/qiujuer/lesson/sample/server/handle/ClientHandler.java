package net.qiujuer.lesson.sample.server.handle;


import net.qiujuer.lesson.sample.foo.Foo;
import net.qiujuer.library.clink.core.Connector;
import net.qiujuer.library.clink.core.Packet;
import net.qiujuer.library.clink.core.ReceivePacket;
import net.qiujuer.library.clink.utils.CloseUtils;

import java.io.File;
import java.io.IOException;
import java.nio.channels.SocketChannel;

public class ClientHandler extends Connector { // 客户端处理
    private final File cachePath; // 缓存路径
    private final ClientHandlerCallback clientHandlerCallback; // 客户端处理回调
    private final String clientInfo; // 客户端信息

    public ClientHandler(SocketChannel socketChannel, ClientHandlerCallback clientHandlerCallback, File cachePath) throws IOException {
        this.clientHandlerCallback = clientHandlerCallback; // 初始化客户端处理回调
        this.clientInfo = socketChannel.getRemoteAddress().toString(); // 初始化客户端信息
        this.cachePath = cachePath; // 初始化缓存路径


        System.out.println("新客户端连接：" + clientInfo); // 打印新客户端连接信息

        setup(socketChannel); // 设置客户端处理，启动
    }

    public void exit() { // 退出客户端处理
        CloseUtils.close(this); // 关闭客户端处理
        System.out.println("客户端已退出：" + clientInfo); // 打印客户端已退出信息
    }

    @Override // 重写onChannelClosed方法，当客户端通道关闭时，退出客户端处理
    public void onChannelClosed(SocketChannel channel) {
        super.onChannelClosed(channel); // 调用父类方法
        exitBySelf(); // 退出客户端处理
    }

    @Override // 重写onChannelClosed方法，当客户端通道关闭时，退出客户端处理
    protected File createNewReceiveFile() { // 创建新的接收文件
        return Foo.createRandomTemp(cachePath); // 创建新的接收文件
    }

    @Override // 重写onReceivedPacket方法，当收到数据包时，处理数据包,收到消息时回调
    protected void onReceivedPacket(ReceivePacket packet) { // 处理数据包
        super.onReceivedPacket(packet); // 调用父类方法
        if (packet.type() == Packet.TYPE_MEMORY_STRING) { // 如果数据包类型为内存字符串
            String string = (String) packet.entity();
            System.out.println(key.toString() + ":" + string); // 打印数据包信息
            clientHandlerCallback.onNewMessageArrived(this, string); // 通知客户端处理回调
        }
    }

    private void exitBySelf() { // 退出客户端处理
        exit(); // 退出客户端处理
        clientHandlerCallback.onSelfClosed(this); // 通知客户端处理回调
    }

    public interface ClientHandlerCallback { // 客户端处理回调
        // 自身关闭通知
        void onSelfClosed(ClientHandler handler); // 自身关闭通知

        // 收到消息时通知
        void onNewMessageArrived(ClientHandler handler, String msg);
    }
}
