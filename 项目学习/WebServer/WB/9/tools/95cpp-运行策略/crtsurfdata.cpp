
#include<_public.h>
using namespace idc;

//省 站号 站名 维度 经度 海拔高度
//北京 11，aa 39.90403 116.40752 10
struct st_stcode {//站点参数的结构体
    char provname[31]; //操作数据库 string 没有优势
    char obtid[11];
    char obtname[31];
    double lat;
    double lon;
    double height;
};
list<struct st_stcode> stlist; // 存放全部站点参数的容器。
bool loadstcode(const char *inifile); // 加载站点参数文件加载到 stlist 容器中。

// 气象站观测数据的结构体
struct st_surfdata
{
    char objtid[11];    // 站点代码。
    char ddatetime[15]; // 数据时间：格式yyyymmddhh24miss，精确到分钟，秒固定填00。
    double t;             // 气温：单位，0.1摄氏度。
    double p;             // 气压：0.1百帕。
    int u;             // 相对湿度，0-100之间的值。
    int wd;            // 风向，0-360之间的值。
    double wf;            // 风速：单位0.1m/s
    double r;             // 降雨量：0.1mm。
    double vis;           // 能见度：0.1米。
};
lsit<struct st_surfdata> datelist; // 存放全部气象站观测数据的容器。
void crtsurfdata(); // 生成随机的气象站观测数据,存放在datalist中

clogfile logfile; // 本程序运行的日志对象。
char strddatetime[15];

bool crtsurffile(const string &outpath,const string &datafmt); // 根据参数datafmt，生成相应的文件。 

void EXIT(int sig) ;//程序退出和信号 2、15 的处理函数。

capctive pactive; // 进程心跳包对象。

int main(int argc,char argv[]) {
    //站点参数文件，生成的测试数据存放的目录，本程序运行的日志， 输出数据文件的格式
    if(argc != 5) {
        // 如果参数非法，给出帮助文档。
        cout << "Using:./crtsurfdata inifile outpath logfile datafmt\n";
        cout << "Examples:/project/tools/bin/procctl 60 project//idc/bin/crtsurfdata /project/idc/ini/stcode.ini /tmp/idc/surfdata /log/idc/crtsurfdata.log csv,xml,json\n"
        cout << "本程序用于生成全国气象站点的观测数据。每分钟执行一次，由调度模块启动\n";
        cout << "inifile 气象站点参数文件名。\n";
        cout << "outpath 气象站点数据文件存放的目录。\n";
        cout << "logfile 本程序运行的日志文件名。\n";
        cout << "datafmt 输出数据文件的格式，支持csv、xml和json两种格式。\n";
        return -1;
    }
    closeioandsignal(true); // 关闭全部的IO和信号，防止多进程或多线程编程产生的意外的信号。
    signal(SIGINT, EXIT); // 忽略SIGPIPE信号。
    signal(SIGTERM, EXIT); // 忽略SIGPIPE信号。

    pactive.addpinfo(10, "crtsurfdata 运行中。"); // 把进程的心跳包加入到共享内存中。

    if(logfile.open(argv[3], "a+") == false) {
        printf("logfile.open(%s) failed.\n", argv[3]);
        return -1;
    }
    if(logfile.open(argv[3]) == false) {
        cout << "logfile.open(" << argv[3] << ") failed.\n";return -1;
    }
    logfile.write("crtsurfdata 开始运行。\n");

    // 在这里编写处理业务的代码。
    // 1）从站点参数文件中加载站点参数，存放于容器中；
    if(loadstcode(argv[1]) == false) {
        EXIT(-1);
    }
    memset(strddatetime,0,sizeof(strddatetime));
    ltime(strddatetime,"yyyymmddhh24miss"); //数据时间
    strncpy(strddatetime + 12,"00",2); //把秒固定填 00
    // 2）根据站点参数，生成站点观测数据（随机数）；
    crtsurfdata();
    // 3）把站点观测数据保存到文件中。
    if(strstr(argv[4],"csv") != 0) {
        if(crtsurffile(argv[2],"csv") != 0) {
            crtsurffile(argv[2],"csv");
        }
        if(strstr(argv[4],"xml")!= 0) {
            if(crtsurffile(argv[2],"xml")!= 0) {
                crtsurffile(argv[2],"xml");
            }
        }
        if(strstr(argv[4],"json")!= 0) {
            if(crtsurffile(argv[2],"json")!= 0) {
                crtsurffile(argv[2],"json");
            }
        }
    }
    logfile.write("crtsurfdata 结束运行。\n");
    return 0;
}

void EXIT(int sig) {
    logfile.write("程序退出，sig = %d\n", sig);
    exit(0);
}
// 从站点参数文件中加载站点参数，存放于容器中。
bool loadstcode(const char *inifile) {
    cfile ifile;    // 读取文件的对象。
    if (ifile.open(infile)==false)
        logfile.write("ifile.open(%s) failed.\n",infile.c_str()); return false;
    string strbuffer;    // 存放从文件中读取的每一行。
    ifile.readline(strbuffer);    // 读取站点参数文件的第一行，它是标题，扔掉。
    ccmdstr cmdstr; //用于拆分从文件中读取的行
    st_stcode stcode;
    while(ifile.readline(strbuffer)) {
        logfile.write("strbuffer=%s\n",strbuffer.c_str());
        //拆分一行，例如：北京,11,aa,39.90403,116.40752,10
        cmdstr.splittocmd(strbuffer,",");
        cmdstr.getvalue(0,stcode.provname,30); //省
        cmdstr.getvalue(1,stcode.obtid,10); //站号
        cmdstr.getvalue(2,stcode.obtname,30); //站名
        cmdstr.getvalue(3,&stcode.lat); //维度
        cmdstr.getvalue(4,&stcode.lon); //经度
        cmdstr.getvalue(5,&stcode.height); //海拔高度
        stlist.push_back(stcode); //把站点参数放入容器中。
    }
    //不需要手动关闭，cifile类的析构函数会关闭文件
    // for(auto L : stlist) {
    //     logfile.write("stcode.provname=%s,stcode.obtid=%s,stcode.obtname=%s,
    //         stcode.lat=%f,stcode.lon=%f,stcode.height=%f\n",
    //     L.provname,L.obtid,L.obtname,L.lat,L.lon,L.height);
    // }
}

