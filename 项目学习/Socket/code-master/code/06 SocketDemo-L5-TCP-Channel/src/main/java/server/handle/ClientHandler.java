package server.handle;

import clink.net.qiujuer.clink.utils.CloseUtils;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler {
    private final Socket socket; // 客户端套接字
    private final ClientReadHandler readHandler; // 客户端读取处理
    private final ClientWriteHandler writeHandler; // 客户端写入处理
    private final CloseNotify closeNotify; // 关闭通知

    public ClientHandler(Socket socket, CloseNotify closeNotify) throws IOException {
        this.socket = socket; // 将客户端套接字赋值给socket
        this.readHandler = new ClientReadHandler(socket.getInputStream()); // 创建一个ClientReadHandler实例 线程
        this.writeHandler = new ClientWriteHandler(socket.getOutputStream()); // 创建一个ClientWriteHandler实例 线程
        this.closeNotify = closeNotify; // 将关闭通知赋值给closeNotify
        System.out.println("新客户端连接：" + socket.getInetAddress() +
                " P:" + socket.getPort());
    }

    public void exit() {
        readHandler.exit(); // 关闭客户端读取处理
        writeHandler.exit(); // 关闭客户端写入处理
        CloseUtils.close(socket); // 关闭客户端套接字
        System.out.println("客户端已退出：" + socket.getInetAddress() +
                " P:" + socket.getPort());
    }

    public void send(String str) {
        writeHandler.send(str); // 发送消息     
    }

    public void readToPrint() {
        readHandler.start(); // 启动客户端读取处理  
    }

    private void exitBySelf() {
        exit(); // 退出客户端
        closeNotify.onSelfClosed(this); // 告知外界自己把自己关闭了 
    }

    public interface CloseNotify { // 关闭通知 callback
        void onSelfClosed(ClientHandler handler); // 通知关闭
    }

    class ClientReadHandler extends Thread {
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
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(inputStream)); // 将输入流转换为BufferedReader        

                do {
                    // 客户端拿到一条数据
                    String str = socketInput.readLine(); // 读取一行数据
                    if (str == null) {
                        System.out.println("客户端已无法读取数据！");
                        // 退出当前客户端
                        ClientHandler.this.exitBySelf(); // 退出当前客户端
                        break;
                    }
                    // 打印到屏幕
                    System.out.println(str); // 打印到屏幕      
                } while (!done);
            } catch (Exception e) { 
                if (!done) { 
                    System.out.println("连接异常断开"); // 打印异常信息
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

        ClientWriteHandler(OutputStream outputStream) { // 构造方法
            this.printStream = new PrintStream(outputStream); // 将输出流转换为打印流
            this.executorService = Executors.newSingleThreadExecutor(); // 创建一个单线程线程池
        }

        void exit() { // 退出客户端写入处理
            done = true; // 设置为true
            CloseUtils.close(printStream); // 关闭打印流
            executorService.shutdownNow(); // 关闭线程池
        }

        void send(String str) {
            executorService.execute(new WriteRunnable(str)); // 执行写入任务,加入到单线程池中
        }

        class WriteRunnable implements Runnable {
            private final String msg; // 要发送的消息

            WriteRunnable(String msg) {
                this.msg = msg; // 将消息赋值给msg
            }

            @Override
            public void run() {
                if (ClientWriteHandler.this.done) {
                    return; // 如果已经完成，则返回
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
