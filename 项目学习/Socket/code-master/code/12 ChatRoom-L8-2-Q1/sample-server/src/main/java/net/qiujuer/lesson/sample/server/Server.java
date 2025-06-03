package net.qiujuer.lesson.sample.server;

import net.qiujuer.lesson.sample.foo.constants.TCPConstants;
import net.qiujuer.library.clink.core.IoContext;
import net.qiujuer.library.clink.impl.IoSelectorProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
 
public class Server { // 服务器
    public static void main(String[] args) throws IOException { // 主方法
        IoContext.setup() // 设置IO上下文
                .ioProvider(new IoSelectorProvider()) // 设置IO提供者
                .start();

        TCPServer tcpServer = new TCPServer(TCPConstants.PORT_SERVER); // 创建TCP服务器
        boolean isSucceed = tcpServer.start(); // 启动TCP服务器
        if (!isSucceed) { // 如果启动失败
            System.out.println("Start TCP server failed!"); // 打印错误信息
            return;
        }

        UDPProvider.start(TCPConstants.PORT_SERVER); // 启动UDP提供者

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in)); // 创建键盘输入流
        String str; // 字符串
        do {
            str = bufferedReader.readLine(); // 读取一行
            tcpServer.broadcast(str); // 广播
        } while (!"00bye00".equalsIgnoreCase(str)); // 循环

        UDPProvider.stop(); // 停止UDP提供者
        tcpServer.stop(); // 停止TCP服务器

        IoContext.close(); // 关闭IO上下文
    }
} // 服务器
