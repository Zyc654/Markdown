package net.qiujuer.lesson.sample.server;

import net.qiujuer.lesson.sample.foo.constants.TCPConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Server { // 服务器
    public static void main(String[] args) throws IOException {
        TCPServer tcpServer = new TCPServer(TCPConstants.PORT_SERVER); // 创建一个TCPServer实例
        boolean isSucceed = tcpServer.start(); // 启动TCPServer
        if (!isSucceed) {
            System.out.println("Start TCP server failed!"); // 打印异常信息
            return; // 如果启动失败，则返回
        }

        UDPProvider.start(TCPConstants.PORT_SERVER); // 启动UDPProvider

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in)); // 创建一个BufferedReader实例
        String str; // 字符串
        do {
            str = bufferedReader.readLine(); // 读取一行数据
            tcpServer.broadcast(str); // 广播消息
        } while (!"00bye00".equalsIgnoreCase(str)); // 如果输入的是00bye00 则退出循环

        UDPProvider.stop(); // 停止UDPProvider
        tcpServer.stop(); // 停止TCPServer
    }
}
