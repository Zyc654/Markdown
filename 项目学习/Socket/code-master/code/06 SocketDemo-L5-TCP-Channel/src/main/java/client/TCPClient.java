package client;

import client.bean.ServerInfo;
import clink.net.qiujuer.clink.utils.CloseUtils;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class TCPClient {
    public static void linkWith(ServerInfo info) throws IOException { // 连接服务器
        Socket socket = new Socket(); // 创建一个Socket实例
        // 超时时间
        socket.setSoTimeout(3000); // 设置超时时间

        // 连接本地，端口2000；超时时间3000ms
        socket.connect(new InetSocketAddress(Inet4Address.getByName(info.getAddress()), info.getPort()), 3000); // 连接服务器

        System.out.println("已发起服务器连接，并进入后续流程～");
        System.out.println("客户端信息：" + socket.getLocalAddress() + " P:" + socket.getLocalPort()); // 打印客户端信息
        System.out.println("服务器信息：" + socket.getInetAddress() + " P:" + socket.getPort()); // 打印服务器信息

        try {
            ReadHandler readHandler = new ReadHandler(socket.getInputStream()); // 创建一个ReadHandler实例 线程
            readHandler.start(); // 启动线程

            // 发送接收数据
            write(socket); // 发送接收数据

            // 退出操作
            readHandler.exit(); // 退出操作
        } catch (Exception e) {
            System.out.println("异常关闭"); // 打印异常信息
        }

        // 释放资源 关闭Socket
        socket.close(); // 关闭Socket   
        System.out.println("客户端已退出～"); // 打印客户端已退出
    }

    // 发送接收数据 输出操作
    private static void write(Socket client) throws IOException {
        // 构建键盘输入流
        InputStream in = System.in; // 获取键盘输入流
        BufferedReader input = new BufferedReader(new InputStreamReader(in)); // 将键盘输入流转换为BufferedReader

        // 输出操作 得到Socket输出流，并转换为打印流
        OutputStream outputStream = client.getOutputStream(); // 获取Socket输出流
        PrintStream socketPrintStream = new PrintStream(outputStream); // 将Socket输出流转换为PrintStream

        do {
            // 键盘读取一行
            String str = input.readLine(); // 读取键盘输入的一行
            // 输出操作 发送到服务器
            socketPrintStream.println(str); // 将键盘输入的一行发送到服务器

            if ("00bye00".equalsIgnoreCase(str)) {
                break; // 如果输入的是00bye00 则退出循环
            }
        } while (true);

        // 资源释放
        socketPrintStream.close(); // 关闭PrintStream
    }

    static class ReadHandler extends Thread {
        private boolean done = false; // 是否完成
        private final InputStream inputStream; // 输入流

        ReadHandler(InputStream inputStream) {
            this.inputStream = inputStream; // 将输入流赋值给inputStream
        }

        @Override
        public void run() {
            super.run(); // 启动线程
            try {
                // 得到输入流，用于接收数据
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(inputStream)); // 将输入流转换为BufferedReader

                do {
                    String str; // 字符串
                    try {
                        // 客户端拿到一条数据
                        str = socketInput.readLine(); // 读取一行数据
                    } catch (SocketTimeoutException e) {
                        continue; // 如果超时，则继续读取
                    }
                    if (str == null) {
                        System.out.println("连接已关闭，无法读取数据！");
                        break; // 如果连接已关闭，则退出循环
                    }
                    // 打印到屏幕
                    System.out.println(str); // 打印到屏幕
                } while (!done);
            } catch (Exception e) {
                if (!done) {
                    System.out.println("连接异常断开：" + e.getMessage()); // 打印异常信息
                }
            } finally {
                // 连接关闭
                CloseUtils.close(inputStream); // 关闭输入流
            }
        }

        void exit() { // 退出ReadHandler    
            done = true; // 设置为true
            CloseUtils.close(inputStream); // 关闭输入流
        }
    }
}
