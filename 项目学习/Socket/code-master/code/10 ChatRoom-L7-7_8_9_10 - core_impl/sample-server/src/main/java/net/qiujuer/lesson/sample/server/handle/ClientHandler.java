package net.qiujuer.lesson.sample.server.handle;


import net.qiujuer.library.clink.core.Connector;
import net.qiujuer.library.clink.utils.CloseUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler { // 客户端处理器
    private final Connector connector; // 连接器
    private final SocketChannel socketChannel; // 套接字通道
    private final ClientWriteHandler writeHandler; // 客户端写处理器
    private final ClientHandlerCallback clientHandlerCallback; // 客户端处理器回调
    private final String clientInfo; // 客户端信息

    public ClientHandler(SocketChannel socketChannel, ClientHandlerCallback clientHandlerCallback) throws IOException { // 构造方法
        this.socketChannel = socketChannel; // 设置套接字通道

        connector = new Connector() { // 连接器
            @Override // 重写onChannelClosed方法，当通道关闭时，执行
            public void onChannelClosed(SocketChannel channel) {
                super.onChannelClosed(channel); // 调用父类方法
                exitBySelf(); // 退出
            }

            @Override // 重写onReceiveNewMessage方法，当收到新消息时，执行
            protected void onReceiveNewMessage(String str) {
                super.onReceiveNewMessage(str); // 调用父类方法
                clientHandlerCallback.onNewMessageArrived(ClientHandler.this, str); // 通知客户端处理器回调
            }
        };
        connector.setup(socketChannel); // 设置连接器，设置连接器后，会自动注册选择器

        Selector writeSelector = Selector.open(); // 打开选择器
        socketChannel.register(writeSelector, SelectionKey.OP_WRITE); // 注册选择器
        this.writeHandler = new ClientWriteHandler(writeSelector); // 设置客户端写处理器

        this.clientHandlerCallback = clientHandlerCallback; // 设置客户端处理器回调
        this.clientInfo = socketChannel.getRemoteAddress().toString(); // 设置客户端信息
        System.out.println("新客户端连接：" + clientInfo); // 打印客户端信息
    }

    public String getClientInfo() { // 获取客户端信息
        return clientInfo; // 返回客户端信息
    }

    public void exit() { // 退出
        CloseUtils.close(connector); // 关闭连接器
        writeHandler.exit(); // 关闭写处理器
        CloseUtils.close(socketChannel); // 关闭套接字通道
        System.out.println("客户端已退出：" + clientInfo);
    }

    public void send(String str) { // 发送消息
        writeHandler.send(str); // 发送消息
    }

    private void exitBySelf() { // 自己退出
        exit(); // 退出
        clientHandlerCallback.onSelfClosed(this); // 通知客户端处理器回调
    }

    public interface ClientHandlerCallback { // 客户端处理器回调
        // 自身关闭通知
        void onSelfClosed(ClientHandler handler);

        // 收到消息时通知
        void onNewMessageArrived(ClientHandler handler, String msg);
    }

    class ClientWriteHandler { // 客户端写处理器
        private boolean done = false; // 是否完成
        private final Selector selector; // 选择器
        private final ByteBuffer byteBuffer; // 字节缓冲区
        private final ExecutorService executorService; // 线程池

        ClientWriteHandler(Selector selector) { // 构造方法
            this.selector = selector; // 设置选择器
            this.byteBuffer = ByteBuffer.allocate(256); // 设置字节缓冲区
            this.executorService = Executors.newSingleThreadExecutor(); // 设置线程池
        }

        void exit() { // 退出
            done = true; // 设置为true
            CloseUtils.close(selector); // 关闭选择器
            executorService.shutdownNow(); // 关闭线程池
        }

        void send(String str) { // 发送消息
            if (done) { // 如果完成则返回
                return; // 如果完成则返回
            }
            executorService.execute(new WriteRunnable(str)); // 执行写操作
        }

        class WriteRunnable implements Runnable { // 写操作
            private final String msg; // 消息

            WriteRunnable(String msg) { // 构造方法
                this.msg = msg + '\n'; // 设置消息
            }

            @Override
            public void run() { // 运行
                if (ClientWriteHandler.this.done) { // 如果完成则返回
                    return; // 返回
                }

                byteBuffer.clear(); // 清空字节缓冲区
                byteBuffer.put(msg.getBytes()); // 设置字节缓冲区
                // 反转操作, 重点
                byteBuffer.flip(); // 反转操作

                while (!done && byteBuffer.hasRemaining()) { // 如果完成则返回
                    try {
                        int len = socketChannel.write(byteBuffer); // 写入字节缓冲区
                        // len = 0 合法
                        if (len < 0) {
                            System.out.println("客户端已无法发送数据！"); // 打印客户端已无法发送数据
                            ClientHandler.this.exitBySelf(); // 退出
                            break; // 退出
                        }
                    } catch (Exception e) {
                        e.printStackTrace(); // 打印异常信息    
                    }
                }
            }
        }
    }
}
