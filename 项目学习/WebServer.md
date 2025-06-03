<center> <font size = 10> 牛客 - WebServe</font> </center>

## 初步配置

1. 更换镜像源

2. 更换`ipv4` 默认地址，掩码和路由及`DNS` 分别是`ip地址 ，255.255.255.0 路由地址 8.8.8.8,8.8.4.8,路由` 
3. 安装`VM-tools`

```cpp
sudo apt update
sudo apt upgrade
sudo apt open-vm-tools-desktop -y
#vmware-toolbox-cmd -v 验证安装成功
reroot
mkdir // 创建文件夹
touch // 创建文件
```



## 搭配远程连接环境

` VScode` 下载 `ssh develop` 插件 ，输入`名称 主机（IP地址）和用户名即可` 

实现免密登录 ：

1. `windows`下`cmd` 窗口运行 `ssh-keygen -r rsa` 生成文件在 `/user/.ssh`中 找到公钥`*.pub` ，在`Ubuntu` 系统中进行`mkdir .ssh` `ssh-key -r rsa`  `cd .ssh/` `vim authorized_keys` 将内容替换为`pub`的内容即可 
2. 可能需要下载`vim` `sudo apt install vim`

## 入门

### `gcc`

![编译过程](../Images/Webserver-gcc.png)

- `gcc` 和 `g++` 的区别 : `gcc`不能自动和`c++`程序使用的库链接，但在编译阶段 `g++ 会调用 gcc` ，两者等价。

![常用参数](../Images/gcc常用参数1.png)

![常用参数](../Images/gcc常用参数2.png)

### 静态库

- Linux : libxxx.a
- windows: libxxx.dll

<font size = 3> 下面所有的 `*` 都是指自定义文件名</font>

<font size = 3> include 文件夹中放头文件，lib 文件夹中放库文件，main.c ，src中放的是源代码文件</font>	

```cpp
cp -r clac/ library/ ../dir*//将calc lib 复制到上一级dirname文件中， -r 表示递归拷贝
//制作静态库
gcc -c *.c *.c ... //表示编译汇编但是不连接 生成 .o 文件
ar rcs lib*.a *.o *.o... // --> lib*.a ，此处 * 名词为库的名称 rcs 分别指插入创建索引
gcc main.c -o * -I ./include/ -l *.a -L ./lib//-o后的*可不写默认main.o
```

### 动态库

#### 命名规则

- Linux : libxxx.so (lib 前缀固定，xxx：库的名字，.so 后缀，libxxx.so 库文件的名字)
- windows : libxxx.dll

#### 动态库的制作

- gcc 得到 .o 文件，得到和位置无关的代码 

``` c
gcc -c -fpic/-fPIC a.c b.c// -fpic 一定不能忘
```

- gcc 得到动态库

```c
gcc -shared a.o b.o -o lib*.so
```

#### 具体步骤

<font size =3>calc 里面存的是 .c .h 之类的源代码，library 内存的是要使用的库文件，动态库和头文件都发送出去才能使用</font>	

```c
cd calc
gcc -c -fpic *.c *.c *.c ...//主函数不需要生成.o文件，main是测试用的
gcc -shared *.o *.o... -o lib*.so // 如果真的写*.o 表示所有后缀为.o 的文件一起
cd ..
cp ../calc/lib*.so ./lib/ //具体路径文件名cala都根据实际改写
gcc main.c -o main -I ./include -L lib/ -l * // 指定头文件和库的名字 
./main // 运行时目前动态库不会被加入打内存中
```

<font size=3>目前为止会找不到文件，没有正确的使用动态库</font>

#### 区别

- 静态库：gcc 进行链接时，会将静态库中代码打包到可执行程序中
- 动态库：gcc 进行链接时，代码不会被打包到可执行程序中 `ldd *(可执行程序名此为main)` ：检查动态库依赖关系，需要系统的**动态载入器**(ld-linux.so)获得绝对路径，先后搜索 elf 文件的`DT_RPATH段 -> 环境变量 LD_LIBRARY_PATH -> /etc/ld.so.cache文件列表 -> /lib/ , /usr/lib` 目录找到库文件后载入内存中。

#### 配置环境变量

##### 配置LD_LIBRARY_PATH

- 终端级别（一次性）

```c
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:绝对路径
//获取绝对路径 ，首先进入到对于 lib 的文件中，即不停的cd 接着使用 pwd 命令即可获取
//echo $LD_LIBRARY_PATH  即可查看后续拼接的变量
./main //此时可正确运行 若在终端中配置只是临时的，仅本次终端可用
```

-  用户级别的配置

```c
//home 文件下 ll 找到 .bashrc 文件
vim .bashrc//shift + g 跳转到最后一行后 o 开始配置
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:绝对路径 // wq/shift+zz保存并退出
. .bashrc / source .bashrc // 更新
```

- 系统级别

```c
sudo vim /etc/profile//shift + g 跳转到最后一行后 o 开始配置
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:绝对路径 // wq/shift+zz保存并退出
. .bashrc / source .bashrc // 更新
```

##### 配置 /etc/ld.so.cache

```c
sudo vim /etc/ld.so.conf
//将绝对路径加进去即可
sudo ldconfig // 更新
```

<font size =3>不建议将动态库文件放入/lib/, /usr/lib 中，为了防止误操作导致的重名问题</font>

#### 静态库和动态库的区别

![image-20250417145618257](./../Images/image-20250417145618257.png)

<table><font size=3>
  一般情况下，库比较小时使用静态库，比较大时使用动态库<br/>
  静态库：<br/>
  优点: 1. 静态库被打包到应用程序中加载速度快  ；2. 发布程序无需提供静态库，移植方便<br/>
  缺点: 1. 消耗系统资源，浪费内存  ；2. 更新、部署、发布麻烦<br/>
  <br/> 
  动态库：<br/>
  优点: 1. 可以实现进程间资源共享（共享库） ；2. 更新、部署、发布简单 ； 3. 可以控制何时加载动态库<br/>
  缺点: 1. 加载速度比静态库慢 ； 2. 发布程序时需要提供依赖的动态库<br/>
  </font></table>


### Makefile  

<font size=3>Makefile 文件定义了一系列的规则来指定哪些文件需要先编译，哪些文件需要后编译，那些文件需要重新编译，甚至于进行更复杂的功能操作，像一个 shell 脚本一样  ---> 自动化编译，只需要 make 命令</font>

#### Makefile 规则

<table><font size=3>
  一个Makefile 文件中可以有一个或多个规则<br/>
    <font color = "red"> <pre>目标 . . . :  依赖  . . . </pre>
<pre>	命令（shell 命令）</font></pre> <!--pre会保留空格等格式-->
 上述水平制表符一定要有，格式一定要对住，否则无法识别<br/>
  <b>目标</b>：最终要生成的文件（伪目标除外)<br>
  <b>依赖</b>：生成目标所需要的文件或是目标<br>
  <b>命令</b>：通过执行命令对依赖操作生成目标，命令前必须 tab 缩进<br>
  其他规则一般都是为第一条规则服务的


```cpp
vim Makefile
*:a.c b.c c.c d.c //目标:依赖
  gcc a.c b.c c.c d.c -o * // esc :wq  命令
sudo apt install make 
make
```

<table><font size = 3>
  命令执行之前是需要判断规则中的依赖是否存在，若不存在依次向下寻找，直至第一条依赖完全存在（满足）<br><br>
  <b>检测更新</b>，在执行规则中的命令时，会比较目标和依赖文件的时间，如果依赖的时间比目标时间晚，需要重新生成目标，反之则目标不需要更新，命令不需要执行
  </font></table>


```cpp
*:a.o b.o c.o d.o
  gcc a.o b.o c.o d.o -o *
a.o:a.c
	gcc -c a.c -o a.o
...
```

#### 变量

<pre><table><font size =3>
  自定义变量： 变量名=变量值 var=hello<br>
  预定义变量：
  1：AR：归档维护程序的名称，默认值为 ar	2：CC：C 编译器的名称，默认为 cc
  3：CXX：c++编译器的名称，默认值为g++	4：$@：目标的完整名称	
  5：$<：第一个依赖文件的名称		6：$^：所有的依赖文件
  (456是自动变量只能在规则的命令中使用)<br>
  获取变量的值：$(变量名)
</font></table></pre>


```c
//定义变量 终端中是 # 注释
xxx=a.o b.o c.o...
xxx1=xxx2
$(xxx1):$(xxx)
  $(CC) $(xxx) -o $(xxx1)
//% 通配符
xxx=a.o b.o c.o...
xxx1=*
%.c:%.o //两个%匹配的是同一个字符串
	$(CC) -c $< -o $@
```

#### 函数

<pre><table><font size =3>  
$(wildcard PATTERN...)
    功能：获取指定目录下指定类型的文件列表
    参数：指的是某个或多个目录下对于的某种类型的文件，多个用空格隔开<br>
$(wildcard *.c ./sub/*.c)
    返回值格式：a.c b.c c.c d.c e.c f.c //得到的若干文件的文件列表，空格隔开<br>
$(patsubst pattern,repalcement,text)
    功能：查找 text 中的单词，以空格回车或 tab 分割，是否符合模式 pattern ，如果匹配则使用replacement 替换  <br>
$(patsubst %.c,%.o,x.c bar.c)
    返回值格式：x.o bar.o    ---> 替换后的字符串


```c
src=$(wildcard ./*.c)
objs=$(patsubst %.c,%.o,$(src))
target=app
$(target):$(objs)
	$(CC) $(objs) -o $(target)
%.c:%.o
	$(CC) -c $< -o $@、
.PHONY:clean//如果不加clean只能一次 因为没有依赖会认为依赖时间早于目标，不会在执行，此表示伪目标，不会生成文件也即不会对比
clean:
	rm $(objs) -f //删除 .o 文件 clean目标和第一条无关系 make是不会执行的，make clean执行touch clean 
make clean
```


### GDB

<pre><font size=3> GDB 是由GUN软件系统社区提供的调试工具
  1.启动程序，可以按照自定义的要求随心所欲的运行程序
  2.可让被调试的程序在所指定的调置的断点处停住（断点可以是条件表达式）
  3.当程序被停住时，可以检查此时程序中所发生的事
  4.可以改变程序，将一个BUG产生的影响修正从而测试其他BUG
  通常需要关闭优化选项 -o 打开调试选项 -g 另外 -wall 在尽量不影响程序行为的情况下选项打开所有警告
  gcc -g -wall xxx.c -o xxx  #-g 是必不可少的 -o -wall可省
  注意调试时 源文件要和可执行文件在一个文件夹中否则会找不到文件
</font></pre>
```cpp	
//启动和退出
gdb 可执行程序
quit/q
//给程序设置参数/获取设置参数
set args 10 20
show args
//查看当前文件代码
list/l (从默认位置显示)
list/l 行号  (从指定的行显示)
list/l 函数名 (从指定的函数显示)
//查看非当前文件代码
list/l 文件名:行号
list/l 文件名:函数名
//设置显示的行数
show list/listsize
show list/listsize 行数
// 演示
gcc test.c -o test -g
gdb test // 进入 gdb 调试
set args 10 20 //例子中主函数有两个参数待传入
g++ a.cpp b.cpp c.cpp -o main -g
gdb main
list a.cpp:1
list a.cpp:xxx //函数名

// 断点操作
//设置断点
b/break 行号/函数名/文件名:行号/文件名:函数
//查看断点
i/info b/break
//删除断点
d/del/delete 断点编号
//设置断点无效/生效
dis/disable 断点编号
ena/enable 断点编号
//设置断点条件（一般用在循环的位置） 10 表示循环内的行号
b/break 10 if i==5
  
//调试命令
//运行GDB程序
start (程序停在第一行)
run (遇到断点才停)
//继续运行，到下一个断点停
c/continue
//向下执行一行代码(不会进入函数体)
n/next // 会跳过函数进入后续的代码
//变量操作
p/print 变量名 // 输出格式是 $num = x
ptype 变量名
//向下单步调试 (遇到函数进入函数体)
s/step 
finish (跳出函数体)
//自动变量操作
display xxx (自动打印指定变量的值)
i/info display
ubdisplay 编号
//else
set var 变量名=变量值
until (跳出循环)  //在断点删除或者不可用时才能跳出或者for循环执行完毕
```

### 文件IO

<pre><font size=3>标准 C 库的 IO 函数比 linux 的 IO 函数要高，linux 更底层
fopen 返回值 FILE *p 包括 :
  1.文件描述符(整型值，定位文件,索引到对应的磁盘位置)  
  2.文件读写指针位置(读写文件过程中指针的实际位置)  
  3.I/O缓冲区(内存地址，通过寻址找到对应的内存块)
数据从内存刷新到磁盘时机 : 
  1.刷新缓冲区：fflush
  2.缓冲区已满 (默认8192b(8K))
  3.正常关闭文件  a. flose  b. return(main 函数)  c. exit(main函数)</pre>
### 虚拟地址空间

<pre><font size=3>  虚拟地址空间是一段连续的内存地址，起始位置为0，它可以用来加载程序数据，数据可能被加载到物理内存上，空间不够就加载到虚拟内存中。虚拟地址空间是被虚拟出来的，不是物理内存的0地址。不同进程使用的虚拟地址是隔离的，一个进程中的代码无法更改另一个进程或操作系统正在使用的物理内存。
  作用：
  当进程 非法的 访问或者映射， OS都会识别到并且终止该进程。
  任意位置处，都可以用虚拟地址+页表解耦用来提高其他进程之间的独立性
  地址空间中有写时拷贝，可以延迟分配，用来提高整机效率 
  可以让进程以统一的独占整个内存（实际上不是，只是以这种视角）的视角，映射到任意不同的物理内存的位置，来完成进程独立性的实现
</pre>



### 文件描述符

<pre><font size  =3>
  PCB 中有一个文件描述符表(一个数组)，前三个是标准输入，标准输出，标准错误，三者指向当前终端
  查看说明文档 man x name (x 表示对应的数字 在第几张,2是linux函数，3是标准c库函数)



### open 打开文件

<pre><font size = 3>
  打开一个新的文件
  int open(const char *pathname,int flag);
  创建一个新的文件
  int open(const char *pathname,int flags,mode_t mode);
  参数：pathname：打开/创建的文件路径  
       flag：对文件的操作权限还有其他的设置，O_RDONLY , O_WRONLY , O_RDWR 三个是互斥的 ，可读 可写 可读可写
       flags：除了上述三个必选项(要求同上)， 可选项：O_APPEND(追加)，O_CREAT(文件不存在创建新的) 例：O_RDWR | O_CREAT ， 是一个int类型，占 32 位
       mode：八进制的数，表示用户对创建出的新的文件的操作权限
       最终的权限是：mode & ~umask (umask作用是抹去某些权限)
       返回值：返回一个新的文件描述符，如果调用失败，返回-1（int）
       errno：属于Linux系统库函数，一个全局变量，记录的是最近的错误号
       perror(const char *s) ：s 参数：用户描述，比如hello，最终输出hello.xxx(实际错误描述)
       close(xxx); xxx 是指文件描述符，上述的返回值
       rwx：分别指读写可执行权限
</pre></font>


### read wirte 函数

<pre><font size =3>
  ssize_t read(int fd,void *buf,size_t count);
  参数
    fd：文件描述符，open得到的，通过这个文件描述符操作某个文件
    buf：缓冲区，需要读取数据存放的地方，数组的地址(传出参数)
    count：指定的数组的大小
    返回值： 成功： >0 返回实际的读取到的字节数  =0 文件已经读取完了
           失败：-1，并设置 errno
  ssize_t write(int fd,const void *buf,size_t count);
    buf：要往磁盘写入的数据，数组
    count：要写的数据的实际的大小
    返回值：成功： 实际写入的字节数    失败：返回-1，并设置 errno
</pre>


```cpp
//通过 open 打开 english.txt 文件
int srcfd = open("english.txt",O_RDONLY);//写的路径一定要全，这里是当前目录下的相对路径
if(srcfd == -1){
  	prror("open");
		return -1;
}
//创建一个新的文件（拷贝文件）
int destfd = open("cpy.txt",O_WRONLY | O_CREAT,0664);
if(destfd == -1){
		prror("open");
  	return -1;
}
//频繁的读写操作
char buf[1024] = {0};
int len = 0;
while((len = read(srcfd,buf,sizeof(buf))) > 0){
  	write(destfd,buf,len);
}
//关闭文件
close(destfd);
close(srcfd);
```

### lseek 函数

<pre><font size =3>
  标准C库的函数
  int fseek(FILE *stream,long offset,int whence);<br>
  Linux 系统函数
  off_t lseek(int fd,off_t offset,int whence);
  参数：fd：文件描述符，通过open得到的，可以通过fd操作该文件
       offset：偏移量
       whence：SEEK_SET：设置文件指针的偏移量  ； SEEK_CUR: 设置偏移量，从当前位置加上第二个参数offset的值  ； SEEK_END：设置偏移量，文件的的大小（文件结尾） + 第二个参数offset的值
       返回值：返回文件指针的位置
       作用： 
       1.移动文件指针到头文件  lseek(fd,0,SEEK_SET);
       2.获取当前文件指针的位置  lseek(fd,0,SEEK_CUR);
       3.获取文件长度 lseek(fd,0,SEEK_END);
       4.拓展文件的长度，当前文件10b，110b，增加了100个字节 lseek(fd,100,SEEK_END);注意：需要写一次数据


```cpp
int fd = open("hello.txt",O_RDWR);
if(fd == -1){
  	perror("open");
  	return -1;
}
//扩展文件的长度
int ret = lseek(fd,100,SEEK_END);
if(ret == -1){
  	prror("lseek");
  	return -1;
}
//写入空数据
write(fd," ",1);//不写可能回扩展失败
//关闭文件
close(fd);
```

### stat

 <pre><font size=3>
   int stat(const char *pathname,struct stat *statbuf);
   作用：获取一个文件相关的一些信息 ---> stat a.txt
   pathname：操作的文件的路径
   statbuf：结构体变量，传出参数，用于保存获取到的文件的信息
   返回值：0 表示成功。失败：-1 并设置 errno<br>
   int lstat(const char *pathname,struct stat *statbuf);
   符号链接时，统计链接本身，而不是链接所引用文件。所以，stat和lstat的区别是：stat遵循链接，但lstat不是 -->  想要识别符号链接文件的时候，只能使用lstat；当我们关注的是符号链接指向的文件时，就应该使用stat。
 </pre>


![image-20250420212459136](./../Images/image-20250420212459136.png)

![image-20250420210914865](./../Images/image-20250420210914865.png)

```cpp
struct stat statbuf;
int ret = stat("a.txt",&statbuf);
if(ret == -1){
  perror("stat");
  return -1;
}
print("size: %ld\n",statbuf.st_size);
```

### ls -l 显示文件信息

```cpp
int main(int argc,char *argv[])//gcc ls-l.c -o ls    ./ls a.txt
{
  //判断输入的参数是否正确，给出提示
  if(argc < 2){
    printf("%s filename\n",argv[0]);
    exit(0);
  }
  //通过stat函数获取用户传入的文件信息
  struct stat st;
  int ret = stat(argv[1],&st);
  if(ret == -1){
    perror("stat");
    return -1;
  }
  //获取文件类型和文件权限
  //-rw-rw-r-- 1 nowcoder nowcoder 12 12月  3 15:48 a.txt
  char perms[11] = {0};//用于保存文件类型和文件权限的字符串
  switch(st.st_mode & S_IFMT){
    case S_IFLINK:
      perms[0] = 'l';//软连接标识是 l
      break;
    case S_IFDIR:
      perms[0] = 'd';//目录
      break;
    case S_IFREG:
      perms[0] = '-';//普通文件
      break;
    case S_IFBLLK:
      perms[0] = 'b';//块文件
      break;
    case S_IFCHR:
      perms[0] = 'c';//字符文件
      break;
    case S_IFSOCK:
      perms[0] = 's';//套接字
      break;
    case S_IFFIFO:
      perms[0] = 'p';//管道
      break;
    default:
      perms[0] ='?';//未知文件
  }
  //访问权限 分为三部分
  //文件所有者
  perms[1] = (st.st_mode & S_IRUSR) ? 'r' : '-';//是否有读权限
  perms[2] = (st.st_mode & S_IWUSR) ? 'w' : '-';//是否有写权限
  perms[3] = (st.st_mode & S_IXUSR) ? 'x' : '-';//是否有可执行权限
  //文件所在组
  perms[4] = (st.st_mode & S_IRGRP) ? 'r' : '-';
  perms[5] = (st.st_mode & S_IWGRP) ? 'w' : '-';
  perms[6] = (st.st_mode & S_IXGRP) ? 'x' : '-';
  //其他人
  perms[7] = (st.st_mode & S_IROTH) ? 'r' : '-';
  perms[8] = (st.st_mode & S_IWOTH) ? 'w' : '-';
  perms[9] = (st.st_mode & S_IXOTH) ? 'x' : '-';
  //硬连接数
  int linkNum = st.st_nlink;
  //文件所有者  pwd.h
  char *fileUser = getpwuid(st.st_uid)->pw_name;
  //文件所在组  grp.h
  char *fileGrp = getgrgid(st.st_gid)->gr_name;
  //文件大小
  long int fileSize = st.st_size;
  //获取修改的时间 time.h
  char *time = ctime(&st.st_mtime); // ctime 将秒数转为本地时间
  char mtime[512] = {0};
  strncpy(mtime,time,strlen(time) - 1);//删除time中默认的最后一个换行符
  char buf[1024];
  sprintf(buf,"%s %d %s %s %ld %s %s",perms,linkNum,fileUser,fileGrp,fileSize,mtime,argv[1]);
  printf("%s\n",buf);
}
```

### 文件属性操作函数

<pre><font size=3>
  int access(const char *pathname,int mode);  ---> unistd.h 
  作用：判断某个文件是否存在某个权限，或者判断文件是否存在
  pathname：文件的路径
  mode：R_OK：判断是否存在读权限  ； W_OK：判断是否存在写权限  ；X_OK：是否存在可执行权限  F_OK：文件是否存在
  返回值：成功返回 0 ，失败返回 -1<br>
  int chmod(const char *pathname,mode_t mode);  --->  sys/stat.h
  作用：修改文件的权限
  mode：需要修改的权限值，八进制的数
  返回值：成功返回 0， 失败返回 -1 <br>
  int chown(const char *path,uid_t owner,gid_t group);
  作用：修改所有者所在组<br>
  int truncate(const char *path,off_t length);  --> unistd.h , sys/types.h
  作用：缩减或者扩展文件的尺寸至指定的大小
  length：需要最终文件变成的大小
  返回值：成功返回 0 ，失败返回 -1




```cpp
int ret = access("a.txt"F_OK)；
if(ret == -1){
  prror("access");
}
printf("文件存在\n");
int ret = chomd("a.txt",0775)；
if(ret == -1){
  prror("chomd");
  return -1;
}
int ret = truncate("b.txt",20);//变多塞进空，变少截断
if(ret == -1){
  prror("truncate");
  return -1;
}
```

### 目录操作函数

<pre><font size = 3>
  int mkdir(const char *pathname,mode_t mode);  --> sys/stat.h , sys/types.h
  作用：创建一个目录
  mode：权限，八进制的数，最终的权限是 mode & ~umask & 0777
  返回值：成功返回 0 ，失败返回 -1<br>
  int rmdir(const char *pathname);//只能删除空目录<br>
  int renamne(const char *oldname,const char *newname);  --> stdio.h<br>
  int chdir(const char *path); //修改进程的工作目录  unistd<br>
  char *getcwd(char *buf,size_t size);//获取当前的工作目录 unistd.h
  参数：buf：存储的路径，指向的是一个数组(传出参数)  ； size：指定的数组大小
  返回值：返回的是指向的一块内存，这个数据就是第一个参数


```cpp
int ret = mkdir("aaa",0777);//0 代表八进制
if(ret == -1){
  prror("mkdir");
  return -1;
}
int ret = rename("aaa","bbb");
if(ret == -1){
  ...
}
char buf[128];
getcwd(buf,sizeof(buf));
int ret = chdir("/home/nowcode/Linux/lesson13");
if(ret == -1){...}
//创建一个新文件
int fd = open("chdir.txt",O_CREAT | O_RDWR,0664);
if(fd == -1){
  ...;
}
close(fd);
char buf1[128];
getcwd(buf,sizeof(buf1));
```

### 目录遍历函数

<pre><font size = 3>
  DIR *opendir(const char *name);  ---> sys/types.h  , dirnet.h
  打开一个目录
  参数：name：需要打开的目录的名称
  返回值：DIR *类型，理解为目录流，错误返回 NULL<br>
  struct dirent *readdir(DIR *dirp);  --> dirent.h   调用一次指针会后移一次
  读取目录中的数据
  dirp 是 opendir 返回的结果
  返回值：结构体，代表读取到的文件的信息，读取到了末尾或者失败了 返回 NULL<br>
  int closedir(DIR *dirp);//关闭目录  dirent.h
</pre>


![image-20250421165024183](./../Images/image-20250421165024183.png)

```cpp
//用于获取目录下所有普通文件的个数
int getFileNum(cosnt char *path){
  //打开目录
  DIR * dir = opendir(path);
  if(DIR == NULL){
    prror("opendir");
    exit(0);
  }
  struct dirent *ptr;
  int tot = 0;
  while((ptr = readdir(dir)) != NULL){
    char *dname = ptr->dname;//获取文件名
    if(strcmp(dname,".") == 0 || strcmp(dname,"..") == 0)
      continue;
    //判断是普通文件还是目录
    if(ptr->type == DT_DIR){
      char newpath[256];
      sprintf(newpath,"%s/%s",path,dname);
      tot += getFileNum(newpath);
    }
    if(ptr->type == DT_REG){
      tot ++ ;
    }
  }
  closedir(dir);
  return tot;
}                              // linux中的out相当于windows中的exe 可执行文件
int main(int argc,char *argv[])// ./a.out xxx    获取某个目录下所有普通文件的个数
{
  if(argc < 2){
    printf("%s path\n",agrv[0]);
    return -1;
  }
  int num = getFileNum(argv[1]);
  printf("%d\n",num);
}
```

### dup、dup2 函数

<pre><font size = 3>
  int dup(int oldfd);  ---> unistd.h
  复制文件描述符，指向同一个文件，从空闲的文件描述符表中找到一个最小的，作为新的拷贝的文件描述符
  int dup2(int oldfd,int newfd);  ---> unistd.h
  重定向文件描述符
  oldfd 指向 a.txt newfd 指向 b.txt 调用函数成功后 newfd close b 指向 a  --> oldfd 必须是有效的文件描述符，oldfd 和 newfd 值相同，相当于什么都没做
</pre>
```cpp
int fd = open("a.txt",O_RDWR | O_CREAT,0664);
int fd1 = dup(fd);
if(fd1 == -1){
  ...;
}//fd != fd1 但是指向的是一个文件
close(fd);
char *str = "hello world";
int ret = write(fd1,str,strlen(str));
if(ret == -1){
  ...;
}//成功返回的是写入的字节数
close(fd1);
int fd = open("1.txt",O_RDWR | O_CREAT,0664);
if(fd == -1){...}
int fd1 = open("2.txt",O_RDWR | O_CREAT,0664);
if(fd1 == -1){...}
cout << fd << " " << fd1 << endl;
int fd2 = dup2(fd,fd1);//后续通过fd1写数据，实际操作的是 a.txt , fd2 == fd1
char *str = "hello world";
int len = write(fd1,str,strlen(str));
cout << fd << " " << fd1 << " " << fd1 << endl;
close(fd);close(fd1);
```

### fcntl 函数

<pre><font size=3>
  int fcntl(int fd,int cmd,... /*arg*/);  --> unistd.h , fcntl.h
  参数： fd：表示需要操作的文件描述符  ； 
  cmd：表示对文件描述符进行如何操作 
  1.F_DUPFD - 复制文件描述符，复制的是第一个参数fd，返回一个新的文件描述符 ； 
  2.F_GETFL - 获取指定的文件描述符文件状态flag  获取的 flag 和我们通过 open 函数传递的 flag 是一个东西 
  3.F_SETFL - 设置文件描述符文件状态 flag  必选项：O_RDONLY，O_WRONLY,O_RDWR不可以被修改  可选项：O_APPEND ： 表示追加数据 ， O_NONBLOCK：设置成非阻塞，
  阻塞和非阻塞：描述的是函数调用的行为。
