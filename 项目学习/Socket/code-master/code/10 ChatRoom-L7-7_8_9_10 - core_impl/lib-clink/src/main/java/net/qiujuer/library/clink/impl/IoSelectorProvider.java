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

public class IoSelectorProvider implements IoProvider { // IO选择器提供者
    private final AtomicBoolean isClosed = new AtomicBoolean(false); // 是否关闭    

    // 是否处于某个过程
    //线程安全，用于是否处于注册输入过程或注册输出过程
    private final AtomicBoolean inRegInput = new AtomicBoolean(false); // 是否处于注册输入过程      
    private final AtomicBoolean inRegOutput = new AtomicBoolean(false); // 是否处于注册输出过程

    private final Selector readSelector; // 读选择器
    private final Selector writeSelector; // 写选择器，分开写，避免竞争

    private final HashMap<SelectionKey, Runnable> inputCallbackMap = new HashMap<>(); // 输入回调映射
    private final HashMap<SelectionKey, Runnable> outputCallbackMap = new HashMap<>(); // 输出回调映射

    private final ExecutorService inputHandlePool; // 输入处理池
    private final ExecutorService outputHandlePool; // 输出处理池

    public IoSelectorProvider() throws IOException { // 构造方法
        readSelector = Selector.open(); // 打开读选择器
        writeSelector = Selector.open(); // 打开写选择器

        inputHandlePool = Executors.newFixedThreadPool(4, // 创建输入处理池
                new IoProviderThreadFactory("IoProvider-Input-Thread-"));
        outputHandlePool = Executors.newFixedThreadPool(4,
                new IoProviderThreadFactory("IoProvider-Output-Thread-"));

        // 开始输出输入的监听
        startRead(); // 开始读取
        startWrite(); // 开始写入
    }

    private void startRead() { // 开始读取 异步操作
        Thread thread = new Thread("Clink IoSelectorProvider ReadSelector Thread") { // 创建读取线程
            @Override // 重写run方法
            public void run() { // 重写run方法
                while (!isClosed.get()) { // 循环直到关闭
                    try {
                        if (readSelector.select() == 0) {
                            waitSelection(inRegInput); // 等待注册输入，判断是否等待完成，如果等待完成则继续循环
                            continue; // 继续循环
                        }

                        Set<SelectionKey> selectionKeys = readSelector.selectedKeys(); // 获取选择器
                        for (SelectionKey selectionKey : selectionKeys) { // 遍历选择器
                            if (selectionKey.isValid()) { // 如果选择器有效
                                handleSelection(selectionKey, SelectionKey.OP_READ, inputCallbackMap, inputHandlePool); // 处理选择器
                            }
                        }
                        selectionKeys.clear(); // 清空选择器
                    } catch (IOException e) { // 捕获异常
                        e.printStackTrace(); // 打印异常信息
                    }
                }
            }


        };
        thread.setPriority(Thread.MAX_PRIORITY); // 设置线程优先级
        //设置遍历操作是最高优先级
        thread.start(); // 启动线程
    }

    private void startWrite() { // 开始写入 
        Thread thread = new Thread("Clink IoSelectorProvider WriteSelector Thread") { // 创建写入线程
            @Override // 重写run方法
            public void run() { // 重写run方法
                while (!isClosed.get()) { // 循环直到关闭
                    try {
                        if (writeSelector.select() == 0) {
                            waitSelection(inRegOutput); // 等待注册输出，判断是否等待完成，如果等待完成则继续循环
                            continue; // 继续循环
                        }

                        Set<SelectionKey> selectionKeys = writeSelector.selectedKeys(); // 获取选择器
                        for (SelectionKey selectionKey : selectionKeys) { // 遍历选择器
                            if (selectionKey.isValid()) { // 如果选择器有效
                                handleSelection(selectionKey, SelectionKey.OP_WRITE, outputCallbackMap, outputHandlePool); // 处理选择器
                            }
                        }
                        selectionKeys.clear(); // 清空选择器
                    } catch (IOException e) { // 捕获异常
                        e.printStackTrace(); // 打印异常信息
                    }
                }
            }
        };
        thread.setPriority(Thread.MAX_PRIORITY); // 设置线程优先级
        thread.start(); // 启动线程
    }


    @Override
    public boolean registerInput(SocketChannel channel, HandleInputCallback callback) { // 注册输入
        return registerSelection(channel, readSelector, SelectionKey.OP_READ, inRegInput,
                inputCallbackMap, callback) != null; // 注册选择器，不等于null说明注册成功
    }

    @Override
    public boolean registerOutput(SocketChannel channel, HandleOutputCallback callback) { // 注册输出
        return registerSelection(channel, writeSelector, SelectionKey.OP_WRITE, inRegOutput,
                outputCallbackMap, callback) != null; // 注册选择器，不等于null说明注册成功
    }

