package net.qiujuer.library.clink.impl;

import net.qiujuer.library.clink.core.IoArgs;
import net.qiujuer.library.clink.core.IoProvider;
import net.qiujuer.library.clink.core.Receiver;
import net.qiujuer.library.clink.core.Sender;
import net.qiujuer.library.clink.utils.CloseUtils;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketChannelAdapter implements Sender, Receiver, Cloneable { // 套接字通道适配器
    private final AtomicBoolean isClosed = new AtomicBoolean(false); // 是否关闭
    private final SocketChannel channel; // 套接字通道
    private final IoProvider ioProvider; // 输入输出提供者
    private final OnChannelStatusChangedListener listener; // 通道状态改变监听器

    private IoArgs.IoArgsEventProcessor receiveIoEventProcessor; // 接收IoArgs事件处理器
    private IoArgs.IoArgsEventProcessor sendIoEventProcessor; // 发送IoArgs事件处理器
    /*
    private IoArgs receiveTemp; // 接收临时IoArgs
    private IoArgs sendTemp; // 发送临时IoArgs
    */
    public SocketChannelAdapter(SocketChannel channel, IoProvider ioProvider,
                                OnChannelStatusChangedListener listener) throws IOException { // 构造方法
        this.channel = channel; // 设置套接字通道
        this.ioProvider = ioProvider; // 设置输入输出提供者
        this.listener = listener; // 设置通道状态改变监听器

        channel.configureBlocking(false); // 设置非阻塞
    }

    @Override
    public void setReceiveListener(IoArgs.IoArgsEventProcessor processor) { // 设置接收IoArgs事件处理器
        receiveIoEventProcessor = processor; // 设置接收IoArgs事件处理器
    }

    @Override
    public boolean postReceiveAsync() throws IOException { // 异步接收
        if (isClosed.get()) { // 如果通道已关闭
            throw new IOException("Current channel is closed!"); // 抛出IOException异常
        }

        /*
        sendIoEventProcessor = listener; // 设置发送IoArgs事件处理器
        //当前发送的数据附加到回调中
        outputCallback.setAttach(args); // 设置输出回调
        */

        return ioProvider.registerInput(channel, inputCallback); // 注册输入
    }

    @Override
    public void setSendListener(IoArgs.IoArgsEventProcessor processor) { // 设置发送IoArgs事件处理器
        sendIoEventProcessor = processor; // 设置发送IoArgs事件处理器
    }

    @Override
    public boolean postSendAsync() throws IOException { // 异步发送
        if (isClosed.get()) { // 如果通道已关闭
            throw new IOException("Current channel is closed!"); // 抛出IOException异常
        }

        // 当前发送的数据附加到回调中
        return ioProvider.registerOutput(channel, outputCallback); // 注册输出
    }

    @Override
    public void close() throws IOException { // 关闭
        if (isClosed.compareAndSet(false, true)) { // 如果通道未关闭
            // 解除注册回调
            ioProvider.unRegisterInput(channel); // 解除注册输入
            ioProvider.unRegisterOutput(channel); // 解除注册输出
            // 关闭
            CloseUtils.close(channel); // 关闭通道
            // 回调当前Channel已关闭
            listener.onChannelClosed(channel); // 回调当前通道已关闭
        }
    }

    private final IoProvider.HandleInputCallback inputCallback = new IoProvider.HandleInputCallback() { // 输入回调
        @Override
        protected void canProviderInput() {
            if (isClosed.get()) { // 如果通道已关闭
                return; // 返回
            }

            /*
             * IoArgs args = receiveTemp;
             * IoArgs.IoArgsEventLister lister = SocketChannelAdapter.this.receiveIoEventLister;
             * lister.onStared(args);
             * try {
             *     if(args.readFrom(channel) > 0) {
             *         lister.onCompleted(args);
             *     }else {
             *         throw new IOException("Cannot read any data!");
             *     }
            */

            IoArgs.IoArgsEventProcessor processor = receiveIoEventProcessor; // 获取接收IoArgs事件处理器
            IoArgs args = processor.provideIoArgs(); // 获取IoArgs

            try {
                if (args == null) { // 如果IoArgs为空
                    processor.onConsumeFailed(null, new IOException("ProvideIoArgs is null.")); // 消费失败
                } else if (args.readFrom(channel) > 0) { // 如果读取到数据
                    // 读取完成回调
                    processor.onConsumeCompleted(args);
                } else {
                    processor.onConsumeFailed(args, new IOException("Cannot read any data!")); // 消费失败
                }
            } catch (IOException ignored) { // 忽略IOException异常
                CloseUtils.close(SocketChannelAdapter.this); // 关闭当前通道适配器
            }
        }
    };


    private final IoProvider.HandleOutputCallback outputCallback = new IoProvider.HandleOutputCallback() { // 输出回调
        @Override //真实的回调
        protected void canProviderOutput() {
            if (isClosed.get()) { // 如果通道已关闭
                return; // 返回
            }
            /*
            IoArgs args = (IoArgs) getAttach(); // 获取IoArgs,当前发送的数据附加到回调中
            IoArgs.IoArgsEventLister lister = sendIoEventLister // 获取发送IoArgs事件处理器
            lister.onStared(args); // 开始写入
            try {
                if(args.writeTo(channel) > 0) {
                    lister.onCompleted(args); // 读取完成回调
                }else {
                    throw new IOException("Cannot write any data!");
                }
            } catch (IOException e) {
                CloseUtils.close(SocketChannelAdapter.this); // 关闭当前通道适配器
                }
            }
            */

            IoArgs.IoArgsEventProcessor processor = sendIoEventProcessor; // 获取发送IoArgs事件处理器
            IoArgs args = processor.provideIoArgs(); // 获取IoArgs

            try {
                if (args == null) { // 如果IoArgs为空
                    processor.onConsumeFailed(null, new IOException("ProvideIoArgs is null.")); // 消费失败
                } else if (args.writeTo(channel) > 0) { // 如果写入到数据
                    // 输出完成回调
                    processor.onConsumeCompleted(args);
                } else {
                    processor.onConsumeFailed(args, new IOException("Cannot write any data!")); // 消费失败
                }
            } catch (IOException ignored) { // 忽略IOException异常
                CloseUtils.close(SocketChannelAdapter.this); // 关闭当前通道适配器
            }
        }
    };


    public interface OnChannelStatusChangedListener { // 通道状态改变监听器
        void onChannelClosed(SocketChannel channel); // 通道已关闭
    }
}