</pre>
```cpp
//复制文件描述符
int fd = open("1.txt",O_RDONLY);
int ret = fcntl(fd,F_DUPFD);
//修改/获取文件状态flag
int fd = open("1.txt",O_RDWR);
if(fd == -1) {...}
//修改文件描述符的flag，加入O_APPEND这个标记
int flag = fcntl(fd,O_GETFL);
int ret = fcntl(fd,F_SETFL,flag | O_APPEND);
char *str = "nihao";
write(fd,str,strlen(str));
close(fd);
```

## Linux 多进程开发

### 程序于进程

![image-20250421215016224](./../Images/image-20250421215016224.png)

![image-20250421215353103](./../Images/image-20250421215353103.png)

<font size=3>
[相关详细资料](../项目学习/WebServer/2.1 进程概述.pptx.pdf)    自己补充调度策略和调度算法
</font>

### 进程的状态转换

<font size=3>[press](../项目学习/WebServer/2.2 进程状态转换.pdf)</font>

<pre><font size =3>
  ps aux / ajx  ： a：显示终端上的所有进程，包括其他用户的进程  ； u：显示进程的详细信息  ；
  x：显示没有控制终端的进程  ； j：列出与作业控制相关的信息
  top -> 实时显示进程动态，加上 -d 来指定显示信息更新的时间间隔。
  kill PID 杀死一个进程，中间加 '-9' 表示强制杀死进程
  执行一个命令后加上 '&' 表示在后台运行，但是会将结果显示在当前终端，此时终端可继续操作
</pre>


### 创建进程

<font size=3>[press](../项目学习/WebServer/2.3 进程创建.pdf)</font>

<pre><font size=3>
  pid_t fork(void);  --> sys/types.h , unistd.h
  作用：用于创建子进程，在父进程中调用的
  返回值：返回两次，父进程和子进程各一次，在父进程中返回子进程的ID，在子进程中返回0，可以区别父子，在父进程中返回-1表示创建子进程失败，并设置errno
</pre>


```cpp
pid_t pid = fork();//在父进程中调用的，子进程并不会调用，子进程直接从下一行开始执行
if(pid > 0){//创建子进程的进程号,当前是父进程
  cout << getpid() << " " << getppid() << endl;
}else if(pid == 0){
  cout << getpid() << " " << getppid() << endl;
}
for(int i=0;i<3;i++){
  cout << i << " " << getpid << endl;
  sleep(1);
}
```

#### 父子进程虚拟地址空间情况

<pre><font size=3>
  fork 函数相当于 clone 一个新的地址空间，子进程的用户区数据区和父进程的一样，内核区也会拷贝，但是 pid 不一样，两者操作互不干扰，父子文件共享文件，fork的父子进程相同的文件描述符指向相同的文件表，引用计数增加，共享文件偏移指针，实际上是写时拷贝，仅仅读时是共享父进程的虚拟地址空间，<b>内核区不共享</b>
</pre>
#### 父子进程关系及GDB多进程调试

<pre><font size =3>
  区别：1.fork() 函数的返回值不同，父进程中 > 0 返回子进程的ID ； 子进程 = 0
       2.pcb 中的一些数据：当前进程的id-pid；当前进程的父进程的id-ppid；信号集、<br>
  共同点：某些状态下子进程刚被创建出来，还没有执行任何的写操作 用户区的数据和文件描述符是一样的
  父子进程对变量刚开始的时候是一样的共享的，如果修改了数据就不共享了，(读时共享，写时拷贝)<br>
  gdb 默认调试父进程，子进程的代码默认运行 set follow-fork-mode [parent | child]
  设置调试模式 set detach-on-fork [on | off]，on 表示调试当前进程时，其他进程继续运行，反之，其他进程被 GDB 挂起<br>
  info inferiors 查看当前调试的进程
  inferior id 切换当前调试的进程 id 指的是编号而不是 PID
  detach inferiors id 使进程脱离 GDB 调试
</pre>
### exec 函数族

<font size=3>[press](../项目学习/WebServer/2.6 exec函数族.pdf)</font>

<pre><font size =3>
  函数族：一系列功能相同或相似的函数
  调用exec 并不是新建一个进程，而是替换用户区的数据
  int execl(const char *path,const char *arg,...);  --> unistd.h
  参数：
    path：需要指定的执行的文件的路径或者名称 （推荐使用绝对路径）
    arg：执行可执行文件所需要的参数列表，第一个参数一般无作用，一般写执行的程序的名称，之后是所需的参数； 参数最后需要以 NULL 结束
  返回值：只有出错时返回-1，并且设置 errno，成功时无返回值<br>
  int execlp(const char *file,const char *arg);  --> unistd.h
  会到环境变量中查找指定的可执行文件，找到就执行，找不到执行不成功
  file：需要执行的可执行文件的文件名 例：xxx.out  ps
  execlp("ps","ps","aux",NULL);<br>
  int execv(const char *path,char *const arge[]);
  argv 是需要的参数的一个字符串数组
  char *agrv[]={"ps","aux",NULL};
  execv("/bin/ps",argv);
</pre>


```cpp
//创建一个子进程，在子进程中执行 exec 函数族的函数  ---> ./xxx 
pid_t pid = fork();
if(pid > 0){//父进程
  cout << getpid() << endl;
  sleep(1);//不加会产生孤儿进程
}else if(pid == 0){
  execl("xxx.out","xxx.out",NULL);//也可执行 shell 命令 execl("/bin/ps","ps","aux",NULL)
  cout << "this is child possess" << endl; //如果excel执行成功此行不会执行 因为子进程用户区数据被替换了
}
cout << "this is end" << endl;//excel执行成功的话只会执行父进程的此行，子进程同样被替换
```

### 进程控制

<font size = 3>[press](../项目学习/WebServer/2.7 进程退出、孤儿进程、僵尸进程.pdf)</font>

#### 进程退出

![image-20250422222451356](./../Images/image-20250422222451356.png)

<pre><font size = 3>
  exit 是标准 c 库函数
  _exit() 函数不会自动刷新缓冲区，如果输出没有 \n 此时不会自动刷新缓冲区，显示的内容暂存缓冲区中，此时输出不会正常显示
</pre>

#### 孤儿进程                                           

<pre><font size =3>
  孤儿进程：父进程结束，子进程还在运行，此时内核会自动将孤儿进程的父进程设置为 init (pid=1)，而init进程会循环的 wait()它的已经退出的子进程，最后 init 回收资源，无危害
</pre>

#### 僵尸进程

<pre><font size =3>
  每个进程结束后，都是释放地址空间中的用户区数据，但是 <b>PCB</b>(进程号，退出状态，运行时间) 没办法释放，需要<b>父进程释放</b>
  僵尸进程：进程终止时，父进程尚未回收，子进程残留资源(PCB)存放于内核中，不能被 kill-9 杀死，PCB进程号会一直被占用<br>
  解决僵尸进程：杀死父进程，子进程由init接收，后子进程被回收    或者 在父进程中进行wait()或者waitpid()得到它的退出状态同时彻底清除子进程
  wait()和waitpid()功能一样，区别在于wait函数会阻塞，waitpid可设置不阻塞，可指定等待哪个子进程结束，一次调用只能清理一个子进程<br>
  pid_t wait(int *wstatus);  --> sys/types.h  sys/wait.h
  功能：等待任意一个子进程结束，如果任意一个子进程结束了，此函数会回收子进程资源
  参数：进程退出时的状态信息，传入的是int类型的地址，传出参数，将状态写到该地址中
  返回值：成功返回被回收的子进程的 id   ； 失败返回-1 (所有子进程都结束或者调用失败等错误)<br>
  调用wait函数的进程会被挂起，直到一个子进程退出或者收到一个不能被忽略的信号时才被唤醒，如果没有子进程或者子进程都已经结束，会立即返回 -1，并且回收资源
</pre>

```cpp
pid_t pid;//为一个父进程创建 5 个子进程
for(int i=1;i<=5;i++){
  pif = fork();
  if(pid == 0) {
    break;//防止产生孙子进程
  }
}
if(pid > 0){
  while(1){
    cout << getpid() << endl;
    //int ret= wait(NULL);//不需要获得状态，需要传入int类型的地址，如果子进程未死会阻塞在这步
    int st;
    int ret = wait(&st);
    if(WIFEXITED(st)){
      cout << "正常退出的状态码：" << WEXITSTATUS(st) << endl;//exit传入的数据
    }
    if(WIFSIGNALED(st)){
      cout << "被哪个信号干掉了：" << WTERMSIG(st) << endl;
    }
    if(ret == -1) break;
    cout << ret << endl;//通过返回值判断子进程是否还存在
    sleep(1);
  }
}
else if(pid == 0){
  cout << getpid << endl;//因为无外力父进程会死循环 此时子进程为僵尸进程，杀死父后成为孤儿
  exit(xxx);
}
```