    @Override
    public void unRegisterInput(SocketChannel channel) { // 取消注册输入
        unRegisterSelection(channel, readSelector, inputCallbackMap); // 取消注册选择器
    }

    @Override
    public void unRegisterOutput(SocketChannel channel) { // 取消注册输出
        unRegisterSelection(channel, writeSelector, outputCallbackMap); // 取消注册选择器
    }

    @Override
    public void close() {
        if (isClosed.compareAndSet(false, true)) { // 如果未关闭
            inputHandlePool.shutdown(); // 关闭输入处理池
            outputHandlePool.shutdown(); // 关闭输出处理池

            inputCallbackMap.clear(); // 清空输入回调映射
            outputCallbackMap.clear(); // 清空输出回调映射

            readSelector.wakeup(); // 唤醒读选择器
            writeSelector.wakeup(); // 唤醒写选择器

            CloseUtils.close(readSelector, writeSelector); // 关闭选择器
        }
    }

    private static void waitSelection(final AtomicBoolean locker) { // 等待选择器
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (locker) { // 同步锁
            if (locker.get()) { // 如果锁定状态为true
                try {
                    locker.wait(); // 等待
                } catch (InterruptedException e) { // 捕获异常
                    e.printStackTrace(); // 打印异常信息
                }
            }
        }
    }

    // 注册选择器
    private static SelectionKey registerSelection(SocketChannel channel, Selector selector,
                                                  int registerOps, AtomicBoolean locker,
                                                  HashMap<SelectionKey, Runnable> map,
                                                  Runnable runnable) {

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (locker) { // 同步锁
            // 设置锁定状态
            locker.set(true); // 设置锁定状态

            try {
                // 唤醒当前的selector，让selector不处于select()状态，目的是让selector可以立即返回
                selector.wakeup(); // 唤醒选择器

                SelectionKey key = null; // 选择器
                if (channel.isRegistered()) { // 如果通道已注册
                    // 查询是否已经注册过
                    key = channel.keyFor(selector); // 获取选择器
                    if (key != null) {
                        key.interestOps(key.readyOps() | registerOps); // 设置选择器
                    }
                }

                if (key == null) { // 如果选择器为空，没有真正注册过
                    // 注册selector得到Key
                    key = channel.register(selector, registerOps); // 注册选择器
                    // 注册回调
                    map.put(key, runnable); // 注册回调 
                }

                return key; // 返回选择器
            } catch (ClosedChannelException e) { // 捕获异常
                return null; // 返回空      
            } finally {
                // 解除锁定状态
                locker.set(false); // 设置锁定状态
                try {
                    // 通知
                    locker.notify(); // 通知
                } catch (Exception ignored) { // 捕获异常
                    ignored.printStackTrace(); // 打印异常信息
                }
            }
        }
    }

    // 取消注册选择器 解除注册
    private static void unRegisterSelection(SocketChannel channel, Selector selector,
                                            Map<SelectionKey, Runnable> map) { // 取消注册选择器
        if (channel.isRegistered()) { // 如果通道已注册
            SelectionKey key = channel.keyFor(selector); // 获取选择器
            if (key != null) { // 如果选择器不为空
                // 取消监听的方法
                key.cancel(); // 取消选择器，cancel是取消所有事件，如果没有分开写不能使用cancel 
                map.remove(key); // 移除选择器
                selector.wakeup(); // 唤醒选择器
            }
        }
    }

    // 处理选择器
    private static void handleSelection(SelectionKey key, int keyOps,
                                        HashMap<SelectionKey, Runnable> map,
                                        ExecutorService pool) {
        // 重点 取消对keyOps的监听
        // 取消继续对keyOps的监听 
        //! ! ! 重点 ! ! !
        key.interestOps(key.readyOps() & ~keyOps); // 取消选择器

        Runnable runnable = null; // 回调
        try {
            runnable = map.get(key); // 获取回调
        } catch (Exception ignored) { // 捕获异常
            ignored.printStackTrace(); // 打印异常信息
        }

        if (runnable != null && !pool.isShutdown()) { // 如果回调不为空且未关闭
            // 异步调度
            pool.execute(runnable); // 执行回调
        }
    }


    static class IoProviderThreadFactory implements ThreadFactory { // IO提供者线程工厂
        private final ThreadGroup group; // 线程组
        private final AtomicInteger threadNumber = new AtomicInteger(1); // 线程编号
        private final String namePrefix; // 线程前缀

        IoProviderThreadFactory(String namePrefix) { // 构造方法
            this.group = Thread.currentThread().getThreadGroup(); // 获取线程组
            this.namePrefix = namePrefix; // 设置线程前缀
        }

        public Thread newThread(Runnable r) { // 创建线程
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0); // 创建线程
            if (t.isDaemon()) // 如果线程为守护线程
                t.setDaemon(false); // 设置为非守护线程
            if (t.getPriority() != Thread.NORM_PRIORITY) // 如果线程优先级不为普通优先级
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
