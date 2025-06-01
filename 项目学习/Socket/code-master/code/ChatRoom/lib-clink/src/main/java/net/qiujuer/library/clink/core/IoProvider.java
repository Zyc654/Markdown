package net.qiujuer.library.clink.core;

import java.io.Closeable;
import java.nio.channels.SocketChannel;

public interface IoProvider extends Closeable {
    boolean registerInput(SocketChannel channel, HandleInputCallback callback); // 注册输入

    boolean registerOutput(SocketChannel channel, HandleOutputCallback callback); // 注册输出

    void unRegisterInput(SocketChannel channel); // 解除注册输入

    void unRegisterOutput(SocketChannel channel); // 解除注册输出

    abstract class HandleInputCallback implements Runnable { // 输入回调
        @Override
        public final void run() { // 运行
            canProviderInput(); // 提供输入
        }

        protected abstract void canProviderInput(); // 提供输入
    }

    abstract class HandleOutputCallback implements Runnable { // 输出回调
        /*
        private Object attach; // 附件
        public Object getAttach() { // 获取附件
            return attach; // 返回附件
        }

        public void setAttach(Object attach) { // 设置附件
            this.attach = attach; // 设置附件
        }
        */
        @Override
        public final void run() { // 运行
            canProviderOutput(); // 提供输出
        }

        protected abstract void canProviderOutput(); // 提供输出
    }

}