<pre><font size = 3>
  pid_t waitpid(pid_t pid,int *wstatus,int options);  --> sys/types.h,sys/wait.h
  功能：回收指定进程号的子进程，可以设置是否阻塞
  参数：  pid > 0：某个子进程的pid    ； pid = 0：回收当前进程组的所有子进程  ；  pid =-1：回收所有的子进程，相当于wait() <b>（最常用）</b> ； pid < -1：回收某个进程组的组id的绝对值，回收指定进程组的子进程
  options：设置阻塞或则和非阻塞，0 表示阻塞， WNOHANG：表示非阻塞
  返回值：>0 返回值子进程 id  ； =0 options=WNOHANG 表示还有子进程活着； =-1 ：无子进程或错误

### 进程间通信 IPC

<font size=3>[press](../项目学习/WebServer/2.10进程间通信.pdf)</font>

<pre><font size=3>
  进程间通信的方式有哪些，原理是什么
</pre>

![image-20250423170407144](./../Images/image-20250423170407144.png)

#### 匿名管道

<pre><font size =3>
  管道也称无名（匿名）管道，是UNIX系统 IPC 的最古老形式，所有的 UNIX 都支持这种机制。
  ls | wc -l   ； '|' 是管道符，ls 和 wc -l 是两个命令
  1.管道其实实在内核的内存中维护的缓冲器，这个缓冲器存储能力是有限的，不同OS大小不同
  2.管道有文件的特质：读、写操作，匿名管道没有实体，有名管道有文件实体，但不存数据。可以按照操作文件的方式对管道进程操作
  3.一个管道是一个字节流，使用管道时不存在消息或者消息边界的概念，从管道读取数据的进程可以读取任意大小的数据块，而不管写入进程写入管道的数据块的大小是多少。
  4.通过管道传递的数据是顺序的，从管道中读取出的字节的顺序和写入顺序一致
  5.数据传递方向是单向的，半双工的
  6.从管道读数据是一次性操作，一旦被读走，就从管道中抛弃，无法使用 lseek 来随机访问数据
  7.<b>只能在具有公共祖先的进程之间使用</b><br>
  int pipe(int pipefd[2]);  --> unistd.h
  功能：创建一个匿名管道，用来进程间通信
  [0] 对应的是读端  [1] 代表的是写端  ； 传出参数
  返回值：成功返回0，失败返回 -1，errno
  管道默认是阻塞的，如果管道中没有数据 read 阻塞，如果满了，write 阻塞
</pre>

```cpp
int pipefd[2];//在 fork 之间创建管道
int ret = pipe(pipefd);
if(ret == -1){...}
pid_t pid = fork();
if(pid > 0){
  char buf[1024] ={0};
  while(1){
    int len = read(pipefd[0],buf,sizeof(buf));//父进程读取数据
    printf("parent rec %s %d\n",buf,getpid);
    char *str="hello i am parent";
 	  write(pipetd[1],str,strlen(str));
    sleep(1);
  }
}else if(pid == 0){
  char buf[1024] ={0};
  //close(pipefd[0]);//实现父子一个读一个写
  while(1){
    char *str="hello i am child";
 	  write(pipetd[1],str,strlen(str));
    sleep(1);//不加sleep 可能会导致自己写自己读  
    int len = read(pipefd[0],buf,sizeof(buf));//父进程读取数据
    printf("child rec %s %d\n",buf,getpid);
  }
}
//查看管道缓冲大小  
ulimit -a // -p 可以修改大小   此时 终端里命令的形式
// 函数查看管道缓冲大小
long size = fpathconf(pipefd[0],_PC_PIPE_BUF);
```

<font size =3>一般来说 父子进程一个读一个写，否则很容易导致自己读自己写的问题</font>

##### 实现 ps aux | grep xxx

<pre><font size=3>
  子进程：ps aux，子进程结束之后，将数据发送给父进程   ； 父进程：获取到数据，过滤
  子进程将标准输出重定向到管道的写端 -- dup2
</pre>

```cpp
int fd[2];
int ret = pipe(fd);
if(ret == -1) {...};
pid_t pid = fork();
if(pid > 0){
  //从管到读
  close(fd[1]);
  char buf[1024] = {0};
  int len = -1;
  while((len = read(fd[0],buf,sizeof(buf)-1)) > 0){//-1 是留一个字符串结束符
  	printf("%s",buf);
    memset(buf,0,1024);
  }
  wait(NULL);
}else if(pid == 0){
  //文件描述符重定向  stdout_fileno -> fd[1]
  colse(fd[0]);
  dup2(fd[1],STDOUT_FILEND);
  //执行 ps aux
  execlp("ps","ps","aux",NULL); // execlp替换子进程的数据区,注意管道最大只能4k
  perror("execlp");
  exit(0);
}else {perror("fork");}
```

##### 管道读写特点

<pre><font size = 3>
  使用管道时，需要注意以下几种特殊情况（假设都是阻塞 I/O 操作）
  1.所有的指向管道写端的文件描述符都关闭了(管道写端引用数为0)，有进程从管道的读端读数据，那么管道中剩余的数据被读取后，再次 read 会返回 0，像读到文件末尾一样。
  2.如果有指向管道写端的文件描述符没有关闭(管道写端引用数>0)，而持有管道写端的进程也没有向管道中写数据，这时有进程从管道中读数据，那么管道中剩余的数据被读取后，再次 read 会阻塞，直到有数据可以读才返回
  3.如果所有指向管道读端的文件描述符都关闭(读端引用数=0)，这个时候有进程向管道中写数据，那么该进程会收到一个信号SIGPIPE，通常会导致进程异常终止
  4.如果有指向管道读端的文件描述符没有关闭(读端引用数>0)，而持有管道读端的进程也没有从管道中读数据，这时有进程向管道中写数据，当管道被写满时，再次 write 会阻塞，直到有空位置才能再次写入并返回<br>
  读管道：
    管道中有数据：read 返回实际读到的字节数
    管道中无数据：写端被完全关闭，read 返回 0（相当于读到文件末尾）   ； 写端未完全关闭：read 阻塞等待
  写管道：
    管道读端全部关闭，进程异常终止（收到SIGPIPE信号）
    管道读端未全部关闭：管道已满，write 阻塞   ；管道未满，write 将数据写入，并返回实际写入字节数
</pre>

```cpp
//设置管道非阻塞
int flags = fcntl(int fd[x],F_GETFL);//获取原来的 flag
flags |= O_NONBLOCK; // 修改 flag
fcntl(fd[x],F_SETFL,flag);// 设置新的flag
```

#### 有名管道 

<pre><font size =3>
  FIFO文件/有名管道/命名管道
  提供了一个路径名与之关联，以FIFO的文件形式存在于文件系统中，只要可以访问该路径，就能彼此通过 FIFO 相互通信，不相关的也可以交换数据。  不支持 lseek() 等文件定位操作
  1. FIFO 在文件系统中作为一个特殊文件存在，但内容存放内存中
  2.当使用 FIFO 的进程退出后，FIFO 文件将继续保存在文件系统中以便后续使用
  3.FIFO 有名字，不相关的进程可以通过打开有名管道进行通信<br>
  1.一个为只读打开的一个管道的进程会阻塞，直到另一个进程为只写打开，反之同理
  2.其余特点和匿名管道一致
</pre>

-   <font size=3>通过命令创建有名管道</font>

```
mkfifo xxx
```

-   <font size =3>通过函数创建</font>

<pre><font size =3>
  int mkfifo(const char *pathname,mode_t mode);// sys/type.h , sys/stat.h
  mode：文件权限，和 open 的 mode 是一样的，和 umask 做处理的最终的权限，八进制
  返回值：成功0，失败-1，errno
</pre>

```cpp
//mkfifo.c
int ret = access("fifo1",F_OK)；
if(ret == -1){
  cout << "管道不存在，创建管道" << endl;
  ret = mkfifo("fifo1",0664);
  if(ret == -1){
    prror("mkfifo");exit(0);
  }
}  
//write.c
int ret = access("test",F_OK)；
if(ret == -1){
  cout << "管道不存在，创建管道" << endl;
  ret = mkfifo("test",0664);
  if(ret == -1){
    prror("mkfifo");exit(0);
  }
} 
//以只写方式打开管道
int fd = open("test",O_WRNOLY);//当没有读端时，会阻塞在这一步
if(fd == -1){}
for(int i=0;i<100;i++){
  char buf[1024];
  sprintf(buf,"hello, %d\n",i);
  printf("write date : %s\n",buf);
  write(fd,buf,strlen(buf));
  sleep(1);
}
close(fd);
//read.c
int fd = open("test",O_RDONLY)；//没有写端也会阻塞在这一步
if(fd == -1){}
while(1){
  char buf[1024]={0};
  int len = read(fd,buf,sizeof(buf));
  if(ret == 0){
    cout << "写端断开连接了...\n"；
      break;
  }
  cout << "rec buf : " << buf << endl;
}
close(fd);
```

##### 有名管道实现简单版聊天功能

 <pre><font size=3>
   需要两个有名管道，实现相互之间通信
   只能一个写一个收交替进行，不能一直写或者一直读
   若想实现真正的聊天，需要将读写写入两个进程中，可以将一个写入子进程 
   A 父进程写  子进程读             B 父进程读   子进程写
 </pre>

```cpp
//chatA.c
int ret = access("fifo1",F_OK);
if(ret == -1){
  cout << "管道不存在，创建对应的有名管道" << endl;
  ret = mkfifo("fifo1",0664);
  if(ret == -1){}
}
ret = access("fifo2",F_OK);
if(ret == -1){
  cout << "管道不存在，创建对应的有名管道" << endl;
  ret = mkfifo("fifo2",0664);
  if(ret == -1){}
}
//只写打开 1 ，只读打开2
int fdw = open("fifo1",O_WRONLY);
if(fdw == -1){}
cout << "打开管道1成功，等待写入..." << endl;
int fdr = open("fifo2",O_RDONLY);
if(fdr == -1){}
cout << "打开管道2成功，等待读取..." << endl;
//循环写读数据
char buf[128];														
while(1){
  memset(buf,0,128);
  fgets(buf,128,stdin);
  ret = write(fdw,buf,strlen(buf));
  if(ret == -1){}
  memset(buf,0,128);
  ret = read(fdr,buf,128);
  if(ret <= 0){
    perror("read");break;
  }
  cout << "buf : " << buf << endl;
}
close(fdr);close(fdw);
//chatW.c
//区别是fdr，fdw需要交换，顺序改为读写顺序
```

```cpp
int main(int argc, char const *argv[])
{
    time_t timer;//time_t就是long int 类型
    struct tm *tblock;
    timer = time(NULL);
    //1.判断文件是否存在
    int ret = access("fifo1",F_OK);
    if(ret==-1)
    {
        printf("fifo1管道不存,创建管道!\n");
        //2.创建管道
        ret = mkfifo("fifo1",0664);
        if(ret==-1)
        {
            perror("mkfifo");
            exit(0);
        }
    }
    ret = access("fifo2",F_OK);
    if(ret==-1)
    {
        printf("fifo2管道不存,创建管道!\n");
        //2.创建管道
        ret = mkfifo("fifo2",0664);
        if(ret==-1)
        {
            perror("mkfifo");
            exit(0);
        }
    }
    //以只写的方式打开管道1
    int fd1 = open("fifo1",O_WRONLY);//以只写的方式打开管道
    if(fd1==-1)
    {
        perror("open");
        exit(0);
    }
    //以只读的方式打开管道2
    int fd2 = open("fifo2",O_RDONLY);
    if(fd2==-1)
    {
        perror("open");
        exit(0);
    }
    //创建子进程
    pid_t pid = fork();
    char buf[1024]={0};
    if(pid==0)
    {
        //子进程，负责读数据
        while(1)
        {
            //读管道2
            int len = read(fd2,buf,sizeof(buf));
            if(len==0)
            {
                printf("B断开连接！\n");
                exit(0);
            }
            else if(len>0)
            {
                timer = time(NULL);
                tblock = localtime(&timer);
                printf("%sB say:%s\n",asctime(tblock),buf);
                memset(buf,0,sizeof(buf));
            }
            else if(len==-1)
            {
                perror("read");
            }

        }
    }
    else if(pid>0)
    {
        //父进程,负责写数据
        while(1)
        {
            fgets(buf,sizeof(buf),stdin);
            //写管道1
            timer = time(NULL);
            tblock = localtime(&timer);
            write(fd1,buf,sizeof(buf));
            printf("%sA say:%s\n",asctime(tblock),buf);
            memset(buf,0,sizeof(buf));
        }
    }
    else if(pid==-1)
    {
        perror("fork");
        exit(0);
    }
    return 0;
}
```

#### 内存映射

