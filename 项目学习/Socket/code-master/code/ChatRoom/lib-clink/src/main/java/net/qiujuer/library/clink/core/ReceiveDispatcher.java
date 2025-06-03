package net.qiujuer.library.clink.core;

import java.io.Closeable;

/**
 * 接收的数据调度封装
 * 把一份或者多分IoArgs组合成一份Packet
 */
public interface ReceiveDispatcher extends Closeable {
    void start(); // 启动

    void stop(); // 停止

    interface ReceivePacketCallback { // 接收包回调,通知上层接收包到达和完成
        ReceivePacket<?, ?> onArrivedNewPacket(byte type, long length); // 到达新的接收包

        void onReceivePacketCompleted(ReceivePacket packet); // 接收包完成
    }
} // 接收调度器
