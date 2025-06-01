package net.qiujuer.library.clink.core;

import java.io.InputStream;

/**
 * 发送的包定义
 */
public abstract class SendPacket<T extends InputStream> extends Packet<T> {
    private boolean isCanceled; // 是否取消

    public boolean isCanceled() { // 是否取消
        return isCanceled; // 返回是否取消
    }

    /**
     * 设置取消发送标记
     */
    public void cancel() { // 取消
        isCanceled = true; // 设置取消
    }
}
