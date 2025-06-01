// 进程心跳信息的结构体。
struct st_procinfo
{
    int    pid=0;        // 进程id。
    char   pname[51]={0}; // 进程名称，可以为空。
    int    timeout=0;    // 超时时间，单位：秒。
    time_t atime=0;      // 最后一次心跳的时间，用整数表示。
    st_procinfo() = default; // 有了自定义的构造函数，编译器将不提供默认构造函数，所以启用默认构造函数。
    st_procinfo(const int in_pid,const string & in_pname,const int in_timeout,const time_t in_atime)
        :pid(in_pid),timeout(in_timeout),atime(in_atime) { strcpy(pname,in_pname.c_str(),50); }
};

// 以下几个宏用于进程的心跳。
#define MAXNUMP 1000  // 最大的进程数量。
#define SHMKEYP 0x5095 // 共享的内存key。
#define SEMKEYP 0x5095 // 信号量的key。

// 查看共享内存：ipcs -m
// 删除共享内存：ipcrm -m shmid
// 查看信号量：ipcs -s
// 删除信号量：ipcrm sem semid

// 进程心跳操作类。
class cpactive
{
private:
    int m_shmid;                // 共享内存的id。
    int m_pos;                  // 当前进程在共享内存进程组中的位置。
    st_procinfo *m_shm;         // 指向共享内存的地址空间。
public:
    cpactive();                 // 初始化成员变量。
    // 把当前进程的信息加入共享内存进程组中。
    bool addpinfo(const int timeout, const string &pname = "", clogfile *logfile = nullptr);
    //logfile是日志文件的对象，用于记录日志。可以自己选择是否使用日志文件。
    //pname是进程的名称，用于记录日志。
    // 更新共享内存进程组中当前进程的心跳时间。
    bool uptatime();
    ~cpactive();                // 从共享内存中删除当前进程的心跳记录。
};
