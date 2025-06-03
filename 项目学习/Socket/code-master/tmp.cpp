#include <iostream>
#include <string>
#include <cstring>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
using namespace std;    
class ctcpclient {
public:
  int m_clientfd; //客户端的socket，-1 表示未连接或连接已断开
  string m_ip; //服务器的 IP/域名
	unsigned short m_port;//通讯端口  
  ctcpclient() : m_clientfa(-1) {}
  //向服务端发起连接请求，成功返回 ture 失败返回 false
  bool connect(const string &in_ip,const unsigned short in_port){
   	if(m_clientfd != -1) return false;//如果socket已连接，直接返回失败
    m_ip = in_ip;m_port = in_port;
    // 第1步：创建客户端的socket。
    if(m_clientfd = socket(AF_INET, SOCK_STREAM, 0) == -1) return false;
    // 第2步：向服务器发起连接请求。
    struct sockaddr_in servaddr; // 用于存放协议、端口和IP地址的结构体。
    memset(&servaddr, 0, sizeof(servaddr)); // ①协议族，固定填AF_INET。
    servaddr.sin_family = AF_INET; 
    servaddr.sin_port = htons(m_port);// ②指定服务端的通信端口。
    struct hostent* h; // 用于存放服务端IP地址（大端序）的结构体的指针。
    if ((h = gethostbyname(m_ip.c_str()) == nullptr) // 把域名/主机名/字符串格式的IP转换成结构体。
        close(m_clientfd); 
        m_clientfd = -1;
        return false;   
    }
    memcpy(&servaddr.sin_addr, h->h_addr, h->h_length);    // ③ 指定服务端的 IP（大端序）。
    if (::connect(m_clientfd, (struct sockaddr *)&servaddr, sizeof(servaddr)) == -1)    // 向服务端发起连接请求。
    {
        ::close(m_clientfd); 
        m_clientfd = -1;
        return false;
    }
    return true;
  }
  // 向服务器发送数据,buffer不要用char* 
  bool send(const string &buffer){
    if ( (::send(m_clientfd, buffer.data(), buffer.size(), 0))<=0) {//首地址和大小
        ::close(m_clientfd); 
        m_clientfd = -1;return false;
    }
    return true;
  }
  // 从服务器接收数据,buffer不要用char* 
  bool recv(string &buffer,const size_t maxlen){
    buffer.clear();
    buffer.resize(maxlen);
    int iret = ::recv(m_clientfd, &buffer[0], maxlen, 0); // 接收数据
    if (iret<=0)
        return false;
    buffer.resize(iret);
    return true;
  }
  bool close(){
    if (m_clientfd == -1) return false;
    ::close(m_clientfd);
    m_clientfd = -1;
    return true;
  }
  ~ctcpclient() { close(); }
};

int main(int argc,int argv[]) {
    if (argc!=3)
    {
        cout << "Using: ./demo7 服务端的IP 服务端的端口\nExample: ./demo7 192.168.101.138 5005\n\n";
        return -1;
    }
    /*
    // 第1步：创建客户端的socket。
    int sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd==-1) {
        perror("socket"); return -1;
    }
    // 第2步：向服务器发起连接请求。
    struct sockaddr_in servaddr; // 用于存放协议、端口和IP地址的结构体。
    memset(&servaddr, 0, sizeof(servaddr)); // ①协议族，固定填AF_INET。
    servaddr.sin_family = AF_INET; 
    servaddr.sin_port = htons(atoi(argv[2]));// ②指定服务端的通信端口。
    struct hostent* h; // 用于存放服务端IP地址（大端序）的结构体的指针。
    if ((h = gethostbyname(argv[1])) == nullptr) // 把域名/主机名/字符串格式的IP转换成结构体。
          cout << "gethostbyname failed.\n" << endl; close(sockfd); return -1;
    }
    memcpy(&servaddr.sin_addr, h->h_addr, h->h_length);    // ③ 指定服务端的 IP（大端序）。
    // servaddr.sin_addr.s_addr = inet_addr(argv[1]);      // ③ 指定服务端的 IP，只能用 IP，不能用域名和主机名
    if (connect(sockfd, (struct sockaddr *)&servaddr, sizeof(servaddr)) == -1)    // 向服务端发起连接请求。
    {
        perror("connect"); close(sockfd); return -1;
    }
    // 第3步：与服务器端通讯，客户发送一个请求报文后等待服务器的回复，收到回复后，再发下一个请求报文。
    char buffer[1024];
    for (int i=0; i<10; i++) // 循环3次，将与服务端进行三次通讯。
    {
        int iret;
        memset(buffer, 0, sizeof(buffer));
        sprintf(buffer, "这是第%d个超级女生，编号%03d。", i+1, i+1); // 生成请求报文内容。
        // 向服务器端发送请求报文。
        if ( (iret=send(tcpclient.m_clientfd, buffer, strlen(buffer), 0))<=0)
        {
            perror("send"); break;
        }
        cout << "发送：" << buffer << endl;

        memset(buffer, 0, sizeof(buffer));
        // 接收服务器的回应报文，如果服务器没有发送回应报文，recv()函数将阻塞等待。
        if ( (iret=recv(tcpclient.m_clientfd, buffer, sizeof(buffer), 0))<=0)
        {
            cout << "iret=" << iret << endl; break;
        }
        cout << "接收：" << buffer << endl;
        sleep(1);
    }   
    */

    ctcpclient tcpclient;
    if (!tcpclient.connect(argv[1], atoi(argv[2])))
    {
        cout << "connect failed.\n" << endl;
        return -1;
    }
   // 第3步：与服务器端通讯，客户发送一个请求报文后等待服务器的回复，收到回复后，再发下一个请求报文。
    string buffer;
    for (int i=0; i<10; i++) // 循环3次，将与服务端进行三次通讯。
    {
        int iret;
        buffer.clear();
        sprintf(buffer, "这是第%d个超级女生，编号%03d。", i+1, i+1); // 生成请求报文内容。
        // 向服务器端发送请求报文。
        if (!tcpclient.send(buffer))
        {
            perror("send"); break;
        }
        cout << "发送：" << buffer << endl;

        buffer.clear();
        // 接收服务器的回应报文，如果服务器没有发送回应报文，recv()函数将阻塞等待。
        if (!tcpclient.recv(buffer,1024))
        {
            perror("recv"); break;
        }
        cout << "接收：" << buffer << endl;
        sleep(1);
    }   
    // 第4步：关闭 socket，释放资源。
    close(tcpclient.m_clientfd);
    return 0;
   
}