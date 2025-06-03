package net.qiujuer.library.clink.impl;

import net.qiujuer.library.clink.core.IoProvider;
import net.qiujuer.library.clink.utils.CloseUtils;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class IoSelectorProvider implements IoProvider { // 实现IoProvider接口
    private final AtomicBoolean isClosed = new AtomicBoolean(false); // 是否关闭

    // 是否处于某个过程
    private final AtomicBoolean inRegInput = new AtomicBoolean(false); // 是否处于注册输入
    private final AtomicBoolean inRegOutput = new AtomicBoolean(false); // 是否处于注册输出

    private final Selector readSelector; // 读选择器
    private final Selector writeSelector; // 写选择器

    private final HashMap<SelectionKey, Runnable> inputCallbackMap = new HashMap<>(); // 输入回调映射
    private final HashMap<SelectionKey, Runnable> outputCallbackMap = new HashMap<>(); // 输出回调映射

    private final ExecutorService inputHandlePool; // 输入处理池
    private final ExecutorService outputHandlePool; // 输出处理池

    public IoSelectorProvider() throws IOException { // 构造方法
        readSelector = Selector.open(); // 打开读选择器
        writeSelector = Selector.open(); // 打开写选择器

        inputHandlePool = Executors.newFixedThreadPool(4, // 创建输入处理池
                new IoProviderThreadFactory("IoProvider-Input-Thread-"));
        outputHandlePool = Executors.newFixedThreadPool(4, // 创建输出处理池
                new IoProviderThreadFactory("IoProvider-Output-Thread-"));

        // 开始输出输入的监听
        startRead();
        startWrite();
    }

    private void startRead() { // 开始读取
        Thread thread = new Thread("Clink IoSelectorProvider ReadSelector Thread") { // 创建读取线程
            @Override
            public void run() {
                while (!isClosed.get()) { // 如果未关闭
                    try {
                        if (readSelector.select() == 0) { // 如果读取选择器没有选择
                            waitSelection(inRegInput); // 等待注册输入
                            continue;
                        }

                        Set<SelectionKey> selectionKeys = readSelector.selectedKeys(); // 获取读取选择器的选择键
                        for (SelectionKey selectionKey : selectionKeys) { // 遍历选择键
                            if (selectionKey.isValid()) { // 如果选择键有效
                                handleSelection(selectionKey, SelectionKey.OP_READ, inputCallbackMap, inputHandlePool); // 处理选择键
                            }
                        }
                        selectionKeys.clear(); // 清空选择键
                    } catch (IOException e) { // 捕获IO异常
                        e.printStackTrace();
                    }
                }
            }


        }; // 创建读取线程
        thread.setPriority(Thread.MAX_PRIORITY); // 设置读取线程优先级
        thread.start(); // 启动读取线程
    }

    private void startWrite() { // 开始写入
        Thread thread = new Thread("Clink IoSelectorProvider WriteSelector Thread") { // 创建写入线程
            @Override
            public void run() {
                while (!isClosed.get()) { // 如果未关闭
                    try {
                        if (writeSelector.select() == 0) { // 如果写入选择器没有选择
                            waitSelection(inRegOutput); // 等待注册输出
                            continue; // 继续
                        }

                        Set<SelectionKey> selectionKeys = writeSelector.selectedKeys(); // 获取写入选择器的选择键
                        for (SelectionKey selectionKey : selectionKeys) { // 遍历选择键
                            if (selectionKey.isValid()) { // 如果选择键有效
                                handleSelection(selectionKey, SelectionKey.OP_WRITE, outputCallbackMap, outputHandlePool); // 处理选择键
                            }
                        }
                        selectionKeys.clear(); // 清空选择键
                    } catch (IOException e) { // 捕获IO异常
                        e.printStackTrace(); // 打印异常信息
                    }
                }
            }
        }; // 创建写入线程
        thread.setPriority(Thread.MAX_PRIORITY); // 设置写入线程优先级
        thread.start(); // 启动写入线程
    }


    @Override
    public boolean registerInput(SocketChannel channel, HandleInputCallback callback) { // 注册输入
        return registerSelection(channel, readSelector, SelectionKey.OP_READ, inRegInput, // 注册读取选择器
                inputCallbackMap, callback) != null; // 注册输入回调
    }

    @Override
    public boolean registerOutput(SocketChannel channel, HandleOutputCallback callback) { // 注册输出
        return registerSelection(channel, writeSelector, SelectionKey.OP_WRITE, inRegOutput, // 注册写入选择器
                outputCallbackMap, callback) != null; // 注册输出回调
    }

    @Override
    public void unRegisterInput(SocketChannel channel) { // 取消注册输入
        unRegisterSelection(channel, readSelector, inputCallbackMap); // 取消注册读取选择器
    }

    @Override
    public void unRegisterOutput(SocketChannel channel) { // 取消注册输出
        unRegisterSelection(channel, writeSelector, outputCallbackMap); // 取消注册写入选择器
    }

    @Override // 关闭
    public void close() { // 关闭
        if (isClosed.compareAndSet(false, true)) { // 如果未关闭
            inputHandlePool.shutdown(); // 关闭输入处理池
            outputHandlePool.shutdown(); // 关闭输出处理池

            inputCallbackMap.clear(); // 清空输入回调映射
            outputCallbackMap.clear(); // 清空输出回调映射

            readSelector.wakeup(); // 唤醒读取选择器
            writeSelector.wakeup(); // 唤醒写入选择器

            CloseUtils.close(readSelector, writeSelector); // 关闭读取选择器和写入选择器
        }
    }

    private static void waitSelection(final AtomicBoolean locker) { // 等待选择
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (locker) { // 同步
            if (locker.get()) { // 如果锁定
                try {
                    locker.wait(); // 等待
                } catch (InterruptedException e) { // 捕获中断异常
                    e.printStackTrace(); // 打印异常信息
                }
            }
        }
    }


    private static SelectionKey registerSelection(SocketChannel channel, Selector selector, // 注册选择
                                                  int registerOps, AtomicBoolean locker, // 注册操作
                                                  HashMap<SelectionKey, Runnable> map, // 注册回调映射
                                                  Runnable runnable) { // 注册回调

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (locker) { // 同步
            // 设置锁定状态
            locker.set(true); // 设置锁定状态

            try {
                // 唤醒当前的selector，让selector不处于select()状态
                selector.wakeup(); // 唤醒当前的selector，让selector不处于select()状态

                SelectionKey key = null; // 选择键
                if (channel.isRegistered()) { // 如果已注册
                    // 查询是否已经注册过
                    key = channel.keyFor(selector); // 查询是否已经注册过
                    if (key != null) { // 如果选择键不为空
                        key.interestOps(key.readyOps() | registerOps); // 设置选择键的兴趣操作
                    }
                }

                if (key == null) { // 如果选择键为空
                    // 注册selector得到Key
                    key = channel.register(selector, registerOps); // 注册selector得到Key
                    // 注册回调
                    map.put(key, runnable); // 注册回调
                }

                return key;
            } catch (ClosedChannelException e) { // 捕获关闭通道异常
                return null; // 返回空
            } finally {
                // 解除锁定状态
                locker.set(false); // 设置锁定状态
                try {
                    // 通知
                    locker.notify(); // 通知
                } catch (Exception ignored) { // 捕获异常
                }
            }
        }
    }

    private static void unRegisterSelection(SocketChannel channel, Selector selector, // 取消注册选择
                                            Map<SelectionKey, Runnable> map) { // 取消注册回调映射
        if (channel.isRegistered()) { // 如果已注册
            SelectionKey key = channel.keyFor(selector); // 获取选择键
            if (key != null) { // 如果选择键不为空
                // 取消监听的方法
                key.cancel(); // 取消选择键
                map.remove(key); // 移除选择键
                selector.wakeup(); // 唤醒选择器
            }
        }
    }

    private static void handleSelection(SelectionKey key, int keyOps, // 处理选择
                                        HashMap<SelectionKey, Runnable> map, // 处理回调映射
                                        ExecutorService pool) { // 处理线程池
        // 重点
        // 取消继续对keyOps的监听，防止收到重复的回调
        key.interestOps(key.readyOps() & ~keyOps); // 取消选择键的兴趣操作

        Runnable runnable = null; // 回调
        try {
            runnable = map.get(key); // 获取回调
        } catch (Exception ignored) { // 捕获异常

        }

        if (runnable != null && !pool.isShutdown()) { // 如果回调不为空且线程池未关闭
            // 异步调度
            pool.execute(runnable); // 执行回调
        }
    }


    static class IoProviderThreadFactory implements ThreadFactory { // 实现ThreadFactory接口
        private final ThreadGroup group; // 线程组
        private final AtomicInteger threadNumber = new AtomicInteger(1); // 线程编号
        private final String namePrefix; // 线程前缀

        IoProviderThreadFactory(String namePrefix) { // 构造方法
            SecurityManager s = System.getSecurityManager(); // 获取安全管理器
            this.group = (s != null) ? s.getThreadGroup() : // 获取线程组
                    Thread.currentThread().getThreadGroup(); // 获取线程组
            this.namePrefix = namePrefix; // 设置线程前缀
        }

        public Thread newThread(Runnable r) { // 创建线程
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0); // 创建线程
            if (t.isDaemon()) // 如果线程为守护线程
                t.setDaemon(false); // 设置线程为非守护线程
            if (t.getPriority() != Thread.NORM_PRIORITY) // 如果线程优先级不为普通优先级
                t.setPriority(Thread.NORM_PRIORITY); // 设置线程优先级为普通优先级
            return t; // 返回线程
        }
    }
}
    