package net.qiujuer.lesson.sample.client;


import net.qiujuer.lesson.sample.client.bean.ServerInfo;
import net.qiujuer.lesson.sample.foo.Foo;
import net.qiujuer.library.clink.box.FileSendPacket;
import net.qiujuer.library.clink.core.IoContext;
import net.qiujuer.library.clink.impl.IoSelectorProvider;

import java.io.*;

public class Client {
    public static void main(String[] args) throws IOException {
        File cachePath = Foo.getCacheDir("client"); // 获取缓存目录
        IoContext.setup().ioProvider(new IoSelectorProvider()) 
                .start(); // 设置IO提供者 启动IO上下文

        ServerInfo info = UDPSearcher.searchServer(10000); // 搜索服务器
        System.out.println("Server:" + info); // 打印服务器信息

        if (info != null) { // 如果服务器信息不为空
            TCPClient tcpClient = null; // 创建TCPClient对象

            try { // 尝试连接服务器
                tcpClient = TCPClient.startWith(info, cachePath); // 创建TCPClient对象
                if (tcpClient == null) { // 如果连接失败
                    return; // 返回
                }

                write(tcpClient); // 发送数据 键盘输出
            } catch (IOException e) { // 捕获IO异常
                e.printStackTrace(); // 打印异常信息
            } finally {
                if (tcpClient != null) { // 如果连接成功
                    tcpClient.exit(); // 关闭连接
                }
            }
        }


        IoContext.close(); // 关闭IO上下文          
    }


    private static void write(TCPClient tcpClient) throws IOException {
        // 构建键盘输入流
        InputStream in = System.in; // 获取系统输入流
        BufferedReader input = new BufferedReader(new InputStreamReader(in)); // 构建缓冲输入流

        do {
            // 键盘读取一行
            String str = input.readLine(); // 读取一行
            if ("00bye00".equalsIgnoreCase(str)) { // 如果输入为00bye00
                break; // 退出循环
            }

            // --f url
            if (str.startsWith("--f")) { // 如果输入为--f
                String[] array = str.split(" "); // 分割输入
                if (array.length >= 2) { // 如果输入长度大于2
                    String filePath = array[1]; // 获取文件路径
                    File file = new File(filePath); // 创建文件对象
                    if (file.exists() && file.isFile()) { // 如果文件存在且为文件
                        FileSendPacket packet = new FileSendPacket(file); // 创建文件发送包
                        tcpClient.send(packet); // 发送文件
                        continue;
                    }
                }
            }

            // 发送字符串
            tcpClient.send(str);
        } while (true);
    }

}
