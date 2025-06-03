package net.qiujuer.lesson.sample.server;

import net.qiujuer.lesson.sample.server.handle.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TCPServer implements ClientHandler.ClientHandlerCallback { // 服务器
    private final int port; // 端口
    private ClientListener mListener; // 客户端监听器
    // private List<ClientHandler> clientHandlerList = Collections.synchronizedList(new ArrayList<>()); // 客户端处理列表
    //保证线程安全, 使用Collections.synchronizedList()方法，仅仅保证添加的时候没问题
    private List<ClientHandler> clientHandlerList = new ArrayList<>(); // 客户端处理列表
    private final ExecutorService forwardingThreadPoolExecutor; // 单线程池


    // 添加一个方法，用于添加客户端处理
    public TCPServer(int port) { // 构造方法
        this.port = port; // 将端口赋值给port

        //转发线程池 用于转发消息
        this.forwardingThreadPoolExecutor = Executors.newSingleThreadExecutor(); // 创建一个单线程池
    }

    public boolean start() { // 启动服务器
        try {
            ClientListener listener = new ClientListener(port); // 创建一个ClientListener实例
            mListener = listener; // 将ClientListener实例赋值给mListener
            listener.start(); // 启动ClientListener
        } catch (IOException e) {
            e.printStackTrace(); // 打印异常信息
            return false; // 如果启动失败，则返回false
        }
        return true; // 如果启动成功，则返回true
    }

    public void stop() { // 停止服务器
        if (mListener != null) { // 如果客户端监听器不为空
            mListener.exit(); // 关闭客户端监听器
        } 

        synchronized (TCPServer.this) { // 同步锁 使用TCPServer.this作为锁对象，实现顺序执行
            for (ClientHandler clientHandler : clientHandlerList) { // 遍历客户端处理列表
                clientHandler.exit(); // 关闭客户端处理
            }
            clientHandlerList.clear(); // 清空客户端处理列表
        }
        // 关闭转发线程池
        forwardingThreadPoolExecutor.shutdownNow(); 
    }

    public synchronized void broadcast(String str) { // 广播消息 m默认同步当前实例 即是TCPServer.this
        for (ClientHandler clientHandler : clientHandlerList) { // 遍历客户端处理列表
            clientHandler.send(str); // 发送消息
        }
    }

    @Override
    public synchronized void onSelfClosed(ClientHandler handler) { // 自身关闭通知
        clientHandlerList.remove(handler); // 移除客户端处理
    }   

    @Override
    public void onNewMessageArrived(final ClientHandler handler, final String msg) { // 收到新消息通知
        //打印客户端信息和消息
        System.out.println("收到新消息：" + handler.getClientInfo() + ":" + msg); // 打印客户端信息和消息 

        //接收线程得到消息后理解调用的 onNewMessageArrived()方法,若不延迟需要尽可能不阻塞，需要异步操作
        // 使用线程池执行广播消息 避免阻塞 异步提交转发任务
        forwardingThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (TCPServer.this) {
                    for (ClientHandler clientHandler : clientHandlerList) {
                        if(clientHandler.equals(handler)){ // 如果客户端处理等于当前客户端处理，则跳过,即是自己转发的
                            continue;
                        }
                        // 对其他客户端发送消息
                        clientHandler.send(msg);
                    }
                }
            }
        });
    }   

    private class ClientListener extends Thread { // 客户端监听器       
        private ServerSocket server; // 服务器套接字
        private boolean done = false; // 是否完成

        private ClientListener(int port) throws IOException {
            server = new ServerSocket(port); // 创建一个ServerSocket实例
            System.out.println("服务器信息：" + server.getInetAddress() + " P:" + server.getLocalPort()); // 打印服务器信息
        }

        @Override
        public void run() {
            super.run(); // 启动线程

            System.out.println("服务器准备就绪～");
            // 等待客户端连接
            do {
                // 得到客户端
                Socket client;
                try {
                    client = server.accept(); // 接受客户端连接
                } catch (IOException e) {
                    continue; // 如果连接失败，则继续等待
                }
                try {
                    // 客户端构建异步线程
                    ClientHandler clientHandler = new ClientHandler(client,
                            TCPServer.this); // 创建一个ClientHandler实例, 客户端处理回调 需要的是TCPServer.this
                    // 读取数据并打印
                    clientHandler.readToPrint();

                    //添加同步处理
                    synchronized (TCPServer.this) { // 同步锁 使用TCPServer.this作为锁对象，实现顺序执行
                        clientHandlerList.add(clientHandler); // 添加客户端处理
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("客户端连接异常：" + e.getMessage());
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
