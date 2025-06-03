package net.qiujuer.library.clink.impl;

import net.qiujuer.library.clink.core.IoArgs;
import net.qiujuer.library.clink.core.IoProvider;
import net.qiujuer.library.clink.core.Receiver;
import net.qiujuer.library.clink.core.Sender;
import net.qiujuer.library.clink.utils.CloseUtils;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean; // 原子布尔值

public class SocketChannelAdapter implements Sender, Receiver, Cloneable { // 套接字通道适配器
    private final AtomicBoolean isClosed = new AtomicBoolean(false); // 是否关闭
    private final SocketChannel channel; // 套接字通道
    private final IoProvider ioProvider; // IO提供者
    private final OnChannelStatusChangedListener listener; // 通道状态改变监听器

    private IoArgs.IoArgsEventListener receiveIoEventListener; // 接收IO事件监听器
    private IoArgs.IoArgsEventListener sendIoEventListener; // 发送IO事件监听器

    public SocketChannelAdapter(SocketChannel channel, IoProvider ioProvider,
                                OnChannelStatusChangedListener listener) throws IOException { // 构造方法
        this.channel = channel; // 设置套接字通道
        this.ioProvider = ioProvider; // 设置IO提供者
        this.listener = listener; // 设置通道状态改变监听器

        channel.configureBlocking(false); // 设置非阻塞
    }


    @Override
    public boolean receiveAsync(IoArgs.IoArgsEventListener listener) throws IOException { // 接收异步数据
        if (isClosed.get()) { // 如果通道已关闭
            throw new IOException("Current channel is closed!"); // 抛出异常
        }

        receiveIoEventListener = listener; // 设置接收IO事件监听器

        return ioProvider.registerInput(channel, inputCallback); // 注册输入
    }

    @Override
    public boolean sendAsync(IoArgs args, IoArgs.IoArgsEventListener listener) throws IOException { // 发送异步数据
        if (isClosed.get()) { // 如果通道已关闭
            throw new IOException("Current channel is closed!"); // 抛出异常
        }

        sendIoEventListener = listener; // 设置发送IO事件监听器
        // 当前发送的数据附加到回调中
        outputCallback.setAttach(args); // 设置发送数据
        return ioProvider.registerOutput(channel, outputCallback); // 注册输出
    }

    @Override // 重写close方法，使用原子布尔值
    public void close() throws IOException { // 关闭
        if (isClosed.compareAndSet(false, true)) { // 如果通道未被关闭，设置为true
            // 解除注册回调
            ioProvider.unRegisterInput(channel); // 取消注册输入
            ioProvider.unRegisterOutput(channel); // 取消注册输出
            // 关闭通道
            CloseUtils.close(channel); // 关闭通道
            // 回调当前Channel已关闭
            listener.onChannelClosed(channel); // 回调当前通道已关闭
        }
    }

    private final IoProvider.HandleInputCallback inputCallback = new IoProvider.HandleInputCallback() { // 输入回调
        @Override // 重写canProviderInput方法
        protected void canProviderInput() { // 重写canProviderInput方法
            if (isClosed.get()) { // 如果通道已关闭
                return;
            }

            IoArgs args = new IoArgs(); // 创建IoArgs
            IoArgs.IoArgsEventListener listener = SocketChannelAdapter.this.receiveIoEventListener; // 获取接收IO事件监听器
            //转换为匿名内部类局部变量，避免内存泄漏
            if (listener != null) {
                listener.onStarted(args); // 回调接收IO事件
            }

            try {
                // 具体的读取操作
                if (args.read(channel) > 0 && listener != null) { // 如果读取到数据且监听器不为空
                    // 读取完成回调
                    listener.onCompleted(args); // 回调接收IO事件
                } else { //在可读事件中，如果读取到数据为0，则抛出异常
                    throw new IOException("Cannot read any data!"); // 抛出异常
                }
            } catch (IOException ignored) { // 捕获IO异常
                CloseUtils.close(SocketChannelAdapter.this); // 关闭适配器，关闭自己
            }


        }
    };


    private final IoProvider.HandleOutputCallback outputCallback = new IoProvider.HandleOutputCallback() { // 输出回调
        @Override // 重写canProviderOutput方法
        protected void canProviderOutput(Object attach) { // 重写canProviderOutput方法
            if (isClosed.get()) { // 如果通道已关闭
                return; // 返回
            }
            // TODO
            sendIoEventListener.onCompleted(null); // 回调发送IO事件
        }
    };


    public interface OnChannelStatusChangedListener { // 通道状态改变监听器
        void onChannelClosed(SocketChannel channel); // 通道已关闭
    }
}
