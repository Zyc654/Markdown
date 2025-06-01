package net.qiujuer.library.clink.core;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

@SuppressWarnings("Duplicates")
public class IoArgs {
    private int limit = 256; // 真正的本次的容纳空间，buffer.limit(limit)
    private ByteBuffer buffer = ByteBuffer.allocate(256); // 缓冲区 

    /**
     * 从bytes数组进行消费
     * 文件传输部分
     */
    public int readFrom(byte[] bytes, int offset, int count) {  //文件传输部分
        int size = Math.min(count, buffer.remaining());
        if (size <= 0) {
            return 0;
        }
        buffer.put(bytes, offset, size);
        return size;
    }

    /**
     * 写入数据到bytes中
     * 文件传输部分
     */
    public int writeTo(byte[] bytes, int offset) {
        int size = Math.min(bytes.length - offset, buffer.remaining());
        buffer.get(bytes, offset, size);
        return size;
    }

    /**
     * 从bytes中读取数据
     */
    public int readFrom(ReadableByteChannel channel) throws IOException {
        int bytesProduced = 0;
        while (buffer.hasRemaining()) {
            int len = channel.read(buffer);
            if (len < 0) {
                throw new EOFException();
            }
            bytesProduced += len;
        }
        return bytesProduced;
    }

    /**
     * 写入数据到bytes中
     */
    public int writeTo(WritableByteChannel channel) throws IOException {
        int bytesProduced = 0;
        while (buffer.hasRemaining()) {
            int len = channel.write(buffer);
            if (len < 0) {
                throw new EOFException();
            }
            bytesProduced += len;
        }
        return bytesProduced;
    }

    /**
     * 从SocketChannel读取数据
     */
    public int readFrom(SocketChannel channel) throws IOException {
        startWriting();

        int bytesProduced = 0; // 真实写入的数据量
        while (buffer.hasRemaining()) {
            int len = channel.read(buffer);
            if (len < 0) {
                throw new EOFException();
            }
            bytesProduced += len;
        }

        finishWriting();
        return bytesProduced;
    }

    /**
     * 写数据到SocketChannel
     */
    public int writeTo(SocketChannel channel) throws IOException {
        int bytesProduced = 0; // 真实写入的数据量  
        while (buffer.hasRemaining()) { // 如果缓冲区还有剩余空间
            int len = channel.write(buffer); // 写入数据
            if (len < 0) { // 如果写入数据失败
                throw new EOFException(); // 抛出EOFException异常
            }
            bytesProduced += len; // 累加写入的数据量
        }
        return bytesProduced; // 返回真实写入的数据量
    }

    /**
     * 开始写入数据到IoArgs
     */
    public void startWriting() {
        buffer.clear();
        // 定义容纳区间
        buffer.limit(limit);// 设置容纳区间
    }

    /**
     * 写完数据后调用
     */
    public void finishWriting() { //文件传输所用
        buffer.flip(); //将写操作变为读操作，记得一定要反转指针的位置
    }

    /**
     * 设置单次写操作的容纳区间
     *
     * @param limit 区间大小
     */
    public void limit(int limit) {
        this.limit = Math.min(limit, buffer.capacity());
    }

    public int readLength() {
        return buffer.getInt();
    }

    public int capacity() {
        return buffer.capacity();
    }

    public boolean remained() { //文件传输所用
        return buffer.remaining() > 0;
    }

    /**
     * 填充数据
     *
     * @param size 想要填充数据的长度
     * @return 真实填充数据的长度
     */
    public int fillEmpty(int size) {
        int fillSize = Math.min(size, buffer.remaining());
        buffer.position(buffer.position() + fillSize);
        return fillSize;
    }

    /**
     * 清空部分数据
     *
     * @param size 想要清空的数据长度
     * @return 真实清空的数据长度
     */
    public int setEmpty(int size) {
        int emptySize = Math.min(size, buffer.remaining());
        buffer.position(buffer.position() + emptySize);
        return emptySize;
    }


    /**
     * IoArgs 提供者、处理者；数据的生产或消费者
     */
    public interface IoArgsEventProcessor {
        /**
         * 提供一份可消费的IoArgs
         *
         * @return IoArgs
         */
        IoArgs provideIoArgs();

        /**
         * 消费失败时回调
         *
         * @param args IoArgs
         * @param e    异常信息
         */
        void onConsumeFailed(IoArgs args, Exception e);

        /**
         * 消费成功
         *
         * @param args IoArgs
         */
        void onConsumeCompleted(IoArgs args);
    }
}
