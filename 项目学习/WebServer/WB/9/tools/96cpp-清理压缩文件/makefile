# 开发框架头文件路径。
PUBINCL = -I/project/public

# 开发框架cpp文件名，直接和程序的源代码文件一起编译，没有采用链接库，是为了方便调试。
PUBCPP = /project/public/_public.cpp

# 编译选项
CFLAGS = -g
#CFLAGS = -O2

all:procctl checkproc deletefiles

procctl:procctl.cpp
    g++ $(CFLAGS) -o procctl procctl.cpp
    cp -f procctl ../bin/.

checkproc:checkproc.cpp
    g++ $(CFLAGS) -o checkproc checkproc.cpp $(PUBINCL) $(PUBCPP)
    cp -f checkproc ../bin/.

deletefiles:deletefiles.cpp
    g++ $(CFLAGS) -o deletefiles deletefiles.cpp $(PUBINCL) $(PUBCPP)
    cp -f deletefiles ../bin/.

gzipfiles:gzipfiles.cpp
    g++ $(CFLAGS) -o gzipfiles gzipfiles.cpp $(PUBINCL) $(PUBCPP)
    cp -f gzipfiles ../bin/.

clean:
    rm -rf proctl checkproc deletefiles gzipfiles