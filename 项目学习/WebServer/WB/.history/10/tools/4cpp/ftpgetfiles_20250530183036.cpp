#include "_public.h"
#define _public.h
#include <signal.h>
#include<bits/stdc++.h>
#include <ftplib.h>
#include <unistd.h> // for close() and fork()   
// using namespace idc;

// 程序退出和信号2、15的处理函数。
void EXIT(int sig);

cftpclient ftp; // 定义ftp对象。

int main(int argc, char *argv[])
{
    // 设置信号，在shell状态下可用 "kill + 进程号" 正常终止些进程。
    // 但请不要用 "kill -9 +进程号" 强行终止。
    // closeioandsignal(true);    // 关闭0、1、2和忽略全部的信号，在调试阶段，这行代码可以不启用。
    signal(SIGINT, EXIT);
    signal(SIGTERM, EXIT);

    return 0;
}

void EXIT(int sig)
{
    printf("程序退出, sig=%d\n\n", sig);
    exit(0);
}