#include <iostream>
#include <string>
#include <cstring>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netdb.h>
#include <fstream>
using namespace std;

class ctcpclient {
public:
    int m_clientfd; // 客户端的socket，-1 表示未连接或连接已断开
    string m_ip; // 服务器的 IP/域名
    unsigned short m_port; // 通讯端口
    ctcpclient() : m_clientfd(-1) {}
    // 向服务端发起连接请求，成功返回 true 失败返回 false
    bool connectToServer(const string &in_ip, const unsigned short in_port) {
        if (m_clientfd != -1) return false; // 如果socket已连接，直接返回失败
        m_ip = in_ip;
        m_port = in_port;
        // 第1步：创建客户端的socket。
        if ((m_clientfd = socket(AF_INET, SOCK_STREAM, 0)) == -1) return false;
        // 第2步：向服务器发起连接请求。
        struct sockaddr_in servaddr; // 用于存放协议、端口和IP地址的结构体。
        memset(&servaddr, 0, sizeof(servaddr));
        servaddr.sin_family = AF_INET; // ①协议族，固定填AF_INET。
        servaddr.sin_port = htons(m_port); // ②指定服务端的通信端口。
        struct hostent* h; // 用于存放服务端IP地址（大端序）的结构体的指针。
        if ((h = gethostbyname(m_ip.c_str())) == nullptr) {
            ::close(m_clientfd);
            m_clientfd = -1;
            return false;
        }
        memcpy(&servaddr.sin_addr, h->h_addr, h->h_length); // ③ 指定服务端的 IP（大端序）。
        if (::connect(m_clientfd, (struct sockaddr *)&servaddr, sizeof(servaddr)) == -1) {
            ::close(m_clientfd);
            m_clientfd = -1;
            return false;
        }
        return true;
    }
    // 向服务器发送数据，buffer 不要用 char*
    bool sendData(const string &buffer) {
        if ((::send(m_clientfd, buffer.data(), buffer.size(), 0)) <= 0) {
            ::close(m_clientfd);
            m_clientfd = -1;
            return false;
        }
        return true;
    }
    // 向服务器发送任意数据类型
    bool sendData(void *buffer, const size_t len) {
        if ((::send(m_clientfd, buffer, len, 0)) <= 0) {
            ::close(m_clientfd);
            m_clientfd = -1;
            return false;
        }
        return true;
    }
    // 从服务器接收数据，buffer 不要用 char*
    bool recvData(string &buffer, const size_t maxlen) {
        buffer.clear();
        buffer.resize(maxlen);
        int iret = ::recv(m_clientfd, &buffer[0], maxlen, 0); // 接收数据
        if (iret <= 0) {
            ::close(m_clientfd);
            m_clientfd = -1;
            return false;
        }
        buffer.resize(iret);
        return true;
    }
    bool recvData(void *buffer, const size_t len) {
        int iret = ::recv(m_clientfd, buffer, len, 0); // 接收数据
        if (iret <= 0) {
            ::close(m_clientfd);
            m_clientfd = -1;
            return false;
        }
        return true;
    }
    bool closeConnection() {
        if (m_clientfd == -1) return false;
        ::close(m_clientfd);
        m_clientfd = -1;
        return true;
    }
    bool sendfile(const string &filename,const size_t filesize) {
        ifstream fin(filename, ios::binary);//以二进制方式打开文件
        if(fin.is_open() == false) {
            cout << "Failed to open file: " << filename << endl;
            return false;
        } 
        int onread = 0; //。每次调用时打算读取的字节数
        int totalbytes = 0;//已经读取的总字节数
        char buffer[1024];//用于存储读取的字节数据的缓冲区
        while(totalbytes < filesize) {
            memset(buffer, 0, sizeof(buffer));
            onread = filesize - totalbytes > 1024 ? 1024 : filesize - totalbytes;
            fin.read(buffer, onread);
            if(sendData(buffer, onread) == false) {
                cout << "Failed to send file: " << filename << endl;
                fin.close();
                return false;
            }
            totalbytes += onread;
        }
        return true;
    }
    ~ctcpclient() { closeConnection(); }
};
struct st_fileinfo {
    char filename[512]; // 文件名
    int filesize; // 文件大小
};
int main(int argc, char* argv[]) {
    if (argc != 5) {
        cout << "Using: ./demo11 服务端的IP 服务端的端口 文件名 文件大小\nExample: ./demo7 192.168.101.138 5005 aaa.txt 2424\n\n";
        return -1;
    }
    ctcpclient tcpclient;
    if (!tcpclient.connectToServer(argv[1], atoi(argv[2]))) {
        cout << "connect failed.\n" << endl;
        return -1;
    }
    // 以下是发送文件的流程
    // 1. 把待传输文件名和文件的大小告诉服务端
    struct st_fileinfo fileinfo;
    memset(&fileinfo, 0, sizeof(fileinfo));
    strcpy(fileinfo.filename, argv[3]);
    fileinfo.filesize = atoi(argv[4]);
    if (!tcpclient.sendData(&fileinfo, sizeof(fileinfo))) {
        cout << "send failed.\n" << endl;
        return -1;
    }
    cout << "send ok. " << fileinfo.filename << " " << fileinfo.filesize << endl;
    // 2. 等待服务端的确认
    string buffer;
    if(tcpclient.recvData(buffer,2) == false) {
        cout << "Failed to receive confirmation from client" << endl;
        return 1;
    }
    if(buffer != "ok") {
        cout << "Client did not send confirmation" << endl;
        return 1;
    }
    // 3. 把文件内容发送给服务端
    if(!tcpclient.sendfile(argv[3], atoi(argv[4]))) {
        cout << "Failed to send file" << endl;
        return 1;
    }
    // 4. 等待服务端的确认
    if(tcpclient.recvData(buffer,2) == false) {
        cout << "Failed to receive confirmation from client" << endl;
        return 1;
    }
    if(buffer!= "ok") {
        cout << "Client did not send confirmation" << endl;return -1;
    }
    cout << "File sent successfully" << endl;
    // 5. 关闭socket
    // 以上流程中，发送和接收数据的函数都是阻塞的，直到数据发送或接收完成。
    return 0;
}