package net.qiujuer.library.clink.impl;

import net.qiujuer.library.clink.core.IoArgs;
import net.qiujuer.library.clink.core.IoProvider;
import net.qiujuer.library.clink.core.Receiver;
import net.qiujuer.library.clink.core.Sender;
import net.qiujuer.library.clink.utils.CloseUtils;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketChannelAdapter implements Sender, Receiver, Cloneable { // 实现Sender、Receiver、Cloneable接口
    private final AtomicBoolean isClosed = new AtomicBoolean(false); // 是否关闭
    private final SocketChannel channel; // 通道
    private final IoProvider ioProvider; // IO提供者
    private final OnChannelStatusChangedListener listener; // 通道状态改变监听器

    private IoArgs.IoArgsEventListener receiveIoEventListener; // 接收IO事件监听器
    private IoArgs.IoArgsEventListener sendIoEventListener; // 发送IO事件监听器

    public SocketChannelAdapter(SocketChannel channel, IoProvider ioProvider, // 构造方法
                                OnChannelStatusChangedListener listener) throws IOException {
        this.channel = channel; // 设置通道
        this.ioProvider = ioProvider; // 设置IO提供者
        this.listener = listener; // 设置通道状态改变监听器

        channel.configureBlocking(false); // 设置通道为非阻塞
    }


    @Override // 重写receiveAsync方法
    public boolean receiveAsync(IoArgs.IoArgsEventListener listener) throws IOException {
        if (isClosed.get()) { // 如果通道已关闭
            throw new IOException("Current channel is closed!"); // 抛出IO异常
        }

        receiveIoEventListener = listener; // 设置接收IO事件监听器

        return ioProvider.registerInput(channel, inputCallback); // 注册输入
    }

    @Override // 重写sendAsync方法
    public boolean sendAsync(IoArgs args, IoArgs.IoArgsEventListener listener) throws IOException {
        if (isClosed.get()) { // 如果通道已关闭
            throw new IOException("Current channel is closed!"); // 抛出IO异常
        }

        sendIoEventListener = listener; // 设置发送IO事件监听器
        // 当前发送的数据附加到回调中
        outputCallback.setAttach(args); // 设置发送数据
        return ioProvider.registerOutput(channel, outputCallback); // 注册输出
    }

    @Override // 重写close方法
    public void close() throws IOException {
        if (isClosed.compareAndSet(false, true)) { // 如果通道未关闭
            // 解除注册回调
            ioProvider.unRegisterInput(channel); // 取消注册输入
            ioProvider.unRegisterOutput(channel); // 取消注册输出
            // 关闭
            CloseUtils.close(channel); // 关闭通道
            // 回调当前Channel已关闭
            listener.onChannelClosed(channel); // 回调通道已关闭
        }
    }

    private boolean runed;

    private final IoProvider.HandleInputCallback inputCallback = new IoProvider.HandleInputCallback() { // 实现HandleInputCallback接口
        @Override // 重写canProviderInput方法
        protected void canProviderInput() {
            if (isClosed.get()) { // 如果通道已关闭
                return;
            }

            if (runed) {
                return;
            }
            runed = true; // 设置已运行
            Thread.sleep(1000); // 睡眠1秒,防止快速刷新导致重复回调

            IoArgs args = new IoArgs(); // 创建IoArgs
            IoArgs.IoArgsEventListener listener = SocketChannelAdapter.this.receiveIoEventListener; // 获取接收IO事件监听器

            if (listener != null) {
                listener.onStarted(args); // 回调开始
            }

            try {
                // 具体的读取操作
                if (args.read(channel) > 0 && listener != null) { // 如果读取成功
                    // 读取完成回调
                    listener.onCompleted(args); // 回调完成
                } else {
                    throw new IOException("Cannot read any data!"); // 抛出IO异常
                }
            } catch (IOException ignored) { // 捕获IO异常
                CloseUtils.close(SocketChannelAdapter.this); // 关闭通道
            }


        }
    };


    private final IoProvider.HandleOutputCallback outputCallback = new IoProvider.HandleOutputCallback() { // 实现HandleOutputCallback接口
        @Override // 重写canProviderOutput方法
        protected void canProviderOutput(Object attach) {
            if (isClosed.get()) { // 如果通道已关闭
                return;
            }
            // TODO
            sendIoEventListener.onCompleted(null); // 回调完成
        }
    };


    public interface OnChannelStatusChangedListener { // 实现OnChannelStatusChangedListener接口             
        void onChannelClosed(SocketChannel channel); // 通道已关闭
    }
}
