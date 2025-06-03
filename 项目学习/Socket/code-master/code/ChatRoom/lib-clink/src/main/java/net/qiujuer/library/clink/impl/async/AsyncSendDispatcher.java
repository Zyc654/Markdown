package net.qiujuer.library.clink.impl.async;

import net.qiujuer.library.clink.core.IoArgs;
import net.qiujuer.library.clink.core.SendDispatcher;
import net.qiujuer.library.clink.core.SendPacket;
import net.qiujuer.library.clink.core.Sender;
import net.qiujuer.library.clink.utils.CloseUtils;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class AsyncSendDispatcher implements SendDispatcher, 
        IoArgs.IoArgsEventProcessor, AsyncPacketReader.PacketProvider { // 异步发送调度器
    private final Sender sender; // 发送器
    private final Queue<SendPacket> queue = new ConcurrentLinkedQueue<>(); // 队列，非阻塞队列
    private final AtomicBoolean isSending = new AtomicBoolean(); // 是否发送
    private final AtomicBoolean isClosed = new AtomicBoolean(false); // 是否关闭

    private final AsyncPacketReader reader = new AsyncPacketReader(this); // 读取器
    private final Object queueLock = new Object(); // 队列锁

    /*
    private SendPacket packetTemp;
    private int position;
    private int total;
    private IoArgs ioArgs = new IoArgs();
    */



    public AsyncSendDispatcher(Sender sender) { // 构造方法
        this.sender = sender; // 设置发送器
        sender.setSendListener(this); // 设置发送监听器
    }

    /**
     * 发送Packet
     * 首先添加到队列，如果当前状态为未启动发送状态
     * 则，尝试让reader提取一份packet进行数据发送
     * <p>
     * 如果提取数据后reader有数据，则进行异步输出注册
     *
     * @param packet 数据
     */
    @Override
    public void send(SendPacket packet) { // 发送
        synchronized (queueLock) { // 同步队列锁
            queue.offer(packet); // 添加到队列
            if (isSending.compareAndSet(false, true)) { // 如果未发送，设置未发送状态为true
                if (reader.requestTakePacket()) { // 如果读取器请求发送，则进行异步输出注册
                    requestSend(); // 请求发送
                }
            }
        }
    }

    /**
     * 取消Packet操作
     * 如果还在队列中，代表Packet未进行发送，则直接标志取消，并返回即可
     * 如果未在队列中，则让reader尝试扫描当前发送序列，查询是否当前Packet正在发送
     * 如果是则进行取消相关操作
     *
     * @param packet 数据
     */
    @Override
    public void cancel(SendPacket packet) { // 取消
        boolean ret; // 返回值
        synchronized (queueLock) { // 同步队列锁
            ret = queue.remove(packet); // 移除包
        }
        if (ret) { // 如果移除成功
            packet.cancel(); // 取消包
            return;
        }

        reader.cancel(packet); // 取消包
    }

    /**
     * reader从当前队列中提取一份Packet
     *
     * @return 如果队列有可用于发送的数据则返回该Packet
     */
    @Override
    public SendPacket takePacket() { // 提取包
        SendPacket packet; // 包
        synchronized (queueLock) { // 同步队列锁
            packet = queue.poll(); // 移除包
            if (packet == null) { // 如果包为空，则队列为空
                // 队列为空，取消发送状态
                isSending.set(false); // 设置发送状态为false
                return null;
            }
        }

        if (packet.isCanceled()) { 
            // 已取消，不用发送
            return takePacket();
        }
        return packet; // 返回包
    }

    /*
    private void sendNextPacket() {
        packetTemp = takePacket();
        if(packetTemp != null) {
            total = packetTemp.length();
            position = 0;
            sendCurrentPacket();
        }
    }

    private void sendCurrentPacket() {
        IoArgs args = ioArgs;
        // 开始，清理
        args.startWriting();
        if(position >= total) {
            sendNextPacket();
            return ;
        }else if(position == 0) {
            //首先，需要携带长度信息
            args.writeLength(total);
        }

        byte[] bytes = packetTemp.bytes();
        // 把 bytes 的数据写入到 IoArgs
        int count = args.readFrom(bytes, position);
        position += count;
        args.finishWriting();
        try {
            sender.sendAsync(args,ioArgsEventLister);
            return ;
        } catch (IOException e) {
            closeAndNotify(); //关闭自己并通知外界
        }
    }
    //进度回调
    private final IoArgs.IoArgsEventLister ioArgsEventLister = new IoArgs.IoArgsEventLister() {
        @Override
        public void onStared(IoArgs args) {
            // 开始写入
        }
        @Override
        public void onCompleted(IoArgs args) {
            // 完成写入
            sendCurrentPacket(); //继续发送当前包
        }
    }
    */


    /**
     * 完成Packet发送
     *
     * @param isSucceed 是否成功
     */
    @Override 
    public void completedPacket(SendPacket packet, boolean isSucceed) { // 完成包
        CloseUtils.close(packet); // 关闭包
    }

    /**
     * 请求网络进行数据发送
     */
    private void requestSend() { // 请求发送
        try {
            sender.postSendAsync(); // 异步发送
        } catch (IOException e) {
            closeAndNotify(); // 关闭并通知
        }
    }

    /**
     * 请求网络发送异常时触发，进行关闭操作
     */
    private void closeAndNotify() { // 关闭并通知
        CloseUtils.close(this); // 关闭
    }

    /**
     * 关闭操作，关闭自己同时需要关闭reader
     *
     * @throws IOException 异常
     */
    @Override
    public void close() throws IOException { // 关闭
        if (isClosed.compareAndSet(false, true)) { // 如果未关闭
            isSending.set(false); // 设置发送状态为false
            /*
             * SendPacket packet = this.packetTemp;
             * if(packet != null) {
             *     packet = null;
             *     CloseUtils.close(packet);
             * } 
            */
            // reader关闭
            reader.close();
        }
    }

    /**
     * 网络发送就绪回调，当前已进入发送就绪状态，等待填充数据进行发送
     * 此时从reader中填充数据，并进行后续网络发送
     *
     * @return NULL，可能填充异常，或者想要取消本次发送
     */
    @Override
    public IoArgs provideIoArgs() { // 提供IoArgs
        return reader.fillData(); // 填充数据
    }

    /**
     * 网络发送IoArgs出现异常
     *
     * @param args IoArgs
     * @param e    异常信息
     */
    @Override
    public void onConsumeFailed(IoArgs args, Exception e) { // 消费失败
        if (args != null) { // 如果IoArgs不为空
            e.printStackTrace(); // 打印异常
        } else {
            // TODO
        }
    }

    /**
     * 网络发送IoArgs完成回调
     * 在该方法进行reader对当前队列的Packet提取，并进行后续的数据发送注册
     *
     * @param args IoArgs
     */
    @Override
    public void onConsumeCompleted(IoArgs args) { // 消费完成
        // 继续发送当前包
        if (reader.requestTakePacket()) { // 如果读取器请求发送
            requestSend(); // 请求发送
        }
    }
} // 异步发送调度器
