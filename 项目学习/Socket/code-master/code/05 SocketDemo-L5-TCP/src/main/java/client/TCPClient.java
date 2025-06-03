package client;

import client.bean.ServerInfo;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TCPClient {
    public static void linkWith(ServerInfo info) throws IOException {
        Socket socket = new Socket(); // 创建一个Socket实例
        // 超时时间
        socket.setSoTimeout(3000); // 设置超时时间

        // 连接本地，端口2000；超时时间3000ms
        socket.connect(new InetSocketAddress(Inet4Address.getByName(info.getAddress()), info.getPort()), 3000); // 连接服务器

        System.out.println("已发起服务器连接，并进入后续流程～"); // 打印信息
        System.out.println("客户端信息：" + socket.getLocalAddress() + " P:" + socket.getLocalPort()); // 打印客户端信息
        System.out.println("服务器信息：" + socket.getInetAddress() + " P:" + socket.getPort()); // 打印服务器信息

        try {
            // 发送接收数据
            todo(socket); // 发送接收数据
        } catch (Exception e) {
            System.out.println("异常关闭"); // 打印异常信息
        }

        // 释放资源
        socket.close(); // 关闭Socket
        System.out.println("客户端已退出～"); // 打印信息

    }

    private static void todo(Socket client) throws IOException {
        // 构建键盘输入流
        InputStream in = System.in; // 获取键盘输入流
        BufferedReader input = new BufferedReader(new InputStreamReader(in)); // 将键盘输入流转换为BufferedReader


        // 得到Socket输出流，并转换为打印流
        OutputStream outputStream = client.getOutputStream(); // 获取Socket输出流
        PrintStream socketPrintStream = new PrintStream(outputStream); // 将Socket输出流转换为PrintStream


        // 得到Socket输入流，并转换为BufferedReader
        InputStream inputStream = client.getInputStream(); // 获取Socket输入流
        BufferedReader socketBufferedReader = new BufferedReader(new InputStreamReader(inputStream)); // 将Socket输入流转换为BufferedReader

        boolean flag = true; // 是否完成
        do {
            // 键盘读取一行
            String str = input.readLine(); // 读取键盘输入的一行
            // 发送到服务器
            socketPrintStream.println(str); // 将键盘输入的一行发送到服务器


            // 从服务器读取一行
            String echo = socketBufferedReader.readLine(); // 读取服务器返回的一行
            if ("bye".equalsIgnoreCase(echo)) {
                flag = false; // 设置为false
            } else {
                System.out.println(echo); // 打印服务器返回的一行
            }
        } while (flag);

        // 资源释放
        socketPrintStream.close(); // 关闭Socket输出流
        socketBufferedReader.close(); // 关闭Socket输入流

    }

}
