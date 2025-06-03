package net.qiujuer.lesson.sample.server;

import net.qiujuer.lesson.sample.server.handle.ClientHandler;
import net.qiujuer.library.clink.utils.CloseUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer implements ClientHandler.ClientHandlerCallback { // 实现ClientHandler.ClientHandlerCallback接口
    private final int port; // 端口
    private ClientListener listener; // 客户端监听器
    private List<ClientHandler> clientHandlerList = new ArrayList<>(); // 客户端处理列表
    private final ExecutorService forwardingThreadPoolExecutor; // 转发线程池
    private Selector selector; // 选择器
    private ServerSocketChannel server; // 服务器通道

    public TCPServer(int port) { // 构造方法
        this.port = port; // 设置端口
        // 转发线程池
        this.forwardingThreadPoolExecutor = Executors.newSingleThreadExecutor(); // 创建转发线程池
    }

    public boolean start() { // 启动
        try {
            selector = Selector.open(); // 创建选择器
            ServerSocketChannel server = ServerSocketChannel.open(); // 创建服务器通道
            // 设置为非阻塞
            server.configureBlocking(false); // 设置为非阻塞
            // 绑定本地端口
            server.socket().bind(new InetSocketAddress(port)); // 绑定本地端口
            // 注册客户端连接到达监听
            server.register(selector, SelectionKey.OP_ACCEPT); // 注册客户端连接到达监听

            this.server = server; // 设置服务器通道


            System.out.println("服务器信息：" + server.getLocalAddress().toString()); // 打印服务器信息

            // 启动客户端监听
            ClientListener listener = this.listener = new ClientListener(); // 创建客户端监听器
            listener.start(); // 启动客户端监听器
        } catch (IOException e) { // 捕获IO异常
            e.printStackTrace(); // 打印异常信息
            return false; // 返回false
        }
        return true; // 返回true
    }

    public void stop() { // 停止
        if (listener != null) {
            listener.exit(); // 退出客户端监听器
        }

        CloseUtils.close(server); // 关闭服务器通道
        CloseUtils.close(selector); // 关闭选择器

        synchronized (TCPServer.this) { 
            for (ClientHandler clientHandler : clientHandlerList) {
                clientHandler.exit(); // 退出客户端处理
            }

            clientHandlerList.clear(); // 清空客户端处理列表
        }

        // 停止线程池
        forwardingThreadPoolExecutor.shutdownNow(); // 停止转发线程池
    }

    public synchronized void broadcast(String str) { // 广播
        for (ClientHandler clientHandler : clientHandlerList) {
            clientHandler.send(str); // 发送消息
        }
    }

    @Override // 重写onSelfClosed方法
    public synchronized void onSelfClosed(ClientHandler handler) { // 重写onSelfClosed方法
        clientHandlerList.remove(handler); // 移除客户端处理
    }

    @Override
    public void onNewMessageArrived(final ClientHandler handler, final String msg) { // 重写onNewMessageArrived方法
        System.out.println(msg.replace("\n","-\\n-")); //发送端发多个消息替换一下换行符
        
        // 异步提交转发任务
        forwardingThreadPoolExecutor.execute(() -> { 
            synchronized (TCPServer.this) { // 同步处理
                for (ClientHandler clientHandler : clientHandlerList) { // 遍历客户端处理列表
                    if (clientHandler.equals(handler)) { // 如果客户端处理等于当前客户端处理
                        // 跳过自己
                        continue;
                    }
                    // 对其他客户端发送消息
                    clientHandler.send(msg); // 发送消息
                }
            }
        });
    }

    private class ClientListener extends Thread { // 实现Thread接口
        private boolean done = false; // 是否完成

        @Override // 重写run方法
        public void run() { // 重写run方法
            super.run();
            Selector selector = TCPServer.this.selector; // 获取选择器
            System.out.println("服务器准备就绪～"); // 打印服务器准备就绪
            // 等待客户端连接
            do {
                // 得到客户端
                try {
                    if (selector.select() == 0) { // 如果选择器没有选择到任何客户端
                        if (done) { // 如果完成
                            break; // 退出循环
                        }
                        continue; // 继续循环
                    }

                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator(); // 获取选择器的选择键
                    while (iterator.hasNext()) { // 遍历选择键
                        if (done) { // 如果完成
                            break; // 退出循环
                        }

                        SelectionKey key = iterator.next(); // 获取选择键
                        iterator.remove(); // 移除选择键

                        // 检查当前Key的状态是否是我们关注的
                        // 客户端到达状态
                        if (key.isAcceptable()) { // 如果选择键是可接受的
                            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel(); // 获取服务器通道
                            // 非阻塞状态拿到客户端连接
                            SocketChannel socketChannel = serverSocketChannel.accept();

                            try {
                                // 客户端构建异步线程
                                ClientHandler clientHandler = new ClientHandler(socketChannel, TCPServer.this);
                                // 添加同步处理
                                synchronized (TCPServer.this) { // 同步处理
                                    clientHandlerList.add(clientHandler); // 添加客户端处理
                                }
                            } catch (IOException e) { // 捕获IO异常
                                e.printStackTrace(); // 打印异常信息
                                System.out.println("客户端连接异常：" + e.getMessage()); // 打印异常信息
                            }
                        }
                    }
                } catch (IOException e) { // 捕获IO异常
                    e.printStackTrace(); // 打印异常信息
                }

            } while (!done); // 循环

            System.out.println("服务器已关闭！"); // 打印服务器已关闭
        }

        void exit() { // 退出
            done = true; // 设置完成
            // 唤醒当前的阻塞
            selector.wakeup(); // 唤醒当前的阻塞
        }
    } // 客户端监听器
} // 服务器
