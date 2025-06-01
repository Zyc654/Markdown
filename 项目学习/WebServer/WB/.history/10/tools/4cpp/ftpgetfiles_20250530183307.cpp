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

cftpclient ftp; // 定义ftp对象。

int main(int argc, char *argv[])
{
    // 第一步计划：从服务器某个目录中下载文件，可以指定文件名匹配的规则。
    // main()的参数：日志文件名 ftp服务器端的ip和端口 ftp服务器的传输模式 ftp用户名 ftp密码 服务端的目录名 文件名匹配规则
    
    // 设置信号，在shell状态下可用"kill + 进程号" 正常终止些进程。
    // 但请不要用 "kill -9 +进程号" 强行终止。
    // closeioandsignal(true);    // 关闭0、1、2和忽略全部的信号，在调试阶段，这行代码可以不启用。
    signal(SIGINT,EXIT); signal(SIGTERM,EXIT);

    return 0;
}

void EXIT(int sig)
{
    printf("程序退出, sig=%d\n\n", sig);
    exit(0);
}