<pre><font size=3>
  将磁盘文件的数据映射到内存，用户通过修改内存就能修改磁盘文件   sys/mman.h
  void *mmap(void *addr,size_t length,int prot,int flags,int fd,off_t offset);  
  功能：将一个文件或设备的数据映射到内存中
  void *addr：NULL 即可，系统指定合适的位置   
  length：映射的数据的长度，这个值 >0，使用文件长度，stat 或者 lseek 函数获取文件长度，会分配满足的页的整数倍
  prot：对申请的内存映射区的操作权限 PROT_EXEC 可执行；PROT_READ 读；PROT_WRITE 写;PROT_NONE 无权限，<b>要操作内存映射，必须要有读权限</b>，一般是READ、READ|WRITE
  flags：MAP_SHARED：映射区的数据会自动和磁盘文件同步，进程间通信，必须设置 ；MAP_PRIVATE ：不同步，不会修改原来的文件，会重新创建以一个新的文件(copy on write)
  fd：文件描述符，open 得到，open指定的权限不能和 prot 冲突，READ -> 读/读写; READ|WRIT->读写
  offset：偏移量，一般不用，必须是 4k 的整数倍
  返回值：返回创建的内存的首地址，失败返回MAP_FAILED，(void *）-1<br>
  int munmap(void *addr,size_t length);
  功能：释放内存映射
  addr：要释放的内存的首地址
  length：要释放的内存的大小，和mmap大小一样<br>
  <b>内存映射区是非阻塞的</b>

#####   有关系的进程

<font size =3>还没有子进程的时候，通过唯一的父进程先创建内存映射区，然后创建子进程，父子进程共享内存映射区</font>

#####   没有关系的进程

<font size=3>准备一个大小不是0的磁盘文件，进程1 通过磁盘文件创建内存映射区，得到一个操作这块内存的指针，进程 2 通过磁盘文件创建内存映射区，得到一个操作这块内存的指针，使用内存映射区通信，因为内存映射区会同步</font>

```cpp
int fd = open("test.txt",O_RDWR);
int size = lseek(fd,0,SEEK_END);//获取文件大小
void *ptr = mmap(NULL,size,PROT_READ | PROT_WRITE,MAP_SHARED,fd,0);
if(ptr == MAP_FAILED){
  perror("mmp");exit(0);
}
pid_t pid = fork();
if(pid > 0){
  wait(NULL);//因为是非阻塞的，wait手动阻塞，子进程写之后再读
  char buf[64];
  strcpy(buf,(char*)ptr);
  cout << "read data : " << buf << endl;
}else {
  strcpy((char*)ptr,"nihao a,son");
}
munmap(ptr,size);
```

##### 实现文件拷贝

```cpp
//对原始文件进行内存映射
int fd = open("test.txt",O_RDWR);
if(fd == -1){}
int len = lseek(fd,0,SEEK_END);//获取文件大小
int fd1 = open("cpy.txt",O_RDWR | O_CREAT,0664);//打开一个新的文件
if(fd1 == -1){}
truncate("cpy.txt",len);//扩展新的文件
write(fd1," ",1);//防止扩展失败，写入一个空
void *ptr = mmap(NULL,len,PROT_READ | PROT_WRITE,MAP_SHARED,fd,0);//分别映射到内存
void *ptr1 = mmap(NULL,len,PROT_READ | PROT_WRITE,MAP_SHARED,fd1,0);
if(ptr == MAP_FAILED || ptr1 == MAP_FAILED) {}
memcpy(ptr1,ptr,len);//ptr -> ptr1
munmap(ptr1,len);munmao(ptr,len);
close(fd1);close(fd);
```

##### 匿名映射

<font size=3>不需要文件实体进行一个内存映射，只能实现父子之间的通信，MAP_ANONYMOUS，fd会被忽略</font>

 ```cpp
 int len = 4096;
 void *ptr = mmap(NULL,len,PROT_READ | PROT_WRITE,MAP_SHARED | MAP_ANONYMOUS,-1,0);
 if(ptr == MAP_FAILED){}
 pid_d pid = fork();
 if(pid > 0){
   strcpy((char *)ptr,"hello world");
   wait(NULL);
 }else if(pis == 0){
   sleep(1);//内存映射是阻塞的 ,先sleep 等待写入
   printf("%s\n",(char*)ptr);
 }
 int ret = munmap(ptr,len);
 if(ret == -1){}
 ```

### 信号

<font size=3>[press](../项目学习/WebServer/2.19 信号.pdf)</font>

#### kill、raise、abort

<pre><font size=3>
  发生错误时，会产生 core 文件，默认大小是 0，可以通过unlimit -c unlimited 来修改（无限制）
  gdb a.out     core-file core 可以查看发生错误的信息<br>
  int kill(pid_t pid,int sig);  -> sys/types.h , signal.h
  功能：给任何进程或者进程组pid，发送人任何信号 sig (信号的编号或者宏值，=0表示不发送)
  pid：>0：发送给指定进程  ； =0：发送给当前进程组  =-1：发送给每一个有权限接收这个信号的进程
  <-1：这个pid=某个进程组的ID取反<br>
  int raise(int sig);  -> signal.h 标准 c 库函数 = kill(getpid(),sig);
  功能：给当前进程发送信号 sig
  返回值：成功0，失败非0<br>
  void abort(void);  = kill(getpid(),SIGABRT);
  功能：发送 SIGABRT 信号给当前进程，杀死当前进程
</pre>

#### alarm

<pre><font size =3>
  unsigned int alarm(unsigned int seconds) ; --> unistd.h
  功能：设置定时器(闹钟)，函数调用时开始倒计时，当倒计时为 0 时，函数会给当前进程发送 SIGALARM
  seconds：倒计时的时长，s，为 0 无效,取消一个计时器 alarm(0)
  返回值：之前没有定时器返回0，之前有返回之前的定时器倒计时的剩余时间
  SIGALARM：默认终止当前的进程，每个进程有且只有一个唯一的计时器。   <b>不阻塞</b>
  ./alarm1 >> a.txt  将这个进程输出的数据写到 a.txt 中 
  定时器和进程的状态无关，自然定时法，无论进程处于什么状态都会定时，定时器是后替换前的
</pre>

```cpp
int seconds = alarm(5);  
cout << "seconds = " << seconds << endl; // 0
sleep(2);
seconds = alarm(10); // 不阻塞
cout << "seconds = " << seconds << endl;// 3
while(1){} //等待 alarm 返回 SIGALARM 结束进程
```

#### setitimer

<pre><font size=3>
  int setitimer(int which,const struct itimerval *new_value,struct itimerval *oldvalue);
  功能：设置定时器(闹钟)，可以替代alarm，精度微妙，可以实现周期性的定时
   which：定时器以什么时间定时
   ITMIMER_REAL：真实时间，时间到达，发送 SIGALRM，常用
   ITIMER_VIRTUAL：用户时间，时间到达，发送 SIGVTALRM
   ITMIMER_PROF：以该进程在用户态和内核态下所消耗的时间来计算，时间到达，发送 SIGPROF
   new_value：设置定时器的属性，每个阶段的时间，间隔时间 ；延迟多长时间执行;延迟x后每y定时一次
   old_value：记录上一次的定时的时间参数，一般不使用，NULL	
   返回值：成功0 ，失败 -1 ,并设置错误号
##### signal 信号捕捉

<pre><font size=3>
  通过信号捕捉，实现真正意义上的周期性定时  typedef void (*sighandler_t) int 函数指针
  sighandler_t signal(int signum,sighandler_t handler); -> signal.h 
  功能：设置某个信号的捕捉行为
  signum：要捕捉的信号，一般使用宏值  handler：捕捉到的信号要如何处理(SIG_IGN：忽略信号，SIG_DEL：使用信号默认的行为；回调函数：内核调用，程序员只写，捕捉到后如何处理)
  回调函数：程序员实现，提前准备好，函数的类型根据实际需求，看函数指针的定义，当信号产生，由<b>内核调用</b>，函数指针是实现回调的手段,函数实现之后，将函数名放到函数指针的位置
  返回值：成功返回前一次注册的信号处理函数的地址(回调函数)，第一次NULL，失败 SIG_ERR设置错误号<br>
  SIGKILL、SIGSTOP 不能被捕捉、不能被忽略
</pre>

```cpp
void myalarm(int num){
  cout << "捕捉到的信号的编号是：" << num << endl;
} //void (*sighandler_t) (int) 函数指针，int 类型的参数表示捕捉到的信号的值
//signal(SIGALRM,SIG_IGN);//在定时开始前注册信号捕捉，此为忽略信号
signal(SIGALRM,myalarm);
struct itimerval new_value;
new_value.it_interval.tv_sec = 2;//间隔时间
new_value.it_interval.tv_usec = 0;//间隔时间 微妙
new_value.it_value.tv_sec = 3;//设置延迟的时间 ---> 延迟 3 秒后，每两秒定时一次，3s后第一次
new_value.it_value.tv_usec = 0;
int ret = setitimer(ITIMER_REAL,&new_value,NULL);//非阻塞
if(ret == -1){}
getchar();//等待定时器结束结束进程
```

### 信号集

<pre><font size =3>
  阻塞信号集和未决信号集
  信号的 “未决” 是一种状态，指的是从信号的产生到信号被处理前的这一段时间。
  信号的 “阻塞” 是一个开关动作，指的是阻止信号被处理，但不是阻止信号产生。
  1.用户通过键盘  Ctrl + C, 产生2号信号SIGINT (信号被创建)<br>
  2.信号产生但是没有被处理 （未决）
    - 在内核中将所有的没有被处理的信号存储在一个集合中 （未决信号集）
    - SIGINT信号状态被存储在第二个标志位上
        - 这个标志位的值为0， 说明信号不是未决状态
        - 这个标志位的值为1， 说明信号处于未决状态<br>
  3.这个未决状态的信号，需要被处理，处理之前需要和另一个信号集（阻塞信号集），进行比较
    - 阻塞信号集默认不阻塞任何的信号
    - 如果想要阻塞某些信号需要用户调用系统的API<br>
  4.在处理的时候和阻塞信号集中的标志位进行查询，看是不是对该信号设置阻塞了
    - 如果没有阻塞，这个信号就被处理
    - 如果阻塞了，这个信号就继续处于未决状态，直到阻塞解除，这个信号就被处理<br>
    以下信号集相关的函数都是对自定义的信号集进行操作
    int sigemptyset(sigset_t *set);
    功能：清空信号集中的数据，将所有标志位置为 0
    set：传出参数，需要操作的信号集
    返回值：成功0，失败1<br>
    int sigfillset(sigset_t *set);
    功能：将信号集中的所有标志位置为 1<br>
    int sigaddset(sigset_t *set,int signum);
    功能：设置信号集中的某一个信号对应的标志位为 1，表示阻塞这个信号
    signum：需要设置阻塞的那个信号<br>
    int sigdelset(sigset_t *set,int signum);
    功能：设置信号集中的某一个信号对应的标志位为 0，表示不阻塞这个信号<br>
    int sigismember(const sigset_t *set,int signum);
    功能：判断某个信号是否阻塞
    set：需要操作的信号集，此处不是传出参数
    返回值：1：signum 被阻塞， 0：signum 不阻塞； -1 调用失败
</pre>

```cpp
sigset_t set;//创建一个信号集
sigemptyest(&set);//情况信号集的内容
int ret = sigismember(&set,SIGINT);//判断是否阻塞
if(ret == 0){
  cout << "SIGINT 不阻塞" << endl;
}
else if(ret == 1){
  cout << "SIGINT 被阻塞" << endl;
}
sigaddset(&set,SIGINT);//添加几个信号到信号集中  ctrl c 2号
sigaddset(&set,SIGQUIT);  // ctrl \  3号
ret = sigismember(&set,SIGINT);
if(ret == 0){
  cout << "SIGINT 不阻塞" << endl;
}
else if(ret == 1){
  cout << "SIGINT 被阻塞" << endl;
}
sigdelset(&set,SIGQUIT);
//if else if ...
```

#### sigprocmask

<pre><font size=3>
  对内核的信号集进行操作，使用系统调用的API   mask 指内核中的信号集
  int sigprocmask(int how,const sigset_t *set,sigset_t *oldset);
  功能：将自定义信号集中的数据设置到内核中（设置阻塞，解除阻塞，替换）
  how：如何对内核阻塞信号集进行处理(SIG_BLOCK：将用户设置的阻塞信号添加到内核中，mask | set，SIG_UNBLOCK：解除阻塞，mask &= ~set，SIG_SETMASK：覆盖内核中原来的值)
  set：已经初始好的用户自定义的信号集
  oldset：保存设置之前内核中的阻塞信号集的状态，一般是NULL，不使用
  返回值：成功0，失败-1，设置错误号<br>
  int sigpending(sigset_t *set);
  功能：获取内核中的未决信号集
  set：传出参数，保存的是内核中的未决信号集中的信息    
  常规信号是 1 - 31  （kill -l 查看）
</pre>

```cpp
//把所有的常规信号的未决状态打印到屏幕，设置某些信号是阻塞的，通过键盘产生这些信号，防止过快
sigset_t set;
sigemptyset(&set);
sigaddset(&set,SIGINT); // 2号 CTRL c
sigaddset(&set,SIGQUIT); // 3 号 CTRL \
sigprocmask(SIG_BLOCK,&set,NULL);//修改内核中的阻塞信号集
while(1){
  sigset_t pendingset;//获取当前的未决信号集的数据
  sigemptyset(&pendingset);
  sigpending(&pendingset);
  for(int i=1;i<=31;i++){
    if(sigismember(&pendingset,i) == 1){
      cout << 1 << endl;//表示未决，阻塞不会被处理，故未决
    }else if(sigismenber(&pendingset,i) == 0){
      cout << 0 << endl;//表示非未决，不阻塞
    }
  }
}
//可以解除阻塞，从而使命令使用默认的处理行为 ctrl c 即可结束进程
```

<font size=3>前台运行是不能操作终端的，可以在执行命令后加 & 表示在后台运行，结果依旧显示在终端上，但是可以操纵终端，fg 切换到前台</font>

#### 信号捕捉函数

<pre><font size=3>  SIGKILL、SIG_STOP 不能被捕捉、阻塞、忽略
  int sigaction(int signum,const struct sigaction *act,struct sigaction *old)
  功能：检查或改变信号的处理，信号捕捉
  signum：需要捕捉的信号的编号或宏值（信号的名称）
  act：捕捉信号之后的处理动作
  oldact：上一次对信号捕捉相关的设置，一般不使用，NULL<br>
  struct sigaction {
        // 函数指针，指向的函数就是信号捕捉到之后的处理函数
        void     (*sa_handler)(int);
        // 不常用
        void     (*sa_sigaction)(int, siginfo_t *, void *);
        // 临时阻塞信号集，在信号捕捉函数执行过程中，临时阻塞某些信号。
        sigset_t   sa_mask;
        // 使用哪一个信号处理对捕捉到的信号进行处理
        // 这个值可以是0，表示使用sa_handler,也可以是SA_SIGINFO表示使用sa_sigaction
        int        sa_flags;
        // 被废弃掉了,NULL
        void     (*sa_restorer)(void);
    };
</pre>

```cpp
struct sigaction act;
act.sa_flags = 0;
act.sa_handler = myalarm;//函数指针，myalarm 同信号中的一致，回调函数
sigemptyset(&act.sa_mask);//清空临时阻塞信号集
sigaction(SIGALRM,&act,NULL);//ITIMER_REAL产生的是SIGALRM信号
struct itimerval new_value;
new_value.it_interval.tv_sec = 2;//间隔时间
new_value.it_interval.tv_usec = 0;//间隔时间 微妙
new_value.it_value.tv_sec = 3;//设置延迟的时间 ---> 延迟 3 秒后，每两秒定时一次，3s后第一次
new_value.it_value.tv_usec = 0;
int ret = setitimer(ITIMER_REAL,&new_value,NULL);//非阻塞
if(ret == -1){}
while(1);//等待定时器结束结束进程
```

![image-20250425152757134](./../Images/image-20250425152757134.png)

#### sigchld信号

<pre><font size=3>
  产生的条件：子进程终止时 ； 子进程收到 SIGSTOP 信号暂停时 ； 子进程处于停止态，接收到SIGCONT 后唤醒时   --> 这三种条件都会给父进程发送 SIGCHLD 信号，父进程默认忽略该信号，但是可以用 SIGCHLD <b>解决僵尸进程问题</b>
</pre>

```cpp
void alarm(int num){
  cout << "捕捉到的信号 :" << num << endl;
  //wait(NULL);//回收子进程 PCB 的资源
  while(1){ // while里不写 wait，因为wait会一直阻塞，导致父进程无法继续运行 
    //waitpid和wait函数不是根据收到信号数来回收的，只要有子进程死了就会一直回收
    int ret = waitpid(-1,NULL,WNOHANG);//回收所有子进程，  设置非阻塞
    if(ret > 0){
      cout << "child die ,pid = " << ret << endl;
    }else if(ret == 0){
      break;//表示还有子进程活着
    }else if(ret == -1){
      break;//没有子进程了
    }
  }
}
// main
//提前设置好阻塞信号集，阻塞SIGCHLD，因为可能子进程很快结束，但是父进程还没有注册完信号捕捉
sigset_ t set;
sigemptyset(&set);
sigaddset(&set,SIGCHLD);
sigprocmask(SIG_BLOCK,&set,NULL);//设置到内核区的信号集
pid_t pid;
for(int i=1;i<=20;i++){
  pid = fork();
  if(pid == 0) {
    break;//子进程不fork
  }
}
if(pid > 0){ //如果使用wait 父进程会挂起，无法继续自己的操作，这里使用 sigchld
  //捕捉子进程死亡时发送的 SIGCHLD 信号
  struct sigaction act;
  act.sa_flags = 0;
  act.sa_handler = myalarm; //回调函数 ,参数 int 类型，捕捉到的信号编号
  sigemptyset(&act.sa_mask);
  sigaction(SIGCHLD,&act,NULL);//可能会存在部分子进程未被回收，因为信号发送太快且是覆盖的
//使用while waitpid 可能会出现子进程全部结束，父进程才刚开始，并发，此时收不到信号，会段错误去
//注册完信号捕捉之后在解除上述的阻塞
  sigprocmask(SIG_UNBLOCK,&set,NULL);//此时会依次处理之前收到的信号
  while(1){
    cout << "parent process : " << getpid() << endl;
    sleep(2);
  }
}else if(pid == 0){
  cout << "child pocess : " << getpid() << endl;
}
```

<font size=3>wait 和 waitpid 函数并不是通过信号的数量来回收子进程的，只要产生信号，两者就是处理已经结束的子进程，只是一次只能处理一个，所以需要循环将所有的结束的子进程全回收</font>

### 共享内存

<font size=3> [press](./../项目学习/WebServer/2.28 共享内存.pdf) </font>

<pre><font size=3>
  shmget()：创建一个新共享内存段或取得一个既有共享内存段的标识符(即有其他进程创建的共享内存段)。这个调用将返回后续调用中需要用到的共享内存标识符
  shmat()：附上共享内存段，即使该段成为调用进程的虚拟内存的一部分，返回 add，指向进程的虚拟地址空间中该共享内存段的起点的指针.
  shmdt()：分离共享内存段，进程终止时会自动完成这一步
  shmctl()：删除共享内存段，当前附加内存段的进程都与之分离后内存段才会销毁，只有一个进程需执行<br>
  int shmget(key_t key, size_t size, int shmflg);
    - 功能：创建一个新的共享内存段，或者获取一个既有的共享内存段的标识。
        新创建的内存段中的数据都会被初始化为0
    - 参数：
        - key : key_t类型是一个整形，通过这个找到或者创建一个共享内存。
                一般使用16进制表示，非0值
        - size: 共享内存的大小，自动->满足的最接近的分页大小
        - shmflg: 属性
            - 访问权限
            - 附加属性：创建/判断共享内存是不是存在
                - 创建：IPC_CREAT
                - 判断共享内存是否存在： IPC_EXCL , 需要和IPC_CREAT一起使用
                    IPC_CREAT | IPC_EXCL | 0664
        - 返回值：
            失败：-1 并设置错误号
            成功：>0 返回共享内存的引用的ID，后面操作共享内存都是通过这个值。<br>
void *shmat(int shmid, const void *shmaddr, int shmflg);
    - 功能：和当前的进程进行关联
    - 参数：
        - shmid : 共享内存的标识（ID）,由shmget返回值获取
        - shmaddr: 申请的共享内存的起始地址，指定NULL，内核指定
        - shmflg : 对共享内存的操作
            - 读 ： SHM_RDONLY, 必须要有读权限
            - 读写： 0
    - 返回值：
        成功：返回共享内存的首（起始）地址。  失败(void *) -1<br>
int shmdt(const void *shmaddr);
    - 功能：解除当前进程和共享内存的关联
    - 参数：
        shmaddr：共享内存的首地址
    - 返回值：成功 0， 失败 -1<br>
int shmctl(int shmid, int cmd, struct shmid_ds *buf);
    - 功能：对共享内存进行操作。删除共享内存，共享内存要删除才会消失，创建共享内存的进程被销毁了对共享内存是没有任何影响。标记删除，
    - 参数：
        - shmid: 共享内存的ID
        - cmd : 要做的操作
            - IPC_STAT : 获取共享内存的当前的状态
            - IPC_SET : 设置共享内存的状态
            - IPC_RMID: 标记共享内存被销毁，只有所有共享该内存的进程没了才会删除
        - buf：需要设置或者获取的共享内存的属性信息
            - IPC_STAT : buf存储数据，传出参数
            - IPC_SET : buf中需要初始化数据，设置到内核中
            - IPC_RMID : 没有用，NULL<br>
key_t ftok(const char *pathname, int proj_id);
    - 功能：根据指定的路径名，和int值，生成一个共享内存的key
    - 参数：
        - pathname:指定一个存在的路径
            /home/nowcoder/Linux/a.txt
        - proj_id: int类型的值，但是这系统调用只会使用其中的1个字节
                   范围 ： 0-255  一般指定一个字符，例：'a'

```cpp
//write
int shmid = shmget(100,4096,IPC_CREAT | 0664);//创建一个共享内存
void *ptr = shmat(shmid,NULL,0);//此时是读写权限，和当前进程进行关联
char *str = "hello world";
memcpy(ptr,str,strlen(str) + 1);//写数据，加一个字符结束符
cout << "按任意键继续!" << endl;getchar();//为了验证，防止直接取消了关联，和删除,编译不等于运行，两文件运行肯定有时差，不加直接就释放删除了
shmdt(ptr);//解除关联
shmctl(shmid,IPC_RMID,NULL);//删除共享内存
//read
int shmid = shmget(100,0,IPC_CREAT);//key一定要对应住，大小小于等于即可，获取共享内存
void *ptr = shmat(shmid,NULL,0);//关联，返回的是首地址
cout << (char *)ptr << endl;
cout << "按任意键继续!" << endl;getchar();//为了验证，防止直接取消了关联，和删除
shmdt(ptr);//解除关联
shmctl(shmid,IPC_RMID,NULL);//删除共享内存
```

<pre><font size=3>
问题1：操作系统如何知道一块共享内存被多少个进程关联？
    - 共享内存维护了一个结构体struct shmid_ds 这个结构体中有一个成员 shm_nattc,记录了关联的进程个数<br>
问题2：可不可以对共享内存进行多次删除 shmctl
    - 可以的,因为shmctl 标记删除共享内存，不是直接删除,当和共享内存关联的进程数为0的时候，就真正被删除,当共享内存的key为0的时候，表示共享内存被标记删除了,如果一个进程和共享内存取消关联，那么这个进程就不能继续操作这个共享内存。也不能进行关联。<br>
    共享内存和内存映射的区别
    1.共享内存可以直接创建，内存映射需要磁盘文件（匿名映射除外）
    2.共享内存效果更高
    3.内存
        所有的进程操作的是同一块共享内存。
        内存映射，每个进程在自己的虚拟地址空间中有一个独立的内存。
    4.数据安全
        - 进程突然退出
            共享内存还存在
            内存映射区消失
        - 运行进程的电脑死机，宕机了
            数据存在在共享内存中，没有了
            内存映射区的数据 ，由于磁盘文件中的数据还在，所以内存映射区的数据还存在。<br>
    5.生命周期
        - 内存映射区：进程退出，内存映射区销毁
        - 共享内存：进程退出，共享内存还在，标记删除（所有的关联的进程数为0），或者关机
            如果一个进程退出，会自动和共享内存进行取消关联。
</pre>

### 进程守护

<font size = 3>[press](../项目学习/WebServer/2.30 守护进程.pdf) <b>本部分一点要看 ppt</b> </font>

<pre><font size=3>  控制终端是指可以通过比如按键产生一些信号，从而控制进程。
  进程中，控制终端是保存在 PCB 中的信息，而fork() 会复制 PCB 中的信息，因而 shell 进程启动的其他进程的控制终端也是这个终端  echo $$ 查看pid ，tty 查看名称 
</pre>

![image-20250426220124254](./../Images/image-20250426220124254.png)

![image-20250426222353089](./../Images/image-20250426222353089.png)

<pre><font size=3> 
  执行 fork 后，父进程退出，子进程继续，并子进程 setsid() 开启一个新的会话，此时不建立联系，即是当前会话没有控制进程，不用父进程开启是为了防止进程组 id 冲突，脱离控制终端，但是还是有终端的 
</pre>

```cpp
void work(int num){
  time_t tm = time(NULL);//获取系统时间，写入磁盘文件, s
  struct  tm *loc = localtime(&tm);//将秒转换为具体时间
  char buf[1024];
  sprintf(buf,"%d-%d-%d %d:%d:%d\n",loc->tm_year,loc->tm_mon,loc->tm_mday,loc->tm_hour,loc->tm_min,loc->tm_sec);
  printf("%s\n",buf);
  int fd = open("time.txt",O_RDWR | O_CREAT | O_APPEND,0664); //生成到对应的目录下
  write(fd,buf,strlen(buf));
  close(fd);
}
//写一个守护进程，每隔两秒获取系统时间，写入磁盘文件中
pid_t  pid = fork(); //1.创建子进程，退出父进程，父进程会默认打开0、1、2标准输入输出错误三个文件描述符
if(pid > 0) exit(0);
//2.将子进程重新创建一个会话
setsid();//返回值是 pid_t
//3.设置掩码
umask(022);
//4.更改工作目录
chdir("/home/zyc/");//更改到根目录
//5.关闭、重定向文件描述符，此时未开启其他文件描述符，不许close
int fd = open("dev/null",O_RDWR); //这个目录下写入的东西会被忽略  重定向
dup2(fd,STRIN_FILENO);// 0
dup2(fd,STDOUT_FILENO);// 1
dup2(fd,STDERR_FILENO);// 2   重定向之后数据不会显示在终端上，会被忽略，防止后台守护进程向终端写信息，要写进对应磁盘文件
//6.业务逻辑
struct sigaction act;
act.sa_flags = 0;//使用第一个回调
act.sa_handler = work;//函数指针 - 函数名
sigemptysset(&act.sa_mask);
sigaction(SIGALRM,&act,NULL);//捕捉定时信号
struct itimerval val;//创建定时器
val.it_value.tv_sec = 2;//延迟时间
val.it_value.tv_usec = 0;
val.it_interval.tv_sec = 2;//间隔时间
val.it_interval.tv_usec = 2;
setitimer(ITIMER_REAL,&val,NULL);
while(1){
  sleep(10);//防止进程结束，此时没有控制终端，不能通过CTRL+c结束进程
}
```

## 线程

<font size=3>[press](../项目学习/WebServer/3.1 线程.pdf)</font>

<pre><font size =3>  线程是允许应用程序并发执行多个任务的一种机制，一个进程可以包含多个线程，<b>同一个程序中的所有线程均会独立执行相同程序，并共享同一份全局共享内存</b>,栈空间和text会分为多部份，每个线程占一部分，共享库，堆空间和内核线程之间共享
  进程是 CPU 分配单元的最小单位，线程是 OS 调度执行的最小单位
  线程是轻量化的进程（LWP）,Linux 下线程的本质仍是进程   ps -Lf pid
![image-20250427143943432](./../Images/image-20250427143943432.png)


### 创建线程

<pre><font size=3>  一般情况下，main函数所在的线程成为主线程(main线程)，其余创建的线程为子线程，程序中默认只有一个进程，程序默认只有一个线程
  gcc pthread_create.c -o create -pthread/-lpthread (链接库)
  int pthread_create(pthread_t *thread,const pthread_attr_t *attr,void *(*start_routine) (void *),void *arg);  --> pthread.h
  功能：创建一个子线程
  参数：
    thread：传输参数，线程创建成功后，子线程的线程 ID 会写入到该变量中
    attr：需要设置线程的属性，一般使用默认值，NULL
    start_routine：函数指针，这个函数是子线程需要处理的逻辑代码
    arg：给第三个参数使用，传参
  返回值：成功：0，失败：返回错误号，和之前的errno不太一样 -> 获取错误号信息：char * strerror(int errnum)
</pre>


```cpp
void *callback(void *arg){//void *是万能指针，表示可以将任何类型的指针传递进来 
  printf("child thread...\n");// 子线程的代码
  printf("arg value: %d\n",*(int*)arg);
  return NULL;
}
pthread_t tid;
int num = 10;
int ret = pthread_create(&tid,NULL,callback,NULL); // (...callback,(void *)&num);
if(ret != 0){
  char *errstr = strerror(ret);
  printf("error : %s\n",errstr);
}
for(int i=1;i<=5;i++){
  printf("%d\n",i); //主线程的代码
}
sleep(1);//防止子线程未执行 程序就被return
```

### 终止线程

<pre><font size=3>  void pthread_exit(void *retval); --> pthread.h，返回的是地址
  功能：终止一个线程,在哪个线程中调用，就表示终止哪个线程
  retval：需要传递一个指针，作为返回值，可以在pthread_join()中获取到<br>
  pthread_t pthread_self(void);
  功能：获取当前的线程的线程 ID<br>
  int pthread_equal(pthread_t t1,pthread_t t2);
  功能：比较两个线程 ID 是否相等,不同的OS pthread_t类型的实现不一样，有的是无符号长整型，有的是结构体
</pre>


```cpp
void *callbck(void *arg){ //返回的是地址
  printf("child pthread id : %ld\n",pthread_self());
  return NULL; //相当于 pthread_exit(NULL)
}
pthread_t tid;
int ret = pthread_create(&tid,NULL,callback,NULL);
if(ret != 0){
  char *errstr = strerror(ret);
  printf("error : %s\n",errstr); 
}
for(int i=1;i<=5;i++){
  printf("%d\n",i);
}
printf("tid : %ld,main thread id : %ld\n",tid,pthread_self());
pthread_exit(NULL);//主线程退出，进程不会直接结束，不会影响其他正常运行的线程
```

### 连接已终止的线程

<pre><font size=3>  int pthread_join(pthread_t thread,void **retval);
 功能：和一个已终止的线程进行连接 --> 回收子线程的资源  ， 阻塞函数 ， 调用一次只能回收一个，一般在主线程中使用
  thread：需要回收的子线程的 ID
  retval：接收子线程退出时的返回值，二级指针<br>
  在上述代码主线程退出前调用 join 回收子线程的资源
  ret = pthread_join(tid,NULL); // 阻塞函数
  printf("回收自线程资源\n");
</pre>


```cpp
 // 如果子线程中不是return而是exit终止：
int value = 10;//cb中的局部变量，exit后会被释放，再次访问该地址是随机值，可以使用全局变量
pthread_exit((void *)&value); // = return (void *)&value
//主线程中
int *thread_retval;
ret = pthread_join(tid,(void **)&thread_retval);//如果不是二级指针，会值传递，不会真的改变变量的值，类似于 * & 改为地址传递，真正修改局部变量的值
printf("exit data: %d\n",*thread_retval);
```

### 线程的分离

<pre><font size=3>  int pthread_detach(pthread_t thread);
  功能：分离线程，被分离的线程在终止的时候会自动释放资源返回给系统
  注意：1.不能多次分离，会产生不可预料的行为。  2.不能去连接一个已经分离的线程，会报错。


```cpp
//在主线程exit之前设置子线程分离,子线程结束时对应的资源就不需要主线程释放
pthread_detach(tid);//注意 不能再次对分离的子线程进行连接了，即是不能 x_join 了
```

### 线程取消

<pre><font size=3>  int pthread_cancel(pthread_t thread);
  功能：取消线程（让线程终止）  取消某个线程可以终止某个线程的运行，但是并不是马上终止，而是当子线程执行到一个取消点，线程才会取消
  取消点：OS 规定好的一些系统调用，可以粗略的理解为从用户区到内核的切换，这个位置称为取消点
</pre>


### 线程属性

<pre><font size=3>  int pthread_attr_init(pthread_attr_t *attr);
  功能：初始化线程属性变量
  int pthread_attr_destory(pthread_attr_t *attr);
  功能：释放线程属性的资源
  int pthread_attr_getdetachstate(const pthread_attr_t *attr,int *detachstate);
  功能：获取线程分离的状态属性
  int pthread_attr_setdetachstate(pthread_attr_t *attr,int detachstate);
  功能：设置线程分离的状态属性
</pre>


```cpp
void *callbck(void *arg){ //返回的是地址
  printf("child pthread id : %ld\n",pthread_self());
  return NULL; //相当于 pthread_exit(NULL)
}
//创建一个线程属性变量
pthread_attr_t attr;
pthread_attr_t_init(&attr);//初始化属性变量
pthread_attr_setdetachstate(&attr,PTHREAD_CREATE_DETACHED);//设置属性,此处指设置了线程分离
pthread_t tid;
int ret = pthread_create(&tid,&attr,callback,NULL);
if(ret != 0){
  char *errstr = strerror(ret);
  printf("error : %s\n",errstr); 
}
size_t size;
pthread_attr_getstacksize(&attr,&size);//获取线程属性中栈空间的大小
printf("thread stack size : %ld\n",size);
for(int i=1;i<=5;i++){
  printf("%d\n",i);
}
printf("tid : %ld,main thread id : %ld\n",tid,pthread_self());
pthread_attr_t_destory(&attr);//有初始化就要有释放
pthread_exit(NULL);//主线程退出，进程不会直接结束，不会影响其他正常运行的线程
```

### 线程同步

<font size=3>[press](../项目学习/WebServer/3.8 线程同步.pdf)</font>

```cpp
int tickets = 100;
void *sellticket(void *arg){
  while（tickets > 0){
    usleep(3000);
    printf("%ld 正在卖第 %d 张门票\n",pthread_self(),tickets)；
      tickets -- ;
  }
  return NULL;
}
//使用多线程实现卖票的案例，有多个窗口，一共100张票
pthread_t tid1,tid2,tid3; // 创建 3 个子线程
pthread_create(&tid1,NULL,sellticket,NULL);
pthread_create(&tid2,NULL,sellticket,NULL);
pthread_create(&tid3,NULL,sellticket,NULL);
pthread_join(tid1,NULL);//连接子线程，回收资源，阻塞的
pthread_join(tid2,NULL);
pthread_join(tid3,NULL);
//pthread_detach(tid1);//设置线程分离
//pthread_detach(tid2);
//pthread_detach(tid3);
pthread_exit(NULL);//退出主线程防止主进程结束，导致所有的线程结束
```

### 互斥锁  P V

<pre><font size=3>  互斥量的类型 pthread_mutex_t
  int pthread_mutex_init(pthread_mutex_t *restrict mutex,const pthread_mutexattr_t *restrict attr);
  功能：初始化互斥量
  mutex：需要初始化的互斥量变量
  attr：互斥量的相关属性，NULL
  restrict：C语言的修饰符，被修饰的指针，不能由另外的一个指针进行操作。<br>
  int pthread_mutex_destroy(pthread_mutex_t *mutex);
  功能：释放互斥量的资源
  int pthread_mutex_lock(pthread_mutex_t *mutex);
  功能：加锁，如果有一个线程加锁，其他线程只能阻塞等待
  int pthread_mutex_trylock(pthread_mutex_t *mutex);
  功能：尝试加锁，如果加锁失败不会阻塞，直接返回
  int pthread_mutex_unlock(pthread_mutex_t *mutex);
  功能：解锁
</pre>


```cpp
int tickets = 100;
pthread_mutex_t mutex;//创建一个互斥量
void *sellticket(void *arg){
  while(1){
    pthread_mutex_lock(&mutex);//加锁一定要在临界区之前，否则会出现多个线程进入临界区，出现阻塞等待现象，此时可能会导致超卖现象
    if(ticket > 0){
       usleep(3000);
    	 printf("%ld 正在卖第 %d 张门票\n",pthread_self(),tickets)；
    	 tickets -- ;
    }
    else {
      pthread_mutex_unlock(&mutex);//break前解锁
      break;
    }
    pthread_mutex_unlock(&mutex);//当前任务结束了在解锁
  }
  return NULL;
}
//main 函数中对互斥量初始化等
pthread_mutex_init(&mutex,NULL);
pthread_mutex_destory(&mutex);//如果是join回收资源，由于会阻塞主进程可以此时可以写道exit前，等所有的子线程结束便销毁，但是如果是detach是不会阻塞的，此时需要while防止直接销毁
```

#### 死锁

<pre><font size=3>  两个或两个以上的进程在执行过程中，因抢占资源而造成的一种互相等待的现象
  1.忘记释放锁   2.重复加锁   3.多线程多锁，抢占锁资源  
</pre> 


#### 读写锁

<pre><font size=3>  提供允许多个读出，但是只允许一个写入
    1.如果有其他线程读数据，则允许其他线程执行读操作，但不允许写操作；  2.如果有其他线程写数据，则其他线程都不允许读、写数据；  3.写是独占的，写的优先级高 <br>
    读写锁类型：pthread_rwlock_t
    int pthread_rwlock_init(xxx,xxx);//参数组成同 mutex，只是将mutex换为 rwlock
     rdlock()   wrlock()  分别表示写锁和读写，其余函数均与mutex类似 ,可以公用一个互斥量，函数调用实现不同的功能
</pre>


```cpp
//3个线程不定时写一个全局变量，其余5个不定时读
int num = 1;
pthread_rwlock_t rwlock;
void *writeNum(void *arg){
  while(1){
    pthread_rwlock()_wrlock(&rwlock);//加写锁
    num ++ ;
    printf("++write,tid ： %ld,num : %d\n",pthread_self(),num);
    pthread_rwlock_unlock(&rwlock);//解锁
    usleep(100);
  }
  return NULL;
}
void *readNum(void *arg){
	while(1){
    pthread_rwlock()_rdlock(&rwlock);//加读锁
    printf("==read,tid : %ld,num %d\n",pthread_self(),num);
    pthread_rwlock_unlock(&rwlock);
    usleep(100);
  } 
  return NULL;
}
//main
pthread_rwlock_init(&rwlock,NULL);//初始化互斥量
pthread_t wtids[3],rtids[5];
for(int i=0;i<3;i++){
  pthread_create(&wtids[i],NULL,writeNum,NULL);
}
for(int i=0;i<5;i++){
  pthread_create(&rtids[i],NULL,readNum,NULL);
}
//设置线程分离   ------> 省略，线程分离不需要传地址
//结束主线程 省略
pthread_rwlock_destroy(&rwlock);//销毁
```

### 条件变量

<pre><font size=3>  pthread_cond_t  ，某个条件满足后阻塞/解除线程
  init destroy 类似 更换关键词为 cond 即可
  int pthread_cond_wait(pthread_cond_t *restrict cond,pthread_mutex_t *restrict mutex); //等待 直至有信号
  功能：阻塞函数，线程会阻塞  <b>wait 会首先对互斥锁解释，当wait不阻塞时，又会对互斥锁加锁</b>
  int pthread_cond_timedwait(pthread_cond_t *restrict cond,pthread_mutex_t *restrict mutex,const struct timespec *restrict abstime);
  功能：等待多长时间，线程会阻塞，直至指定的时间结束
  int pthread_cond_signal(pthread_cond_t *cond);//唤醒等待的一个或多个
  int pthread_cond_broadcast(pthread_cond_t *cond);//唤醒所有等待的线程
</pre>

### 信号量

<pre><font size=3>  sem_t
  int sem_init(sem_t *sem,int pshared,unsigned int value);
    pshared：0 用在线程，非 0 用在进程
    value：信号量的值
  int sem_destroy(sem_y *sem);
  int sem_wait(sem_t *sem);// -1 信号量
  功能：对信号量加锁，信号量的值 -1，如果为 0 ，就阻塞
  int sem_trywait(sem_t *sem);
  int sem_timedwait(sem_t *sem,const struct timespec *abs_timeout);
  int sem_post(sem_T *sem);// +1 
  功能：对信号量解锁，信号量值 +1
  int sem_getvalue(sem_t *sem,int *sval);
</pre>

#### 生产者消费者

```cpp
pthread_mutex_t mutex;
//pthread_cond_t cond;//创建条件变量 直接使用信号量即可，条件变量只是一个过渡期
sem_t psem,csem;// 类似于模拟生产者消费者还有多少可以操作
struct node{
  int num;
  struct node *next;
}; 
struct node *head = NULL;
void *producer(void *arg){
  while(1){//创建新的节点
    sem_wait(&psem);
    pthread_mutex_lock(&mutex);
    struct node *newnode = (struct node *)malloc(sizeof(struct node));//不能写自动变量，写自动变量作用域结束后自动释放，本部分明显需要长期存在
    newnode->next = head;
    head = newnode;
    newnode->num = rand() % 1000;
    printf("add node, num : %d, tid : %ld\n",nownode->num,pthread_self());
    //只要生产一个就通知消费者消费
    //pthread_cond_signal(&cond);
    pthread_mutex_unlock(&mutex);
    sem_post(&csem);//对消费者 +1
    usleep(100);
  }
  return NULL;
}
void *customer(void *arg){
  while(1){
    sem_wait(&csem);
    pthread_mutex_lock(&mutex);
    struct node *tmp = head;
    if(head != NULL){
      head = head->next;
    	printf("del node ; num ： %d, tid : %d\n",tmp->num,pthread_self());
    	free(tmp);
    	pthread_mutex_unlock(&mutex);
    	usleep(100);
      sem_post(&psem);
    }
    //else {//没有数据、需要等待，并通知生产者生产 ---->  条件变量需要使用
    	//pthread_cond_wait(&cond,&mutex);//wait会首先对互斥锁解锁，wait不阻塞时会重新对其加锁
      //pthread_mutex_unlock(&mutex);
    //}
  }
  return NULL;
}
int main(){
  //使用链表实现，不考虑满，只考虑空
  pthread_mutex_init(&mutex,NULL);
  //pthread_cond_init(&cond,NULL);
  sem_init(&psem,0,8);//地址，线程，个数
  sem_init(&csem,0,0);
  pthread_t ptids[5],ctids[5];
  for(int i=0;i<5;i++){
    pthread_create(&ptids[i],NULL,producer,NULL);
    pthread_create(&ctids[i],NULL,customer,NULL);
  }
  //线程分离 pthread_detach(ptids[i]);...省略  detach 或 join 决定了 destroy 的写法，位置
  while(1){ //防止直接将mutex销毁
    sleep(10);
  }
  pthread_mutex_destroy(&mutex);//不能放exit后，exit后的代码不会执行，
  //注意上述destroy 如果使用的是join释放子线程则不用写while 因为join是阻塞的，子线程回收完才会继续
  //pthread_cond_destroy(&cond);
  pthread_exit(NULL);
  return 0;
}
```

```cpp
// while 版本
void *customer(void *arg) {
    while (1) {
        pthread_mutex_lock(&mutex);
        // 判断是否有数据
        while (head == NULL)
            // 当这个函数调用阻塞的时候，会对互斥锁进行解锁，当不阻塞的，继续向下执行，会重新加锁。
            pthread_cond_wait(&cond, &mutex);
        // 保存头结点的指针
        struct Node *tmp = head;
        assert(head != NULL);
        // 有数据
        head = head->next;
        printf("del node, num : %d, tid : %ld\n", tmp->num, pthread_self());
        free(tmp);
        pthread_mutex_unlock(&mutex);
        sleep(1);
    }
    return NULL;
}
```

## 网络结构模式

 <font size=3>[press](../项目学习/WebServer/4.1 网络基础.pdf)     <b>最重要的部分</b></font>

<pre><font size=3>  MAC 地址，48位，12个16进制数组成，网卡上唯一的标识
  ip 地址，为互联网上的每个主机或者网络分配一个逻辑地址，32位，点分十进制，网络ID，主机ID
  端口：2个字节，设备与外界通讯交流的出口，<b>虚拟端口</b>指计算机内部和交换机路由器内的端口，不可见，特指TCP/IP协议的端口，逻辑意义上的；<b>物理端口</b>又称接口，可见。端口是由端口号标记的，0-65535


![image-20250429111414074](./../../../../mine/nodeT/Images/image-20250429111414074-1747048688641-2.png)

### 网络通信

![网络通信的过程](./../../../../mine/nodeT/Images/%E7%BD%91%E7%BB%9C%E9%80%9A%E4%BF%A1%E7%9A%84%E8%BF%87%E7%A8%8B-1746621989850-3-1747048688641-3.png)

<pre><font size=3>  每层协议都将在上层数据的基础上加上自己的头部信息（有时还包括尾部信息），以实现该层的功能，这个过程称为 <b>封装</b>
  <b>分用</b> 当帧到达目的主机时，将沿着协议栈自底向上依次传递。各层协议依次处理帧中本层负责的头部数据，以获取所需的信息，并最终将处理后的帧交给目标应用程序。


![arp请求封装](./../../../../mine/nodeT/Images/arp%E8%AF%B7%E6%B1%82%E5%B0%81%E8%A3%85-1747048688641-4.png)

<pre><font size=3>  <b>ARP 协议：</b> 通过 ip 地址来查找 mac 地址，网管一般是最大的或最小可用的，例如：192.168.1.1 / 192.168.1.254  上述具体字节数和意义详见相关资料内的说明，ARP 报文需要加上以太网帧才能正确使用。ff:ff:ff:ff:ff:ff 表示给网站中所有的机器都发送请求。


### socket 

<font size=3>[press](../项目学习/WebServer/4.8 socket通信基础.pdf)  此处不在赘述，详细看pdf文件</font>

<pre><font size=3>  socket ：是对网络中不同主机上的应用进程之间进行双向通信的端点的抽象。上联应用进程，下联网络协议栈，是应用程序通过网络协议进行通信的<b>接口</b>，是应用程序与网络协议根进行交互的<b>接口</b>。socket 可以看作两个网络应用进行通信时，各自通信连接中的端点，逻辑上的概念，由 ip 地址和端口结合的，提供向应用层进程传送数据包的机制。<b>本质为</b>内核借助缓冲区形成的伪文件，用于网络进程间数据的通信。
  套接字通信分两部分：服务器：被动接受连接，一般不主动发连接 和 客户端：主动向服务器发起连接


####   字节序

<pre><font size=3>  字节的顺序，就是大于一个字节类型的数据在内存中的存放顺序，大端字节序是指一个整数的最高位字节(23-31bit)存储在内存的低地址处，低位字节存储在内存的高位地址，小段字节序反之。


```cpp
//通过代码检测当前主机的字节序
union {
  short value; // 2 字节
  char bytes[sizeof(short)];
} test；//联合体变量，联合体共享内存，size为成员最大的一个
test.value = 0x0102;
if(test.bytes[0] == 1 && test.bytes[1] == 2) {
  cout << "大端字节序" << endl;
}else if(test.bytes[0] == 2 && test.bytes[1] == 1) {
  cout << "小端字节序" << endl;
}
else cout << "未知" << endl;
```

##### 字节序转换函数

<pre><font size=3>  发送端总是把发送的数据转化为大端字节序数据后再发送，接收方自行判断是否转换。
  <b>网络字节序</b> ：TCP/IP规定的一种数据表示格式，与CPU、OS无关，采用大端排序方式。
  头文件：arpa/inet.h  uint16_t unsigned short   32 -> unsigned int  (long)
  uint16_t htons(uint16_t hostshort); // 主机转换为网络字节序
  uint16_t ntohs(uint16_t netshort);  // 网络转换为主机字节序 short 转换端口
  uint32_t htonl(uint32_t hostlong); // 主机转化为网络字节序 
  uint32_t ntohl(uint32_t netlong); // 网络转换为主机字节序  long 转化 ip


#####  socket 地址

<pre><font size=3>  结构体，封装端口号和 IP等信息，后面的 socket 相关的 api 中需要使用这个地址


![socket地址](./../../../../mine/nodeT/Images/socket%E5%9C%B0%E5%9D%80-1747048688641-1.png)

###   ip 地址转换

<pre><font size=3>  字符串 ip - 整数，主机、网络字节序的转换
  arpa/inet.h     , in_addr_t  --> unsigned int
  in_addr_t inet_addr(const char *cp);
  int inet_aton(const char *cp,struct in_addr *inp); 返回值 1 成功
  char *inet_ntoa(struct in_addr in);  // 以上 只适用于 ipv4 <br>
  int inet_pton(int af,const *src,void *dst);
  p：表示点分十进制的ip字符串，n：表示network，网络字节序的整数
  af：地址族：AF_INET- ipv4  AF_INET6
  src：需要转换的点分十进制的 ip 字符串
  dst：转换后的结果保存在这个里面
  const char *inet_ntop(int af,void *src,char *dst,socklen_t size);
  size：第三个参数的大小（数组的大小）
  返回值：返回转换后的数据的地址（字符串），和 dst 一致


```cpp
// 创建一个ip字符串,点分十进制的IP地址字符串
char buf[] = "192.168.1.4";
unsigned int num = 0;// 将点分十进制的IP字符串转换成网络字节序(大端字节序)的整数
inet_pton(AF_INET, buf, &num);
unsigned char * p = (unsigned char *)&num;
printf("%d %d %d %d\n", *p, *(p+1), *(p+2), *(p+3));// 192 168 1 4
char ip[16] = "";// 将网络字节序的IP整数转换成点分十进制的IP字符串
const char * str =  inet_ntop(AF_INET, &num, ip, 16);
printf("str : %s\n", str);// 192.168.1.4
printf("ip : %s\n", str);
printf("%d\n", ip == str);
```

### TCP 通信流程

<font size=3>[press](../项目学习/WebServer/4.8 socket通信基础.pdf)  此处不在赘述，详细看pdf文件</font>

<pre><font size=3>  UDP：用户数据报协议，面向无连接的，可以单播，多播，广播，面向数据报，不可靠。首部8字节
  TCP：传输控制协议，面向连接的，可靠的，基于字节流的传输协议，仅支持单播传输。首部最少20字节
  UDP 用在实时应用(会议、直播)   TCP：可靠性高得应用(文件传输)<br>
  服务器端
    1.创建一个用于监听的套接字。(监听：监听有客户端的连接；套接字：其实是一个文件描述符)
    2.将这个监听文件描述符和本地的 ip 和 端口 绑定(ip 和 端口 就是服务器的地址信息),客户端连接服务器的时候使用的就是这个 ip 和 端口
    3.设置监听，监听的 fd 开始工作
    4.解除阻塞，当有客户端发起连接，解除阻塞，接受客户端的连接，会得到一个和客户端通信的套接字(fd)
    5.通信：接收数据，发送数据
    6.通信结束，断开连接。


![TCP通信](./../../../../mine/nodeT/Images/TCP%E9%80%9A%E4%BF%A1-1747048688641-5.png)

#### 服务器端

```cpp
#include<arpa/inet.h> 
#include<unistd.h>
//1. 创建用于监听的套接字
int lfd = socket(AF_INET,SOCK_STREAM,0);//协议族，ipv4；流式协议(TCP)；0 默认TCP协议
struct sockaddr_in saddr; //2. 绑定
saddr.sin_family = AF_INET;
inet_pton(AF_INET,"192.168.94.1",asddr.sin_addr.s_addr);//ip 地址，十进制转化为网络字节序
//asddr.sin_addr.s_addr = 0；//表示本机的任意 ip INADDR_ANY ，只有服务端可以这样
saddr.sin_port = htons(9999); // 主机字节序转换为 network
int ret = bind(lfd,(struct sockaddr *)&saddr,sizeof(saddr));
ret = listen(lfd,8); // 3.监听
struct sockaddr_in clientaddr; //4. 接受客户端连接
int len = sizeof(clientaddr);
int cfd = accept(lfd,(struct sockaddr *)&clientaddr,&len); // 客户端的文件描述符
char clientIP[16]; // 输出客户端的信息
inet_ntop(AF_INET,&clientaddr.sin_addr.s_addr,clientIP,sizeof(clientIP));
unsigned short clientPort = ntohs(clientaddr.sin_poet);
printf("client ip is %s,port is %d\n",clientIP,clientPort);
char recvBuf[1024] = {0};// 5.获取客户端的数码
int len1 = read(cfd,recvBuf,sizeof(recvBuf));
if(len1 > 0)
  printf(“recv client data : %s\n",recvBuf);
else if(len1 == 0) {
  printf("client closed...\n");
}
char *data = "hello xxxxx";//给客户端发送
write(cfd,data,strlen(data));
close(cfd);clode(lfd);
```

#### 客户端

```cpp
int fd = socket(AF_INET,SOCK_STREAM,0); //1. socket
struct sockaddr_in serveraddr;// 2. 连接 服务器端
serveraddr.sin_family - AF_INET; 
inet_pton(AF_INET,"192.168.x.x",&serveraddr.sin_addr.s_addr);
serveraddr.sin_port = htons(9999);//对应住
connet(fd,(struct sockaddr *)&serveraddr,sizeof(serveraddr));
char *data = "hello i am client";
write(fd,data,strlen(data)); // 写数据
char recvBuf[1024] = {0};// 5.获取客户端的数码
int len = read(fd,recvBuf,sizeof(recvBuf)); // 读服务端数据
if(len > 0)
  printf(“recv server data : %s\n",recvBuf);
else if(len == 0) {
  printf("server closed...\n");
}
```

### TCP 三次握手

<font size=3>[press](../项目学习/WebServer/4.8 socket通信基础.pdf)     [图解](../../nodeT/项目学习/WebServer/4.17 TCP三次握手四次挥手.pdf)</font>

<pre><font size=3>  三次握手的<b>目的</b>是保证双方建立了连接。发生在客户端连接的时候。调用connet()时，底层会通过 TCP 协议进行三次握手
  只有收到的是 SYN 或者 FIN 时，确认序号 sack 才会加一，其余情况加上具体的数据
  第一次握手：
    1.客户端将 SYN 标志位置为 1
    2.生成一个随机的 32 位的序号 seq = j，这个序号后边是可以携带数据(数据的大小)
  第二次握手：
    1.服务器端接收客户端的连接：ACK = 1
    2.服务器会回发一个确认序号：ack = 客户端的序号 + 数据长度 + SYN/FIN(按一个字节算)
    3.服务器会向客户端发起连接请求：SYN = 1
    4.服务器会生成一个随机序号：seq = k
  第三次握手：
    1.客户端应答服务器的连接请求，ACK = 1
    2.客户端回复收到了服务器端的数据：ack = 服务端的序号 + 数据长度 + SYN/FIN(按一个字节算)


### 滑动窗口

<pre><font size=3>  滑动窗口：流量控制技术，允许发送方在接收任何应答之前传送附加的包。接收方告诉发送方在某一时刻能发送多少包(称窗口尺寸)。
  TCP 中采用滑动窗口来进行传输控制，滑动窗口的大小意味着接收方还有多大的缓冲区可以用于接收数据。发送方可以通过滑动窗口的大小来确定应该发送多少字节的数据。当滑动窗口为 0时，发送方一般不能再发送数据报。
  滑动窗口理解为缓冲区的大小
  滑动窗口的大小会随着发送数据和接收数据而发生变化，通信的双方都有发送缓冲区和接收缓冲区
### 多进程实现并发服务器


```cpp
void recyleChild(int arg){
  while(1){
    int ret = waitpid(-1,NULL,WHNOHANG);//回收所有的子进程 并且设置为非阻塞
    if(ret == -1) {
      break;//所有的子进程都回收完了
    }
    else if(ret == 0){
      break;//还有子进程或者
    }else if(ret > 0){
      printf("子进程 %d 被回收了\n",ret);
    }
  }
}
int main() {
  //注册信号捕捉，实现非阻塞的情况下回收子进程资源
  struct sigaction act;
  act.sa_flag = 0;
  sigemptyset(&act.sa_mask);
  act.sa_handler = recyleChild;
  sigaction(SIGCHLD,&act,NULL);
  int lfd = socket(PF_INET,SOCK_STREAM,0); // 创建 socket
  if(lfd == -1) {...}
  struct sockaddr_in saddr; // 绑定
  saddr.sin_family = AF_INET;
  saddr.sin_port = htons(9999);
  saddr.sin_addr.s_addr = INADDR_ANY; // 0
  int ret = bind(lfd,(struct sockaddr *)&saddr,sizeof(saddr));
  if(ret == -1){}
  ret = listen(lfd,128) ;//监听
  if(ret == -1){}
  while(1) { //循环等待客服端连接
    struct sockaddr_in  cliaddr;
    int len = sizeof(cliaddr);
    int cfd = accept(lfd,(struct sockaddr*)&cliaddr,&len);//接受客户端连接
    if(cfd == -1){ //回收子进程资源后，如果信号在有效的连接之前一个信号被捕捉会报错
      if(errno == EINTR){
        continue;
      }
      exit(-1);
    } 
    pid_t pid = fork();
    if(pid == 0){ // 子进程
      char cliIP[16]; // 获取客户端信息
      inet_ntop(AF_INET,&cliaddr.sin_addr.s_addr,cliIP,sizeof(cliIP));//获取 ip
      unsigned short cliPort = ntohs(cliaddr.sin_port);
      printf("client ip is : %s ,port is %d\n",cliIP,cliPort);
      char recvBuf[1024] = {0};//接受客户端发来的数据
      while(1){
        int len = read(cfd,&recvBuf,sizeof(recvBuf));
        if(len == -1){}
        else if(len > 0) {
          printf("recv client data : %s\n",recvBuf);
        }
        else if(len == 0) {
          printf("client closed...\n");
        }
        write(cfd,recvBuf,strlen(recvBuf));
      }
      close（cfd);
      exit(0);//退出当前子进程
    }
  }
  close(lfd);
}
//client
int main() {
    // 1.创建套接字
    int fd = socket(AF_INET, SOCK_STREAM, 0);
    if(fd == -1) {
        perror("socket");
        exit(-1);
    }
    // 2.连接服务器端
    struct sockaddr_in serveraddr;
    serveraddr.sin_family = AF_INET;
    inet_pton(AF_INET, "192.168.193.128", &serveraddr.sin_addr.s_addr);
    serveraddr.sin_port = htons(9999);
    int ret = connect(fd, (struct sockaddr *)&serveraddr, sizeof(serveraddr));
    if(ret == -1) {
        perror("connect");
        exit(-1);
    }
    // 3. 通信
    char recvBuf[1024] = {0};
    int i = 0;
    while(1) {   
        sprintf(recvBuf, "data : %d\n", i++);        
        // 给服务器端发送数据
        write(fd, recvBuf, strlen(recvBuf)+1);
        int len = read(fd, recvBuf, sizeof(recvBuf));
        if(len == -1) {
            perror("read");
            exit(-1);
        } else if(len > 0) {
            printf("recv server : %s\n", recvBuf);
        } else if(len == 0) {
            // 表示服务器端断开连接
            printf("server closed...");
            break;
        }
        sleep(1);
    }
    close(fd);
    return 0;
}
```

### 多线程实现并发服务器

```cpp
struct sockInfo {
  int fd;//通信的文件描述符
  pthread_t tid;
  struct sockaddr_in addr;
};
struct sockInfo sockinfos[128];//表示最多支持128个客户端同时连接进来
void *working(void *arg){//子线程和客户端通信  cfd，客户端的信息 tid
  struct sockInfo *pinfo = (struct sockInfo *) arg;
  char cliIP[16]; // 获取客户端信息
  inet_ntop(AF_INET,&pinfo->addr.sin_addr.s_addr,cliIP,sizeof(cliIP));//获取 ip
  unsigned short cliPort = ntohs(pinfo->addr.sin_port);
  printf("client ip is : %s ,port is %d\n",cliIP,cliPort);
  char recvBuf[1024] = {0};//接受客户端发来的数据
  while(1){
    int len = read(pinfo->fd,&recvBuf,sizeof(recvBuf));
    if(len == -1){}
    else if(len > 0) {
      printf("recv client data : %s\n",recvBuf);
    }
    else if(len == 0) {
      printf("client closed...\n");
    }
    write(pinfo->fd,recvBuf,strlen(recvBuf));
  }
  close（pinfo->fd);
  exit(0);//退出当前子进程
}
int main() {
  int lfd = socket(PF_INET,SOCK_STREAM,0); // 创建 socket
  if(lfd == -1) {...}
  struct sockaddr_in saddr; // 绑定
  saddr.sin_family = AF_INET;
  saddr.sin_port = htons(9999);
  saddr.sin_addr.s_addr = INADDR_ANY; // 0
  int ret = bind(lfd,(struct sockaddr *)&saddr,sizeof(saddr));
  if(ret == -1){}
  ret = listen(lfd,128) ;//监听
  if(ret == -1){}
  //初始化数据 sockinfos
  int max = sizeof(sockinfos) / sizeof(sockinfos[0]);
  for(int i=0;i<max;i++){
    bzero(&sockinfos[i],sizeof(sockinfos[i]))；//初始化为 0
    sockinfos[i].fd = -1;// -1 是无效的文件描述符，表示此时还可用
    sockinfos[i].pid = -1;
  }
  while(1){//循环等待，创建子线程
    struct sockaddr_in cliaddr;
    int len = sizeof(cliaddr);
    int cfd = accept(lfd,(struct sockaddr*)&cliaddr,&len);
    //pthread_t tid;//tid 只有在线程创建之后才有具体的值
    struct sockInfo *pinfo; // 防止局部变量结束后被直接释放,防止循环后子线程仍在继续
    for(int i=0;i<max;i++){ // 找到一个可用的 sockInfo
      if(sockinfos[i].fd == -1){
        pinfo = &sockinfos[i];
        break;
      }
    }
    pinfo->fd = cfd;
    memcpy(&pinfo->addr,&cliaddr,len);
    pthread_create(&pinfo->tid,NULL,working,pinfo);//第4个参数是 working 需要的参数
    pthread_detach(pinfo->tid);//设置线程分离，结束时自己释放资源
  }
  close(lfd);
}
//client 代码不变
```

<pre><span style="font-size: 16px">  关于进程和线程中局部变量的不同：由于子进程读时共享，写时拷贝，拷贝后，子进程有自己的虚拟资源地址，会将 cfd 复制到自己的地址中，故此时即便循环结束局部变量被释放，子进程并没有结束的情况下，子进程由于有自己的地址保存数据并不会出现问题。
  对于线程，由于父子线程共享用户数据区，栈区间是隔离的，每个线程分一部分，堆区间是共享的，由于局部变量存在栈区间中，如果使用普通变量那么循环结束时，相关的数据会直接被释放，如果此时子线程并未结束就会出现问题，所以使用全局变量来接受循环中的数据，存在堆区间中，在子线程使用的过程中都不会被释放，直至子线程回收资源

### 端口复用

<pre><span style = "font-size:16px">  1.防止服务器重启时之前绑定的端口还未释放
  2.程序突然退出而系统未释放端口

```cpp
int main(int argc, char *argv[]) {
    // 创建socket
    int lfd = socket(PF_INET, SOCK_STREAM, 0);
    if(lfd == -1) {}
    struct sockaddr_in saddr;
    saddr.sin_family = AF_INET;
    saddr.sin_addr.s_addr = INADDR_ANY;
    saddr.sin_port = htons(9999);
    int optval = 1;
    setsockopt(lfd, SOL_SOCKET, SO_REUSEPORT, &optval, sizeof(optval));
    // 绑定
    int ret = bind(lfd, (struct sockaddr *)&saddr, sizeof(saddr));
    if(ret == -1) {}
    ret = listen(lfd, 8);
    if(ret == -1) {}
    // 接收客户端连接
    struct sockaddr_in cliaddr;
    socklen_t len = sizeof(cliaddr);
    int cfd = accept(lfd, (struct sockaddr *)&cliaddr, &len);
    if(cfd == -1) {}
    // 获取客户端信息
    char cliIp[16];
    inet_ntop(AF_INET, &cliaddr.sin_addr.s_addr, cliIp, sizeof(cliIp));
    unsigned short cliPort = ntohs(cliaddr.sin_port);
    printf("client's ip is %s, and port is %d\n", cliIp, cliPort );
    char recvBuf[1024] = {0};
    while(1) {
        int len = recv(cfd, recvBuf, sizeof(recvBuf), 0);
        if(len == -1) {
        } else if(len == 0) {
            printf("客户端已经断开连接...\n");
            break;
        } else if(len > 0) 
            printf("read buf = %s\n", recvBuf);
        for(int i = 0; i < len; ++i) 
            recvBuf[i] = toupper(recvBuf[i]);
        printf("after buf = %s\n", recvBuf);
        // 大写字符串发给客户端
        ret = send(cfd, recvBuf, strlen(recvBuf) + 1, 0);
        if(ret == -1) {}
    }
    close(cfd);
    close(lfd);
}
//client
int main() {
    int fd = socket(PF_INET, SOCK_STREAM, 0);
    if(fd == -1) {
        perror("socket");
        return -1;
    }
    struct sockaddr_in seraddr;
    inet_pton(AF_INET, "127.0.0.1", &seraddr.sin_addr.s_addr);
    seraddr.sin_family = AF_INET;
    seraddr.sin_port = htons(9999);
    // 连接服务器
    int ret = connect(fd, (struct sockaddr *)&seraddr, sizeof(seraddr));
    if(ret == -1){}
    while(1) {
        char sendBuf[1024] = {0};
        fgets(sendBuf, sizeof(sendBuf), stdin);//阻塞的
        write(fd, sendBuf, strlen(sendBuf) + 1);
        int len = read(fd, sendBuf, sizeof(sendBuf));//接受
        if(len == -1) {
        }else if(len > 0) {
            printf("read buf = %s\n", sendBuf);
        } else {
            printf("服务器已经断开连接...\n");
            break;
        }
    }
    close(fd);
}
```

### I/O 多路复用

<span style= "font-size:16px"> [press](../../nodeT/项目学习/WebServer/4.25 IO多路复用.pdf)                       [图解](../../nodeT/项目学习/WebServer/4.25 IO多路复用图解.pdf) </span>

<pre><span style="font-size:16px">  I/O 多路复用使得程序能同时及监听多个文件描述符，<b>能提高程序的性能</b>

#### select 实现多客户端的连接

```cpp
int main() {
  int lfd = socket(PF_INET,SOCK_STREAM,0);//创建 socket
  struct sockaddr_in saddr;
  saddr.sin_port = htons(9999);
  saddr.sin_family = AF_INET;
  saddr.sin_addr.s_addr = INADDR_ANY;//所有网卡的 ip
  bind(lfd,(struct sockaddr*)&saddr,sizeof(saddr));
  listen(lfd,8);
  fd_set rdset,tmp;//创建一个fd_set集合，存放的是需要检测的文件描述符，最多1024个
  FD_ZERO(&rdset);
  FD_SET(lfd,&rdset);// 将监听的文件描述符加入 rdset
  int maxfd = lfd;
  while(1) {
    tmp = rdset;
    int ret = select(maxfd + 1,&tmp,NULL,NULL,NULL);//调用函数，内核检测哪些文件描述符有数据
    if(ret == -1){}
    else if(ret == 0){
      continue;//实际不存在，因为是阻塞的，在有数据前一直都在阻塞中
    }else if(ret > 0){
      //说明检测到了有文件描述符对应的缓冲区的数据发生了改变
      if(FD_ISSET(lfd,&tmp)){//表示有新的客户端连接进来了，lfd表示监听的文件描述符
        struct sockaddr_in cliaddr;
        int len = sizeof(cliaddr);
        int cfd = accept(lfd,(struct sockaddr*)&cliaddr,&len);
        FD_SET(cfd,&rdset);//将新的文件描述符加入到集合中
        maxfd = maxfd > cfd ? maxfd : cfd;
      }
      for(int i=lfd+1;i<=maxfd;i++){
        if(FD_ISSET(i,&tmp)){//说明文件对应客户端发来了数据
          char buf[1024] = {0};
          int len = read(i,buf,sizeof(buf));
          if(len == -1){}
          else if(len == 0){//对方断开
            printf("client closed...\n");
            close(i);
            FD_CLR(i,&rdset);
          }else if(len > 0){
            printf("read buf = %s\n",buf);
            write(i,buf,strlen(buf) + 1);
          }
        }
      }
    }
  }
  close(lfd);
}
```

<pre><span style="font-size:16px">1. select 函数和监听套接字
  编程中，select 函数用于等待一个或多个文件描述符（在这里是套接字）的状态变化。对于服务器端的监听套接字（lfd），当有客户端尝试连接时，这个监听套接字的状态就会发生变化。
  监听套接字的作用是等待客户端的连接请求。当客户端通过 connect 函数向服务器发起连接时，服务器的监听套接字就会收到这个连接请求，并且会进入连接队列（也称为未完成连接队列）。
2. FD_ISSET 检测监听套接字可读的原因
  在服务器调用 select 函数之前，监听套接字会被设置为监听状态。当 select 函数检测到监听套接字可读时，其实并不是因为有数据到达（像普通通信套接字那样有数据可读），而是因为连接队列中有新的连接请求等待处理。此时，监听套接字的状态变化是由于有新的客户端连接请求的到来。
3. 与 accept 的关系
  只有通过 accept 函数，服务器才会从连接队列中取出一个连接请求，完成三次握手（在 TCP 协议下），建立一个新的通信套接字，用于和客户端进行实际的数据通信。这个新建立的通信套接字才会用于后续的数据收发操作。
#### poll

<span style= "font-size:16px"> [press](../../nodeT/项目学习/WebServer/4.25 IO多路复用.pdf)                                  [图解](../../nodeT/项目学习/WebServer/4.25 IO多路复用图解.pdf) </span>

```cpp
listen(lfd,8);
struct pollfd fds[1024];//初始化检测的文件描述符数组
for(int i=0;i<1024;i++){
  fds[i].fd = -1;
  fds[i].events = POLLIN;//检测读事件 
}
fds[0].fd = lfd; // 0 表示监听的文件描述符
int nfds = 0;
while(1)
{
  int ret = pll(fds,nfds + 1,-1);//-1 表示阻塞，只有有状态变化才会解除阻塞
  if(ret == -1){}
  else if(ret == 0) continue;
  else 
  {
    if(fds[0].revents & POLLIN == POLLIN)
    {
      struct sockaddr_in cliaddr;
      int len = sizeof(cliaddr);
      int cfd = accept(lfd,(struct sockaddr*)&cliaddr,&len);
      for(int i=1;i<1024;i++)
      {
        if(fds[i].fd == -1)
        {
          fds[i].fd = cfd;
          fds[i].events = POLLIN;
          break;
        }
      }
      nfds = nfds > cfd ? nfds : cfd;
    }
    for(int i=1,i<=nfds;i++)
    {
      if(fds[i].revents & POLLIN)
      {
        char buf[1024] = {0};
        int len = read(fds[i].fd,buf,sizeof(buf));
        if(len == -1){}
        else if(len == 0){
         	printf("client closed ...\n");
          close(fds[i].fd);
          fds[i].fd = -1;
        }
        else if(len > 0)
        {
          printf("read buf = %s\n",buf);
          write(fds[i].fd,buf,strlen(buf) + 1);
        }
      }
    }
  }
}
```

#### epoll

<span style= "font-size:16px"> [press](../../nodeT/项目学习/WebServer/4.25 IO多路复用.pdf)                          [图解](../../nodeT/项目学习/WebServer/4.25 IO多路复用图解.pdf) </span>

```cpp
listen(lfd,8);
int epfd = epoll_create(100);//调用 epoll_create创建一个 epoll 实例
struct epoll_event epev;
epev.events = EPOLLIN;
epev.data.fd = lfd;
epoll_ctl(cpfd,EPOLL_CTL_ADD,lfd,&epev);
struct epoll_event epevs[1024];
while(1) {
  int ret = epoll_wait(epfd,epevs,1024,-1);
  if(ret == -1){}
  printf("res = %d\n",ret); //返回的是多少个
  for(int i=0;i<ret;i++) {
    if(epevs[i].data.fd == lfd) { //监听的文件描述符,有客户端连接
      struct sockaddr_in cliaddr;
      int len = sizeof(cliaddr);
      int cfd = accept(lfd,(struct sockaddr*)&cliaddr,&len);
      epev.events = EPOLLIN;
      epev.data.fd = cfd;
      epoll_ctl(epfd,EPOLL_CTL_ADD,cfd,&epev);
    }
    else { //有数据到达 需要通信
      if(epevs[i].events & EPOLLOUT) continue;//目前忽略out
      char buf[1024] = {0};
      int len = read(epevs[i].data.fd,buf,sizeof(buf));
      if(len == -1){}
      else if(len == 0){
        printf("client closed ...\n");
        epoll_ctl(epfd,EPOLL_CTL_DEL,epevs[i].data.fd,NULL);
        close(epevs[i].data.fd);
      }
      else if(len > 0)
      {
        printf("read buf = %s\n",buf);
        write(epevs[i].data.fd,buf,strlen(buf) + 1);
      }
    }
  }
  close(lfd);close(epfd);
}
```

##### epoll 工作模式

```cpp
//server_lt    client使用fget输入字符串
//只需要读数据的时候更换 char 数组的大小即可 ，会一直读，直至缓冲区数据完全完毕
char buf[5];//会一直通知，直至缓冲区无数据 

//server_et  边沿触发不是默认的，需要手动设置  EPOLLET  
int cfd = accept(lfd,(struct sockaddr*)&cliaddr,&len);
int flag = fcntl(cfd,F_GETFL);
flag |= O_NONBLOCK;//read是否阻塞和传入的 fd 的文件描述符一致
fcntl(cfd,F_SETFL,flag);//将文件描述符设置为非阻塞，防止read阻塞 
epev.events = EPOLLIN | EPOLLET; //事件加上 EPOLLET  只会通知一次 
....
else {
  if(epevs[i].events & EPOLLOUT) continue;//目前忽略out
  char buf[5] = {0};
  int len = 0;
  while((len = read(epevs[i].data.fd,buf,sizeof(buf))) > 0) { 
  	printf("recv data : %s\n",buf);
    //write(STDOUT_FILENO,buf,len);//直接写道终端 
    write(epevs[i].data.fd,buf,len);
  }
  if(len == 0) {
    printf("client closed ...\n");
  }else if(len == -1){
    if(errno == EAGAIN){//读到缓冲区尾部
      printf("data over...\n");
    }else {exit(-1);}
  }//read在信号前被打断时，会报错 EINIR；数据读完也会返回-1(EAGAIN)
}

```

### UDP 通信

<span style="font-size:16px">[press](../../nodeT/项目学习/WebServer/4.32 UDP通信、本地套接字.pdf)   UDP 不需要多线程或者多进程就可以实现多个 client 和 server 通信</span>

```cpp
//server
int main() {
  int fd = socket(PF_INET,SOCK_DGRAM,0);//UDP使用数据报协议
  if(fd == -1){}
  struct sockaddr_in addr;
  addr.sin_family = AF_INET;
  addr.sin_port = htons(9999);
  addr.sin_addr.s_addr = INADDR_ANY;
  int ret = bind(fd,(struct sockaddr*)&addr,sizeof(addr));
  if(ret == -1){}
  while(1) {
    char recvbuf[128] = {0};
    char ipbuf[16] = {0};
    struct sockaddr_in cliaddr;
    int len = sizeof(cliaddr);
    int num = recvfrom(fd,buf,sizeof(buf),0,(struct sockaddr*)&cliaddr,&len);//接收数据
    if(num == -1){}
    printf("client IP : %s ,Port : %d\n",inet_ntop(AF_INET,&cliadddr.sin_addr.s_addr,ipbuf,sizeof(ipbuf)),ntohs(cliaddr.sin_port));
    printf("client say : %s\n",recvbuf);
    sendto(fd,recvbuf,strlen(recvbuf) + 1,0,(struct sockaddr*)&cliaddr,sizeof(cliaddr));//发送数据
  }
  close(fd);
}
//client
int main() {
  int fd = socket(PF_INET,SOCK_DGRAM,0);//UDP使用数据报协议
  if(fd == -1){}
  struct sockaddr_in saddr;//服务器的地址信息
  saddr.sin_family = AF_INET;
  saddr.sin_port = htons(9999);
  inet_pton(AF_INET,"127.0.0.1",&saddr.sin_addr.s_addr);
  if(ret == -1){}
  int num = 0;
  while(1) { 
  	char sendbuf[128] = {0};
    sprintf(sendbuf,"hello,i am client %d\n",num ++ );
    sendto(fd,sendbuf,strlen(sendbuf) + 1,0,(struct sockaddr*)&saddr,len);
    int num = recvfrom(fd,sendbuf,sizeof(sendbuf),0,NULL,NULL);//不用传服务器的地址
    printf("server say : %s\n",snedbuf);
  }
}
```

### 广播

<span style="font-size:16px">[press](../../nodeT/项目学习/WebServer/4.32 UDP通信、本地套接字.pdf)    广播只能在局域网中使用，主机位全 1 代表广播</span>

```cpp
//server   广播时 服务端作为发送数据方
int main() {
  int fd = socket(PF_INET,SOCK_DGRAM,0);//UDP使用数据报协议
  if(fd == -1){}
  //2.设置广播属性
  int op = 1;//允许广播
  setsockopt(fd,SOL_SOCKET,SO_BROADCAST,&op,sizeof(op));
  struct sockaddr_in cliaddr;//3.创建一个广播地址
  cliaddr.sin_family = AF_INET;
  cliaddr.sin_port = htons(9999);
  inet_pton(AF_INET,"102.168.xxx.255",&cliaddr.sin_addr.s_addr);
  int num = 0;
  while(1) {//4.通信
    char sendbuf[128] = {0};
    sprintf(sendbuf,"hello，client... %d\n",num ++ );
    sendto(fd,sendbuf,strlen(sendbuf) + 1,0,(struct sockaddr*)&cliaddr,sizeof(cliaddr));//发送数据
    printf("广播的数据 :%s\n",sendbuf); 
  }
  close(fd);
}
//client
int main() {
  int fd = socket(PF_INET,SOCK_DGRAM,0);//UDP使用数据报协议
  if(fd == -1){}
  struct sockaddr_in addr;//客户端绑定本地ip和端口
  addr.sin_family = AF_INET;
  addr.sin_port = htons(9999);
  addr.sin_addr.s_addr = INADDR_ANY;//绑定本机 ip
  bind(fd,(struct sockaddr*)&addr,sizeof(addr));
  if(ret == -1){}
  while(1) { 
  	char buf[128] = {0};
    int num = recvfrom(fd,buf,sizeof(buf),0,NULL,NULL);//不用传服务器的地址
    printf("server say : %s\n",buf);
  }
}
```

<pre><span style="font-size:16px">  Xshell 会在后台为每个会话分配不同的源端口，即使目标端口可能相同。而 UDP 的实现中，源端口和目标端口是直接绑定的，无法通过这种方式区分不同的会话。
  TCP 使用四元组（源 IP、源端口、目标 IP、目标端口）来区分不同的连接，因此即使两个会话使用相同的源端口和目标端口，只要源 IP 不同或者目标 IP 不同，它们就可以同时存在。而 UDP 是无连接的，它依赖于端口来区分不同的会话。如果一个端口已经被某个会话绑定，其他会话无法再绑定到同一个端口上，因此会提示端口被占用。在UDP通信中，同一端口只能被一个应用程序用于发送或接收数据，除非使用特殊的选项（如SO_REUSEADDR）来允许端口复用。</span>

### 组播

```cpp
//server   广播时 服务端作为发送数据方
int main() {
  int fd = socket(PF_INET,SOCK_DGRAM,0);//UDP使用数据报协议
  if(fd == -1){}
  //2.设置多播属性
  struct in_addr imr_multiaddr;
  inet_pton(AF_INET,"239.0.0.10",&imr_multiaddr.s_addr);//初始化多播地址
  setsockopt(fd,IPPROTO_IP,IP_MULTICAST_IF,&&imr_multiaddr.s_addr,sizeof(&imr_multiaddr.s_addr));
  struct sockaddr_in cliaddr;//3.初始化客户端的地址信息
  cliaddr.sin_family = AF_INET;
  cliaddr.sin_port = htons(9999);
  inet_pton(AF_INET,"239.0.0.10",&cliaddr.sin_addr.s_addr);
  int num = 0;
  while(1) {//4.通信
    char sendbuf[128] = {0};
    sprintf(sendbuf,"hello，client... %d\n",num ++ );
    sendto(fd,sendbuf,strlen(sendbuf) + 1,0,(struct sockaddr*)&cliaddr,sizeof(cliaddr));//发送数据
    printf("多播的数据 :%s\n",sendbuf); 
  }
  close(fd);
}
//client
int main() {
  int fd = socket(PF_INET,SOCK_DGRAM,0);//UDP使用数据报协议
  if(fd == -1){}
  struct sockaddr_in addr;//客户端绑定本地ip和端口
  addr.sin_family = AF_INET;
  addr.sin_port = htons(9999);
  addr.sin_addr.s_addr = INADDR_ANY;//绑定本机 ip
  bind(fd,(struct sockaddr*)&addr,sizeof(addr));
  if(ret == -1){}
  //通信前要加入多播组
  struct ip_mreq op;
  inet_pton(AF_INET,"239.0.0.10",&op.imr_multiaddr.s_addr);
  op.imr_interface.s_addr = INADDR_ANY;
  setsockopt(fd,IPPROTO_IP,IP_ADD_MEMBERSHIP,&op,sizeof(op));
  while(1) { 
  	char buf[128] = {0};
    int num = recvfrom(fd,buf,sizeof(buf),0,NULL,NULL);//不用传服务器的地址
    printf("server say : %s\n",buf);
  }
}
```

### 本地套接字

```cpp
unlink("server.sock");//加上后不必在终端内手动 rm 删除sock文件，client中同理
//1.创建监听的套接字
int lfd = socket(AF_LOCAL,SOCK_STREAM,0);
//2.绑定本地套接字文件
struct sockaddr_un addr;
addr.sin_family = AF_LOCAL;
strcpy(addr.sun_path,"server.sock");//文件只能绑定一次，存在文件后除非产出sock文件否则无法再次运行可执行文件
int ret = bind(lfd,(struct sockaddr*)&addr,sizeof(addr));
//3.监听
ret = listen(lfd,100);
//4.等待客户端连接
struct sockaddr_un cliaddr;
int len = sizeof(cliaddr);
int cfd = accept(lfd,&cliaddr,&len);
printf("client socket filename: %s\n"，cliaddr.sun_path);
//5.通信
while(1) {
  char buf[128] = {0};
  int len = recv(cfd,buf,sizeof(buf),0);
  if(len == -1) {}
  else if(len == 0) {
    printf("client closed ...\n");
    break;
  }
  else if(len > 0) {
    printf("client say %s\n",buf);
    send(cfd,buf,len,0);
  }
  close(cfd);close(lfd);
}
//client
//1.创建套接字
int cfd = socket(AF_LOCAL,SOCK_STREAM,0);
//2.绑定本地套接字文件
struct sockaddr_un addr;
addr.sin_family = AF_LOCAL;
strcpy(addr.sun_path,"client.sock");
int ret = bind(cfd,(struct sockaddr*)&addr,sizeof(addr));
//3.主动连接服务器
struct sockaddr_un seraddr;
seraddr.sun_family = AF_LOACL;
strcpy(seraddr.sun_path,"server.sock");//指定socket文件描述符
connect(cfd,(struct sockaddr*)&seraddr,sizeof(seraddr));
int num = 0;
while(1) {
  char buf[128] = {0};
  sprintf(buf,"hello,i am client %d\n",num ++ );
  send(cfd,buf,strlen(buf) + 1,0);
  printf("client say : %s\n",buf);
  int len = recv(cfd,buf,sizeof(buf),0);
  if(len == -1) {}
  else if(len == 0){
    printf("server closed...\n");
    break;
  }
  else if(len > 0) { 
  	printf("server say : %s\n",buf);
  }
}
```

## 项目实战

<span style="font-size:16px">[press](../../nodeT/项目学习/WebServer/5.1 项目实战.pdf)            [图解](../../nodeT/项目学习/WebServer/5.1 阻塞、非阻塞、同步、异步.png)</span>

### 阻塞/非阻塞、同步/异步

<pre><span style="font-size:16px">  阻塞和非阻塞是描述操作行为的术语，主要和资源的占用情况有关；同步和异步则是从时间顺序和流程控制的角度来描述任务的执行方式。
1. 阻塞（Blocking）
   定义 ：阻塞是指在程序执行过程中，当一个操作（如 I/O 操作、等待资源等）发生时，调用该操作的线程会被挂起，直到该操作完成才能继续执行后续代码。
   特点
      阻塞操作会使得线程进入等待状态，不能有效地利用 CPU 时间；在阻塞期间，线程无法响应其他事件，这可能会导致程序的响应性变差。 
2. 非阻塞（Non Blocking）
   定义 ：非阻塞操作允许调用操作的线程在发起操作后继续执行其他任务，而不需要等待操作完成。在这种情况下，线程可以利用等待操作完成的时间来执行其他有用的工作，从而提高 CPU 利用率。
   特点
      非阻塞操作能够充分利用线程的执行时间，提高的程序并发性能；需要程序提供机制来检查操作的完成情况，例如轮询或者使用事件通知等机制。
3. 同步（Synchronous）
   定义 ：同步是指多个线程进程或者之间按照一定的顺序执行，通常通过共享变量、信号量等机制来协调它们的执行顺序。在同步操作中，一个线程的执行往往需要等待其他线程完成某些操作或者达到某种状态才能继续。
   特点
      同步操作可以保证线程之间的执行顺序，确保数据的一致性和正确性；但是同步可能会导致线程等待，降低程序的并发性能。
4. 异步（Asynchronous）
   定义 ：异步是指线程之间不需要按照严格的顺序执行，每个线程可以独立地执行自己的任务，而不需要等待其他线程完成特定操作。在这种模式下，线程之间通过事件通知、回调函数等方式来相互协作。
   特点
      异步操作可以使程序能够同时处理多个任务，提高并发能力；异步编程模式通常需要更复杂的逻辑来处理任务完成后的结果，因为任务的完成时间是不确定的。<b>异步和非阻塞结合，提高效率</b>
  总结来说，阻塞和非阻塞主要是针对操作在执行过程中对线程状态的影响；同步和异步是关于线程或进程之间如何协作和协调执行顺序。</span></pre>

### 常见的IO模型

<pre><span style="font-size:16px"> 以下是对阻塞（Blocking）、非阻塞（Non-blocking）、IO复用（IO Multiplexing）、信号驱动（Signal driven）和异步（Asynchronous）的总结：
1. 阻塞（Blocking）
  定义：阻塞是指在程序执行过程中，当一个操作（如 I/O 操作、等待资源等）发生时，调用该操作的线程会被挂起，直到该操作完成才能继续执行后续代码。
  特点：阻塞操作会使得线程进入等待状态，不能有效地利用 CPU 时间，在阻塞期间，线程无法响应其他事件，这可能会导致程序的响应性变差。
2. 非阻塞（Non Blocking）
  定义：非阻塞操作允许调用操作的线程在发起操作后继续执行其他任务，而不需要等待操作完成。
  特点：非阻塞操作能够充分利用线程的执行时间，提高程序的并发性能。通常需要程序提供机制来检查操作的完成情况，例如轮询或者使用事件通知等机制。
3. IO 复用（IO Multiplexing）
  定义：IO 复用是一种高效的 I/O 处理方式，它允许一个线程同时管理多个 I/O 操作。通过 IO 复用，线程可以等待多个 I/O 事件中的任何一个发生，当有事件发生时，线程会得到通知并进行相应的处理。
  特点：IO 复用能够提高系统的并发性能，减少线程的创建和切换开销。常见的 IO 复用技术包括 select、poll 和 epoll 等。
4. 信号驱动（Signal driven）
  定义：信号驱动是一种异步 I/O 处理方式，它允许线程注册对特定事件的兴趣。当事件发生时，系统会向线程发送一个信号，线程可以在这个信号的处理函数中进行相应的处理。
  特点：信号驱动能够实现异步事件通知，提高程序的响应速度。但是，信号处理函数的编写需要谨慎，因为信号的处理时机是不确定的。
5. 异步（Asynchronous）
  定义：异步是指线程之间不需要按照严格的顺序执行，每个线程可以独立地执行自己的任务，而不需要等待其他线程完成特定操作。在这种模式下，线程之间通过事件通知、回调函数等方式来相互协作。
  特点：异步操作可以使程序能够同时处理多个任务，提高并发能力。异步编程模式通常需要更复杂的逻辑来处理任务完成后的结果，因为任务的完成时间是不确定的。
它们之间的区别与联系：
   阻塞与非阻塞：阻塞和非阻塞主要是针对操作在执行过程中对线程状态的影响。阻塞操作会挂起线程，而非阻塞操作允许线程继续执行。
   同步与异步：同步是指多个线程或进程之间按照一定的顺序执行，通常通过共享变量、信号量等机制来协调它们的执行顺序。异步则是线程之间可以独立执行，通过事件通知等方式协作。同步和异步更多地是关于线程或进程之间的协作方式。
   非阻塞与 IO 复用：非阻塞操作和 IO 复用都可以用于提高并发性能。非阻塞操作允许线程在等待 I/O 操作时继续执行其他任务，而 IO 复用则允许一个线程同时管理多个 I/O 操作。
   信号驱动与异步：信号驱动是一种实现异步 I/O 的方式之一。通过信号驱动，线程可以在事件发生时得到通知，从而实现异步事件处理。

### Web Server

<pre><span style="font-size:16px">
  URL 是统一资源定位符（Uniform Resource Locator）的缩写，它是用于标识网络上资源位置的字符串，是互联网上标准资源地址的表示方法。
 URL 的结构
一个典型的 URL 由以下几个部分组成：
  协议（Protocol）：也称为方案（Scheme），它指定了访问资源时使用的协议。常见的协议有：
    http：超文本传输协议，用于传输网页内容。
    https：安全的超文本传输协议，使用 SSL/TLS 加密。
    ftp：文件传输协议，用于传输文件。
    mailto：用于发送电子邮件，后面通常跟电子邮件地址。
    file：用于访问本地文件系统。
  子协议（Subprotocol，可选）：在某些协议中，还可以有子协议，进一步指定访问方式。例如在 svn+ssh://example.com/repo 中，svn+ssh 是子协议，表示通过 SSH 协议访问 Subversion 仓库。
  用户名（Username，可选）：在需要身份验证的 URL 中，可以包含用户名。例如 username@hostname，用户名和主机名之间用 @ 分隔。
  密码（Password，可选）：如果需要提供密码进行身份验证，则跟在用户名之后，中间用冒号分隔。例如 username:password@hostname。
  主机（Host）：主机名（或 IP 地址），用于指定资源所在的服务器。主机名可以是域名（如 example.com）或 IP 地址（如 192.168.1.1）。
  端口号（Port，可选）：用于指定主机上提供资源服务的端口号。端口号紧跟在主机名之后，用冒号分隔。例如 :8080，其中 8080 是端口号。
  路径（Path）：用于指定资源在主机上的具体位置。例如 /path/to/resource。
  查询字符串（Query string，可选）：用于向服务器传递参数。查询字符串从问号 ? 开始，后面跟着一系列的键值对，键值对之间用 & 分隔。例如 ?key1=value1&key2=value2。
  片段标识符（Fragment identifier，可选）：用于指定文档中的某个部分，通常用于 HTML 页面。片段标识符从井号  开始，后面跟着片段名。例如 section1。
 URL 的语法
标准的 URL 语法如下：
protocol://[username[:password]@]host[:port]/path[?query][fragment]
 举例说明
http://www.example.com/index.html
  协议：http
  主机：www.example.com
  路径：/index.html
ftp://user:password@ftp.example.com:21/files/document.txt
  协议：ftp
  用户名：user
  密码：password
  主机：ftp.example.com
  端口号：21
  路径：/files/document.txt
https://example.com/search?q=query+string&lang=enresults
  协议：https
  主机：example.com
  路径：/search
  查询字符串：?q=query+string&lang=en
  片段标识符：results
URL 的主要作用是为用户提供更多的信息访问方式，使得用户可以通过浏览器等工具方便地获取网络上的各种资源。
</pre>

### GET 和 POST 的区别

<pre><span style="font-size:16px">
  GET 请求和 POST 请求是 HTTP 协议中最常用的两种请求方法，它们在多个方面存在区别，以下是详细对比：
 1. 作用和用途
   GET 请求
     主要用于从服务器获取资源，是 HTTP 协议中最基本的请求方法之一。
     例如，当我们访问一个网页，像浏览新闻资讯、查看产品列表等操作，通常会使用 GET 请求来获取页面内容。
   POST 请求
     用于向服务器提交数据，以创建新的资源或更新现有资源。
     常见的应用场景包括用户注册、登录、提交表单等操作，比如向服务器发送用户填写的个人信息来完成注册流程。
 2. 数据传输方式
   GET 请求
     数据是通过 URL 参数传递的。这些参数会附加在请求的 URL 后面，以键值对的形式存在，键值对之间用 “&” 分隔，格式为 “?”。
     例如，https://example.com/search?q=query+string&lang=en，其中 “q=query+string&lang=en” 是 GET 请求的参数部分。
   POST 请求
     数据是放在请求体（body）中传输的。这样可以传输更多的数据，并且数据不会像 GET 请求那样直接暴露在 URL 中。
     例如，当用户在登录页面输入用户名和密码并提交表单时，这些数据会放在 POST 请求的请求体中发送到服务器。
 3. 数据长度限制
   GET 请求
     由于数据是包含在 URL 中的，而 URL 长度有限制（具体限制因浏览器而异，但一般来说在 2 - 8KB 左右），所以 GET 请求能携带的数据量比较小。
     这限制了通过 GET 请求发送复杂数据的能力，例如不能用于上传大文件等操作。
   POST 请求
     没有理论上对数据长度的限制（实际上可能会受到服务器配置等限制），能够传输大量数据，如文件上传、长文本内容提交等场景。
 4. 安全性
   GET 请求
     数据直接暴露在 URL 中，这可能会带来安全风险。例如，如果 URL 被保存在浏览器历史记录、网页服务器日志或者通过邮件等渠道分享时，其中包含的敏感信息（如密码、个人身份信息等）可能会被泄露。
     因此，对于敏感数据的传输，不适合使用 GET 请求。
   POST 请求
     数据在请求体中传输，相对隐蔽，不容易被直接从 URL 中获取。不过，这并不意味着 POST 请求的数据是完全安全的，因为服务器端的日志文件等可能仍然会记录请求体中的内容。
     所以对于敏感数据的传输，还需要采取其他安全措施，如使用 HTTPS 协议来加密传输过程中的数据。
 5. 缓存
   GET 请求
     可以被缓存。浏览器、代理服务器等缓存机制会缓存 GET 请求的结果，这样当再次请求相同的 URL 时，可以快速返回缓存的内容，提高性能。
     例如，对一些静态页面或者经常访问的资源，使用 GET 请求可以利用缓存来减少服务器的负载和网络传输时间。
   POST 请求
     通常不会被缓存，因为 POST 请求一般用于提交数据并修改服务器状态，缓存 POST 请求的结果可能会导致数据不一致或者其他问题。
 6. 对资源的可重复性操作
   GET 请求
     是一种幂等操作。也就是说，对同一个 URL 发起多次相同的 GET 请求，其效果和发起一次 GET 请求是一样的，不会改变服务器上的资源状态。
     例如，多次访问一个新闻详情页面（使用 GET 请求），不会对新闻内容本身产生任何改变。
   POST 请求
     一般是非幂等操作。多次发送相同的 POST 请求可能会导致服务器上资源状态的改变。例如，多次提交一个表单用于添加新用户，可能会在数据库中创建多个相同的用户记录。
 7. 书签功能
   GET 请求
     因为数据是包含在 URL 中的，所以可以方便地将包含参数的 URL 作为书签保存。当用户再次点击这个书签时，可以直接访问带有这些参数的页面。
   POST 请求
     由于数据是在请求体中，无法通过简单的 URL 来包含这些数据，因此不能直接将 POST 请求对应的页面内容作为书签保存。
 8. 对数据类型的支持
   GET 请求
     一般只支持 ASCII 字符。如果需要传递其他类型的字符，需要进行编码，如 URL 编码。
   POST 请求
     可以支持多种数据类型，包括二进制数据（如文件上传）、JSON 数据、XML 数据等。这使得 POST 请求能够满足各种复杂的数据提交需求。
 9. 浏览器历史记录
   GET 请求
     访问的每个 GET 请求的 URL 都会记录在浏览器的历史记录中。这对于用户回溯浏览历史是有帮助的，但如前面所说，可能会带来安全和隐私方面的隐患。
   POST 请求
     浏览器历史记录中一般不会保存 POST 请求的具体数据，只是保存访问过的页面 URL。
 10. 应用场景
   GET 请求
     适用于获取不涉及敏感信息的资源，如浏览网页内容、获取公共 API 数据等。
   POST 请求
     适用于需要向服务器提交数据的场景，如用户登录、注册、提交表单、上传文件等。
</pre>


 总结表格

| 对比项目       | GET 请求                  | POST 请求                  |
| -------------- | ------------------------- | -------------------------- |
| 主要用途       | 获取资源                  | 提交数据以创建或更新资源   |
| 数据传输方式   | URL 参数                  | 请求体                     |
| 数据长度限制   | 较小                      | 较大                       |
| 安全性         | 较低（数据暴露在 URL 中） | 相对较高（数据在请求体中） |
| 缓存           | 可以缓存                  | 通常不缓存                 |
| 幂等性         | 是                        | 否                         |
| 书签功能       | 支持                      | 不支持                     |
| 数据类型支持   | ASCII 字符（需编码）      | 多种数据类型               |
| 浏览器历史记录 | 保存 URL                  | 保存页面 URL，不保存数据   |

### 线程池























