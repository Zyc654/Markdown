import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.UUID;

/**
 * UDP 提供者，用于提供服务 接受方
 */
public class UDPProvider {
    private static final int PORT = 20000; // 定义端口常量

    public static void main(String[] args) throws IOException {
        // 生成一份唯一标示
        String sn = UUID.randomUUID().toString();
        Provider provider = new Provider(sn);
        provider.start();

        // 读取任意键盘信息后可以退出
        // noinspection ResultOfMethodCallIgnored
        System.in.read();
        provider.exit();
    }

    // 创建线程
    private static class Provider extends Thread {
        private final String sn; // 唯一标示
        private boolean done = false;// 是否完成
        private DatagramSocket ds = null;// 数据报套接字

        public Provider(String sn) {
            super();
            this.sn = sn;
        }

        // 提供者
        @Override
        public void run() {
            super.run();

            System.out.println("UDPProvider Started.");

            try {
                // 监听指定端口
                // 作为接收者，指定一个端口用于数据接收
                ds = new DatagramSocket(PORT);

                while (!done) {
                    // 构建接收实体
                    final byte[] buf = new byte[512];
                    DatagramPacket receivePack = new DatagramPacket(buf, buf.length);

                    // 接收
                    ds.receive(receivePack);

                    // 打印接收到的信息与发送者的信息
                    // 发送者的IP地址
                    String ip = receivePack.getAddress().getHostAddress();
                    int port = receivePack.getPort();
                    int dataLen = receivePack.getLength();
                    String data = new String(receivePack.getData(), 0, dataLen);
                    System.out.println("UDPProvider receive form ip:" + ip
                            + "\tport:" + port + "\tdata:" + data);

                    // 解析端口号
                    int responsePort = MessageCreator.parsePort(data);
                    if (responsePort != -1) {
                        // 构建一份回送数据
                        String responseData = MessageCreator.buildWithSn(sn);
                        byte[] responseDataBytes = responseData.getBytes();

                        // 获取发送者的实际IP地址
                        InetAddress senderAddress = receivePack.getAddress();
                        // 直接根据发送者构建一份回送信息
                        DatagramPacket responsePacket = new DatagramPacket(responseDataBytes,
                                responseDataBytes.length,
                                senderAddress,
                                responsePort);

                        System.out.println("UDPProvider response to ip:" + senderAddress.getHostAddress()
                                + "\tport:" + responsePort + "\tdata:" + responseData);
                        ds.send(responsePacket);
                    }
                }
            } catch (IOException e) {
                System.err.println("UDPProvider error: " + e.getMessage());
            } finally {
                close();
            }

            // 完成
            System.out.println("UDPProvider Finished.");
        }

        private void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }

        /**
         * 提供结束
         */
        void exit() {
            done = true;
            close();
        }
    }
}
