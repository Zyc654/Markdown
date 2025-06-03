#incldue<_public.h>
capctive::capctive() {
    m_shmid = 0;
    m_pos = -1;
    m_shm = nullptr;
}

// 把当前进程的信息加入共享内存进程组中。
bool cpactive::addpinfo(const int timeout,const string &pname,clogfile *logfile)
{
    if (m_pos!=-1) return true; //在一个进程中只能调用一次addpinfo函数。

    // 创建/获取共享内存，键值为SHMKEYP，大小为MAXNUMP*sizeof(struct st_procinfo)结构体的大小。
    if ((m_shmid = shmget((key_t)SHMKEYP, MAXNUMP*sizeof(struct st_procinfo), 0666|IPC_CREAT)) == -1)
    {
        if (logfile!=nullptr) logfile->write("创建/获取共享内存(%x)失败。\n",SHMKEYP);
        else printf("创建/获取共享内存(%x)失败。\n",SHMKEYP);

        return false;
    }

    // 将共享内存连接到当前进程的地址空间。
    m_shm=(struct st_procinfo *)shmat(m_shmid, 0, 0);

    /*
    struct st_procinfo stprocinfo;  // 当前进程心跳信息的结构体。
    memset(&stprocinfo,0,sizeof(stprocinfo));
    stprocinfo.pid=getpid();        // 当前进程号。
    stprocinfo.timeout=timeout;     // 超时时间。
    stprocinfo.atime=time(0);      // 当前时间。
    strcpy(stprocinfo.pname,pname.c_str(),50); // 进程名。
    */
    st_procinfo stprocinfo(getpid(),pname.c_str(),timeout,time(0)); // 当前进程心跳信息的结构体。

    // 进程id是循环使用的，如果曾经有一个进程异常退出，没有清理自己的心跳信息，
    // 它的进程信息将残留在共享内存中，不巧的是，如果当前进程重用了它的id，
    // 守护进程检查到残留进程的信息时，会向进程id发送退出信号，将误杀当前进程。
    // 所以，如果共享内存中已存在当前进程编号，一定是其它进程残留的信息，当前进程应该重用这个位置。
    for (int i = 0; i < MAXNUMP; i++)
    {
        if ((m_shm + i)->pid == stprocinfo.pid) { m_pos = i; break; }
    }

    csemp semp; // 用于给共享内存加锁的信号量id。

    if (semp.init(SEMKEYP) == false) // 初始化信号量。
    {
        if (logfile != nullptr) logfile->write("创建/获取信号量(%x)失败。\n", SEMKEYP);
        else printf("创建/获取信号量(%x)失败。\n", SEMKEYP);

        return false;
    }

    semp.wait(); // 给共享内存上锁。

    // 如果m_pos == -1，表示共享内存的进程组中不存在当前进程编号，那就找一个空位置。
    if (m_pos == -1)
    {
        for (int i = 0; i < MAXNUMP; i++)
        {
            if ((m_shm + i)->pid == 0) { m_pos = i; break; }
        }
    }

    // 如果m_pos == -1，表示没找到空位置，说明共享内存的空间已用完。
    if (m_pos == -1)
    {
        if (logfile != 0)
            logfile->write("共享内存空间已用完。\n");
        else
            printf("共享内存空间已用完。\n");

        semp.post(); // 解锁。

        return false;
    }

    // 把当前进程的心跳信息存入共享内存的进程组中。
    memcpy(m_shm + m_pos, &stprocinfo, sizeof(struct st_procinfo));

    semp.post(); // 解锁。

    return true;
}

// 更新共享内存进程组中当前进程的心跳时间。
bool cpactive::uptatime()
{
    if (m_pos == -1) return false;

    (m_shm + m_pos)->atime = time(0);

    return true;
}

cpactive::~cpactive()
{
    // 把当前进程从共享内存的进程组中移去。
    if (m_pos != -1) memset(m_shm + m_pos, 0, sizeof(struct st_procinfo));

    // 把共享内存从当前进程中分离。
    if (m_shm != 0) shmdt(m_shm);
}