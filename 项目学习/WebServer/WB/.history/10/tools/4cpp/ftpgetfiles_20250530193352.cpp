// #include "../_public.h"
#define _public.h
#include <signal.h>
#include<bits/stdc++.h>
#include <ftplib.h>
#include "../public/_ftp.h"
#include <unistd.h> // for close() and fork()   
// using namespace idc;

// 程序退出和信号2、15的处理函数。
void EXIT(int sig);

struct st_arg {
    char host[31];//  ip 和端口
    int mode;// 1-被动模式，2-主动模式。缺省表示被动模式
    char username[31];// ftp用户名。
    char password[31];// ftp密码。
    char remotepath[101];//  ftp服务器上的目录名。
    char localpath[101];//  本地目录名。
    char matchname[101];// 文件名匹配规则，多个匹配规则之间用","分隔。
    int ptype; //下载后服务端文件处理方式，2删除，3备份
    char remotepathbak[256]; //  ftp服务器上的备份目录名。
    char okfilename[256];//  成功下载的文件名列表。
    bool checktime;// 是否检查文件的修改时间。
} starg;


struct st_fileinfo {
    string filename;// 文件名。
    string mtime;// 文件的修改时间，格式：yyyymmddhh24miss。
    st_fileinfo() = default;// 文件的大小和修改时间。
    st_fileinfo(string _filename, string _mtime) : filename(_filename), mtime(_mtime) {}
    void clear() { filename.clear(); mtime.clear(); }// 清空结构体成员变量的值。
};


map<string, string> mfromok; // 用于存放已下载成功文件名和文件的修改时间。
list<st_fileinfo> vfromnlist; // 下载前列出的文件列表。从 nlist 中加载


vector<st_fileinfo> vfilelist; // 用于存放文件名和文件的修改时间。

bool loadistfile();// 加载本地文件列表。

void help();
bool xmltoarg(const char *strmlbuffer);// 解析xml，得到程序运行的参数。


clogfile logfile;

cftpclient ftp; // 定义ftp对象。

