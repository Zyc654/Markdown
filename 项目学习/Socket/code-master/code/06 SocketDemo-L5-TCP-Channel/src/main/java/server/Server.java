package server;

import constants.TCPConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Server {
    public static void main(String[] args) throws IOException { // 主方法
        TCPServer tcpServer = new TCPServer(TCPConstants.PORT_SERVER); // 创建一个TCPServer实例
        boolean isSucceed = tcpServer.start(); // 启动TCPServer
        if (!isSucceed) {
            System.out.println("Start TCP server failed!"); // 打印错误信息
            return; // 退出程序
        }

        UDPProvider.start(TCPConstants.PORT_SERVER); // 启动UDPProvider

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in)); // 将键盘输入流转换为BufferedReader
        String str; // 字符串
        do {
            str = bufferedReader.readLine(); // 读取键盘输入的一行
            tcpServer.broadcast(str); // 广播消息
        } while (!"00bye00".equalsIgnoreCase(str)); // 如果输入的是00bye00 则退出循环

        UDPProvider.stop(); // 停止UDPProvider
        tcpServer.stop(); // 停止TCPServer
    }
}
