#include "网络***.h"
bool ctcpclient::connect(const string &ip, const int port)
{
    // 如果已连接到服务端，则断开，这种处理方法没有特别的原因，不要纠结。
    if (m_connnfd != -1) { ::close(m_connnfd); m_connnfd = -1; }

    // 忽略SIGPIPE信号，防止程序异常退出。
    // 如果send到一个disconnected socket上，内核就会发出SIGPIPE信号。这个信号
    // 的缺省处理方法是终止进程，大多数时候这都不是我们期望的。我们重新定义这
    // 个信号的处理方法，大多数情况是直接屏蔽它。
    signal(SIGPIPE, SIG_IGN);
    m_ip = ip;
    m_port = port;
    struct hostent* h;
    struct sockaddr_in servaddr;
    if ((m_connnfd = socket(AF_INET, SOCK_STREAM, 0)) < 0) return false;
    if (!(h = gethostbyname(m_ip.c_str())))
    {
        ::close(m_connnfd);
        m_connnfd = -1;
        return false;
    }
    memset(&servaddr, 0, sizeof(servaddr));
    servaddr.sin_family = AF_INET;
    servaddr.sin_port = htons(m_port); // 指定服务端的通讯端口
    memcpy(&servaddr.sin_addr, h->h_addr, h->h_length);
    if (::connect(m_connnfd, (struct sockaddr *)&servaddr, sizeof(servaddr)) != 0)
    {
        ::close(m_connnfd);
        m_connnfd = -1;
        return false;
    }
    return true;
}
bool ctcpclient::write(const string &buffer)
{
    if (m_connfd == -1) return false;
    return (tcpwrite(m_connfd, buffer));
}
bool tcpwrite(const int sockfd, const string &buffer) // 发送文本数据。
{
    if (sockfd == -1) return false;
    int buflen = buffer.size();
    // 先发送报头。
    if (writen(sockfd, (char*)&buflen, 4) == false) return false;
    // 再发送报文体。
    if (writen(sockfd, buffer.c_str(), buflen) == false) return false;
    return true;
}
bool tcpread(const int sockfd, string &buffer, const int itimeout) // 接收文本数据。
{
    if (sockfd == -1) return false;
    // 如果itimeout>0，表示需要等待itimeout秒，如果itimeout秒后还没有数据到达，返回false。
    if (itimeout > 0)
    {
        struct pollfd fds;
        fds.fd = sockfd;
        fds.events = POLLIN;
        if (poll(&fds, 1, itimeout * 1000) <= 0) return false;
    }
    // 如果itimeout==0，表示不等待，立即判断socket的缓冲区中是否有数据，如果没有，返回false。
    if (itimeout == 0)
    {
        struct pollfd fds;
        fds.fd = sockfd;
        fds.events = POLLIN;
        if (poll(&fds, 1, 0) <= 0) return false;
    }
    int buflen = 0;
    // 先读取报文长度，4个字节。
    if (readn(sockfd, (char*)&buflen, 4) == false) return false;
    buffer.resize(buflen); // 设置buffer的大小。
    // 再读取报文内容。
    if (readn(sockfd, &buffer[0], buflen) == false) return false;
    return true;
}





bool ctcpserver::initserver(const unsigned int port, const int backlog)
{
    // 如果服务端的socket>0，关掉它，这种处理方法没有特别的原因，不要纠结。
    if (m_listenfd > 0) { ::close(m_listenfd); m_listenfd=-1; }
    if ( (m_listenfd = socket(AF_INET,SOCK_STREAM,0))<=0 ) return false;
    // 忽略SIGPIPE信号，防止程序异常退出。
    signal(SIGPIPE,SIG_IGN);
    // 打开SO_REUSEADDR选项，当服务端连接处于TIME_WAIT状态时可以再次启动服务器，
    // 否则 bind()可能会不成功，报：Address already in use。
    int opt = 1;
    setsockopt(m_listenfd,SOL_SOCKET,SO_REUSEADDR,&opt,sizeof(opt));
    memset(&m_servaddr,0,sizeof(m_servaddr));
    m_servaddr.sin_family = AF_INET;
    m_servaddr.sin_addr.s_addr = htonl(INADDR_ANY); // 任意ip地址。
    m_servaddr.sin_port = htons(port);
    if (bind(m_listenfd,(struct sockaddr *)&m_servaddr,sizeof(m_servaddr)) != 0 )
    {
        closelisten(); return false;
    }
    if(listen(m_listenfd,backlog)!= 0) {
        closelisten(); return false;
    }
    return true;
}
bool ctcpserver::accept()
{
    if (m_listenfd == -1) return false;

    int m_socklen = sizeof(struct sockaddr_in);
    if ((m_connfd = ::accept(m_listenfd, (struct sockaddr *)&m_clientaddr, (socklen_t*)&m_socklen)) < 0)
        return false;

    return true;
}
char *ctcpserver::getip()
{
    return (inet_ntoa(m_clientaddr.sin_addr));
}
bool ctcpserver::read(void *buffer, const int ibuflen, const int itimeout) // 接收二进制数据。
{
    if (m_connfd == -1) return false;
    return (tcpread(m_connfd, buffer, ibuflen, itimeout));
}