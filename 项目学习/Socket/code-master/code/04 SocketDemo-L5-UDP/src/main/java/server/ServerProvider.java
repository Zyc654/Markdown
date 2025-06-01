package server;

import clink.net.qiujuer.clink.utils.ByteUtils;
import constants.UDPConstants;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.UUID;

class ServerProvider {
    private static Provider PROVIDER_INSTANCE;

    static void start(int port) {
        stop();
        String sn = UUID.randomUUID().toString(); // 生成一个UUID
        Provider provider = new Provider(sn, port);// 创建一个Provider实例 线程
        provider.start(); // 启动线程   （线程启动后会执行run方法） 
        PROVIDER_INSTANCE = provider; // 将Provider实例赋值给PROVIDER_INSTANCE
    }

    static void stop() {
        if (PROVIDER_INSTANCE != null) {
            PROVIDER_INSTANCE.exit();
            PROVIDER_INSTANCE = null;
        }
    }

    private static class Provider extends Thread {
        private final byte[] sn; // 设备序列号
        private final int port; // 端口
        private boolean done = false; // 线程是否结束
        private DatagramSocket ds = null; // 数据报套接字   
        // 存储消息的Buffer
        final byte[] buffer = new byte[128]; // 存储消息的缓冲区

        Provider(String sn, int port) {
            super();
            this.sn = sn.getBytes(); // 将设备序列号转换为字节数组
            this.port = port; // 将端口赋值给port
        }

        @Override
        public void run() {
            super.run();

            System.out.println("UDPProvider Started."); // 打印启动信息

            try {
                // 监听端口 30201
                ds = new DatagramSocket(UDPConstants.PORT_SERVER); // 创建一个DatagramSocket实例
                // 接收消息的Packet
                DatagramPacket receivePack = new DatagramPacket(buffer, buffer.length); // 创建一个DatagramPacket实例

                while (!done) {

                    // 接收
                    ds.receive(receivePack); // 接收消息

                    // 打印接收到的信息与发送者的信息
                    // 发送者的IP地址
                    String clientIp = receivePack.getAddress().getHostAddress(); // 获取发送者的IP地址
                    int clientPort = receivePack.getPort(); // 获取发送者的端口
                    int clientDataLen = receivePack.getLength(); // 获取发送者的数据长度
                    byte[] clientData = receivePack.getData(); // 获取发送者的数据
                    boolean isValid = clientDataLen >= (UDPConstants.HEADER.length + 2 + 4) // 判断数据长度是否大于等于UDPConstants.HEADER.length + 2 + 4
                            && ByteUtils.startsWith(clientData, UDPConstants.HEADER); // 判断数据是否以UDPConstants.HEADER开头
                    // 2 是指令 short 4 是端口 int 
                    System.out.println("ServerProvider receive form ip:" + clientIp
                            + "\tport:" + clientPort + "\tdataValid:" + isValid);

                    if (!isValid) {
                        // 无效继续
                        continue;
                    }

                    // 解析命令与回送端口
                    int index = UDPConstants.HEADER.length;
                    short cmd = (short) ((clientData[index++] << 8) | (clientData[index++] & 0xff));
                    int responsePort = (((clientData[index++]) << 24) |
                            ((clientData[index++] & 0xff) << 16) |
                            ((clientData[index++] & 0xff) << 8) |
                            ((clientData[index] & 0xff)));

                    // 判断合法性
                    if (cmd == 1 && responsePort > 0) { // 1 是搜索请求 端口大于0 是合法的
                        // 构建一份回送数据
                        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
                        byteBuffer.put(UDPConstants.HEADER);
                        byteBuffer.putShort((short) 2);
                        byteBuffer.putInt(port); //TCP的端口号
                        byteBuffer.put(sn);
                        int len = byteBuffer.position();
                        // 直接根据发送者构建一份回送信息
                        DatagramPacket responsePacket = new DatagramPacket(buffer,
                                len,
                                receivePack.getAddress(),
                                responsePort);
                        ds.send(responsePacket);
                        System.out.println("ServerProvider response to:" + clientIp + "\tport:" + responsePort + "\tdataLen:" + len);
                    } else {
                        System.out.println("ServerProvider receive cmd nonsupport; cmd:" + cmd + "\tport:" + port);
                    }
                }
            } catch (Exception ignored) {
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
