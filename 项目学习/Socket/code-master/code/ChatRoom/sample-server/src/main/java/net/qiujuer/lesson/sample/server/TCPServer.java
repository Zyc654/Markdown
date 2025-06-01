package net.qiujuer.lesson.sample.server;

import net.qiujuer.lesson.sample.server.handle.ClientHandler;
import net.qiujuer.library.clink.utils.CloseUtils;

import java.io.File;
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
    private final int port; // 端口号
    private final File cachePath; // 缓存路径
    private final ExecutorService forwardingThreadPoolExecutor; // 转发线程池
    private ClientListener listener; // 客户端监听器
    private List<ClientHandler> clientHandlerList = new ArrayList<>(); // 客户端处理列表
    private Selector selector; // 选择器
    private ServerSocketChannel server; // 服务器通道

    public TCPServer(int port, File cachePath) { // 构造函数，初始化端口号和缓存路径
        this.port = port; // 初始化端口号
        this.cachePath = cachePath; // 初始化缓存路径
        // 转发线程池
        this.forwardingThreadPoolExecutor = Executors.newSingleThreadExecutor();
    }

    public boolean start() { // 启动服务器
        try { // 尝试启动服务器
            selector = Selector.open(); // 创建 选择器
            ServerSocketChannel server = ServerSocketChannel.open(); // 创建服务器通道
            // 设置为非阻塞
            server.configureBlocking(false); // 设置为非阻塞
            
            // 绑定本地端口
            server.socket().bind(new InetSocketAddress(port)); // 绑定本地端口

            // 注册客户端连接到达监听
            server.register(selector, SelectionKey.OP_ACCEPT); // 注册客户端连接到达监听

            this.server = server; // 初始化服务器通道

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

    public void stop() { // 停止服务器
        if (listener != null) {
            listener.exit(); // 停止客户端监听器
        }

        CloseUtils.close(server); // 关闭服务器通道
        CloseUtils.close(selector); // 关闭选择器

        synchronized (TCPServer.this) { // 同步处理
            for (ClientHandler clientHandler : clientHandlerList) { // 遍历客户端处理列表
                clientHandler.exit(); // 退出客户端处理
            }

            clientHandlerList.clear(); // 清空客户端处理列表
        } 

        // 停止线程池
        forwardingThreadPoolExecutor.shutdownNow(); // 停止线程池
    }

    public synchronized void broadcast(String str) { // 广播字符串
        for (ClientHandler clientHandler : clientHandlerList) { // 遍历客户端处理列表
            clientHandler.send(str); // 发送字符串
        }
    }

    @Override
    public synchronized void onSelfClosed(ClientHandler handler) { // 重写onSelfClosed方法，当客户端关闭时，移除客户端处理
        clientHandlerList.remove(handler); // 移除客户端处理
    }

    @Override
    public void onNewMessageArrived(final ClientHandler handler, final String msg) { // 重写onNewMessageArrived方法，当收到新消息时，提交转发任务
        // 异步提交转发任务
        forwardingThreadPoolExecutor.execute(() -> { // 异步提交转发任务
            synchronized (TCPServer.this) { // 同步处理
                for (ClientHandler clientHandler : clientHandlerList) { // 遍历客户端处理列表
                    if (clientHandler.equals(handler)) { // 如果客户端处理等于当前处理
                        // 跳过自己
                        continue; // 继续循环
                    }
                    // 对其他客户端发送消息
                    clientHandler.send(msg); // 发送消息
                }
            }
        });
    }

    private class ClientListener extends Thread { // 客户端监听器
        private boolean done = false; // 是否完成

        @Override
        public void run() { // 重写run方法，当客户端监听器启动时，执行
            super.run(); // 调用父类方法
            
            Selector selector = TCPServer.this.selector; // 获取选择器
            System.out.println("服务器准备就绪～"); // 打印服务器准备就绪信息
            // 等待客户端连接
            do {
                // 得到客户端
                try {
                    if (selector.select() == 0) { // 如果选择器没有选择到任何事件,有可能是被唤醒的,所以需要判断是否完成
                        if (done) { // 如果完成
                            break; // 退出循环
                        }
                        continue; // 继续循环
                    }

                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator(); // 获取选择器选择的事件
                    while (iterator.hasNext()) { // 遍历选择器选择的事件
                        if (done) { // 如果完成
                            break; // 退出循环
                        }

                        SelectionKey key = iterator.next();
                        iterator.remove(); // 移除选择器选择的事件

                        // 检查当前Key的状态是否是我们关注的
                        // 客户端到达状态
                        if (key.isAcceptable()) { // 如果客户端到达状态
                            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel(); // 获取服务器通道
                            // 非阻塞状态拿到客户端连接
                            SocketChannel socketChannel = serverSocketChannel.accept(); // 获取客户端连接

                            try {
                                // 客户端构建异步线程
                                ClientHandler clientHandler = new ClientHandler(socketChannel,
                                        TCPServer.this, cachePath); // 创建客户端处理
                                // 添加同步处理
                                synchronized (TCPServer.this) { // 同步处理
                                    clientHandlerList.add(clientHandler); // 添加客户端处理
                                }
                            } catch (IOException e) { // 捕获IO异常
                                e.printStackTrace(); // 打印异常信息
                                System.out.println("客户端连接异常：" + e.getMessage()); // 打印客户端连接异常信息
                            }
                        }
                    }
                } catch (IOException e) { // 捕获IO异常
                    e.printStackTrace(); // 打印异常信息
                }

            } while (!done); // 循环直到完成

            System.out.println("服务器已关闭！"); // 打印服务器已关闭信息
        }

        void exit() { // 退出客户端监听器
            done = true; // 设置完成
            // 唤醒当前的阻塞，当前是监听，监听到了客户端连接，所以需要唤醒
            selector.wakeup(); // 唤醒当前的阻塞
        }
    }
}
