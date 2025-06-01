package net.qiujuer.lesson.sample.server;

import net.qiujuer.lesson.sample.foo.Foo;
import net.qiujuer.lesson.sample.foo.constants.TCPConstants;
import net.qiujuer.library.clink.core.IoContext;
import net.qiujuer.library.clink.impl.IoSelectorProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Server {
    public static void main(String[] args) throws IOException { // 主方法，启动服务器
        File cachePath = Foo.getCacheDir("server"); // 获取缓存目录

        IoContext.setup() // 设置IO上下文
                .ioProvider(new IoSelectorProvider()) // 设置IO提供者
                .start(); // 启动IO上下文

        TCPServer tcpServer = new TCPServer(TCPConstants.PORT_SERVER, cachePath); // 创建TCP服务器
        boolean isSucceed = tcpServer.start(); // 启动TCP服务器
        if (!isSucceed) { // 如果启动失败
            System.out.println("Start TCP server failed!"); // 打印启动失败信息
            return; // 返回
        }

        UDPProvider.start(TCPConstants.PORT_SERVER); // 启动UDP服务器

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in)); // 创建缓冲输入流
        String str; // 定义字符串
        do {
            str = bufferedReader.readLine(); // 读取一行
            if ("00bye00".equalsIgnoreCase(str)) { // 如果输入为00bye00
                break; // 退出循环
            }
            // 发送字符串
            tcpServer.broadcast(str); // 广播字符串
        } while (true); // 循环直到完成

        UDPProvider.stop(); // 停止UDP服务器
        tcpServer.stop();

        IoContext.close();
    }
}
