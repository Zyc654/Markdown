package net.qiujuer.library.clink.box;

import java.io.ByteArrayOutputStream;


/**
 * 字符串接收包
 */
public class StringReceivePacket extends AbsByteArrayReceivePacket<String> { // 字符串接收包

    public StringReceivePacket(long len) { // 构造方法
        super(len); // 调用父类构造方法
    }

    @Override // 重写父类方法
    protected String buildEntity(ByteArrayOutputStream stream) { // 构建实体
        return new String(stream.toByteArray()); // 将字节数组转换为字符串
    }

    @Override
    public byte type() { // 获取类型
        return TYPE_MEMORY_STRING; // 返回类型
    }

}
