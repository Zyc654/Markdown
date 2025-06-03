package net.qiujuer.lesson.sample.server.handle;


import net.qiujuer.library.clink.utils.CloseUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler { // 客户端处理
    private final SocketChannel socketChannel; // 客户端通道
    private final ClientReadHandler readHandler; // 客户端读取处理
    private final ClientWriteHandler writeHandler; // 客户端写入处理
    private final ClientHandlerCallback clientHandlerCallback; // 客户端处理回调
    private final String clientInfo; // 客户端信息

    public ClientHandler(SocketChannel socketChannel, ClientHandlerCallback clientHandlerCallback) throws IOException { // 构造方法
        this.socketChannel = socketChannel; // 初始化客户端通道

        // 设置非阻塞模式
        socketChannel.configureBlocking(false); // 设置非阻塞模式

        Selector readSelector = Selector.open(); // 创建读取选择器
        socketChannel.register(readSelector, SelectionKey.OP_READ); // 注册读取选择器
        this.readHandler = new ClientReadHandler(readSelector); // 初始化客户端读取处理

        Selector writeSelector = Selector.open(); // 创建写入选择器
        socketChannel.register(writeSelector, SelectionKey.OP_WRITE); // 注册写入选择器
        this.writeHandler = new ClientWriteHandler(writeSelector); // 初始化客户端写入处理

        // 初始化客户端处理回调 和 客户端信息
        this.clientHandlerCallback = clientHandlerCallback; // 初始化客户端处理回调 
        this.clientInfo = socketChannel.getRemoteAddress().toString(); // 初始化客户端信息
        System.out.println("新客户端连接：" + clientInfo); // 打印新客户端连接信息
    }

    public String getClientInfo() { // 获取客户端信息
        return clientInfo; // 返回客户端信息
    }

    public void exit() { // 退出客户端处理
        readHandler.exit(); // 关闭客户端读取处理
        writeHandler.exit(); // 关闭客户端写入处理
        CloseUtils.close(socketChannel); // 关闭客户端通道
        System.out.println("客户端已退出：" + clientInfo); // 打印客户端已退出信息
    }

    public void send(String str) { // 发送消息
        writeHandler.send(str); // 发送消息
    }

    public void readToPrint() { // 读取消息并打印
        readHandler.start(); // 启动客户端读取处理
    }

    private void exitBySelf() { // 退出客户端处理
        exit(); // 退出客户端处理
        clientHandlerCallback.onSelfClosed(this); // 通知客户端处理回调
    }

    public interface ClientHandlerCallback { // 客户端处理回调
        // 自身关闭通知
        void onSelfClosed(ClientHandler handler); // 自身关闭通知

        // 收到消息时通知
        void onNewMessageArrived(ClientHandler handler, String msg); // 收到消息时通知
    }

    class ClientReadHandler extends Thread { // 客户端读取处理
        private boolean done = false; // 是否完成
        private final Selector selector; // 选择器
        private final ByteBuffer byteBuffer; // 字节缓冲区

        ClientReadHandler(Selector selector) { // 构造方法
            this.selector = selector; // 初始化选择器
            this.byteBuffer = ByteBuffer.allocate(256); // 初始化字节缓冲区
        }

        @Override // 重写run方法，当客户端读取处理启动时，执行
        public void run() { // 重写run方法，当客户端读取处理启动时，执行
            super.run(); // 调用父类方法
            try { // 尝试读取消息
                do {
                    // 客户端拿到一条数据
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

                        if (key.isReadable()) { // 如果可读
                            SocketChannel client = (SocketChannel) key.channel(); // 获取客户端通道
                            // 清空操作
                            byteBuffer.clear(); // 清空字节缓冲区
                            // 读取
                            int read = client.read(byteBuffer); // 读取字节缓冲区,返回值为读取到的字节数,如果返回-1,则表示连接已关闭
                            if (read > 0) { // 如果读取到数据
                                // 丢弃换行符
                                String str = new String(byteBuffer.array(), 0, read - 1); // 将字节缓冲区转换为字符串
                                // 通知到TCPServer
                                clientHandlerCallback.onNewMessageArrived(ClientHandler.this, str); // 通知客户端处理回调
                            } else { // 如果读取到数据
                                System.out.println("客户端已无法读取数据！"); // 打印客户端已无法读取数据信息
                                // 退出当前客户端
                                ClientHandler.this.exitBySelf(); // 退出当前客户端
                                break;
                            }
                        }
                    }
                } while (!done); // 循环直到完成
            } catch (Exception e) { // 捕获异常
                if (!done) { // 如果未完成
                    System.out.println("连接异常断开");
                    ClientHandler.this.exitBySelf(); // 退出当前客户端
                }
            } finally { // 最终执行
                // 连接关闭
                CloseUtils.close(selector);
            }
        }

        void exit() { // 退出客户端读取处理
            done = true; // 设置完成
            selector.wakeup(); // 唤醒当前的阻塞
            CloseUtils.close(selector); // 关闭选择器
        }
    }

    class ClientWriteHandler { // 客户端写入处理
        private boolean done = false; // 是否完成
        private final Selector selector; // 选择器  输出流
        private final ByteBuffer byteBuffer; // 字节缓冲区
        private final ExecutorService executorService; // 线程池

        ClientWriteHandler(Selector selector) { // 构造方法
            this.selector = selector; // 初始化选择器
            this.byteBuffer = ByteBuffer.allocate(256); // 初始化字节缓冲区
            this.executorService = Executors.newSingleThreadExecutor(); // 初始化线程池
        }

        void exit() { // 退出客户端写入处理
            done = true; // 设置完成
            CloseUtils.close(selector); // 关闭选择器
            executorService.shutdownNow(); // 关闭线程池
        }

        void send(String str) { // 发送消息
            if (done) { // 如果完成
                return; // 返回
            }
            executorService.execute(new WriteRunnable(str)); // 执行写入任务
        }

        class WriteRunnable implements Runnable { // 写入任务
            private final String msg; // 消息

            WriteRunnable(String msg) { // 构造方法
                this.msg = msg + '\n'; // 初始化消息
            }

            @Override // 重写run方法，当写入任务启动时，执行
            public void run() { // 重写run方法，当写入任务启动时，执行
                if (ClientWriteHandler.this.done) { // 如果完成
                    return; // 返回
                }

                byteBuffer.clear(); // 清空字节缓冲区
                byteBuffer.put(msg.getBytes()); // 将消息转换为字节，当前指针等于长度值，所以需要先put，再flip
                // 反转操作, 重点
                byteBuffer.flip(); // 反转操作，将写模式转换为读模式，开始和结束位置互换

                while (!done && byteBuffer.hasRemaining()) { // 循环直到完成，当前指针小于结束位置，则表示有数据可写
                    try {
                        int len = socketChannel.write(byteBuffer); // 写入字节缓冲区
                        // len = 0 合法，表示写入成功，但是没有数据可写，非阻塞IO有可能并不是真正写入成功，所以需要循环判断
                        if (len < 0) { // 如果写入失败
                            System.out.println("客户端已无法发送数据！"); // 打印客户端已无法发送数据信息
                            ClientHandler.this.exitBySelf(); // 退出当前客户端
                            break; // 退出循环
                        }
                    } catch (Exception e) { // 捕获异常
                        e.printStackTrace(); // 打印异常信息
                    }
                }
            }
        }
    }
}
