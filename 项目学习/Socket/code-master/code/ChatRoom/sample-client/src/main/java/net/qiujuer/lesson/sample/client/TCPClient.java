package net.qiujuer.lesson.sample.client;


import net.qiujuer.lesson.sample.client.bean.ServerInfo;
import net.qiujuer.lesson.sample.foo.Foo;
import net.qiujuer.library.clink.core.Connector;
import net.qiujuer.library.clink.core.Packet;
import net.qiujuer.library.clink.core.ReceivePacket;
import net.qiujuer.library.clink.utils.CloseUtils;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class TCPClient extends Connector { // 继承Connector类，实现TCP客户端    
    private final File cachePath; // 缓存路径

    public TCPClient(SocketChannel socketChannel, File cachePath) throws IOException { // 构造函数，初始化缓存路径
        this.cachePath = cachePath;
        setup(socketChannel);
    }

    public void exit() { // 退出方法，关闭连接
        CloseUtils.close(this); // 调用CloseUtils类的方法，关闭连接
    }

    @Override
    public void onChannelClosed(SocketChannel channel) { // 重写onChannelClosed方法，当连接关闭时，打印提示信息
        super.onChannelClosed(channel); // 调用父类方法，处理连接关闭事件
        System.out.println("连接已关闭，无法读取数据!");
    }

    @Override
    protected File createNewReceiveFile() { // 重写createNewReceiveFile方法，创建新的接收文件
        return Foo.createRandomTemp(cachePath); // 调用Foo类的方法，创建新的接收文件
    }

    @Override
    protected void onReceivedPacket(ReceivePacket packet) { // 重写onReceivedPacket方法，当收到数据包时，打印提示信息
        super.onReceivedPacket(packet); // 调用父类方法，处理接收到的数据包
        if (packet.type() == Packet.TYPE_MEMORY_STRING) { // 如果数据包类型为字符串
            String string = (String) packet.entity(); // 获取数据包内容
            System.out.println(key.toString() + ":" + string); // 打印提示信息
        }
    }

    public static TCPClient startWith(ServerInfo info, File cachePath) throws IOException { // 静态方法，启动TCP客户端
        SocketChannel socketChannel = SocketChannel.open(); // 创建SocketChannel对象

        // 连接本地，端口2000；超时时间3000ms
        socketChannel.connect(new InetSocketAddress(Inet4Address.getByName(info.getAddress()), info.getPort()));

        System.out.println("已发起服务器连接，并进入后续流程～");// 打印提示信息        
        System.out.println("客户端信息：" + socketChannel.getLocalAddress().toString());// 打印客户端信息
        System.out.println("服务器信息：" + socketChannel.getRemoteAddress().toString());// 打印服务器信息

        try {
            return new TCPClient(socketChannel, cachePath); // 创建TCPClient对象
        } catch (Exception e) {
            System.out.println("连接异常");// 打印提示信息
            CloseUtils.close(socketChannel); // 关闭SocketChannel对象
        }

        return null;
    }
}
