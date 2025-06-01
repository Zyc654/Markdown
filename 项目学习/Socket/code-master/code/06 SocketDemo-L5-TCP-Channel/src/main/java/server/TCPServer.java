package server;

import server.handle.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TCPServer {
    private final int port; // 端口
    private ClientListener mListener; // 客户端监听器
    private List<ClientHandler> clientHandlerList = new ArrayList<>(); // 客户端处理列表

    public TCPServer(int port) {
        this.port = port;
    }

    public boolean start() {
        try {
            ClientListener listener = new ClientListener(port); // 创建一个ClientListener实例 线程
            mListener = listener; // 将ClientListener实例赋值给mListener
            listener.start(); // 启动线程   （线程启动后会执行run方法） 
        } catch (IOException e) {
            e.printStackTrace(); // 打印异常信息
            return false; // 返回false
        }
        return true;
    }

    public void stop() { // 停止TCPServer
        if (mListener != null) {
            mListener.exit(); // 关闭客户端监听器    
        }

        for (ClientHandler clientHandler : clientHandlerList) {
            clientHandler.exit(); // 关闭客户端处理
        }

        clientHandlerList.clear(); // 清空客户端处理列表
    }

    public void broadcast(String str) { // 广播消息
        for (ClientHandler clientHandler : clientHandlerList) {
            clientHandler.send(str); // 发送消息
        }
    }

    private class ClientListener extends Thread {
        private ServerSocket server; // 服务器套接字
        private boolean done = false; // 是否完成

        private ClientListener(int port) throws IOException {
            server = new ServerSocket(port); // 创建一个ServerSocket实例 （端口）
            System.out.println("服务器信息：" + server.getInetAddress() + " P:" + server.getLocalPort()); // 打印服务器信息
        }

        @Override
        public void run() {
            super.run(); // 启动线程

            System.out.println("服务器准备就绪～");
            // 等待客户端连接
            do {
                // 得到客户端
                Socket client; // 客户端套接字
                try {
                    client = server.accept(); // 接受客户端连接
                } catch (IOException e) {
                    continue; // 继续等待客户端连接
                }
                try {
                    // 客户端构建异步线程 回调函数
                    ClientHandler clientHandler = new ClientHandler(client,
                            handler -> clientHandlerList.remove(handler)); // 创建一个ClientHandler实例 线程
                    // 回调函数 告知外界自己把自己关闭了 
                    
                    // 读取数据并打印
                    clientHandler.readToPrint(); // 读取数据并打印
                    clientHandlerList.add(clientHandler); // 添加客户端处理
                } catch (IOException e) {
                    e.printStackTrace();  // 打印异常信息
                    System.out.println("客户端连接异常：" + e.getMessage()); // 打印异常信息
                }
            } while (!done);

            System.out.println("服务器已关闭！");
        }

        void exit() { // 关闭服务器
            done = true; // 设置为true
            try {
                server.close(); // 关闭服务器套接字
            } catch (IOException e) {
                e.printStackTrace(); // 打印异常信息    
            }
        }
    }
}
