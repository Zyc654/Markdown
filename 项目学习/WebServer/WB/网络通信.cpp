// client 文本文件
int main(int argc, char *argv[])
{
    if (argc != 3)
    {
        printf("Using: ./demo44 ip port\n");
        printf("Sample./demo44 192.168.150.128 5005\n");
        return -1;
    }
    ctcpclient tcpclient;
    if (tcpclient.connect(argv[1], atoi(argv[2])) == false) // 向服务器端发起连接请求。
    {
        printf("tcpclient.connect(%s, %s) failed.\n", argv[1], argv[2]);
        return -1;
    }
    string sendbuf, recvbuf;
    for (int i = 0; i < 10; i++)
    {
        sendbuf = sformat("这是第%d个超级女生。", i);
        if (tcpclient.write(sendbuf) == false) // 向服务器端发送请求报文。
            printf("tcpclient.write() failed.\n"); break;
        cout << "发送：" << sendbuf << endl;
        sleep(1);
        if (tcpclient.read(recvbuf) == false) // 接收服务端的回应报文。
            printf("tcpclient.read() failed.\n"); break;
        cout << "接收：" << recvbuf << endl;
    }
}


//server 文本数据

int main(int argc, char *argv[])
{
    if (argc != 2)
    {
        printf("Using: ./demo45 port\n");
        printf("Sample: ./demo45 5005\n");
        return -1;
    }
    ctcpserver tcpserver;
    if (tcpserver.initserver(atoi(argv[1])) == false) // 服务端初始化。
    {
        printf("tcpserver.initserver(%s) failed.\n", argv[1]);
        return -1;
    }
    if (tcpserver.accept() == false) // 等待客户端的连接。
        printf("accept() failed.\n");
    cout << "客户端已连接(" << tcpserver.getip() << "). \n";
    string sendbuf, recvbuf;
    for (int i = 0; i < 10; i++)
    {
        if (tcpserver.read(recvbuf) == false) // 接收客户端的请求报文。
            printf("tcpserver.read() failed.\n"); break;
        cout << "接收：" << recvbuf << endl;
        sendbuf = "ok";
        if (tcpserver.write(sendbuf) == false) // 向客户端发送回应报文。
            printf("tcpserver.write() failed.\n"); break;
        cout << "发送：" << sendbuf << endl;
    }
}


// client 二进制文件
