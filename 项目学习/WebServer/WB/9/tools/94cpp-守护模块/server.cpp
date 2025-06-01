#include "_public.h"
using namespace idc;

// 进程心跳信息的结构体。
struct stprocinfo
{
    int    pid=0;        // 进程id。
    char   pname[51]={0}; // 进程名称，可以为空。
    int    timeout=0;    // 超时时间，单位：秒。
    time_t atime=0;      // 最后一次心跳的时间，用整数表示。
    strprocinfo() = default; // 构造函数，初始化结构体。有了自定义的构造函数，就不能使用默认的构造函数了。
    strprocinfo(int _pid,const char *_pname,int _timeout,time_t _atime) : pid(_pid),timeout(_timeout),atime(_atime) {
        strncpy(pname,_pname.c_str(),50);
    }
};

int m_shmid=-1;     // 共享内存的id。
int m_pos = -1;
stprocinfo *m_shm = nullptr; //指向共享内存的地址空间

void EXIT(int sig) ;

int main()
{
    // 处理程序的退出信号。
    signal(SIGINT,EXIT);
    signal(SIGTERM,EXIT);
    // 创建/获取共享内存。
    if((m_shmid = shmget((key_t)0x5095,1000*sizeof(struct stprocinfo),0644|IPC_CREAT)) == -1) {
        printf("创建/共享共享内存(%x)失败\n",0x5095);return -1;
    }
    // 将共享内存连接到当前进程的地址空间。
    m_shm = (stprocinfo *)shmat(m_shmid,0,0);
    // 把当前进程的信息填充到结构体中。
    // strprocinfo procinfo;
    // memset(&procinfo,0,sizeof(procinfo));
    // procinfo.pid = getpid(); // 获取当前进程的id。
    // strncpy(procinfo.pname,"server1",50);// 获取当前进程的名称。
    // procinfo.timeout = 30;// 设置超时时间，单位：秒。
    // procinfo.atime = time(0);// 获取当前时间。
    strprocinfo procinfo(getpid(),"server1",30,time(0));
    csemp semlock;// 信号量
    if(semlock.init(0x5095) == false) {
        printf("信号量初始化失败\n");return -1;
    }
    semlock.wait();// 等待信号量

    // 进程id是循环使用的，如果曾经有一个进程异常退出，没有清理自己的心跳信息，
    // 它的进程信息将残留在共享内存中，不巧的是，如果当前进程重用了它的id，
    // 所以，如果共享内存中已存在当前进程编号，一定是其它进程残留的信息，当前进程应该重用这个位置。
    for(int i=0;i<1000;i++) {
        if(m_shm[i].pid == procinfo.pid) {
            m_pos = i;
            cout << "找到旧位置 m_pos:" << m_pos << endl;
            break;
        }
    }
    // 在共享内存中寻找一个空的位置，把当前进程的结构体保存到共享内存中。
    if(m_pos == -1) {
        for(int i=0;i<1000;i++) {
            if(m_shm[i].pid == 0) {
                m_pos = i;
                cout << "m_pos:" << m_pos << endl;
                break;
            }
        }
    }
    if(m_pos == -1) {
        semlock.post();// 释放信号量
        printf("共享内存已满\n");return -1;
    }   
    memcpy(&m_shm[m_pos],&procinfo,sizeof(procinfo));
    semlock.post();// 释放信号量    
    while (1)
    {
        printf("服务程序正在运行中...\n");
        sleep(10);
        // 更新进程的心跳信息。
        m_shm[m_pos].atime = time(0);
    }
    
    return 0;
}
void EXIT(int sig) { //信号处理函数
    cout << "sig = " << sig << endl;
    // 从共享内存中删除当前进程的心跳信息。
    if(m_pos != -1) {
        memset(&m_shm[m_pos],0,sizeof(stprocinfo));
    }
    //把共享内存从当前进程分离
    if(shmdt(m_shm)==-1) {
        shmdt(m_shm);
    }
    exit(0);
}