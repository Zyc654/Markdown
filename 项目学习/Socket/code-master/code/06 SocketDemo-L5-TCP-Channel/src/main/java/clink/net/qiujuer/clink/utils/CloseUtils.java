package clink.net.qiujuer.clink.utils;

import java.io.Closeable;
import java.io.IOException;

public class CloseUtils { // 关闭工具类
    public static void close(Closeable... closeables) { // 关闭资源
        if (closeables == null) {
            return; // 如果资源为空，则返回
        }
        for (Closeable closeable : closeables) {
            try {
                closeable.close(); // 关闭资源
            } catch (IOException e) {
                e.printStackTrace(); // 打印异常信息
            }
        }
    }
}
