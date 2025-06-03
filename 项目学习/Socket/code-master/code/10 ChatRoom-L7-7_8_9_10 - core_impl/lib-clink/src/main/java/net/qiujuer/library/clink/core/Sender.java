package net.qiujuer.library.clink.core;

import java.io.Closeable;
import java.io.IOException;

public interface Sender extends Closeable { // 发送者
    boolean sendAsync(IoArgs args, IoArgs.IoArgsEventListener listener) throws IOException; // 异步发送数据
}
