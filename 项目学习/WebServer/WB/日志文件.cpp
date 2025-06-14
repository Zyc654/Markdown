class clogfile
{
private:
    ofstream fout;                 // 日志文件对象。
    string m_filename;             // 日志文件名，建议采用绝对路径。
    ios::openmode m_mode;          // 日志文件的打开模式。
    bool m_backup;                 // 是否自动切换日志。
    int m_maxsize;                 // 当日志文件的大小超过本参数时，自动切换日志。
    bool m_enbuffer;               // 是否启用文件缓冲区。
    spinlock_mutex m_splock;       // 多进程和多线程中日志文件的锁需要测试。
public:
    // 构造函数，日志文件的大小缺省100M。
    clogfile(int maxsize = 100) : m_maxsize(maxsize) {}
    // 打开日志文件。
    // filename: 日志文件名，建议采用绝对路径，如果文件名中的目录不存在，就先创建目录。
    // openmode: 日志文件的打开模式，缺省值是ios::app。
    // bbackup: 是否自动切换（备份），true-切换，false-不切换，在多进程的服务程序中，如果多个进程共用一个日志文件，bbackup必须为true。
    // benbuffer: 是否启用文件缓冲机制，true-启用，false-不启用，如果启用缓冲区，那么写进日志文件中的内容不会立即写入文件，缺省是不启用。
    // 注意，在多进程的程序中，多个进程往同一日志文件写入大量的日志时，可能会出现小混乱，但是，多线程不会。
    bool open(const string &filename, const ios::openmode mode = ios::app, bool bbackup = true, bool benbuffer = false);
    // 把数据以文本的方式格式化输出到文件。
    template<typename... Args>
    bool write(const char* format, Args... args)
    {
        if(fout.is_open()==false) return false;
        backup(); // 判断是否需要切换日志
        m_splock.lock();
        fout << ltime1() << " " << sformat(format, args...); // 格式化输出到文件。
        m_splock.unlock(); //解锁
        return fout.good();
    }
    // 重载<<运算符，把日志内容以文本的方式输出到日志文件，不会在日志内容前面写时间。
    // 注意：内容换行用\n，不能用endl。
    template<typename T>
    clogfile& operator<<(const T &value)
    {
        m_splock.lock();
        fout << value;
        m_splock.unlock();
        return *this;
    }
private:
    // 如果日志文件的大小超过m_maxsize的值，就把当前的日志文件名改为历史日志文件名，再创建新的当前日志文件。
    // 备份后的文件会在日志文件名后加上日期时间，如/tmp/log/filetodb.log.20200101112302。
    // 注意，在多进程的程序中，日志文件不可切换，多线的程序中，日志文件可以切换。
    bool backup();
public:
    void close() { fout.close(); }
    ~clogfile() { close(); }
};
};
bool clogfile::open(const string &filename, const ios::openmode mode, const bool bbackup, const bool benbuffer)
{
    // 如果日志文件是打开的状态，先关闭它。
    if (fout.is_open()) fout.close();
    m_filename = filename;     // 日志文件名。
    m_mode = mode;             // 打开模式。
    m_backup = bbackup;        // 是否自动备份。
    m_enbuffer = benbuffer;    // 是否启用文件缓冲区。
    newdir(m_filename, true);  // 如果日志文件的目录不存在，创建它。
    fout.open(m_filename, m_mode);  // 打开日志文件。
    if (m_enbuffer == false) fout << unitbuf;  // 是否启用文件缓冲区。
    return fout.is_open();
}
bool clogfile::backup()
{
    // 不备份
    if(m_backup == false) return true;
    if(fout.is_open() == false) return false;
    // 如果当前日志文件超过m_maxsize，备份日志。
    if(fout.tellp() > m_maxsize * 1024 * 1024)
    {
        m_splock.lock();   // 加锁。
        fout.close();      // 关闭当前日志文件。
        // 拼接备份日志文件名。
        string bak_filename = m_filename + "." + ltime1("yyyymmddhh24miss");
        rename(m_filename.c_str(), bak_filename.c_str()); // 把当前日志文件改名为备份日志文件。
        fout.open(m_filename, m_mode); // 重新打开当前日志文件。
        if(m_enbuffer == false) fout << unitbuf; // 判断是否启动文件缓冲区。
        m_splock.unlock(); // 解锁。
        return fout.is_open();
    }
    return true;
}
int main()
{
    clogfile logifle; //创建日志对象。
    //打开日志文件。
    if (logfile.open("/tmp/log/demo42.log") == false)
    {
        printf("logfile.open(/tmp/log/demo42.log) failer.\n");
        return -1;
    }
    logfile.write("程序开始运行。\n");
    for (int i = 0; i < 10; i++)
        logfile.write("这是第%d个%s...ok\n", i, "超级女生");
    logfile.write("程序运行结束。\n");
}