import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Server {
    private static final int PORT = 20000;

    public static void main(String[] args) throws IOException {
        ServerSocket server = createServerSocket();
        // 初始化ServerSocket
        initServerSocket(server);

        // 绑定到本地端口上 backlog 是指允许等待的连接队列长度
        server.bind(new InetSocketAddress(Inet4Address.getLocalHost(), PORT), 50);


        System.out.println("服务器准备就绪～");
        System.out.println("服务器信息：" + server.getInetAddress() + " P:" + server.getLocalPort());

        // 等待客户端连接
        for (; ; ) {
            // 得到客户端
            Socket client = server.accept();
            // 客户端构建异步线程
            ClientHandler clientHandler = new ClientHandler(client);
            // 启动线程
            clientHandler.start();
        }

    }

    private static ServerSocket createServerSocket() throws IOException {
        // 创建基础的ServerSocket
        ServerSocket serverSocket = new ServerSocket();

        // 绑定到本地端口20000上，并且设置当前可允许等待链接的队列为50个
        //serverSocket = new ServerSocket(PORT);

        // 等效于上面的方案，队列设置为50个
        //serverSocket = new ServerSocket(PORT, 50);

        // 与上面等同
        //serverSocket = new ServerSocket(PORT, 50, Inet4Address.getLocalHost());

        return serverSocket;
    }

    private static void initServerSocket(ServerSocket serverSocket) throws IOException {
        // 是否复用未完全关闭的地址端口
        serverSocket.setReuseAddress(true);

        // 等效Socket#setReceiveBufferSize 数据接收之前设置 size 是有效的
        serverSocket.setReceiveBufferSize(64 * 1024 * 1024);

        // 设置serverSocket#accept超时时间
         serverSocket.setSoTimeout(2000);  //一般建议永久等待

        // 设置性能参数：短链接，延迟，带宽的相对重要性
        serverSocket.setPerformancePreferences(1, 1, 1);
    }

    /**
     * 客户端消息处理
     */
    private static class ClientHandler extends Thread {
        private Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("新客户端连接：" + socket.getInetAddress() +
                    " P:" + socket.getPort());

            try {
                // 得到套接字流
                OutputStream outputStream = socket.getOutputStream(); // 得到输出流
                InputStream inputStream = socket.getInputStream(); // 得到输入流

                byte[] buffer = new byte[256]; // 创建一个字节数组
                int readCount = inputStream.read(buffer); // 读取输入流
                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, readCount); // 将字节数组包装成ByteBuffer

                // byte
                byte be = byteBuffer.get(); // 读取byte类型数据

                // char
                char c = byteBuffer.getChar(); // 读取char类型数据

                // int
                int i = byteBuffer.getInt(); // 读取int类型数据


                // bool
                boolean b = byteBuffer.get() == 1; // 读取boolean类型数据

                // Long
                long l = byteBuffer.getLong(); // 读取long类型数据

                // float
                float f = byteBuffer.getFloat(); // 读取float类型数据

                // double
                double d = byteBuffer.getDouble(); // 读取double类型数据

                // String
                int pos = byteBuffer.position(); // 获取当前位置
                String str = new String(buffer, pos, readCount - pos - 1); // 将字节数组转换为String类型数据

                System.out.println("收到数量：" + readCount + " 数据："
                        + be + "\n"
                        + c + "\n"
                        + i + "\n"
                        + b + "\n"
                        + l + "\n"
                        + f + "\n"
                        + d + "\n"
                        + str + "\n");

                outputStream.write(buffer, 0, readCount);
                outputStream.close();
                inputStream.close();

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
