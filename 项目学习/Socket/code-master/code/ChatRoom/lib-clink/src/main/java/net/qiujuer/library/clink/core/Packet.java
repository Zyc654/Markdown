package net.qiujuer.library.clink.core;

import java.io.Closeable;
import java.io.IOException;

/**
 * 公共的数据封装
 * 提供了类型以及基本的长度的定义
 */
public abstract class Packet<Stream extends Closeable> implements Closeable {
    // BYTES 类型
    public static final byte TYPE_MEMORY_BYTES = 1; // 字节数组类型
    // String 类型
    public static final byte TYPE_MEMORY_STRING = 2; // 字符串类型
    // 文件 类型
    public static final byte TYPE_STREAM_FILE = 3; // 文件类型
    // 长链接流 类型
    public static final byte TYPE_STREAM_DIRECT = 4; // 长链接流类型

    protected long length; // 长度
    private Stream stream; // 流

    public long length() { // 获取长度
        return length;
    }

    /**
     * 对外的获取当前实例的流操作
     *
     * @return {@link java.io.InputStream} or {@link java.io.OutputStream}
     */
    public final Stream open() { // 打开
        if (stream == null) { // 如果流为空
            stream = createStream(); // 创建流
        }
        return stream; // 返回流
    }

    /**
     * 对外的关闭资源操作，如果流处于打开状态应当进行关闭
     *
     * @throws IOException IO异常
     */
    @Override
    public final void close() throws IOException { // 关闭
        if (stream != null) { // 如果流不为空
            closeStream(stream); // 关闭流
            stream = null; // 设置流为空
        }
    }

    /**
     * 类型，直接通过方法得到:
     * <p>
     * {@link #TYPE_MEMORY_BYTES}
     * {@link #TYPE_MEMORY_STRING}
     * {@link #TYPE_STREAM_FILE}
     * {@link #TYPE_STREAM_DIRECT}
     *
     * @return 类型
     */
    public abstract byte type(); // 获取类型

    /**
     * 创建流操作，应当将当前需要传输的数据转化为流
     *
     * @return {@link java.io.InputStream} or {@link java.io.OutputStream}
     */
    protected abstract Stream createStream(); // 创建流

    /**
     * 关闭流，当前方法会调用流的关闭操作
     *
     * @param stream 待关闭的流
     * @throws IOException IO异常
     */
    protected void closeStream(Stream stream) throws IOException { // 关闭流
        stream.close(); // 关闭流
    }

    /**
     * 头部额外信息，用于携带额外的校验信息等
     *
     * @return byte 数组，最大255长度
     */
    public byte[] headerInfo() { // 获取头部信息
        return null; // 返回空数组
    }
}
