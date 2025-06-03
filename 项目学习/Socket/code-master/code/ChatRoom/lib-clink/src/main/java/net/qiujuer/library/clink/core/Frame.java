package net.qiujuer.library.clink.core;

import java.io.IOException;

/**
 * 帧-分片使用
 */
public abstract class Frame {
    // 帧头长度
    public static final int FRAME_HEADER_LENGTH = 6;
    // 单帧最大容量 64KB
    public static final int MAX_CAPACITY = 64 * 1024 - 1;


    // Packet头信息帧
    public static final byte TYPE_PACKET_HEADER = 11;
    // Packet数据分片信息帧
    public static final byte TYPE_PACKET_ENTITY = 12;
    // 指令-发送取消
    public static final byte TYPE_COMMAND_SEND_CANCEL = 41;
    // 指令-接受拒绝
    public static final byte TYPE_COMMAND_RECEIVE_REJECT = 42;

    // Flag标记
    public static final byte FLAG_NONE = 0;

    // 头部6字节固定
    protected final byte[] header = new byte[FRAME_HEADER_LENGTH];

    public Frame(int length, byte type, byte flag, short identifier) {
        if (length < 0 || length > MAX_CAPACITY) {// 如果长度小于0或大于最大容量，则抛出异常
            throw new RuntimeException("The Body length of a single frame should be between 0 and " + MAX_CAPACITY);
        }

        if (identifier < 1 || identifier > 255) {// 如果标识符小于1或大于255，则抛出异常
            throw new RuntimeException("The Body identifier of a single frame should be between 1 and 255");
        }

        // 00000000 00000000 00000000 01000000
        header[0] = (byte) (length >> 8);// 将长度右移8位,这里仅仅提取后两个字节
        header[1] = (byte) (length);// 设置长度

        header[2] = type;// 设置类型
        header[3] = flag;// 设置标志

        header[4] = (byte) identifier;// 设置标识符
        header[5] = 0;// 设置保留字节

    }

    public Frame(byte[] header) {
        System.arraycopy(header, 0, this.header, 0, FRAME_HEADER_LENGTH);// 复制头部数据
    }

    /**
     * 获取Body的长度
     *
     * @return 当前帧Body总长度[0~MAX_CAPACITY]
     */
    public int getBodyLength() {
        // 00000000
        // 01000000

        // 00000000 00000000 00000000 01000000

        // 01000000   直接拼接前面的位会变为1，所以需要&0xFF (自动补齐前面为 1)
        // 11111111 11111111 11111111 01000000  
        // 00000000 00000000 00000000 11111111 0xFF
        // 00000000 00000000 00000000 01000000

        return ((((int) header[0]) & 0xFF) << 8) | (((int) header[1]) & 0xFF);// 返回Body的长度
    }


    /**
     * 获取Body的类型
     *
     * @return 类型[0~255]
     */
    public byte getBodyType() {
        return header[2];// 返回Body的类型
    }

    /**
     * 获取Body的Flag
     *
     * @return Flag
     */
    public byte getBodyFlag() {
        return header[3];// 返回Body的标志
    }

    /**
     * 获取Body的唯一标志
     *
     * @return 标志[0~255]
     */
    public short getBodyIdentifier() {
        return (short) (((short) header[4]) & 0xFF);// 返回Body的标识符 
    }

    /**
     * 进行数据读或写操作
     *
     * @param args 数据
     * @return 是否已消费完全， True：则无需再传递数据到Frame或从当前Frame读取数据
     */
    public abstract boolean handle(IoArgs args) throws IOException;


    /**
     * 基于当前帧尝试构建下一份待消费的帧
     *
     * @return NULL：没有待消费的帧
     */
    public abstract Frame nextFrame(); // 返回下一帧
    // 64MB 64KB 1024+1 6，每个帧是 64KB 需要 1024+1个帧，header独立一个帧，一个帧6字节

    public abstract int getConsumableLength(); // 返回可消费的长度
}
