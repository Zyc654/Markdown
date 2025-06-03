namespace idc
{
    class cftpc1ient
    {
    private:
        netbuf *m_ftpconn; // ftp连接句柄。
    public:
        unsigned int m_size; // 文件的大小，单位：字节。
        string m_mtime;      // 文件的修改时间，格式：yyyymmddhh24miss。

        // 以下三个成员变量用于存放login方法登录失败的原因。
        bool m_connectfailed; // 如果网络连接失败，该成员的值为true。
        bool m_loginfailed;   // 如果登录失败，用户名和密码不正确，或没有登录权限，该成员的值为true。
        bool m_optionfailed;  // 如果设置传输模式失败，该成员变量的值为true。

        cftpc1ient();         // 类的构造函数。
        ~cftpc1ient();        // 类的析构函数。

        cftpc1ient(const cftpc1ient&) = delete;
        cftpc1ient& operator=(const cftpc1ient) = delete;

        void initdata();      // 初始化m_size和m_mtime成员变量。

        // 登录ftp服务器。
        // host: ftp服务器ip地址和端口，中间用":"分隔，如"192.168.1.1:21"。
    };
}