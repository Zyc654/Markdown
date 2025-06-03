package net.qiujuer.library.clink.impl.async;

import net.qiujuer.library.clink.core.IoArgs;
import net.qiujuer.library.clink.core.ReceiveDispatcher;
import net.qiujuer.library.clink.core.ReceivePacket;
import net.qiujuer.library.clink.core.Receiver;
import net.qiujuer.library.clink.utils.CloseUtils;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 接收调度
 */
public class AsyncReceiveDispatcher implements ReceiveDispatcher,
        IoArgs.IoArgsEventProcessor, AsyncPacketWriter.PacketProvider { // 异步接收调度器
    private final AtomicBoolean isClosed = new AtomicBoolean(false); // 是否关闭

    private final Receiver receiver; // 接收器
    private final ReceivePacketCallback callback; // 接收包回调

    /*
     * private ReceivePacket packetTemp;
     * private byte[] buffer;
     * private int total;
     * private int position;
     * public AsyncReceiveDispatcher(Receiver receiver, ReceivePacketCallback callback) {
     *     this.receiver = receiver;
     *     this.receiver.setReceiveListener(this);
     *     this.callback = callback;
     * }
     *
     * //完成数据接收操作
     * private void completedPacket(ReceivePacket packet, boolean isSucceed) {
     *     ReceivePacket packet = this.packetTemp;
     *     CloseUtils.close(packet);
     *     callback.onReceivePacketCompleted(packet);
     * }
     * 
     * private IoArgs.IoArgsEventLister receiveIoEventLister = new IoArgs.IoArgsEventLister() {
     *     @Override
     *     public void onStared(IoArgs args) {
     *         int receiveSize;
     *         if(packetTemp == null) {
     *             receiveSize = args.length(); 
     *         }else {
     *              receiveSize = Math.min(total - position, args.capacity());
     *         }
     *          //设置本次接收数据大小
     *          args.limit(receiveSize);
     *     }
     * 
     *     @Override
     *     public void onCompleted(IoArgs args) {
     *              assemblePacket(args);
     *              //继续接收吓一跳数据
     *              registerReceive();
     *         }
     * 
     * }
     * //解析数据到 packet
     * private void assemblePacket(IoArgs args) {
     *     if (packetTemp == null) {
     *         int length = args.length();
     *         packetTemp = new StringReceivePacket(length);
     *         buffer = new byte[length];
     *         total = 0;
     *         position = 0;
     *     }
     *     int count = args.writeTo(buffer, position);
     *     if(count > 0) {
     *         packetTemp.save(buffer, count);

     *         position += count;
     *         //检查是否完成一份 packet
     *         if(position == total) {
     *             completedPacket(packetTemp, true);
     *             packetTemp = null;
     *         }
     *     }
     * }
     * 
     * 
    */



    private AsyncPacketWriter writer = new AsyncPacketWriter(this); // 异步包写入器

    public AsyncReceiveDispatcher(Receiver receiver, ReceivePacketCallback callback) { // 构造方法
        this.receiver = receiver; // 设置接收器
        this.receiver.setReceiveListener(this); // 设置接收监听器
        this.callback = callback; // 设置接收包回调
    }

    /**
     * 开始进入接收方法
     */
    @Override
    public void start() {
        registerReceive(); // 注册接收
    }

    /**
     * 停止接收数据
     */
    @Override
    public void stop() {

    }

    /**
     * 关闭操作，关闭相关流
     */
    @Override
    public void close() {
        if (isClosed.compareAndSet(false, true)) { // 如果未关闭
            writer.close(); // 关闭写入器
            /*
             * ReceivePacket packetTemp = this.packetTemp;
             * if(packetTemp != null) {
             *     packetTemp = null;   
             *     CloseUtils.close(packetTemp); // 关闭当前对象
             * }
            */
        }
    }

    /**
     * 自主发起的关闭操作，并且需要进行通知
     */
    private void closeAndNotify() {
        CloseUtils.close(this); // 关闭当前对象
    }

    /**
     * 注册接收数据
     */
    private void registerReceive() {
        try {
            receiver.postReceiveAsync(); // 异步接收
        } catch (IOException e) {
            closeAndNotify(); // 关闭并通知
        }
    }

    /**
     * 网络接收就绪，此时可以读取数据，需要返回一个容器用于容纳数据
     *
     * @return 用以容纳数据的IoArgs
     */
    @Override
    public IoArgs provideIoArgs() {
        return writer.takeIoArgs(); // 提供IoArgs
    }

    /**
     * 接收数据失败
     *
     * @param args IoArgs
     * @param e    异常信息
     */
    @Override
    public void onConsumeFailed(IoArgs args, Exception e) {
        e.printStackTrace(); // 打印异常信息
    }

    /**
     * 接收数据成功
     *
     * @param args IoArgs
     */
    @Override
    public void onConsumeCompleted(IoArgs args) {
        // 有数据则重复消费
        do {
            writer.consumeIoArgs(args); // 消费IoArgs
        } while (args.remained()); // 如果还有剩余
        registerReceive(); // 注册接收
    }

    /**
     * 构建Packet操作，根据类型、长度构建一份用于接收数据的Packet
     */
    @Override
    public ReceivePacket takePacket(byte type, long length, byte[] headerInfo) {
        return callback.onArrivedNewPacket(type, length); // 构建Packet
    }

    /**
     * 当Packet接收数据完成或终止时回调
     *
     * @param packet    接收包
     * @param isSucceed 是否成功接收完成
     */
    @Override
    public void completedPacket(ReceivePacket packet, boolean isSucceed) {
        CloseUtils.close(packet); // 关闭Packet
        callback.onReceivePacketCompleted(packet); // 接收包完成回调
    }
}
