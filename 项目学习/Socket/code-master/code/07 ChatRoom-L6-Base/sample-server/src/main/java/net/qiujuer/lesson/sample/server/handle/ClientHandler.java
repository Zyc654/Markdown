package net.qiujuer.lesson.sample.server.handle;


import net.qiujuer.library.clink.utils.CloseUtils;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler { // 客户端处理
    private final Socket socket; // 客户端套接字
    private final ClientReadHandler readHandler; // 客户端读取处理
    private final ClientWriteHandler writeHandler; // 客户端写入处理
    private final ClientHandlerCallback clientHandlerCallback; // 客户端处理回调
    private final String clientInfo; // 客户端信息 

    public ClientHandler(Socket socket, ClientHandlerCallback clientHandlerCallback) throws IOException { // 构造方法
        this.socket = socket; // 将客户端套接字赋值给socket
        this.readHandler = new ClientReadHandler(socket.getInputStream()); // 创建一个ClientReadHandler实例
        this.writeHandler = new ClientWriteHandler(socket.getOutputStream()); // 创建一个ClientWriteHandler实例
        this.clientHandlerCallback = clientHandlerCallback; // 将客户端处理回调赋值给clientHandlerCallback
        this.clientInfo = "A[+ " socket.getInetAddress() +
                "] P: [" + socket.getPort() + "]"; // 将客户端信息赋值给clientInfo   
        System.out.println("新客户端连接：" + socket.getInetAddress() +
                " P:" + clientInfo); // 打印客户端信息
    }

    public String getClientInfo() { // 获取客户端信息
        return clientInfo; // 返回客户端信息
    }   

    public void exit() { // 退出客户端
        readHandler.exit(); // 关闭客户端读取处理
        writeHandler.exit(); // 关闭客户端写入处理
        CloseUtils.close(socket);
        System.out.println("客户端已退出：" + socket.getInetAddress() +
                " P:" + socket.getPort()); // 打印客户端信息
    }

    public void send(String str) { // 发送消息
        writeHandler.send(str); // 发送消息
    }

    public void readToPrint() { // 读取消息并打印
        readHandler.start(); // 启动客户端读取处理
    }

    private void exitBySelf() { // 退出客户端
        exit(); // 退出客户端
        clientHandlerCallback.onSelfClosed(this); // 通知关闭
    }

    public interface ClientHandlerCallback { // 客户端处理回调
        //自身关闭的通知
        void onSelfClosed(ClientHandler handler); // 通知关闭

        //收到消息的通知
        void onNewMessageArrived(ClientHandler handler,String msg); // 收到消息的通知
    }

    class ClientReadHandler extends Thread { // 客户端读取处理
        private boolean done = false; // 是否完成
        private final InputStream inputStream; // 输入流

        ClientReadHandler(InputStream inputStream) {
            this.inputStream = inputStream; // 将输入流赋值给inputStream
        }

        @Override
        public void run() {
            super.run(); // 启动线程
            try {
                // 得到输入流，用于接收数据
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(inputStream)); // 创建一个BufferedReader实例

                do {
                    // 客户端拿到一条数据
                    String str = socketInput.readLine(); // 读取一行数据
                    if (str == null) {
                        System.out.println("客户端已无法读取数据！"); // 打印客户端已无法读取数据
                        // 退出当前客户端
                        ClientHandler.this.exitBySelf(); // 退出当前客户端
                        break; // 退出循环
                    }
                    // 打印到屏幕
                    System.out.println(str); // 打印到屏幕

                    // 收到消息的通知 并且把自身也传递回去
                    clientHandlerCallback.onNewMessageArrived(ClientHandler.this,str);
                    
                } while (!done); // 如果done为false，则继续读取
            } catch (Exception e) {
                if (!done) {
                    System.out.println("连接异常断开"); // 打印连接异常断开
                    ClientHandler.this.exitBySelf(); // 退出当前客户端
                }
            } finally {
                // 连接关闭
                CloseUtils.close(inputStream); // 关闭输入流
            }
        }

        void exit() { // 退出客户端读取处理
            done = true; // 设置为true
            CloseUtils.close(inputStream); // 关闭输入流
        }
    }

    class ClientWriteHandler { // 客户端写入处理
        private boolean done = false; // 是否完成
        private final PrintStream printStream; // 打印流
        private final ExecutorService executorService; // 线程池

        ClientWriteHandler(OutputStream outputStream) {
            this.printStream = new PrintStream(outputStream); // 创建一个PrintStream实例
            this.executorService = Executors.newSingleThreadExecutor(); // 创建一个线程池
        }

        void exit() { // 退出客户端写入处理
            done = true; // 设置为true
            CloseUtils.close(printStream); // 关闭打印流
            executorService.shutdownNow(); // 关闭线程池
        }

        void send(String str) { // 发送消息
            if(done) { // 如果done为true，则返回，表示自己已经完成了
                return ;
            }
            executorService.execute(new WriteRunnable(str)); // 执行写入任务
        }

        class WriteRunnable implements Runnable { // 写入任务
            private final String msg; // 消息

            WriteRunnable(String msg) {
                this.msg = msg; // 将消息赋值给msg
            }

            @Override
            public void run() {
                if (ClientWriteHandler.this.done) { // 如果done为true，则返回
                    return; // 返回
                }

                try {
                    ClientWriteHandler.this.printStream.println(msg); // 打印消息
                } catch (Exception e) { 
                    e.printStackTrace(); // 打印异常信息
                }
            }
        }
    }
}
