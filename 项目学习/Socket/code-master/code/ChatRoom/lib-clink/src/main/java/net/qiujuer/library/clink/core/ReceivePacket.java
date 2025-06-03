package net.qiujuer.library.clink.core;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 接收包的定义，网络层接收都是基于流，所以需要定义流和实体
 */
public abstract class ReceivePacket<Stream extends OutputStream, Entity> extends Packet<Stream> { // 接收包
    // 定义当前接收包最终的实体
    private Entity entity; // 实体

    public ReceivePacket(long len) { // 构造方法
        this.length = len; // 设置长度
    }

    /**
     * 得到最终接收到的数据实体
     *
     * @return 数据实体
     */
    public Entity entity() { // 获取实体
        return entity; // 返回实体
    }

    /**
     * 根据接收到的流转化为对应的实体
     *
     * @param stream {@link OutputStream}
     * @return 实体
     */
    protected abstract Entity buildEntity(Stream stream); // 构建实体

    /**
     * 先关闭流，随后将流的内容转化为对应的实体
     *
     * @param stream 待关闭的流
     * @throws IOException IO异常
     */
    @Override
    protected final void closeStream(Stream stream) throws IOException { // 关闭流
        super.closeStream(stream); // 关闭流
        entity = buildEntity(stream); // 构建实体
    }
} // 接收包
