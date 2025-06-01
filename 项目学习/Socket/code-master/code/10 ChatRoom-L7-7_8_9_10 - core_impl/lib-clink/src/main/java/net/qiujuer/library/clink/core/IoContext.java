package net.qiujuer.library.clink.core;

import java.io.IOException;

public class IoContext { // 上下文
    private static IoContext INSTANCE; // 单例
    private final IoProvider ioProvider; // IO提供者 全局单例

    private IoContext(IoProvider ioProvider) { // 构造方法
        this.ioProvider = ioProvider; // 设置IO提供者
    }

    public IoProvider getIoProvider() { // 获取IO提供者
        return ioProvider; // 返回IO提供者
    }

    public static IoContext get() { // 获取上下文
        return INSTANCE; // 返回上下文
    }

    public static StartedBoot setup() { // 设置
        return new StartedBoot(); // 返回启动器
    }

    public static void close() throws IOException { // 关闭
        if (INSTANCE != null) { // 如果上下文不为空
            INSTANCE.callClose(); // 调用关闭方法
        }
    }

    private void callClose() throws IOException { // 调用关闭方法
        ioProvider.close(); // 关闭IO提供者
    }

    public static class StartedBoot { // 启动器
        private IoProvider ioProvider; // IO提供者

        private StartedBoot() { // 构造方法

        }

        public StartedBoot ioProvider(IoProvider ioProvider) { // 设置IO提供者
            this.ioProvider = ioProvider; // 设置IO提供者
            return this; // 返回启动器
        }

        public IoContext start() { // 启动
            INSTANCE = new IoContext(ioProvider); // 创建上下文
            return INSTANCE; // 返回上下文
        }
    }
}