void crtsurfdata() {
    srand(time(0));
    //遍历站点容器，生成随机的站点观测数据
    st_surfdata stsurfdata;
    for(auto L : stlist) {
        memset(&stsurfdata,0,sizeof(struct st_surfdata));
        strcpy(stsurfdata.obtid,L.obtid); //站点代码
        strcpy(stsurfdata.ddatetime,strddatetime); //数据时间
        stsurfdata.t = rand() % 350; //气温：单位，0.1摄氏度。
        stsurfdata.p = rand() % 265 + 10000; //气压：0.1百帕。
        stsurfdata.u = rand() % 101; //相对湿度，0-100之间的值。
        stsurfdata.wd = rand() % 360; //风向，0-360之间的值。
        stsurfdata.wf = rand() % 150; //风速：单位0.1m/s
        stsurfdata.r = rand() % 16; //降雨量：0.1mm。
        stsurfdata.vis = rand() % 5001 + 10000; //能见度：0.1米。

        datelist.push_back(stsurfdata); //把站点观测数据放入容器中。
    }
}

bool crtsurffile(const string &outpath,const string &datafmt) {
    // 拼接生成数据的文件名，例如：/tmp/idc/surfdata/2020010112/110000_(pid).csv
    string strfilename = outpath + "/" + "SURF_ZH" + strddatetime + "_" + to_string(getpid()) + "." + datafmt;

    cofile ofile; // 写文件的对象。
    if(ofile.open(strfilename) == false) {
        logfile.write("ofile.open(%s) failed.\n",strfilename.c_str()); return false;
    }

    //把 datalist 容器中的观测数据写入文件中
    if(datafmt == "csv") {
        // 写入标题行
        ofile.writeline("站点代码,数据时间,气温,气压,相对湿度,风向,风速,降雨量,能见度\n");
    }
    if(datafmt == "xml") {
        // 写入标题行
        ofile.writeline("<data>\n");
    }
    if(datafmt == "json") {
        // 写入标题行
        ofile.writeline("[\n");
    }
    for(auto L : datelist) {
        if(datafmt == "csv") {
            ofile.writeline("%s,%s,%.1f,%.1f,%d,%d,%.1f,%.1f,%.1f\n",
                L.obtid,L.ddatetime,L.t/10.0,L.p/10.0,L.u,L.wd,L.wf/10,0,L.r/10.0,L.vis/10.0);
        }
        if(datafmt == "xml") {
            ofile.writeline("<row>\n");
            ofile.writeline("<obtid>%s</obtid>\n",L.obtid);
            ofile.writeline("<ddatetime>%s</ddatetime>\n",L.ddatetime); 
            ofile.writeline("<t>%.1f</t>\n",L.t/10.0);
            ofile.writeline("<p>%.1f</p>\n",L.p/10.0);
            ofile.writeline("<u>%d</u>\n",L.u);
            ofile.writeline("<wd>%d</wd>\n",L.wd);
            ofile.writeline("<wf>%.1f</wf>\n",L.wf/10.0);
            ofile.writeline("<r>%.1f</r>\n",L.r/10.0);
            ofile.writeline("<vis>%.1f</vis>\n",L.vis/10.0);
            ofile.writeline("</row>\n");
        }
        if(datafmt == "json") {
            ofile.writeline("{\n");
            ofile.writeline("\"obtid\":\"%s\",\n",L.obtid);
            ofile.writeline("\"ddatetime\":\"%s\",\n",L.ddatetime);
            ofile.writeline("\"t\":%.1f,\n",L.t/10.0);
            ofile.writeline("\"p\":%.1f,\n",L.p/10.0);
            ofile.writeline("\"u\":%d,\n",L.u);
            ofile.writeline("\"wd\":%d,\n",L.wd);
            ofile.writeline("\"wf\":%.1f,\n",L.wf/10.0);
            ofile.writeline("\"r\":%.1f,\n",L.r/10.0);
            ofile.writeline("\"vis\":%.1f\n",L.vis/10.0);
            //注意 json 文件的最后一行不要有逗号。
            static int i = 0;
            if(i++ < datelist.size() - 1) ofile.writeline("},\n");
            else ofile.writeline("}\n");
        }
    }
    if(datafmt == "xml") {
        // 生成 xml 格式的数据文件。
        ofile.writeline("</data>\n");
    }  
    if(datafmt == "json") {
        // 生成 json 格式的数据文件。
        ofile.writeline("]\n");
    }
    ofile.closeandrename(); // 关闭文件并重命名。
    logfile.write("生成数据文件 %s成功，数据时间 %s ,记录数 %d.\n",strfilename.c_str(),strddatetime,datelist.size());
}