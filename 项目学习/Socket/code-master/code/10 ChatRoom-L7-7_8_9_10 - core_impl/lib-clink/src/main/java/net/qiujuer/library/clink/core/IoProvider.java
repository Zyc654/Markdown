package net.qiujuer.library.clink.core;

import java.io.Closeable;
import java.nio.channels.SocketChannel;

/**
 * IO提供者接口，用于处理SocketChannel的读写操作
 * 继承自Closeable接口，支持资源的关闭操作
 */
public interface IoProvider extends Closeable { 
    /**
     * 注册输入回调，当SocketChannel可读时触发
     * @param channel SocketChannel通道
     * @param callback 输入回调处理器
     * @return 是否注册成功
     */
    boolean registerInput(SocketChannel channel, HandleInputCallback callback);

    /**
     * 注册输出回调，当SocketChannel可写时触发
     * @param channel SocketChannel通道
     * @param callback 输出回调处理器
     * @return 是否注册成功
     */
    boolean registerOutput(SocketChannel channel, HandleOutputCallback callback);

    /**
     * 取消注册输入回调
     * @param channel SocketChannel通道
     */
    void unRegisterInput(SocketChannel channel);

    /**
     * 取消注册输出回调
     * @param channel SocketChannel通道
     */
    void unRegisterOutput(SocketChannel channel);

    /**
     * 输入回调处理器抽象类
     * 当SocketChannel可读时，会触发此回调
     */
    abstract class HandleInputCallback implements Runnable {
        @Override
        public final void run() {
            canProviderInput();
        }

        /**
         * 当SocketChannel可读时，会调用此方法
         * 子类需要实现此方法，处理具体的读取逻辑
         */
        protected abstract void canProviderInput(); // 处理具体的读取逻辑
    }

    /**
     * 输出回调处理器抽象类
     * 当SocketChannel可写时，会触发此回调
     */
    abstract class HandleOutputCallback implements Runnable {
        private Object attach; // 附加数据

        @Override // 重写run方法，当SocketChannel可写时，会调用此方法   
        public final void run() {
            canProviderOutput(attach);
        }

        /**
         * 设置附加数据
         * @param attach 附加数据对象
         */
        public final void setAttach(Object attach) {
            this.attach = attach;
        }

        /**
         * 当SocketChannel可写时，会调用此方法
         * 子类需要实现此方法，处理具体的写入逻辑
         * @param attach 附加数据对象
         */
        protected abstract void canProviderOutput(Object attach);
    }
}
