package net.qiujuer.library.clink.frames;

import net.qiujuer.library.clink.core.Frame;
import net.qiujuer.library.clink.core.IoArgs;

import java.io.IOException;

public abstract class AbsSendFrame extends Frame {
    // 帧头可读写区域大小
    volatile byte headerRemaining = Frame.FRAME_HEADER_LENGTH;
    //volatile 保证可见性，保证线程之间的可见性，保证线程之间的同步
    // 帧体可读写区域大小
    volatile int bodyRemaining;

    public AbsSendFrame(int length, byte type, byte flag, short identifier) {
        super(length, type, flag, identifier); // 调用父类构造函数
        bodyRemaining = length; // 设置帧体可读写区域大小
    }

    @Override
    //synchronized 保证线程安全，保证线程之间的同步
    public synchronized boolean handle(IoArgs args) throws IOException {
        try {
            args.limit(headerRemaining + bodyRemaining); // 设置可读写区域大小
            args.startWriting(); // 开始写入

            if (headerRemaining > 0 && args.remained()) { // 还有可读区域
                headerRemaining -= consumeHeader(args); // 消费头部
            }

            if (headerRemaining == 0 && args.remained() && bodyRemaining > 0) {
                bodyRemaining -= consumeBody(args); // 消费帧体
            }

            return headerRemaining == 0 && bodyRemaining == 0; //返回1 代表全部消费完全，0代表未消费完全
        } finally {
            args.finishWriting(); // 完成写入
        }
    }

    @Override
    public int getConsumableLength() {
        return headerRemaining + bodyRemaining; // 返回可消费的长度
    }

    private byte consumeHeader(IoArgs args) { // 消费头部
        int count = headerRemaining; // 获取头部剩余长度
        int offset = header.length - count; // 获取头部偏移量
        return (byte) args.readFrom(header, offset, count); // 读取头部
    }

    protected abstract int consumeBody(IoArgs args) throws IOException;

    /**
     * 是否已经处于发送数据中，如果已经发送了部分数据则返回True
     * 只要头部数据已经开始消费，则肯定已经处于发送数据中
     *
     * @return True，已发送部分数据
     */
    protected synchronized boolean isSending() {
        return headerRemaining < Frame.FRAME_HEADER_LENGTH; // 如果头部剩余小于6字节，则返回True
    }
}