int main(int argc, char *argv[])
{
    // 第一步计划：从服务器某个目录中下载文件，可以指定文件名匹配的规则。
    // main()的参数：日志文件名 ftp服务器端的ip和端口 ftp服务器的传输模式 ftp用户名 ftp密码 服务端的目录名 文件名匹配规则
    // ./ftpgetfiles /log/idc/ftpgetfiles.log 192.168.150.28:21 1 wucz oracle /data/server/surfdata /data/client/surfdata "*.xml,*.csv"
    // ./ftpgetfiles "/log/idc/ftpgetfiles.log" "<host>192.168.150.28:21</host><mode>1</mode><username>wucz</username>"\
    "<password>oracle</password><remotepath>/data/server/surfdata</remotepath>"\
    "<localpath>/data/client/surfdata</localpath><matchname>*.xml,*.csv</matchname>"
    // 第一步计划：从服务器某个目录中下载文件，可以指定文件名匹配的规则。
    if(argc != 3) {
        help();return -1;
    }


    // 设置信号，在shell状态下可用"kill + 进程号" 正常终止些进程。
    // 但请不要用 "kill -9 +进程号" 强行终止。
    // closeioandsignal(true);    // 关闭0、1、2和忽略全部的信号，在调试阶段，这行代码可以不启用。
    signal(SIGINT,EXIT); signal(SIGTERM,EXIT);

    // 打开日志文件。
    if(logfile.open(argv[1],"a+") == false) {
        printf("logfile.open(%s) failed.\n", argv[1]); return -1;
    }
    // 解析xml，得到程序运行的参数。
    if(xmltoarg(argv[2]) == false) {
        logfile.write("xmltoarg(%s) failed.\n", argv[2]); return -1;
    }

    // 登录ftp服务器。
    if(ftp.login(starg.host, starg.username, starg.password, starg.mode) == false) {
        logfile.write("ftp.login(%s, %s, %s, %d) failed.\n", starg.host, starg.username, starg.password, starg.mode); return -1;
    }


    // 进入ftp服务器存放文件的目录。
    if(ftp.chdir(starg.remotepath) == false) {
        logfile.write("ftp.chdir(%s) failed.\n", starg.remotepath); return -1;
    }

    // 调用ftpclient.nlist()方法列出服务器目录中的文件名，保存在本地文件中。
    if(ftp.nlist(".",sformat("/tmp/nlist/ftpgetfiles_%d.nlist",getpid())) == false) {
        logfile.write("ftp.nlist(list.txt) failed.\n"); return -1;
    }
    logfile.write("ftp.nlist(list.txt) success.\n",sformat("/tmp/nlist/ftpgetfiles_%d.nlist",getpid()).c_str());
    
    // 把ftpclient.nlist()方法获取到的list文件加载到容器vfilelist中。
    if(loadistfile() == false) {
        logfile.write("loadistfile() failed.\n"); return -1;
    }
    string strremptefilename, strlocalfilename; // 远程文件名和本地文件名。
    // 遍历vfilelist容器。
    for (auto & aa : vfilelist) {
        strremptefilename = sformat("%s/%s", starg.remotepath, aa.filename.c_str()); // 远程文件名。
        strlocalfilename = sformat("%s/%s", starg.localpath, aa.filename.c_str()); // 本地文件名。
        logfile.write("strremptefilename=%s, strlocalfilename=%s\n", strremptefilename.c_str(), strlocalfilename.c_str());
        // 调用ftpclient.mtime()方法获取远程文件的修改时间。
        if(ftp.mtime(strremptefilename) == false) {
            logfile << "ftp.mtime(" << strremptefilename << ") failed.\n"; return -1;
        }
        logfile << "ftp.mtime(" << strremptefilename << ") success, mtime=" << ftp.m_mtime << "\n";
    // ptype==1，增量下载文件。
    if (starg.ptype==1) {
        getxmlbuffer(strxmlbuffer,"okfilename",starg.okfilename,255); // 从strxmlbuffer中获取okfilename的值。
        // 检查本地文件是否存在，如果存在，比较本地文件和远程文件的修改时间。
        if(strlen(starg.okfilename) == 0) { 
            logfile.write("strlen(starg.okfilename) == 0\n");
        }

        getxmlbuffer(strxmlbuffer,"checktime",starg.checktime,255); // 从strxmlbuffer中获取checktime的值。
        if(starg.checktime == false) { // 如果checktime为false，直接下载文件。
            logfile.write("starg.checktime == false\n");
            // 调用ftpclient.get()方法下载文件。    
    }

    // ptype==2，删除服务端的文件。
    if (starg.ptype==2)
    {
        if (ftp.ftpdelete(strremotefilename)==false)
        {
            logfile.write("ftp.ftpdelete(%s) failed.\n",strremotefilename.c_str());
            return -1;
        }
    }
    // ptype==3，备份服务端的文件。
        if (starg.ptype==3){
            if (ftp.ftprename(strremotefilename,starg.remotepathbak)==false)    
            {
                logfile.write("ftp.ftprename(%s) failed.\n",strremotefilename.c_str());
                return -1;
            }
        }
    }

    //for (auto & aa : vfilelist)
    //{
    //    调用ftpclient.get()方法下载文件。
    //}


    return 0;
}

void EXIT(int sig)
{
    printf("程序退出, sig=%d\n\n", sig);
    exit(0);
}

bool loadistfile() {
    // 把ftpclient.nlist()方法获取到的list文件加载到容器vfilelist中。
    // 打开list文件。
    vfilelist.clear();
    cifile ifile;
    if(ifile.open(sformat("/tmp/nlist/ftpgetfiles_%d.nlist",getpid())) == false) {
        logfile.write("ifile.open(%s) failed.\n", sformat("/tmp/nlist/ftpgetfiles_%d.nlist",getpid()).c_str()); return false;
    }

    string strfilename; // 文件名。
    string strmtime; // 文件的修改时间。
    while(true) {
        if(ifile.fgets(strfilename) == false) break; // 从list文件中读取一行。
        if(matchstr(strfilename.c_str(), starg.matchname) == false) continue; // 匹配文件名。
        vfilelist.push_back(st_fileinfo(strfilename, "")); // 把文件名和文件的修改时间添加到容器vfilelist中。

    }
    ifile.closeandremove(); // 关闭list文件。
    return true;
}

void help() {
    printf("\n");
    printf("Sample:/project/tools/bin/proctl 30 /project/tools/bin/ftpgetfiles /log/idc/ftpgetfiles_surfdata.log \" \
        \"<host>192.168.150.128:21</host><mode>1</mode> \
        \"<username>wucz</username><password>oracle</password>\" \
        \"<remotepath>/tmp/idc/surfdata</remotepath><localpath>/idcdata/surfdata</localpath>\" \
        \"<matchname>SURF_ZH*.XML,SURF_ZH*.CSV</matchname>\" \
        \"<ptype>3</ptype><remotepathbak>/tmp/idc/surfdata_bak</remotepathbak>\"\n\n");
        
        // 1)下载文件后，删除ftp服务器上的文件。
        // 2)下载文件后，把ftp服务器上的文件移动到备份目录。
        // 3)增量下载文件，每次只下载新增的和修改过的文件。
        
        printf("本程序是通用的功能模块，用于把远程ftp服务端的文件下载到本地目录。\n");
        printf("logfilename是本程序运行的日志文件。\n");
        printf("xmlbuffer为文件下载的参数，如下：\n");
        printf("<host>192.168.150.128:21</host> 远程服务端的IP和端口。\n");
        printf("<mode>1</mode> 传输模式，1-被动模式，2-主动模式，缺省采用被动模式。\n");
        printf("<username>wucz</username> 远程服务端ftp的用户名。\n");
        printf("<password>oracle</password> 远程服务端ftp的密码。\n");
        printf("<remotepath>/tmp/idc/surfdata</remotepath> 远程服务端存放文件的目录。\n");
        printf("<localpath>/idcdata/surfdata</localpath> 本地文件存放的目录。\n");
        printf("<matchname>SURF_ZH*.XML,SURF_ZH*.CSV</matchname> 待下载文件匹配的规则。\n");
        printf("不匹配的文件不会被下载，本字段尽可能设置精确，不建议用*匹配全部的文件。\n");
        printf("<ptype>1</ptype> 文件下载成功后，远程服务端文件的处理方式： \"\
        1-什么也不做；2-删除；3-备份，如果为3，还要指定备份的目录。\n");
        printf("<remotepathbak>/tmp/idc/surfdata_bak</remotepathbak> 文件下载成功后，服务端文件的备份目录， \"\
        此参数只有当ptype=3时才有效。\n\n");
}

bool xmltoarg(const char *strxmlbuffer) {
    // 解析xml，得到程序运行的参数。
    memset(&starg, 0, sizeof(starg));
    getxmlbuffer(strxmlbuffer, "host", starg.host, 30); // 从xmlbuffer中提取host节点的值。
    if (strlen(starg.host) == 0) { logfile.write("host is null.\n"); return false; }
    
    getxmlbuffer(strxmlbuffer, "mode", starg.mode, 10); // 从xmlbuffer中提取mode节点的值。
    if(strag.mode != 2)
        starg.mode = 1;
    
    getxmlbuffer(strxmlbuffer, "username", starg.username, 30); // 从xmlbuffer中提取username节点的值。
    if(strlen(starg.username) == 0) { logfile.write("username is null.\n"); return false; }

    getxmlbuffer(strxmlbuffer, "password", starg.password, 30); // 从xmlbuffer中提取password节点的值。
    if(strlen(starg.password) == 0) { logfile.write("password is null.\n"); return false; }

    getxmlbuffer(strxmlbuffer, "remotepath", starg.remotepath, 100); // 从xmlbuffer中提取remotepath节点的值。    
    if(strlen(starg.remotepath) == 0) { logfile.write("remotepath is null.\n"); return false; }

    getxmlbuffer(strxmlbuffer, "localpath", starg.localpath, 100); // 从xmlbuffer中提取localpath节点的值。
    if(strlen(starg.localpath) == 0) { logfile.write("localpath is null.\n"); return false; }

    getxmlbuffer(strxmlbuffer, "matchname", starg.matchname, 100); // 从xmlbuffer中提取matchname节点的值。   
    if(strlen(starg.matchname) == 0) { logfile.write("matchname is null.\n"); return false; }
    
    getxmlbuffer(strxmlbuffer, "ptype", starg.ptype); // 从xmlbuffer中提取ptype节点的值。
    if(starg.ptype!= 1 && starg.ptype!= 2 && starg.ptype!= 3) { logfile.write("ptype is error.\n"); return false; }

    if(starg.ptype == 3) { // 如果ptype=3，还要指定备份的目录。
        // 从xmlbuffer中提取remotepathbak节点的值。  
        getxmlbuffer(strxmlbuffer, "remotepathbak", starg.remotepathbak, 256); 
        if(strlen(starg.remotepathbak) == 0) { logfile.write("remotepathbak is null.\n"); return false; }
    }

    return true;
}