#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>

int main(int argc,char *argv[])
{
    if (argc<3)
    {
        printf("Using:./proctl timetlv program argv ...\n");
        printf("Example:/project/tools/bin/proctl 10 /usr/bin/tar zcvf /tmp/tmp.tgz /usr/include\n");
        printf("Example:/project/tools/bin/proctl 60 /project/idc/bin/crtsurfdata /project/idc/ini/stcode.ini /tmp/idc/surfdata /log/idc/crtsurfdata.log csv,xml,json\n");

        printf("本程序是服务程序的调度程序，周期性启动服务程序或shell脚本。\n");
        printf("timetv1 运行周期，单位：秒。\n");
        printf("被调度的程序运行结束后，在timetv1秒后会被proctl重新启动。\n");
        printf("如果被调度的程序是周期性的任务，timetv1设置为运行周期。\n");
        printf("如果被调度的程序是常驻内存的任务，timetv1设置小于5秒。\n");
        printf("program 被调度的程序名，必须使用全路径。\n");
        printf("... 被调度的程序的参数。\n");
        printf("注意，本程序不会被kill杀死，但可以用kill -9强行杀死。\n\n\n");

        return -1;
    }
    // 关闭信号和I/O，本程序不希望被打扰。
    // 注意：1）为了防调度程序被误杀，不处理退出信号；2）如果关闭了I/O，将影响被调度的程序（也会关闭I/O）。
    for (int i=0;i<64;i++)
    {
        signal(i,SIG_IGN); close(i);
    }
    // 生成子进程，父进程退出，让程序运行在后台，由系统1号进程托管。调度程序不受shell的控制
    if (fork()!=0) exit(0);
    // 把子进程退出的信号SIGCHLD信号恢复为默认行为，让父进程可以wait子进程退出的状态。
    signal(SIGCHLD,SIG_DFL);
    // 定义一个和argv一样大的指针数组，存放被调度程序名及其参数。
    char *pargv[argc];
    for(int i=2;i<argc;i++)
        pargv[i-2] = argv[i];

    pargv[argc-2] = NULL; // 空表示参数已结束。
    while (true)
{
    if (fork()==0)
    {
        // 子进程运行被调度的程序。
        execv(argv[2],pargv);
        exit(0); // 如果被调度的程序运行失败，才会执行这行代码。
    }
    else
    {
        // 父进程等待子进程终止（被调度的程序运行结束）。
        int status;
        wait(&status); //wait函数会阻塞，直到被调度的程序终止，传null表示不关心状态
        sleep(atoi(argv[1])); // 休眠timetvl秒，然后回到循环。
    }
}
}