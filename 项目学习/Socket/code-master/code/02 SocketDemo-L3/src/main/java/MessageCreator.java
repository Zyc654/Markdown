public class MessageCreator {
    private static final String SN_HEADER = "收到暗号，我是（SN）:";
    private static final String PORT_HEADER = "这是暗号，请回电端口（Port）:";

    // 构建端口
    public static String buildWithPort(int port) {
        return PORT_HEADER + port;
    }

    // 解析端口
    public static int parsePort(String data) {
        if (data.startsWith(PORT_HEADER)) {
            return Integer.parseInt(data.substring(PORT_HEADER.length()));
        }

        return -1;
    }

    // 构建sn
    public static String buildWithSn(String sn) {
        return SN_HEADER + sn;
    }

    // 解析sn
    public static String parseSn(String data) {
        if (data.startsWith(SN_HEADER)) {
            return data.substring(SN_HEADER.length());
        }
        return null;
    }

}
