package net.qiujuer.lesson.sample.server;

import net.qiujuer.lesson.sample.foo.constants.TCPConstants;
import net.qiujuer.library.clink.core.IoContext;
import net.qiujuer.library.clink.impl.IoSelectorProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Server { // 服务器     
    public static void main(String[] args) throws IOException { // 主方法
        IoContext.setup() // 设置IoContext
                .ioProvider(new IoSelectorProvider()) // 设置IoProvider
                .start(); // 启动

        TCPServer tcpServer = new TCPServer(TCPConstants.PORT_SERVER); // 创建TCP服务器
        boolean isSucceed = tcpServer.start(); // 启动TCP服务器
        if (!isSucceed) {
            System.out.println("Start TCP server failed!"); // 打印启动TCP服务器失败
            return; // 返回
        }

        UDPProvider.start(TCPConstants.PORT_SERVER); // 启动UDP服务器

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in)); // 创建BufferedReader
        String str; // 字符串
        do {
            str = bufferedReader.readLine(); // 读取一行
            tcpServer.broadcast(str); // 广播消息
        } while (!"00bye00".equalsIgnoreCase(str)); // 如果输入00bye00则退出

        UDPProvider.stop(); // 停止UDP服务器
        tcpServer.stop(); // 停止TCP服务器

        IoContext.close(); // 关闭IoContext
    }
}
