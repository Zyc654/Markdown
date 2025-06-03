package net.qiujuer.lesson.sample.client;

import net.qiujuer.lesson.sample.client.bean.ServerInfo;
import net.qiujuer.lesson.sample.foo.Foo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientTest {
    private static boolean done; // 是否完成

    public static void main(String[] args) throws IOException {
        File cachePath = Foo.getCacheDir("client/test"); // 获取缓存目录

        ServerInfo info = UDPSearcher.searchServer(10000); // 搜索服务器
        System.out.println("Server:" + info); // 打印服务器信息
        if (info == null) { // 如果服务器信息为空
            return; // 返回
        }

        // 当前连接数量
        int size = 0;
        final List<TCPClient> tcpClients = new ArrayList<>(); // 创建TCP客户端列表
        for (int i = 0; i < 10; i++) { // 循环10次
            try { // 尝试连接服务器
                TCPClient tcpClient = TCPClient.startWith(info, cachePath); // 创建TCP客户端
                if (tcpClient == null) { // 如果连接失败
                    System.out.println("连接异常"); // 打印连接异常
                    continue; // 继续循环
                }

                tcpClients.add(tcpClient); // 添加TCP客户端到列表

                System.out.println("连接成功：" + (++size)); // 打印连接成功

            } catch (IOException e) { // 捕获IO异常
                System.out.println("连接异常"); // 打印连接异常 
            }

            try { // 尝试休眠
                Thread.sleep(20); // 休眠20毫秒
            } catch (InterruptedException e) { // 捕获中断异常
                e.printStackTrace(); // 打印异常信息
            }
        }


        System.in.read(); // 读取系统输入

        Runnable runnable = () -> { // 创建线程
            while (!done) { // 循环直到完成
                for (TCPClient tcpClient : tcpClients) {
                    tcpClient.send("Hello~~"); // 发送Hello~~
                }
                try { // 尝试休眠
                    Thread.sleep(1000); // 休眠1000毫秒
                } catch (InterruptedException e) { // 捕获中断异常
                    e.printStackTrace(); // 打印异常信息
                }
            }
        };

        Thread thread = new Thread(runnable); // 创建线程
        thread.start(); // 启动线程

        System.in.read(); // 读取系统输入

        // 等待线程完成
        done = true; // 设置完成
        try { // 尝试休眠
            thread.join(); // 等待线程完成，完成后自动释放资源
        } catch (InterruptedException e) { // 捕获中断异常
            e.printStackTrace(); // 打印异常信息
        }

        // 客户端结束操作
        for (TCPClient tcpClient : tcpClients) {
            tcpClient.exit(); // 关闭TCP客户端      
        }

    }


}
