package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    private final int port; // 端口     
    private ClientListener mListener; // 客户端监听器

    public TCPServer(int port) {
        this.port = port;
    }

    public boolean start() {
        try {
            ClientListener listener = new ClientListener(port); // 创建一个ClientListener实例 线程
            mListener = listener; // 将ClientListener实例赋值给mListener
            listener.start(); // 启动线程   （线程启动后会执行run方法） 
        } catch (IOException e) {
            e.printStackTrace(); // 打印异常信息
            return false;
        }
        return true;
    }

    public void stop() { // 关闭服务器
        if (mListener != null) {
            mListener.exit(); // 关闭客户端监听器    
        }
    }

    private static class ClientListener extends Thread {
        private ServerSocket server; // 服务器套接字
        private boolean done = false; // 是否完成

        private ClientListener(int port) throws IOException {
            server = new ServerSocket(port); // 创建一个ServerSocket实例 （端口）
            System.out.println("服务器信息：" + server.getInetAddress() + " P:" + server.getLocalPort()); // 打印服务器信息
        }

        @Override
        public void run() {
            super.run();

            System.out.println("服务器准备就绪～");
            // 等待客户端连接
            do {
                // 得到客户端
                Socket client;
                try {
                    client = server.accept();
                } catch (IOException e) {
                    continue;
                }
                // 客户端构建异步线程
                ClientHandler clientHandler = new ClientHandler(client);
                // 启动线程
                clientHandler.start();
            } while (!done);

            System.out.println("服务器已关闭！");
        }

        void exit() { // 关闭服务器
            done = true; // 设置为true
            try {
                server.close(); // 关闭服务器套接字
            } catch (IOException e) {
                e.printStackTrace(); // 打印异常信息
            }
        }
    }

    /**
     * 客户端消息处理
     */
    private static class ClientHandler extends Thread {
        private Socket socket; // 客户端套接字
        private boolean flag = true; // 是否完成

        ClientHandler(Socket socket) {
            this.socket = socket; // 将客户端套接字赋值给socket
        }

        @Override
        public void run() {
            super.run(); // 启动线程
            System.out.println("新客户端连接：" + socket.getInetAddress() +
                    " P:" + socket.getPort()); // 打印客户端信息

            try {
                // 得到打印流，用于数据输出；服务器回送数据使用
                PrintStream socketOutput = new PrintStream(socket.getOutputStream());
                // 得到输入流，用于接收数据
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));

                do {
                    // 客户端拿到一条数据
                    String str = socketInput.readLine();
                    if ("bye".equalsIgnoreCase(str)) {
                        flag = false;
                        // 回送
                        socketOutput.println("bye");
                    } else {
                        // 打印到屏幕。并回送数据长度
                        System.out.println(str);
                        socketOutput.println("回送：" + str.length());
                    }

                } while (flag);

                socketInput.close();
                socketOutput.close();

            } catch (Exception e) {
                System.out.println("连接异常断开");
            } finally {
                // 连接关闭
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("客户端已退出：" + socket.getInetAddress() +
                    " P:" + socket.getPort());

        }
    }
}
