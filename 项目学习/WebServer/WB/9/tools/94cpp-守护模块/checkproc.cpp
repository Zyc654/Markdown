// 守护程序：检查共享内存中进程的心跳，如果超时，则终止进程。
#include "_public.h"
using namespace idc;

int main(int argc, char *argv[])
{
    // 程序的使用帮助。
    if (argc != 2)
    {
        printf("\n");
        printf("Usage: ./checkproc logfilename\n");
        printf("Example: /project/tools/bin/proctl 10 /project/tools/bin/checkproc /tmp/log/checkproc.log\n\n");
        printf("本程序用于检查后台服务程序是否超时，如果已超时，就终止它。\n");
        printf("注意：\n");
        printf("  1. 本程序由procctl启动，运行周期建议为10秒。\n");
        printf("  2. 为了避免被普通用户误杀，本程序应该用root用户启动。\n");
        printf("  3. 如果要停止本程序，只能用killall -9 终止。\n\n");
        return 0;
    }

    closeioandsigpipe(true);// 关闭所有的I/O和信号。不处理程序的退出信号

    // 打开日志文件。
    clogfile logfile;// 日志文件对象
    if(logfile.open(argv[1]) == false) {
        printf("logfile.open(%s) failed.\n",argv[1]);return -1;
    }
    // 创建/获取共享内存，键值为SHMKEYP，大小为MAXNUMP个st_procinfo结构体的大小。
    int shmid = 0;
    if((shmid = shmget((key_t)SHMKEYP,MAXNUMP*sizeof(struct st_procinfo),0644|IPC_CREAT)) == -1) {
        logfile.write("创建/获取共享内存(%x)失败\n",SHMKEYP);return -1;
    }
    // 将共享内存连接到当前进程的地址空间。
    struct st_procinfo *shm = (struct st_procinfo *)shmat(shmid,0,0);
    // 遍历共享内存中全部的记录，如果进程已超时，终止它。
    for(int i=0;i<MAXNUMP;i++) {
        // 如果pid=0，表示该记录为空，跳过。
        if(shm[i].pid == 0) continue;
        //如果 pid !=0 且 atime+timeout<当前时间，表示进程已超时，终止它。
        if(shm[i].atime + shm[i].timeout < time(0)) {
            logfile.write("进程(%d,%s)超时，终止它。\n",shm[i].pid,shm[i].pname);
        }
        // 显示进程信息，用于调试。
        logfile.write("进程(%d,%s)的心跳时间是：%s\n",shm[i].pid,shm[i].pname,ctime(&shm[i].atime));
        
        //如果进程不存在了，共享内存中残留的是心跳信息
        //向进程发送信号0，判断是否存在，不存在，从共享内存中删除记录
        int iret = kill(shm[i].pid,0);
        if(iret == -1) {
            logfile.write("进程(%d,%s)不存在，从共享内存中删除它。\n",shm[i].pid,shm[i].pname);
            memset(&shm[i],0,sizeof(struct st_procinfo));
            continue;
        }
       
        //判断进程的心跳是否超时，
        time_t now = time(0);
        if(shm[i].atime + shm[i].timeout < now) {
            struct st_procinfo tmp = shm[i]; // 临时变量
            if(tmp.pid == 0) continue; // 如果pid=0，表示该记录为空，跳过。


            logfile.write("进程(%d,%s)超时，终止它。\n",tmp.pid,tmp.pname);

            kill(tmp.pid,SIGKILL);//尝试正常终止已超时的进程
            
            //如果直接正常杀死了，此时 后续判断的都是 pid =0，会误删除
            for(int j=0;j<5;j++) {
                sleep(1);//等待1秒，避免进程还没退出，就被kill -9杀死
                iret = kill(tmp.pid,0);
                if(iret == -1) break;
            }

            if(iret == -1) {
                logfile.write("进程(%d,%s)不存在，从共享内存中删除它。\n",tmp.pid,tmp.pname);
            }else {
                kill(tmp.pid,9);//尝试强制终止已超时的进程
                logfile.write("进程(%d,%s)正常终止失败，发送信号9。\n",tmp.pid,tmp.pname);
               
                //从共享内存中删除已经超时的心跳记录
                memset(&shm[i],0,sizeof(struct st_procinfo));
            }
        }
    }
    // 把共享内存从当前进程中分离。
    shmdt(shm);
    return 0;
}