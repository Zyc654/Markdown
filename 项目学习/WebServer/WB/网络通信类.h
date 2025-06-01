// client 文本文件
// socket通讯的客户端类
// 向 socket 的对端发送数据。
// sockfd: 可用的 socket 连接。
// buffer: 待发送数据缓冲区的地址。
// ibuflen: 待发送数据的字节数。
// 返回值: true-成功; false-失败，如果失败，表示 socket 连接已不可用。
bool tcpwrite(const int sockfd, const string &buffer); // 写入文本数据。
bool tcpwrite(const int sockfd, const void *buffer, const int ibuflen); // 写入二进制数据。
// 从已经准备好的 socket 中读取数据。
// sockfd: 已经准备好的 socket 连接。
// buffer: 接收数据缓冲区的地址。
// n: 本次接收数据的字节数。
// 返回值: 成功接收到 n 字节的数据后返回 true，socket 连接不可用返回 false。
bool readn(const int sockfd, char *buffer, const size_t n);
// 向已经准备好的 socket 中写入数据。
// sockfd: 已经准备好的 socket 连接。
// buffer: 待发送数据缓冲区的地址。
// n: 待发送数据的字节数。
// 返回值: 成功发送完 n 字节的数据后返回 true，socket 连接不可用返回 false。
bool writen(const int sockfd, const char *buffer, const size_t n);
// 以上是 socket 通讯的函数和类
class ctcpclient
{
private:
    int m_connfd;  // 客户端的socket。
    string m_ip;    // 服务端的ip地址。
    int m_port;    // 服务端通讯的端口。
public:
    ctcpclient():m_connfd(-1),m_port(0) {} // 构造函数。
    // 向服务端发起连接请求。
    bool connect(const string &ip,const int port);
    // 接收对端发送过来的数据。
    // ibuflen: 打算接收数据的大小。
    // itimeot: 等待数据的超时时间（秒）：-1-不等待；0-无限等待；>0-等待的秒数。
    // 返回值: true-成功; false-失败，失败有两种情况：1）等待超时；2）socket连接不可用。
    bool read(string &buffer,const int itimeot=0);
    bool read(void *buffer,const int ibuflen,const int itimeot=0); // 接收二进制数据。
    // 向对端发送数据。
    // buffer: 待发送数据缓冲区。
    // ibuflen: 待发送数据的大小。
    // 返回值: true-成功；false-失败，如果失败，表示socket连接已不可用。
    bool write(const string &buffer); // 发送字符串数据。
    bool write(const void *buffer, const int ibuflen); // 发送二进制数据。
    // 断开与服务端的连接
    void close();
    ~ctcpclient(); // 析构函数自动关闭socket，释放资源。
};




// ---------------------------------------------------------------
//server 文本数据
// socket通讯的服务端类
class ctcpserver
{
private:
    struct sockaddr_in m_clientaddr; // 客户端的地址信息。
    int m_socklen;
    struct sockaddr_in m_servaddr;  // 服务端的地址信息。
    int m_listenfd;                 // 服务端用于监听的socket。
    int m_connfd;                   // 客户端连接上来的socket。
public:
    ctcpserver() : m_listenfd(-1), m_connfd(-1) {} // 构造函数。
    // 服务端初始化。
    // 返回值: true-成功；false-失败，一般情况下，只要port设置正确，没有被占用，初始化都会成功。
    bool initserver(const unsigned int port, const int backlog = 5);
    // 从已连接队列中取出一个客户端的连接。如果没有客户端连接上来，本函数会阻塞等待。
    // 返回值: true-有新的客户端已连接上来；false-失败，Accept被中断，如果Accept失败，可以重新Accept。
    bool accept();
    // 获取客户端的ip地址。
    char *getip();
    // 接收对端发送过来的数据。
    // buffer: 存放接收数据缓冲区。
    // ibuflen: 打算接收数据的大小。
    // itimeout: 等待数据的超时时间（秒）：-1-不等待；0-无限等待；>0-等待的秒数。
    // 返回值：true-成功；false-失败，失败有两种情况：1）等待超时；2）socket连接不可用。
    bool read(string &buffer, const int itimeout = 0); // 接收字符串数据。
    bool read(void *buffer, const int ibuflen, const int itimeout = 0); // 接收二进制数据。
    // 向对端发送数据。
    // 关闭监听的socket，即m_listenfd，常用于多进程服务程序的子进程代码中。
    void closelisten();
    // 关闭客户端的socket，即m_connfd，常用于多进程服务程序的父进程代码中。
    void closeclient();
    ~ctcpserver(); // 析构函数自动关闭socket，释放资源。
};
