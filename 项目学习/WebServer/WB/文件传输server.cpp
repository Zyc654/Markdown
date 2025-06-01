#include <iostream>
#include <string>
#include <cstring>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <fstream>
#include<signal.h>
#include<sys/wait.h>

using namespace std;

class ctcpserver {
private:
    int m_listenfd; // 监听套接字
    int m_clientfd; // 客户端套接字
    string m_clientip; // 客户端IP地址
    unsigned short m_port; // 端口号
public:
    ctcpserver() : m_listenfd(-1), m_clientfd(-1) {} // 构造函数

    // 初始化服务器，修正拼写错误
    bool initserver(unsigned short port) {
        // 创建套接字，修正运算符优先级问题
        if ((m_listenfd = socket(AF_INET, SOCK_STREAM, 0)) == -1) return false;
        m_port = port;
        // 把服务器 ip 和 端口绑定套接字
        struct sockaddr_in serveraddr;  // 服务器地址
        memset(&serveraddr, 0, sizeof(serveraddr));
        serveraddr.sin_family = AF_INET;
        serveraddr.sin_port = htons(m_port);    // 主机字节序转网络字节序
        serveraddr.sin_addr.s_addr = htonl(INADDR_ANY); // 主机字节序转网络字节序

        if (bind(m_listenfd, (struct sockaddr*)&serveraddr, sizeof(serveraddr)) == -1) {
            close(m_listenfd);
            m_listenfd = -1;
            return false;
        }

        // 将套接字设置为监听模式
        if (listen(m_listenfd, 10) == -1) {
            close(m_listenfd);
            m_listenfd = -1;
            return false;
        }

        return true;
    }
    // 接受客户端连接，修改函数名避免与标准库重名
    bool acceptClient() {
        // 接受客户端连接，accpet返回一个新的套接字，用于与客户端通信，阻塞
        struct sockaddr_in caddr;
        socklen_t addrlen = sizeof(caddr);    // 客户端地址长度
        // 修正运算符优先级问题
        if ((m_clientfd = ::accept(m_listenfd, (struct sockaddr*)&caddr, &addrlen)) == -1) {
            return false;
        }
        m_clientip = inet_ntoa(caddr.sin_addr); // 网络字节序转主机字节序
        return true;
    }
    // 获取客户端IP地址
    const string & clientip() const {
        return m_clientip;
    }
    // 发送数据
    bool send(const string &buffer) {
        if (m_clientfd == -1) return false;
        if ((::send(m_clientfd, buffer.c_str(), buffer.size(), 0)) <= 0) {
            return false;
        }
        return true;
    }
    // 接收数据
    bool recv(string &buffer, size_t maxlen) {
        buffer.clear();
        buffer.resize(maxlen);
        int readn = ::recv(m_clientfd, &buffer[0], maxlen, 0);
        if (readn <= 0) {
            buffer.clear();
            return false;
        }
        buffer.resize(readn);
        return true;
    }
    bool send(void *buffer, size_t len) {
        if (m_clientfd == -1) return false;
        if ((::send(m_clientfd, buffer, len, 0)) <= 0) {
            return false;
        }
        return true;
    }
    bool recv(void *buffer, size_t len) {
        int readn = ::recv(m_clientfd, buffer, len, 0);
        if (readn <= 0) {
            return false;
        }
        return true;
    }
    // 关闭监听socket，修正逻辑错误
    bool closelisten() {
        if (m_listenfd == -1) {
            return false;
        }
        ::close(m_listenfd);
        m_listenfd = -1;
        return true;
    }
    // 关闭客户端socket
    bool closeclient() {
        if (m_clientfd == -1) return false;
        ::close(m_clientfd);
        m_clientfd = -1;
        return true;
    }
    bool recvfile(const string &filename, const size_t filesize) {
        ofstream fout(filename, ios::binary);
        if (!fout.is_open()) {
            cout << "Failed to open file: " << filename << endl;
            return false;
        }
        int onread = 0; // 每次调用时打算读取的字节数
        int totalbytes = 0; // 已经读取的总字节数
        char buffer[1024]; // 用于存储读取的字节数据的缓冲区
        while (totalbytes < filesize) {
            memset(buffer, 0, sizeof(buffer));
            onread = (filesize - totalbytes > 1024) ? 1024 : (filesize - totalbytes);
            // 调用正确的 recv 函数版本
            if (this->recv(buffer, onread) == false) {
                cout << "Failed to receive file: " << filename << endl;
                fout.close();
                return false;
            }
            fout.write(buffer, onread);
            totalbytes += onread;
        }
        fout.close();
        return true;
    }
    ~ctcpserver() {
        closelisten();
        closeclient();
    }
};
// 修正 st_fileinfo 定义
struct st_fileinfo {
    char filename[512]; // 文件名
    int filesize; // 文件大小
};
ctcpserver server;
void FathEXIT(int sig) {// 父进程信号处理函数
    //以下代码是为了防止信号处理函数在执行时被再次触发
    signal(SIGTERM, SIG_IGN);
    signal(SIGINT, SIG_IGN);
    // cout << "Server exiting..." << endl;
    kill(0, SIGTERM); // 向所有子进程发送 SIGTERM 信号
    server.closelisten(); // 关闭监听套接字
    exit(0); // 父进程退出
} 
void ChildEXIT(int sig) { // 子进程信号处理函数
    //以下代码是为了防止信号处理函数在执行时被再次触发
    signal(SIGTERM, SIG_IGN);
    signal(SIGINT, SIG_IGN);    
    // cout << "Child process exiting..." << endl;
    server.closeclient(); // 关闭客户端套接字
    exit(0); // 子进程退出
}
int main(int argc, char* argv[]) {
    if (argc != 2) {
        cout << "Usage: " << argv[0] << " <port>" << endl;
        return 1;
    }
    for(int i=1;i<=64;i++) signal(i,SIG_IGN); // 忽略所有信号，顺便解决僵尸进程问题
    //设置信号，在 shell 可用kill 或 ctrl+c 结束父进程
    //单不要用kill -9 结束父进程
    signal(SIGTERM, FathEXIT); //15
    signal(SIGINT,FathEXIT); // 父进程信号处理函数 2
    if (!server.initserver(atoi(argv[1]))) {
        cout << "Server initialization failed" << endl;
        return 1;
    }
    cout << "Server started on port " << argv[1] << endl;
    while (true) {
        // 接受客户端连接，从已接受的客户端队列中取出一个客户端连接
        // 并返回一个新的套接字，用于与客户端通信
        if (!server.acceptClient()) {
            cout << "Failed to accept client connection" << endl;
            continue;
        }
        // 创建子进程处理客户端请求
        pid_t pid = fork();
        if (pid < 0) {
            perror("fork");
            server.closeclient();
        } else if(pid > 0) {
            // 父进程
            server.closeclient(); // 父进程不需要客户端套接字
            continue;
        }else if (pid == 0) {
            // 子进程
            // 子进程不需要再次调用 accept，因为它已经在父进程中被调用过了
            // 子进程可以直接使用父进程的客户端套接字进行通信
            signal(SIGTERM, ChildEXIT); // 子进程信号处理函数
            signal(SIGINT, SIG_IGN); // 子进程忽略 SIGINT 信号
            server.closelisten(); // 子进程不需要监听套接字
            // 以下是接收文件的流程
            cout << "Client connected from " << server.clientip() << endl;
            st_fileinfo fileinfo;
            // 1. 接收文件名和文件大小信息
            memset(&fileinfo, 0, sizeof(fileinfo));
            if (!server.recv(&fileinfo, sizeof(fileinfo))) {
                cout << "Failed to receive file info" << endl;
                server.closeclient();
                return 1;
            }
            cout << "Received file info: " << fileinfo.filename << " " << fileinfo.filesize << endl;
            // 2. 给客户端回复确认报文，表示客户端可以发送文件了
            if (!server.send("ok")) {
                cout << "send failed.\n" << endl;
                server.closeclient();
                return -1;
            }
            // 3. 接收文件内容
            if (!server.recvfile(fileinfo.filename, fileinfo.filesize)) {
                cout << "Failed to receive file: " << fileinfo.filename << endl;
                server.closeclient();
                return 1;
            }
            cout << "File received successfully: " << fileinfo.filename << endl;
            // 4. 给客户端回复确认报文，表示文件接收完成
            if (!server.send("ok")) {
                cout << "send failed.\n" << endl;
                server.closeclient();
                return -1;
            }
            return 0;//子进程一定要退出，否则会回到accetp等待状态，变为僵尸进程
        } 
    }
    return 0;
}