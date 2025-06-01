package net.qiujuer.lesson.sample.client;


import net.qiujuer.lesson.sample.client.bean.ServerInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Client { // 客户端
    public static void main(String[] args) { // 主方法
        ServerInfo info = UDPSearcher.searchServer(10000); // 搜索服务器
        System.out.println("Server:" + info); // 打印服务器信息

        if (info != null) {
            TCPClient tcpClient = null; // 创建TCP客户端

            try {
                tcpClient = TCPClient.startWith(info); // 启动TCP客户端
                if (tcpClient == null) { // 如果TCP客户端为空
                    return; // 返回
                }

                write(tcpClient);
            } catch (IOException e) { // 捕获IO异常
                e.printStackTrace(); // 打印异常信息
            } finally {
                if (tcpClient != null) { // 如果TCP客户端不为空
                    tcpClient.exit(); // 退出TCP客户端
                }
            }
        }
    }


    private static void write(TCPClient tcpClient) throws IOException { // 写入
        // 构建键盘输入流
        InputStream in = System.in; // 获取系统输入流
        BufferedReader input = new BufferedReader(new InputStreamReader(in)); // 构建键盘输入流

        do { // 循环
            // 键盘读取一行
            String str = input.readLine();
            // 发送到服务器
            tcpClient.send(str); //一次发送多个消息
            tcpClient.send(str);
            tcpClient.send(str);

            if ("00bye00".equalsIgnoreCase(str)) { // 如果输入为00bye00
                break; // 退出循环
            }
        } while (true); // 循环
    } // 写入

}
