#include "_public.h"
using namespace idc;

cpative pative; // 增加心跳

// 程序退出和信号2、15的处理函数。
void EXIT(int sig);

int main(int argc,char *argv[])
{
    // 程序的帮助。
    if (argc != 4)
    {
        printf("\n");
        printf("Using:/project/tools/bin/gzipfiles pathname matchstr timeout\n\n");
        printf("Example:/project/tools/bin/gzipfiles /tmp/idc/surfdata \"*.xml,*.json\" 0.01\n");
        cout << R"(      /project/tools/bin/gzipfiles /log/idc "*.log.20*" 0.02)" << endl;
        printf("      /project/tools/bin/procctl 300 /project/tools/bin/gzipfiles /log/idc \"*.log.20*\" 0.02\n");
        printf("      /project/tools/bin/procctl 300 /project/tools/bin/gzipfiles /tmp/idc/surfdata \"*.xml,*.json\" 0.01\n\n");

        printf("这是一个工具程序，用于压缩历史的数据文件或日志文件。\n");
        printf("本程序把pathname目录及子目录中timeout天之前的匹配matchstr文件全部压缩，timeout可以是小数。\n");
        printf("本程序不写日志文件，也不会在控制台输出任何信息。\n");
        printf("本程序调用/usr/bin/gzip命令压缩文件。\n\n\n");

        return -1;
    }

    // 忽略全部的信号和关闭I/O，设置信号处理函数。
    closeioandsignnal(true);
    signal(2,EXIT); signal(15,EXIT);

    //忽略全部的信号和关闭 I/O，设置信号处理函数
    closeioandsignal(true);
    signal(2,EXIT);signal(15,EXIT);

    pactive.addpinfo(120,"deletefile"); // 把进程的心跳信息添加到共享内存中。
    
    //获取被定义为隶属数据文件的时间点
    string strtimeout = ltime1("yyyymmddhh24miss",0-(int)atof(argv[3])*24*60*60);

    //打开目录
    cdir dir;
    if (dir.opendir(argv[1],argv[2],10000,true,false)==false) {
        printf("dir.opendir(%s) failed.\n",argv[1]);return -1;
    }
    //遍历目录的文件，如果是历史文件，压缩它
    while(dir.readdir()==true) {
        //把文件时间和历史文件的时间点比较，更早且不是压缩文件就压缩
        if(dir.m_mtime<strtimeout && dir.m_filename.find(".gz")==string::npos) {
            char cmdstr[1024];
            string strcmd = "/usr/bin/gzip -f" + dir.m_fullfilename + "1>/dev/null 2>/dev/null";
            if(system(strcmd.c_str())!=0) {
                printf("system(%s) failed.\n",strcmd.c_str());
                printf("压缩文件：%s\n",dir.m_fullfilename);
                // gzipfiles(dir.m_fullfilename.c_str());
            }
            //压缩比较慢
            pactive.uptatime();
        }
    }   
}