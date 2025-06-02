# 基础知识

## 卡常

```c++
 int ans;
while (l <= r)
{
    int mid = l + r>> 1;
    if (check(mid))
    {
        r=mid-1;
        ans=mid;
    }
    else
    {
        l = mid+1;
    }
    // debug(mid)
}
#include<bits/stdc++.h>
#include<stime>
using namespace std;
int start = clock();
//由于clock函数本身非常慢，所以可以每个10000次使用一次clock函数来卡常，
int main()
{
	for(int i=0;;i++)
	{
		if(i % 10000 == 0 && clock() - start >= 850000)// 1/1000000 s
		{
			cout << i << endl;
			exit(0);
		}
		/*或者另一种写法*/
		if(i % 10000 == 0 && clock() - start >= CLOCKS_PER_SEC *0.9)
		{//CLOCKS_PER_SEC变量名 值为 1/1000000 s
			cout << i << endl;
			exit(0);
		}
	}
	return 0;
}

#pragma GCC optimize (3)
#pragma GCC optimize ("Ofast")
```

## STL 二分

```c++
这两个二分查找操作可以在set，数组，vector，map中使用；
 
数组 或者 vector 中的语法：
序列是升序的（从小到大）
lower_bound(begin(),end(),x) //返回序列中第一个大于等于x的元素的地址
upper_bound(begin(),end(),x) //返回序列中第一个大于x的元素的地址
 
序列是降序的（从大到小）
lower_bound(begin(),end(),x,greater<tpye>()) //返回序列中第一个小于等于x的元素的地址
upper_bound(begin(),end(),x,greater<type>()) //返回序列中第一个小于x的元素的地址
 
set 或者 map 中的语法：
和数组差不多，只不过返回的是迭代器：
s.lower_bound(x) //返回序列中第一个大于等于x的元素的地址
s.upper_bound(x) //返回序列中第一个大于x的元素的地址

std::binary_search(ans + 1, ans + ct + 1, h[a][b]) ? "1" : "0");
//返回类型是bool型,即是,只能返回真或假,不能返回存在位置的下标
重点注意：如果当前序列中找不到符合条件的元素，那么返回end(),对于数组来说，返回查询区间的首地址位置，对于set来讲，返回end()-1后面元素的迭代器，也就是begin();
```

## vector

> - vector 数组查找    if (find(res.begin(),res.end(),b.substr(i, 2))!=res.end())  // //vector数组find函数，从begin开始到end结束，后面的是查找的内容。
> - vector 数组去重   

```c++
//set 数组辅助去重
set<int>s(vec.begin(), vec.end()); 
vec.assign(s.begin(), s.end());
//vector 使用unique函数去重，这个函数只能去重相邻的数据
sort(vec.begin(),vec.end());    
//vector <int>::iterator it;
//it=unique(vec.begin(),vec.end()); 
//vec.erase(it,vec.end());
vec.erase(unique(vec.begin(), vec.end()), vec.end());
```

- vector初始化方式

    ```c++
    //使用vector一次性完成二维数组的定义（注意：此种方法适用于每一行的列数相等的二维数组）
    vector<vector<int>> matrix(m, vector<int>(n, -1));
    //以下是拆分理解
    //创建一维数组matirx，这个数组里有m个元素，元素是int型vector。
    vector<vector<int>> matrix(m);
    //除了定义数组类型及数组大小外，同时给数组中的元素赋值：将元素赋值为大小为n的int型vector。
    vector<vector<int>> matrix(m, vector<int>(n));
    //除了定义数组类型、数组大小、列的大小，同时给数组列中的元素（或者说，数组中的所有元素）赋值为-1。
    vector<vector<int>> matrix(m, vector<int>(n, -1));
    //当每一行的列数不相等的时候
    vector<vector<int>> matrix;//创建一维数组matirx，这个数组里的元素是int型vector。
    int m = 3; //matrix有m行
    int n = 10; //matrix有n列
    int value = 1; //最终matrix成为二维数组后，其中每个元素的值为1（如果不需要进行初始化，此语句可以省略）
    for (int i = 0; i < m; ++ i) {
        vector<int> tmp(n, value); //定义int型一维数组tmp，该数组有n个int型元素，且每个元素的初始值为value
        matrix.push_back(tmp); //将一维数组tmp（小容器）加入matrix（大容器）中，使之成为matrix的元素，令matrix成为二维数组
    }
    ```

## map

> - map数组可以使用 auto 的方式遍历

```c++
map/multimap 
(它们都是关联容器，增删效率为log级别，并且依据key能自动排序，默认小于，前者key不允许重复，后者允许)
    insert()  插入的数是一个pair
    erase()  输入的参数是pair或者迭代器
    find()
    []  注意multimap不支持此操作。 时间复杂度是 O(logn)
    lower_bound()/upper_bound()
//当数据范围较大时,但是数据量较小,无法常规遍历的时候使用map保序离散化,没有值的位置在map里面不会有空间
mp.end();//返回的值是map里面最后一个元素的下一个位置,即是一个虚拟位置,并没有实际的值,直接使用会出现一系列问题
//map的最后一个元素是 --mp.end(),   也可以使用 .rbegin()
```

## set

> `set`容器中的成员函数二分比调用快的多

```c++
st.lower_bound(r[i]);
lower_bound(st.begin(),st.end(),r[i]);
上比下速度快的多
```

## 优先队列 - 堆

- 注意对于结构体堆,重载的时候需要加const和引用
- **堆顶是堆(数组)中的第一个元素**,故`greater`在sort里面是从小到大排序,堆顶(第一个元素)是最小值,结构体重载小于号,故堆顶(第一个元素)是最大值 !!!

```c++
priority_queue<int,vector<int>,greater<int> > q;//按照从小到大排序，小根堆
priority_queue<int,vector<int>,less<int> > q;//按照从大到小排序
struct node
{ 
	int x,y;
	bool operator < (const node & a) const//堆结构体重载的时候一定要加引用.
	{
		return x < a.x;
	}
};
priority_queue <node> q; //按照键值从大到小排序 .
```

## 模拟栈，可以删除中间的数

```cpp
//此有额外的功能，判断栈中是否只有一个元素
void add(int x)
{
    ++ cnt[x];
    if(cnt[x] == 1)
    {
        stk[++ top] = x;//栈顶元素是 x
        pos[x] = top;//栈顶元素 x 的下标是 top
    }
    else if(cnt[x] == 2)
    {
        stk[pos[x]] = stk[top];//栈顶元素替换 x 元素所在的位置
        pos[stk[top]] = pos[x];//对应的下标也替换
        stk[top --] = pos[x] = 0;//将 x 元素从栈中删除,清零
    }
}
void del(int x)
{
    -- cnt[x];
    if(cnt[x] == 1)
    {
        stk[ ++ top] = x;
        pos[x] = top;
    }
    else if(cnt[x] == 0)//从栈中删除一个元素
    {
        stk[pos[x]] = stk[top];
        pos[stk[top]] = pos[x];
        stk[top --] = pos[x] = 0; 
    }
}
```



## bitset

- std :: bitset 是标准库中的一个存储 0/1 的大小不可变容器.    bitset 就是通过固定的优化,使得一个字节的八个比特能分别储存 8 位的 0/1.int的 1/32 倍.

-  == / !=   表示两个 bitset 内容是否完全一样 .

- &  /  &=  /  |  /  |=  /  ^  / ^=  /  ~  按位位运算,bitset 只能与 bitset 进行位运算,若想与整型进行位运算,要先将 整型转为 bitset

-  <<    >>   左移右移

- ```c++
  count() ;// 返回 true 的数量
  size() ; // 返回 bitset 的大小
  test(pos) ;//他和vector中的  at() 的作用是一样的,和 [] 运算符的区别是越界检查 
  any() ;//若存在某一位是 true 则返回 true,否则返回 false
  none() ;//若所有的位都是 false ,则返回 true,否则返回 false .
  all() ;// C++11 .若所有的位都是 true则返回 true,否则返回false. 
  []操作符    s[k] 表示 s 的第 k 位，即可取值也可赋值，编号从 0 开始
  输出bitset 变量名时，是直接输出全部内容的，且是倒着输出的，即是第 0 位对应最右边
  ```
  
  [P1537弹珠][P1537 弹珠 - 洛谷 | 计算机科学教育新生态 (luogu.com.cn)](https://www.luogu.com.cn/problem/P1537)
  
  ```cpp
  //bitset 优化问题   --- 一般用于优化 01 背包分割问题
  f[i][j] |= f[i - 1][j - a[i]];//表示前 i-1 维能否达到 j - a[i] 的状态，判断是否可以将 j 作为分割点
  //将 二维总体积倒序循环 即可优化掉第一维
  for(int i=1;i<=n;i++)//表示组
  {
      for(int j=s;j>=a[i];j--)//总体积倒序
          f[j] |= f[j - a[i]];
  }
  //考虑优化第一维之后实质上就是将原数组的 01 串左移了 w[i] 位与原串取或，则转移可以写为
  f[j] |= f[j] << w[i];
  for(int i=1;i<=n;i++)//表示体积
  {
      for(int j=1;j<=a[i];j++)//个数
          f |= (f << i);
  }
  ```
  
  ---
  
  **attention : ** `bitset`的第 `x`位数据表示数据位 x 的状态是否可行，就是当为 x 的时候是否可以分割，所以`bitset`的大小是数据和，这样才能表示出所有状态，同时，比较的时候即是当前状态异或状态右移`x`，返回值也是`bitset`即是将对应的位置的值改变 01 值，例：`f[0] = 1，f |= (f << 2) ,即是 f[2] = 1，2 可做分割点，这里的 2 表示的是当前价值为 2 `

## 数学知识

> - \__lg(tmp),内置函数,返回的值是2 ^ x <= tmp  ,x的最大值 ,即  \__lg(4) = 2,   \__lg(5) = 2,   __lg(9) = 3
>
> - 求n个数的最小公倍数就是求这些数中每一个同样质因子的最大指数
>   拓展：求n个数的最大公约数就是这些数中每一个同样质因子的最小指数）

## 迭代器

```c++
vector<type>::iterator iter;//type 类型
map<type,type>::iterator iter;//迭代器类型可以使用auto
set<type>::iterator iter;
等等.....
迭代器可以像指针一样，遍历STL时可以直接对迭代器 ++ --  ；
访问迭代器的值的形式：
*iter
iter->first    iter->second   
```

## int128

```c++
inline __int128 read(){
    __int128 x=0,f=1;
    char ch=getchar();
    while(ch<'0'||ch>'9'){
        if(ch=='-')
            f=-1;
        ch=getchar();
    }
    while(ch>='0'&&ch<='9'){
        x=x*10+ch-'0';
        ch=getchar();
    }
    return x*f;
}
inline void print(__int128 x){
    if(x<0){
        putchar('-');
        x=-x;
    }
    if(x>9)
        print(x/10);
    putchar(x%10+'0');
}
```

## 位运算

- (a ^ b) ^ (b ^ c) = c
- a & b + a ^ b =  a | b
- a & b + a | b = a + b
-  a + b = (a ⊕ b) + 2(a ∧ b)
- (a + b) − (a ⊕ b) = 2(a ∧ b)
- a ^ b = c   ------>  b = a ^ c  

## 匿名函数 

1. 如果捕获列表为[&]，则表示所有的外部变量都按引用传递给lambda使用, &c 只会将 c 按照引用传进去
2. 如果捕获列表为[=]，则表示所有的外部变量都按值传递给lambda使用, c只会将 c 按值传进去
3. 匿名函数构建的时候对于按值传递的捕获列表，会立即将当前可以取到的值拷贝一份作为常数，然后将该常数作为参数传递，即：

```c++
 auto dfs = [&](auto self, int x, int p) -> void 
 {
        for (auto y : adj[x])
        {
            if (y != p)
            {
                self(self, y, x);
                siz[x] += siz[y];
            }
        }
        if (siz[x] == 0) 
        {
            siz[x] = 1;
        }
};
dfs(dfs, 0, -1);
```

| 格式                | 解释                                                         |
| ------------------- | ------------------------------------------------------------ |
| **[]**              | 空捕获列表，Lambda不能使用所在函数中的变量。                 |
| [names]             | names是一个逗号分隔的名字列表，这些名字都是Lambda所在函数的局部变量。默认情况下，这些变量会被拷贝，然后按值传递，名字前面如果使用了&，则按引用传递 |
| [&]                 | 隐式捕获列表，Lambda体内使用的局部变量都按引用方式传递       |
| [=]                 | 隐式捕获列表，Lanbda体内使用的局部变量都按值传递             |
| [&,identifier_list] | identifier_list是一个逗号分隔的列表，包含0个或多个来自所在函数的变量，这些变量采用值捕获的方式，其他变量则被隐式捕获，采用引用方式传递，identifier_list中的名字前面不能使用&。 |
| [=,identifier_list] | identifier_list中的变量采用引用方式捕获，而被隐式捕获的变量都采用按值传递的方式捕获。identifier_list中的名字不能包含this，且这些名字面前必须使用&。 |

## STL 集合小函数

- ```c++ 
  accumulate(begin,end,init);//对一个元素序列求和,初始值是 init 即是将和再加上init
  ```

- ```c++
  fill(begin,end,num);//初始化函数,与memset相比可以初始化任意值,而且没有无用循环,给定初始化范围,
  ```

- ```c++
  is_sorted(begin,end);//判断是否有序(升序),返回bool值,升序返回yes
  ```

- ```c++
  iota(begin,end,num);//让序列递增赋值,num是给定的begin的值,后面的值依次大一
  ```

- ```c++
  lower_bound(begin,end,num);//查找第一个大于等于 x 的数,返回地址,减去首地址begin,返回下标
  upper_bound(begin,end,num);//查找第一个大于 x 的数,返回地址,返回下标同理
  ```

- ```c++
  max_element();min_element();//最大值最小值,返回的是地址,需要加 * 解引用,返回值
  ```

- ```c++
  next_permutation(begin,end);//求序列的下一个排列,下一个排列是字典序大一号的排列.如果是最后一个排列返回false,否则求出下一个排列后返回true
  ```

- ```c++ 
  prev_permutation(begin,end);//求出前一个排列,如果是最小的排列,将其重排为最大的排列,返回false
  ```

- ```c++
  reverse(begin,end);//对序列进行翻转
  ```

- ```c++
  unique(begin,end);//消除重复元素,返回消除完重复元素的下一个位置的地址.(这个函数消除的必须是连续重复的不是不连续的出现过多次的).
  ```

- __builtin(用位运算函数)

- ```c++
  用法 : 返回括号内的二进制表示形式中末尾 0 的个数.
  __builtin_ctz()  /  __builtin_ctzll() //(有ll表示按longlong计算) 、
      
  用法 : 返回括号内数的二进制表示形式中的前导 0 的个数.
  __builtin_clz()  /  __builtin_clzll() // ll 同上
      
  用法 : 返回括号内的二进制表示形式中 1 的个数
  __builtin_popcount() ;
  
  用法 : 返回括号内数的二进制表示形式中 1  的个数的奇偶性(偶 : 0 ,,奇 : 1)
  __builtin_parity() ;
  
  用法 : 返回括号内数的二进制形式中最后一个 1 在第几位数(从后往前).
  __builtin_ffs() ;
  
  用法 : 快速开平方 
  __builtin_sqrt() / __builtin_sqrtf() ; //同上,sqrt八位,sqrtf四位,比sqrt快 10 倍
  ```
  
- 二进制中 1 的个数

```c++
void fun(int x)
{
    int countx = 0; 
	while(x) 
	{ 
		countx++; 
		x = x&(x-1); //清除掉整数a二进制中最右边的1。 //或者 x -= x & (-x); x & -x 是二进制中最后一个 1 以及后面的 0
	} 
	return countx; 
}
```



## 快读

```c++
// __int128 快读
inline __int128 read(){
    __int128 x=0,f=1;
    char ch=getchar();
    while(ch<'0'||ch>'9'){
        if(ch=='-')
            f=-1;
        ch=getchar();
    }
    while(ch>='0'&&ch<='9'){
        x=x*10+ch-'0';
        ch=getchar();
    }
    return x*f;
}
 
inline void print(__int128 x){
    if(x<0){
        putchar('-');
        x=-x;
    }
    if(x>9)
        print(x/10);
    putchar(x%10+'0');
}
//正常快读
char *p1,*p2,buf[100000];//快读和同步流二者只能选一个
#define nc() (p1==p2 && (p2=(p1=buf)+fread(buf,1,100000,stdin),p1==p2)?EOF:*p1++)
int read()
{
    int x = 0,f = 1;char ch = nc();
    while(ch<48||ch>57)  {if(ch=='-')f=-1;ch=nc();}
    while(ch>=48&&ch<=57)  x=x*10+ch-48,ch=nc();
    return x*f;
}
void write(int x)
{
    if(x<0) putchar('-'),x=-x;
    if(x>9) write(x/10);
    putchar(x%10+'0');
    return;
}
```

## 进制转换合集

```c++
void solve(int n, int k)//递归写法
{
	if(n == 0)
		return;
	int x = n % k;
	if(x < 0)
		x -= k, n += k;
	solve(n / k, k);
	if(x > 9)
		cout << char(x-10+'A');
	else
		cout << x;
}//下面是循环写法
string intToA(int n,int radix)    //n是待转数字，radix是指定的进制
{
	string ans = "";
	do{
		int t = n % radix;
		if(t >= 0 && t <= 9) ans += t + '0';
		else ans += t - 10 + 'a';
		n /= radix;
	}while(n != 0);	//使用do{}while（）以防止输入为0的情况
	reverse(ans.begin(),ans.end());
	return ans;	
}
//函数类,其中std::oct类即是内置函数转换
cout << "35的8进制:" << std::oct << 35<< endl;  
cout << "35的10进制" << std::dec << 35 << endl;  
cout << "35的16进制:" << std::hex << 35 << endl;  
cout << "35的2进制: " << bitset<8>(35) << endl;      //<8>：表示保留8位输出
```

## String

- 插入函数 insert

```c++
s.insert(pos,c);//插入的时候是插入在下标为 pos 元素的前面
```

- 初始化

```c++
string s(num,ch);
```



# 基础课类算法

## 排序

### 快排

``` c++
void quick_sort(int q[], int l, int r)
{
	if(l>=r) return;
	int x = q[l+r>>1] , i = l-1 , j = r+1;//先移动，这样保证一定是两段
	while(i<j)
	{
		do i++;while(q[i] < x);
		do j--;while(q[j] >x);
		if(i < j) swap(q[i],q[j]); 
	}
	quick_sort(q,l,j);
	quick_sort(q,j+1,r);
}
```

### 归并排序

> 主要用于求逆序对和正序对的问题

```c++
int n;
int q[N],tmp[N];//tmp存储中间排序的结果
ll merge_sort(int l,int r)
{
	if(l>=r) return 0;
	int mid = l + r >> 1;
	ll res = merge_sort(l,mid) + merge_sort(mid+1,r);
	//归并的过程
	int k =0,i=l,j=mid+1;
	while(i <=mid && j <= r)
		if(q[i] <= q[j]) tmp[k++] = q[i++];
		else
		{
			tmp[k++] = q[j++];
			res += mid - i + 1;//一左一右
		}
	while(i<=mid) tmp[k++] = q[i++];//扫尾
	while(j<=r) tmp[k++] = q[j++];	
	for(int i=l,j=0;i<=r;i++,j++)//物归原主 i循环原数组 j循环临时数组
		q[i] = tmp[j];
	return res;
}
int main()
{
	cin >> n;
	for(int i=0;i<n;i++)
		cin >> q[i];
	cout << merge_sort(0,n-1) << endl;
	return 0;
}
```

## 二分

```c++
// 区间[l, r]被划分成[l, mid]和[mid + 1, r]时使用：
int bsearch_1(int l, int r)//求满足要求的最小值,求左端点
{
	while (l < r)
	{
		int mid = l + r >> 1;
		if (check(mid)) r = mid;    // check()判断mid是否满足性质
		else l = mid + 1;
	}
	return r;
}
// 区间[l, r]被划分成[l, mid - 1]和[mid, r]时使用：
int bsearch_2(int l, int r)//求满足要求的最大值,求右端点
{
	while (l < r)
	{
		int mid = l + r + 1 >> 1;
		if (check(mid)) l = mid;
		else r = mid - 1;
	}
	return r;
}//浮点数二分注意精度即可,比要求多2位小数即可


int ans;
while (l <= r)
{
    int mid = l + r>> 1;
    if (check(mid))
    {
        r=mid-1;
        ans=mid;
    }
    else
    {
        l = mid+1;
    }
    // debug(mid)
}
```

## 高精度

```c++
//高精度计算时,存数据的时候都是逆序的,从后往前存,对于vector数组从后往前遍历是原数据
for(int i = a.size()-1;i>=0;i--) A.push_back(a[i] - '0');//A = [6,5,4,3,2,1]
for(int i = b.size()-1;i>=0;i--) B.push_back(b[i] - '0');
//输出的时候也要注意从后往前输出
for(int i = C.size()-1;i>=0;i--) cout << C[i];

//加法
vector<int> add(vector<int> &A,vector<int> &B)//添加引用可以节省时间
{
	
	vector<int> C;
	int t = 0;//进度
	for(int i=0;i<A.size() || i < B.size();i++){
		if(i < A.size()) t += A[i];
		if(i < B.size()) t += B[i];
		C.push_back(t % 10);
		t /= 10;
	}
	if(t) C.push_back(t);
	return C;
}

//减法
//判断AB之间的大小关系
bool cmp(vector<int> &A,vector<int> &B)
{
	if(A.size() != B.size()) return A.size() > B.size();//0 或 1
	for(int i = A.size() - 1;i>=0;i--)
		if(A[i] != B[i]) return A[i] > B[i]; // 0 或 1
	return true;//两者相等
}
vector<int> sub(vector<int> &A,vector<int> &B)//添加引用可以节省时间
{
	vector<int> C;
	for(int i =0,t=0; i<A.size();i++)
	{
		t = A[i] - t;
		if(i < B.size()) t -= B[i];
		C.push_back((t + 10)%10);//中和了借1和单纯减
		if(t < 0) t = 1;
		else t = 0;
	}
	while(C.size() > 1 && C.back() == 0 ) C.pop_back();//删除前导0
	return C;
}

//乘法
vector<int> mul(vector<int> &A,int b)
{
	vector<int> C;
	int t=0;  //进位
	for(int i=0;i<A.size() || t;i++) //加上t是因为最后一位乘法时可能出现进位
	{
		if(i < A.size()) t += A[i] * b;
		C.push_back(t%10);
		t /= 10;  //进位
	}
	return C;	
}

//除法
vector<int> div(vector<int> &A,int b,int &r)//r必须传引用或指针因为需要其改变
{
	vector<int> C;//商
	r = 0;
	for(int i = A.size() - 1;i>=0;i--)
	{
		r = r * 10 + A[i];  //余数放在下一位相当于下一位变成10*r + A[i]
		C.push_back(r / b);
		r %= b;
	}
	reverse(C.begin(),C.end()); //因为i从大到小所以结果是倒序的反转回来
	while(C.size() > 1 && C.back() ==0) C.pop_back();
	
	return C;
}
```

## 前缀和与差分数组

```c++
for(int i=1;i<=n;i++)//求前缀和
		for(int j=1;j<=m;j++)
			s[j][j] = s[i-1][j] + s[i][j-1] - s[i-1][j-1] + a[i][j];
while(q--)//算子矩阵的和
	{
		int x1,y1,x2,y2;
		cin >> x1 >> y1 >> x2 >> y2;
		cout << s[x2][y2] - s[x1-1][y2] - s[x2][y1-1] + s[x1-1][y1-1];
	}
// 差分
void insert(int x1,int y1,int x2,int y2,int c)
{
	b[x1][y1] += c;
	b[x2+1][y1] -= c;
	b[x1][y2+1] -=c;
	b[x2+1][y2+1] +=c;
}//差分数组求前缀和得到当前元素
```

## 异或前缀和

区间异或,也可以用前缀和的思想,只不过需要做的是用左边界的前缀和与右边界的前缀和进行异或,即可得到区间的元素异或.

- 应用 : 求子段和为 0 的子段个数,map存下来前缀异或的值,即可,当这个值再次出现时答案次数(也可以直接使用前缀和判断和出现过几次).
- 异或差分 : 将[l,r]区间内的数都异或上 x ,先维护异或差分,即是 b[i] = a[i] ^ a[i-1] ,操作的时候将 b[l] 和 b[r + 1]异或上 x 即可,最终的结果求一遍前缀异或.          (利用的性质主要是 x ^ x =  0)

## 双指针

```c++
for(int i=0,j=0;i<n;i++){
		s[a[i]]++;
		while(j < i && s[a[i]] > 1)
		{
			s[a[j]]--;//j到i之间一定有一个数多次出现。
			j++;
		}
		res = max(res,i-j+1);
	}
```

## lowbit

```c++
int lowbit(int x)//求最后一个 1所代表的值
{
	return x & -x;
}
int func(int x) //判断一个数的二进制表示中有多少 1
{
	int countx = 0; 
	while(x) 
	{ 
		countx++; 
		x = x&(x-1); //清除掉整数a二进制中最右边的1。 
        //x -= lowbit(x);
	} 
	return countx; 
}
```

## 离散化

### 保序方式

```c++
vector<int> alls; // 存储所有待离散化的值,这里是将下标离散化
sort(alls.begin(), alls.end()); // 将所有值排序
alls.erase(unique(alls.begin(), alls.end()), alls.end());   // 去掉重复元素
 
// 二分求出x对应的离散化的值
int find(int x) // 找到第一个大于等于x的位置,传入的值是下标,二分查找vector里面等于这个数存值,查找左端点和右端点求区间和
{
    int l = 0, r = alls.size() - 1;
    while (l < r)
    {
        int mid = l + r >> 1;
        if (alls[mid] >= x) r = mid;
        else l = mid + 1;
    }
    return r + 1; // 映射到1, 2, ...n,+1是离散化下标从1开始，不加从0开始
}
```

### 无序map

```c++
unordered_map<int,int> mp;//增删改查的时间复杂度是 O(1)
int res;
int find(int x)
{
	if(mp.count(x)==0) return mp[x]=++res;
	return mp[x];
}
```

## RMQ(ST表查询区间最值)

```c++
//以查询最大值为例,ST表查询主要是2倍的跳
状态表示： 集合：f(i,j)表示从位置i开始长度为2^j的区间的最大值；
           属性：MAX
状态转移： f(i,j)=max(f(i,j-1),f(i+(1<<(j-1)),j-1));
           含义：把区间[i,i+2^j],分成两半，[i,i+2^(j-1)]和[i+(1<<(j-1)),2^j],整个区间最大值就是这两段区间最大值的最大值
const int N=2e5+7,M=20;
int dp[N][M]; //存储区间最大值
int a[N];//存放每个点的值
//dp求从位置i开始长度为2^j的区间的最大值
for(int j=0;j<M;j++)  // 求区间最大值
{
    for(int i=1;i+(1<<j)-1<=n;i++)
    {
        if(!j) dp[i][j]=a[i];
        else dp[i][j]=max(dp[i][j-1],dp[i+(1<<(j-1))][j-1]);
    }
}
//求任意区间的最大值；（可以预处理log）
int res=log2(b-a+1);  //求  l - r 的最大值
cout <<max(dp[a][res],dp[b-(1<<res)+1][res])<<endl;
}
```

# 数据结构

## 链表

### 单链表

```c++
int h = -1,idx = 0;
//将x插入下标是k的点的后面
void add(int k,int x)
{
	e[idx] = x,ne[idx] = ne[k],ne[k] = idx,idx++;
}
//将下标是k的点后面的点删除
void remove(int k)
{
	ne[k] = ne[ne[k]];//只需要更新指向即可，跳过下一个点，指向下下个点
}
```

### 双链表

```c++
//初始化
void init()
{
	//0表示左节点，1表示右节点
	r[0] = 1,l[1] = 0;
	idx = 2;
}

//在第k个点的右边插入一个点x---在k的左边插入时传入 l[k],x 即可
void add(int k,int x)
{
	e[idx] = x;
	r[idx] = r[k];
	l[idx] = k;
	l[r[k]] = idx;//左右更新不能写反，右节点的左指针更新需要用到左节点的右指针
	r[k] = idx;
	
}
//删除第 k 个点
void remove(int k)
{
	r[l[k]] = r[k];
	l[r[k]] = l[k];
}
```

## Tire树

#### 字符chu'xian

```c++
int son[N][26],cnt[N],idx=0;//下标是0的点，既是根节点，又是空节点 idx存的是当前用到的节点
char str[N]; //son数组模拟的指针-下标是x的点，x节点的儿子存在son里面，cnt存的是以x结尾的单词数量    
void insert(char str[])
{
	int p = 0;
	for(int i=0;str[i];i++)
	{
		int u = str[i] - 'a';//将字母映射到0~26的数字表示
		if(!son[p][u]) son[p][u] = ++idx;//p节点不存在u这个字母-新建一个节点
		p = son[p][u];//走到下一个节点
	}//次数的!son[p][u]决定了是++idx不是idx++，因为son不应该等于0
	cnt[p]++;//表示以对应的u结尾的单词个数加 1
}
int query(char str[])//查询操作是返回单词出现多少次
{
	int p=0;
	for(int i=0;str[i];i++)
	{
		int u = str[i] - 'a';//将字母转化为对应的子节点的编号
		if(!son[p][u]) return 0; //不存在，表示没有这个单词
		p = son[p][u];
	}
	
	return cnt[p];//返回单词个数
}
```

#### 区间最大异或值

```cpp
void insert(int x) {
    int u = 0;
    for (int i = 31; ~i; i--) {
        bool p = x >> i & 1;
        if (!tr[u][p]) tr[u][p] = ++h;
        u = tr[u][p];
    }
}

int query(int x) {
    int u = 0, ret = 0;
    for (int i = 31; ~i; i--) {
        bool p = x >> i & 1;
        if (tr[u][!p]) // 存在取反后的节点
            u = tr[u][!p], (ret <<= 1)++;
        else
            u = tr[u][p], ret <<= 1;
    }
    return ret;
}
```



## 并查集

> - 并查集维护集合内有多少点

```c++
for(int i=1;i<=n;i++) p[i] = i,si[i]=1;//将所有节点的父节点先赋值为自己
if(find(a) == find(b)) continue;//a和b在一个集合里
si[find(b)] += si[find(a)];//数据个数相加
p[find(a)] = find(b);	//si数组存储的即是并查集内的所有点的数量
```

### 带权并查集

```c++
int dis[100001];//记录该集合的总大小
int tot[100001];//前面的节点数
int fa[100001];//记录元素祖先
void clean(int n)//初始化 
{
	for (int i=0;i<=n;i++) 
		fa[i]=i,tot[i]=1;
}
int find(int k)//查找该元素的祖先
{
	if(fa[k] == k)
		return k;
	int f = find(fa[k]);
	dis[k] += dis[fa[k]];
	return fa[k];
}
void add(int x,int y)//合并两个集合
{
	x = find(x);
	y = find(y);
	dis[x] = tot[y];//a 树作为 b 树的子树
	tot[y] += tot[x];
	fa[x] = y;
}
```

### 扩展域并查集

> - 拓展域并查集解决了一种多个有相互关系的并查集，放在一起考虑的问题,拓展域并查集讲的是多个集合，之间有相互关系一般为相互排斥关系，判断是否在一个集合等。

```c++
典型例题 ---- 食物链
//判断到根节点的距离即可 距离%3==0 与根节点是同一类 %3==1 可以吃根节点 %3==2,被根节点吃 ，路径压缩的时候维护距离即可
int n,m;//动物数与说话个数
int p[N],d[N];//并查集和维护的距离
int find(int x)
{
	if(p[x] != x)//x不是根
	{
		int t = find(p[x]);//不能直接写p[x] = find(p[x])
		d[x] += d[p[x]];//直接写的话，p[x]会改变，不是初始x的p
		p[x] = t;
	}
	return p[x];
}
int main()
{
	cin >> n >> m;
	for(int i=1;i<=n;i++) p[i] = i;//距离不必初始化全局变量为0，表示其实都是与自己同一类
	int res = 0;
	while(m --)
	{
		int t,x,y;
		cin >> t >> x >> y;
		if(x > n || y > n)
			res ++;
		else 
		{
			int px = find(x),py = find(y);
			if(t==1)
			{    //父节点相同是前提 然后不是都%3=0，不是同一类
				if(px == py && (d[x] - d[y])%3 != 0) res ++;
				else if(px != py)
				{
					p[px] = y;
					d[px] = d[y] - d[x];
				}
			}
			else 
			{//X吃Y   X到根的距离应该比Y到根多 1 （mod 3）的情况下
				if(px == py && (d[x] - d[y] - 1	) %3 != 0) res ++;
				else if(px != py)
				{
					p[px] = py;//先合并
					d[px] = d[y] + 1 - d[x];
				}
			}
		}
	}
	cout << res << endl;
	return 0;	
}
```

## 搜索

### DFS

- 深度搜索,DFS最显著的特征在于其 **递归调用自身** ,同时与BFS类似,DFS会对其访问过的点打上访问标记,在遍历图时跳过已打过标记的点,以确保**每个点仅访问一次**,在 DFS 过程中，通过记录每个节点从哪个点访问而来，可以建立一个树结构，称为 DFS 树。DFS 树是原图的一个生成树。

```c++
int n;
char g[N][N];//路径
bool col[N],dg[N],udg[N];
//col表示该列是否有皇后 dg表示对角线 udg表示反对角线
/*第一种搜索顺序*/
void dfs(int u)
{
	if(u == n)//行
	{
		for(int i=0;i<n;i++)
			puts(g[i]);
		puts("");
		return;
	}	
	for(int i=0;i<n;i++)
		if(!col[i] && !dg[u+i] && !udg[n - u + i])
		{
			g[u][i] = 'Q';
			col[i] = dg[u + i] = udg[n - u + i] = true;
			dfs(u + 1);
			col[i] = dg[u + i] = udg[n - u + i] = false;
			g[u][i] = '.';
		}
}
int main01()
{
	cin >> n;
	for(int i=0;i<n;i++)
		for(int j=0;j<n;j++)
			g[i][j] = '.';
	dfs(0);
	return 0;	
}
```

### BFS

- 广度搜索,BFS算法找到的路径是从起点开始的 **最短合法路径**,  一般借助队列存储要处理的点,bool 数组记录是否访问过, 在 BFS 过程中，通过记录每个节点从哪个点访问而来，可以建立一个树结构，即为 **BFS 树**.

```c++
#include <iostream>
#include<algorithm>
#include<cstring>
using namespace std;
typedef  pair<int,int> PII;
const int N = 110;
int n,m;
int g[N][N];//g数组用来存储输入的图
int d[N][N];//d数组用来每一个点到起点的距离
PII q[N*N],Prev[N][N];//手写队列，可以直接用queue,prev记录这个路是从哪一点过来的
int bfs()
{
	int hh=0,tt=0;//头 尾 - 用来表示 点的移动
	q[0] = {0,0};
	memset(d,-1,sizeof(d));//距离初始化，表示没有走过
	d[0][0] = 0;
	int dx[4] = {-1,0,1,0},dy[4]={0,1,0,-1}; // 表示一个点向四个方向找下一层是，坐标的变化
	while(hh <= tt)//队列不空
	{
		auto t = q[hh++];//每一次取出队头元素
		for(int i=0;i<4;i++)//上下左右四个方向判断
		{//用向量表示移动情况
			int x = t.first + dx[i],y=t.second + dy[i];//x y表示沿着这个方向走可以走到的点
			if(x>=0 && x<n && y>=0 && y<m && g[x][y] == 0 && d[x][y]==-1)
			{ // 在图的范围内                 是通路 - 0      没有走过
				d[x][y] = d[t.first][t.second] + 1; //更新这个点的距离
				Prev[x][y] = t;//这个队列从t点过来的，即队列的头，终点未存进队列中
				q[++tt] = {x,y};//把这个点存进队列中              当hh = n时跳出循环了
			}//Prev队列中存储的最后一个点是终点的前一个点，终点距离更新了，但终点未存进队列
		}
	}
	int x = n-1,y = m-1;//表示路径，看题目是否要求 - 此处逆序的
	while(x || y)
	{
		cout << x <<' ' << y << endl;
		auto t = Prev[x][y];//最后一点未存进队列中
		x = t.first,y= t.second;//死记-先存y再存x
	}	
	return d[n-1][m-1];//输出右下交的点代表的距离 - 看出口在哪里	
}
int main()
{
	cin >> n >> m;
	for(int i=0;i<n;i++)
		for(int j=0;j<m;j++)
			cin >> g[i][j];
	
	cout << bfs() << endl;
	return 0; 
}
```

### 双端BFS

- 又称 0-1 BFS,即是边权为 0 或 1的最短路,**实现 : **一般情况下把没有边权的边扩展到的点放到队首,有权值的边扩展到的点放到队尾,.这样即可 保证像普通 BFS一样整个队列队首到队尾权值单调不下降     [cf 173B](https://codeforces.com/problemset/problem/173/B)

```c++
#define INF (1 << 29)
int n, m;
char grid[1001][1001];
int dist[1001][1001][4];
int fx[] = {1, -1, 0, 0};
int fy[] = {0, 0, 1, -1};
deque<int> q;  // 双端队列
void add_front(int x, int y, int dir, int d) {  // 向前方加
  if (d < dist[x][y][dir]) {
    dist[x][y][dir] = d;
    q.push_front(dir);
    q.push_front(y);
    q.push_front(x);
  }
}
void add_back(int x, int y, int dir, int d) {  // 向后方加
  if (d < dist[x][y][dir]) {
    dist[x][y][dir] = d;
    q.push_back(x);
    q.push_back(y);
    q.push_back(dir);
  }
}
int main() {
  cin >> n >> m;
  for (int i = 0; i < n; i++) cin >> grid[i];

  for (int i = 0; i < n; i++)
    for (int j = 0; j < m; j++)
      for (int k = 0; k < 4; k++) dist[i][j][k] = INF;
  add_front(n - 1, m - 1, 3, 0);
  while (!q.empty()) {  // 具体搜索的过程，可以参考上面写的题解
    int x = q[0], y = q[1], dir = q[2];
    q.pop_front();
    q.pop_front();
    q.pop_front();
    int d = dist[x][y][dir];
    int nx = x + fx[dir], ny = y + fy[dir];
    if (nx >= 0 && nx < n && ny >= 0 && ny < m)
      add_front(nx, ny, dir, d);  // 判断条件
    if (grid[x][y] == '#')
      for (int i = 0; i < 4; i++)
        if (i != dir) add_back(x, y, i, d + 1);
  }
  if (dist[0][0][3] == INF)
    cout << -1 << endl;
  else
    cout << dist[0][0][3] << endl;
  return 0;
}
```

## ST表     [ST](https://www.luogu.com.cn/problem/P3865)

- ST表是用于解决 **可重复贡献问题** 的数据结构.可贡献问题指的是对于运算 opt,满足 x opt x = x,则对应的区间查询就是一个可重复贡献问题.例如 RMQ(区间最值),和max,gcd 类,但是区间和这种区间一旦重复区间和就一定会改变的不是,同时**opt必须满足结合律才能使用ST表**.O(nlogn) 询问,O(1)查询,但不支持区间修改,**二维DP表示从 i 长度为$\ 2^{j}\ 的区间内的最值,如此可以直接预处理所有长度为\ 2^{j}\ 的区间内的最值,询问的时候将长度分为两个前后区间查询$ **       

```c++
const int N = 1e5 + 10;//数据比较极限,需要加快读,否则超时.    可以证明 2 * pow(2,__lg(len)) >= len
int f[N][22];//长度为2^j次方,从i开始的最大值
int n, m;
int Log[N * 2];//因为log(a)表示小于等于a的2的最大几次方。即 2 ^ log <= a,即是log(a)下取整
int main() 
{
    n = read(),m = read();
    for (int i = 1; i <= n; i++) 
        cin >> f[i][0];//从i开始长度为 1 的最大值即为自己
    //更新f数组  转换为长度累加即可
    for (int i = 1; i < 22; i++) //求区间最大值,每次更新以二进制为基础
    {
        for (int j = 1; j + (1 << i) - 1 <= N; j++)
        {//拆分为左右两等段
            f[j][i] = max(f[j][i - 1], f[j + (1 << (i - 1))][i - 1]);
        }
    }
    int l ,r ;
    for (int i = 0; i < m; i++) 
    {
        l = read(),r = read();//求  l - r 内的最大值
        int s = __lg(r - l + 1);//__lg自带的log2的函数,返回的是log(n),下取整
        cout << max(f[l][s], f[r - (1 << s) + 1][s]) << endl;//为了防止越界或者不足,右边的区间起点用r-len
    }//所以l到r的最小值可以表示为min(从l往后2^t的最小值，从r往前2^t的最小值)
    return 0;
}

```

[Problem - E - Codeforces](https://codeforces.com/contest/1878/problem/E)

## 树状数组

<img src="C:\Users\19669\Desktop\nodeT\图片\树状数组 1.png" alt="树状数组解析图" style="zoom:80%;" />

<img src="C:\Users\19669\Desktop\nodeT\图片\树状数组2.png" alt="树状数组2" style="zoom: 80%;" />

### 单点修改求区间和

> - lowbit函数,add函数,sum函数都是基于二进制,**树状数组存的是区间前缀和**   [洛谷 楼兰图腾](https://www.luogu.com.cn/problem/U88715) ,

```c++
const int N = 2e5 + 10;//树状数组维护的是值不是坐标
int a[N];//原数组
int tr[N];//表示树状数组,表示y=i的高度出现了几次,树状数组tr本身存的就是前缀和,基于二进制跳动前缀和,不是常规前缀和
int great[N],lower[N];
int n; 
int lowbit(int x)
{
	return x & -x;
}//返回最后一个1及后面的0
void add(int x,int c)//在x的位置加上c
{
	for(int i=x;i<=n;i+=lowbit(i))//基于二进制前缀和,除了第一个读入的数据,存到 10,100,110,1000这种整二进制类型
		tr[i] += c;
}
int sum(int x)//查询操作
{
	int res = 0;
	for(int i=x;i;i-=lowbit(i))//前缀和,tr的子节点的求法,计算常规前缀和.
		res += tr[i];
	return res;	
}
int main()//树状数组中存的是高度出现次数的前缀和,不是区间下标
{
	_;
	cin >> n;
	for(int i=1;i<=n;i++)//对应的y
		cin >> a[i];
	for(int i=1;i<=n;i++)//先从左向右算一遍
	{
		int y = a[i];//下为前缀和求区间和,因为是全排列,所以最大为 n, y - 1的差值,为区间和
		great[i] = sum(n) - sum(y);//高度大于等于y + 1里面有多少数
		lower[i] = sum(y - 1);//高度为 1 到 y - 1 (包含)里面有多少数
		add(y,1);
	}
	memset(tr,0,sizeof tr);
	ll res1 = 0,res2 = 0;
	for(int i=n;i;i--)//倒序求出来 V 型 和 ^ 型
	{
		int y = a[i];
		res1 += great[i] * (ll)(sum(n) - sum(y));
		res2 += lower[i] * (ll)sum(y - 1);
		add(y,1);
	}
	cout << res1 << " " << res2 << endl;
    return 0;
}
```

### 区间修改单点求和

- 树状数组维护的是差分,树状数组 tr 记录的是差分的前缀和,即是当前元素(基于二进制,需要转换为前缀和)

```c++
const int N = 1e5 + 10;
int n,m,a[N];//原数组
int tr[N];//差分数组,树状数组维护的前缀和是基于lowbit,二进制的最后一位 1 所代表的大小所跳动的,不是基于 1 
int lowbit(int x)
{
    return x & -x;
}
void add(int x,int c)
{
    for(int i=x;i<=n;i+=lowbit(i))
        tr[i] += c;
}
ll sum(int x)//求和
{
    ll res = 0;
    for(int i=x;i;i-=lowbit(x))
        res += tr[i];
    return res;
}
int main()
{
    _;
    cin >> n >> m ;
    for(int i=1;i<=n;i++)
       cin >> a[i];//先读入原数组
    for(int i=1;i<=n;i++)
        add(i,a[i] - a[i -1]);//转换为差分数组
    while(m --)
    {
        char op[2]  ;
        int l,r ,d;
        cin >> op >> l;
        if(op[0] == 'C')
        {//修改区间,利用差分的性质,修改 l 和 r+1 两个位置即可,tr 求的二进制下的前缀和
            cin >> r >> d;
            add(l,d),add(r+1,-d);   
        }
        else
            cout << sum(l) << endl;//需要将二进制转换为常规前缀和 
    }
    return 0;
}
```

### 区间修改区间求和

- 区间修改加上或减去**同一个值**,树状数组维护的是数组b[]的前缀和,b是读入数组的**差分数组**,tr存的是差分数组的二进制前缀和,建立一个(n+1)*(n+1)的每一行$\ 为a_{1}到\ a_{n}\ 的矩阵发现,区间和等于,\sum_{1}^{n} a_{i}\ * (n + 1) - \sum_{1}^{n} i*a_{i}\ ,所以建立两个tr数组通过树状数组求解$

```c++
ll tr1[N];//树状数组维护b[]的前缀和(b是a的差分数组)
ll tr2[N];//维护b[i] * i 的前缀和
int n,m;//数组长度和操作个数
int lowbit(int x)
{
    return x & -x;
}
void add(ll tr[],int x,ll c)
{
    for(int i=x;i<=n;i+=lowbit(i))
        tr[i] += c;
}
ll sum(ll tr[],int x)
{
    ll res = 0;
    for(int i=x;i;i-=lowbit(i))
        res += tr[i];
    return res;
}
ll prefix_sum(int x)
{
    return sum(tr1,x) * (x + 1) - sum(tr2,x);
}
int main()
{
    _;
    cin >> n >> m;
    for(int i=1;i<=n;i++)
        cin >> a[i];
    for(int i=1;i<=n;i++)
    {
        int b = a[i] - a[i - 1];
        add(tr1,i,b);
        add(tr2,i,(ll)b * i);
    }
    while(m --)
    {
        char op[2];
        int l,r,d;
        cin >> op >> l >> r;
        if(op[0] == 'Q')
            cout << prefix_sum(r) - prefix_sum(l-1) << endl;
        else 
        {
            cin >> d;
            add(tr1,l,d),add(tr2,l,l * d);//修改a[l] += d;            
            add(tr1,r+1,-d),add(tr2,r+1,-(r+1)*d);//a[r+1] -= d;
        }
    }
}
```

- 区间中每个位置添加或减去的是**差分数组**,维护的差分数组的差分即是**二维差分**,建立树状数组的方法发现,即给 l 位置+ k , l + 1 位置 +(d−k) , 给r + 1 位置  −(k+len∗d), 给 r+2 位置 +(k+(len−1)∗d).

```c++
void add(int x, int c) {
	for (int i = x; i <= n; i += lowbit(i)) {
		t1[i] += c, t2[i] += 1ll * x * c;
	} 
}
ll ask(int x) {
	ll res = 0;
	for (int i = x; i; i -= lowbit(i)) res += (x + 1) * t1[i] - t2[i];
	return res;
}
add(i, x), add(i + 1, -x);//读入数据是同理同样需要四个位置
add(i + 1, -x), add(i + 2, x);
len = r - l + 1;//处理改变的差分数组
add(l, k), add(l + 1, d - k);
add(r + 1, -k - len * d), add(r + 2, k + (len - 1) * d);
```

- 树状数组结合二分求解,给定每一个数前面有多少比自己小的数,求每个数是多少,这些数是排列,从后往前遍历,确定一个数后add(i,-1)将这个数减去

```c++
 for(int i=1;i<=n;i++)
     tr[i] = lowbit(i);//每一个数都是 1,tr前缀和就是lowbit(i)
// for(int i=1;i=n;i++)
//     add(i,1);//两种初始化操作
for(int i=n;i;i--)
{//h[i]是输入的数,表示前面有多少比它小的数
    int k = h[i] + 1;//算上自己前面一共 k 头牛
    int l = 1,r = n;//二分
    while(l < r)//查找第一个大于等于k的数,是排列,第一个等于k的数
    {
        int mid = l + r >> 1;
        if(sum(mid) >= k) r = mid;
        else l = mid + 1;
    }//int r = upper_bound(sum + 1,sum + 1 + n,k) - sum;
    ans[i] = r;
    add(r,-1);
}
```

## 二维树状数组

### 单点修改,子矩阵查询

二维树状数组,用来维护**二维数组**上的单点修改和前缀信息.,与一维树状数组类似,

## 莫队

> 注意 : 莫队初始化的时候 L= 1,R = 0,否则容易出现边界问题,先左右指针移动,在时间戳移动,指针压缩时注意先增和后增 !!!
>
> **普通莫队**分块大小最佳为 $\sqrt{n}$,  **带修莫队**分块大小最佳为 $\sqrt[4]{n^3}$。
>
> 卡常：尽量不要使用STL，氧气$O~3~$优化不能少，快读不加可能会被卡常

### 伪莫队,左右端点不是很极限

```c++
int aa[maxn], cnt[maxn], l = 1, r = 0, now = 0; //每个位置的数值、每个数值的计数器、左指针、右指针、当前统计结果（总数）
void add(int pos) {//添加一个数
    if(!cnt[aa[pos]]) ++ now;//在区间中新出现，总数要+1
    ++ cnt[aa[pos]];
}
void del(int pos) {//删除一个数
    -- cnt[aa[pos]];
    if(!cnt[aa[pos]]) -- now;//在区间中不再出现，总数要-1
}
void work() {//优化2主过程
    for(int i = 1; i <= q; ++i) {//对于每次询问
        int ql, qr;
       	cin >> ql >> qr;//输入询问的区间
        while(l < ql) del(l ++);//如左指针在查询区间左方，左指针向右移直到与查询区间左端点重合
        while(l > ql) add(-- l);//如左指针在查询区间左端点右方，左指针左移
        while(r < qr) add(++ r);//右指针在查询区间右端点左方，右指针右移
        while(r > qr) del(r --);//否则左移
        cout << now << endl;//输出统计结果
    }
}//若询问端点在区间端点,在区间端点最坏情况下左右指针均需要移动O(nm)次,O(1)更新,复杂度还是O(nm),此时会超时
```

### 基础莫队 - $O(n\sqrt[]{n})$ - 离线做法,只支持查询不支持修改

- 预处理 : 莫队算法优化的核心是分块和排序,将大小为`n`的序列分为$\sqrt[]{n}$个块,从`1`到$\sqrt[]{n}$编号,然后根据这个对查询区间进行排序,一种方法是把查询区间按照**左端点所在块的序号**排序,如果左端点所在块相同,在按照**右端点**排序,排完之后再进行左右指针移动的操作.
- 设每个块内有 $x _{i}$个左端点,并且左端点同块的右端点有序,删除的操作是O(1),处理块` i` 的复杂度最坏的是$O(x\sqrt[]{n})$,指针跨越整块的复杂度是$O(\sqrt[]{n})$,最坏需要跨越 n 次,右端点最坏需要O(n)的时间跳,最多跳$O(\sqrt[]{n})$次,故总复杂度是$O(n√n)+O(n√n)+O(nlogn)$

```c++
bool cmp1(kkk a,kkk b){ return a.id<b.id; }		//复原原来顺序
bool cmp(kkk a,kkk b)
{							//询问排序
	if(blo[a.l] != blo[b.l])
        return a.l < b.l;		//先按左端点所在块为第一关键字排序
	return a.r < b.r;						//相同则按右端点排序
}
block = sqrt(n);	//块大小
for(int i=1;i<=n;i++) blo[i] = (i - 1) / block + 1;		//划分块
for(int i=1;i<=m;i++)
{
    cin >> a[i].l >> a[i].r;
    a[i].id = i;	//标记原来编号
}
sort(a+1,a+m+1,cmp);	//对询问排序
sort(a+1,a+m+1,cmp1);	//复原顺序
//排序之后的操作和伪莫队一样,del 和 add函数,优化主要在询问区间的顺序,防止询问顺序不合理导致不断遍历到端点
void work() {//对于排序过
    for(int i = 1; i <= q; ++i) {//对于每次询问
        int ql, qr;
       	cin >> ql >> qr;//输入询问的区间
        while(l < ql) del(l ++);//如左指针在查询区间左方，左指针向右移直到与查询区间左端点重合
        while(l > ql) add(-- l);//如左指针在查询区间左端点右方，左指针左移
        while(r < qr) add(++ r);//右指针在查询区间右端点左方，右指针右移
        while(r > qr) del(r --);//否则左移
        cout << now << endl;//输出统计结果
    }
}
```

### 莫队的优化

- #pragma GCC optimize(2)

> 开了 $O_{2}$,速度回快的多,大概是不开$O_{2}$的4-5倍

- 玄学奇偶性排序

> 对于**左端点**在同一奇数块的区间,右端点按升序排列,反之降序,主要原理是右指针跳完奇数块往回跳时在同一个方向能顺路把偶数跳完,然后跳完这个偶数块又能顺便把下一个奇数块跳完.

```c++
bool cmp(kkk a,kkk b){
	if(blo[a.l] != blo[b.l])
        return a.l < b.l;
	if(blo[a.l] % 2 == 0)
        return a.r > b.r;//多加了行判断,是奇数块右端点按照升序排列
		return a.r < b.r;//偶数块,右端点按照降序排列
}
int cmp(query a, query b) {
	return (belong[a.l] ^ belong[b.l]) ? belong[a.l] < belong[b.l] : ((belong[a.l] & 1) ? a.r < b.r : a.r > b.r);
}//异或操作,只有在同一个块才会返回0,不同返回1,即是三目运算符前面,直接按照左端点排序,然后按照奇偶块排序右端点
```

- 移动指针的常数压缩

```c++
//将add 和 del 函数以及判断l , r的大小直接压缩为
while(l < ql) now -= !-- cnt[aa[l ++]];
while(l > ql) now += !cnt[aa[-- l]] ++;
while(r < qr) now += !cnt[aa[++ r]] ++;
while(r > qr) now -= !-- cnt[aa[r --]];
//非常考验运算符优先级,不熟勿用
```

- 快读快写优化

- 代码实现 [小B的询问](https://www.luogu.com.cn/problem/P2709)

```c++
#include <bits/stdc++.h>
using namespace std;
#define int long long
#define endl "\n"
#define ios ios::sync_with_stdio(false),cin.tie(nullptr),cout.tie(nullptr);
#define pre(i,a,b) for(int i=a;i<=b;i++)
#define rep(i,a,b) for(int i=a;i>=b;i--)
const int N = 1e6 + 10;
int a[N],cnt[N],ans[N],belong[N];
int n,m,k;
struct query
{
    int l,r,id;
    bool operator<(query a)
    {
        return (belong[l] ^ belong[a.l] ? belong[l] < belong[a.l] : (belong[l] & 1 ? r < a.r : r > a.r));
    }//奇偶排序
}q[N];

void solve()
{
    cin >> n >> m >> k;
    int size = pow(n, 3.0 / 4.0);
//    pre(i,1,ceil(1.0 * n / size))//容易RE，这种写法会多开一个块，所以需要开大一点空间
//        pre(j,(i - 1) * size + 1;i * size)
//        	belong[j] = i;
    pre(i,1,n) //使用这中进行分块 ble = (i - 1) / size + 1
        cin >> a[i],belong[i] = (i - 1) / size + 1;
    pre(i,1,m)
    {
        int l,r;
        cin >> l >> r;
        q[i] = {l,r,i};
    }
    sort(q + 1,q + 1 + m);
    int l = 1,r = 0,now = 0;
    pre(i,1,m)
    {
        int ql = q[i].l,qr = q[i].r;
        while(l < ql) now -= 2 * cnt[a[l ++]]-- - 1;//删除
        while(l > ql) now += 1 + 2 * cnt[a[-- l]] ++;
        while(r < qr) now += 1 + 2 * cnt[a[++ r]] ++;
        while(r > qr) now -= 2 * cnt[a[r --]] -- - 1;  
        ans[q[i].id] = now;   
    }//前自增后自增理解一下即可,在内部是计算过的,在外面是未计算的
    pre(i,1,m)
        cout << ans[i] << endl;
}
// #define LOCAL
signed main()
{
    ios 
    int t=1;
    while(t--)
		solve();
    return 0;
}
```

### 扩展  -- 维护可任意位置删除的栈

[莫队+模拟可任意删除栈](https://www.luogu.com.cn/problem/CF1000F?contestId=152942)

> 本题非常卡常，奇偶排序，快读，氧气优化都要使用，不能使用STL
> 关键点在于模拟可以在任意位置删除的栈，代表存放莫队中只出现一次的元素，没有这类元素，输出0

```cpp
#pragma GCC optimize (3)
#pragma GCC optimize ("Ofast")
#include<bits/stdc++.h>
using namespace std;
#define pre(i,a,b) for(int i=a;i<=b;i++)
const int N = 2e6 + 10;
int n,m;
int a[N],ble[N],cnt[N],stk[N],pos[N],top;//主要是模拟可以删除栈任意位置的元素，做到O(1)删除和添加
int ans[N];//模拟栈的删除功能是直接将被删除的数替换为现在的栈顶元素即可，本身是栈顶是即是清零操作
char *p1,*p2,buf[100000];//快读和同步流二者只能选一个
#define nc() (p1==p2 && (p2=(p1=buf)+fread(buf,1,100000,stdin),p1==p2)?EOF:*p1++)
int read()
{
    int x = 0,f = 1;char ch = nc();
    while(ch<48||ch>57)  {if(ch=='-')f=-1;ch=nc();}
    while(ch>=48&&ch<=57)  x=x*10+ch-48,ch=nc();
    return x*f;
}
void write(int x)
{
    if(x<0) putchar('-'),x=-x;
    if(x>9) write(x/10);
    putchar(x%10+'0');
    return;
}
struct query
{
    int l,r,id;
    bool operator<(query w)
    {
        return (ble[l] ^ ble[w.l] ? ble[l] < ble[w.l] : (ble[l] & 1) ? r < w.r : r > w.r);
    }
}q[N];
void add(int x)
{
    ++ cnt[x];
    if(cnt[x] == 1)
    {
        stk[++ top] = x;//栈顶元素是 x
        pos[x] = top;//栈顶元素 x 的下标是 top
    }
    else if(cnt[x] == 2)
    {
        stk[pos[x]] = stk[top];//栈顶元素替换 x 元素所在的位置
        pos[stk[top]] = pos[x];//对应的下标也替换
        stk[top --] = pos[x] = 0;//将 x 元素从栈中删除,清零
    }
}
void del(int x)
{
    -- cnt[x];
    if(cnt[x] == 1)
    {
        stk[ ++ top] = x;
        pos[x] = top;
    }
    else if(cnt[x] == 0)//从栈中删除一个元素
    {
        stk[pos[x]] = stk[top];
        pos[stk[top]] = pos[x];
        stk[top --] = pos[x] = 0; 
    }
}
void solve()
{
    n = read();
    int size = sqrt(n);
    pre(i,1,n)
    {
        a[i] = read();
        ble[i] = (i - 1) / size + 1;
    }
    int bnum = ble[n];
    m = read();
    pre(i,1,m)
    {
        q[i].l = read();q[i].r = read();
        q[i].id = i;
    }
    sort(q + 1,q + 1 + m);
    int l = 1,r = 0;
    pre(i,1,m)
    {
        int ql = q[i].l,qr = q[i].r,id = q[i].id;
        while(l < ql)   del(a[l ++]);
        while(l > ql) add(a[-- l]);
        while(r < qr) add(a[++ r]);
        while(r > qr) del(a[r --]);
        ans[id] = stk[top];
    }
    pre(i,1,m)
        write(ans[i]),puts("");
}
signed main()
{
    int t = 1;
    // cin >> t;
    while(t --)
        solve();
}

```



### 莫队算法的扩展 --- 带修改的莫队

[洛谷P1903](https://www.luogu.com.cn/problem/P1903)

- 基础莫队只能离线操作,对于需要修改的强制在线操作,某些允许离线的待修改区间查询来说,将莫队直接加上一维,变为待修莫队可以实现操作.(具体操作是将修改操作标号,记作"时间戳",**而查询操作的时间戳沿用之前最近的修改操作的时间戳**,跑主算法时定义当前时间戳为 t, 对于每个查询操作,如果当前时间戳相对大了,把之前的改回来,反之往后改,只有当前区间与查询区间左右端点,时间戳都重合时,才认定区间完全重合,求出答案.)
- 通俗的说就是在加上一个时间戳指针,在修改操作上跳,区间的移动方向由四个变为六个

```c++
([l - 1,r],[l + 1,r],[l,r - 1],[l,r + 1]);//原来的四个
([l - 1,r ,t],[l + 1,r ,t],[l,r - 1,t],[l,r + 1,t],[l,r,t - 1],[l,r,t + 1]);//加上时间戳六个
int cmp(query a, query b) {//排序的时候加上时间戳即可
	return (belong[a.l] ^ belong[b.l]) ? belong[a.l] < belong[b.l] : ((belong[a.r] ^ belong[b.r]) ? belong[a.r] < belong[b.r] : a.time < b.time);
}
```

- 移完 t 后做完一处修改后可能需要改回来所以可能需要将原值存下来备用.分块大小为 $\sqrt[4]{n^{3}t}$ 时复杂度最优,死记即可.

```c++
#include<bits/stdc++.h>
using namespace std;
#define maxn 50500
#define maxc 1001000
int a[maxc], cnt[maxc], ans[maxc], belong[maxn];
struct query {
	int l, r, time, id;
} q[maxc];
struct modify {
	int pos, color, last;
} c[maxc];
int cntq, cntc, n, m, size, bnum;
int cmp(query a, query b) {
	return (belong[a.l] ^ belong[b.l]) ? belong[a.l] < belong[b.l] : ((belong[a.l] ^ belong[b.l]) ? belong[a.r] < belong[b.r] : a.time < b.time);
}
#define isdigit(x) ((x) >= '0' && (x) <= '9')
inline int read() {
	int res = 0;
	char c = getchar();
	while(!isdigit(c)) c = getchar();
	while(isdigit(c)) res = (res << 1) + (res << 3) + (c ^ 48), c = getchar();
	return res;
}
int main() {
	n = read(), m = read();
	size = pow(n, 3.0 / 4.0);//块的大小,这里块的大小是 n^(3/4)
	bnum = ceil((double)n / size);//分块的个数
	for(int i = 1; i <= bnum; ++i) 
		for(int j = (i - 1) * size + 1; j <= i * size; ++j)
            belong[j] = i;
	for(int i = 1; i <= n; ++i) 
		a[i] = read();
	for(int i = 1; i <= m; ++i) {
		char opt[100];
		scanf("%s", opt);
		if(opt[0] == 'Q') {//区间,
			q[++cntq].l = read();
			q[cntq].r = read();
			q[cntq].time = cntc;//代表当前查询前面有多少次修改
			q[cntq].id = cntq;//第几次查询
		}
		else if(opt[0] == 'R') {//修改操作
			c[++cntc].pos = read();//cntc 代表修改操作的编号
			c[cntc].color = read();
		}
	}
	sort(q + 1, q + cntq + 1, cmp);
	int l = 1, r = 0, time = 0, now = 0;
	for(int i = 1; i <= cntq; ++i) {
		int ql = q[i].l, qr = q[i].r, qt = q[i].time;//当前询问区间左端点右端点,之前做多少修改操作.
		while(l < ql) now -= !--cnt[a[l++]];//先移动左右端点,压缩指针版  区间删除
		while(l > ql) now += !cnt[a[--l]]++;//区间添加
		while(r < qr) now += !cnt[a[++r]]++;//区间添加
		while(r > qr) now -= !--cnt[a[r--]];//区间删除
		while(time < qt) { //时间戳移动
			++time;//区间添加先改变指针,因为操作的指针不在区间内,先加到区间内,在考虑改变
			if(ql <= c[time].pos && c[time].pos <= qr) now -= !--cnt[a[c[time].pos]] - !cnt[c[time].color]++;
			swap(a[c[time].pos], c[time].color);//只有这个修改下标在询问区间里才有更改的必要,否则不会对答案产生影响
		}//交换操作,将 time 对应下标的颜色修改为执行修稿操作后的颜色,并存下来,方便后续移动时间戳回溯.
		while(time > qt) {//区间删除,先改变对答案的影响,因为指针在区间内.
			if(ql <= c[time].pos && c[time].pos <= qr) now -= !--cnt[a[c[time].pos]] - !cnt[c[time].color]++;
			swap(a[c[time].pos], c[time].color);
			--time;
		}
		ans[q[i].id] = now;
	}
	for(int i = 1; i <= cntq; ++i) 
		printf("%d\n", ans[i]);
	return 0;
}
```

### 回滚莫队

> **回滚莫队的数组需要开大一点不然就会 WA ，**而且左右指针移动的数组一定要在重复下一次操作前清零（不在同一块了）。

使用背景 ：当普通莫队不可解的问题就是在转移区间过程中，可能是删点或加点操作**之一**，此时使用回滚莫队解决。

#### 只加不减的回滚莫队

加点操作可以实现，但是删点操作无法有效的实现：
> 1、对原序列进行分块，并对询问按照如下的方式排序，**以左端点所在的块升序为第一关键字，以右端点升序为第二关键字**。
> 2、对于处理所有左端点在块**T**内的询问，先将莫队区间左端点初始化为**R[T] + 1，右端点初始化为R[T]，这是一个空区间**
> 3、对于左右端点在同一个块中的询问，直接暴力扫描回答
> 4、对于左右端点不在同一块中的所有询问，由于其右端点升序，我们对右端点只做加法操作，总共最多加点`n`次
> 5、对于左右端点不在同一块中的所有询问，其左端点可能是乱序的，每一次从**R[T] + 1**的位置出发，只做加法操作，到达询问位置即可，每一次询问最多加$\sqrt{n}$次。回答完后，**撤销本次移动左端点的所有改动，使左端点回到R[T] + 1的位置。**
> 6、依次处理下一块

根据其操作的过程可知，回滚莫队的时间复杂度仍然是$O(n\sqrt{n})$,并且，在回答询问的过程中我们只进行了加点操作，没有涉及删点操作，这样就完成了我们需要的操作。

#### 只减不加的回滚莫队

和上一种典型的回滚莫队类似，还可以实现只有删点操作没有加点操作的回滚莫队，当然，前提是我们可以正确的先将整个序列加入莫队中：
> 1、对原序列进行分块，并对询问按照如下的方式排序：**以左端点所在的块升序作为第一关键字，以右端点降序作为第二关键字**。
> 2、对于处理所有左端点在块**T**内的询问，先将莫队区间左端点初始化为**L[T]**，右端点初始化为`n`，这是一个大区间。
> 3、对于左右端点在同一个块中的询问，直接暴力扫描即可。
> 4、对于左右端点不在同一个块中的所有询问，由于其右端点降序，从`n`的位置开始，我们对右端点只做删点操作，总共最多删点`n`次。
> 5、对于左右端点不在同一个块中的所有询问，其左端点是可能乱序的，我们每一次从`L[T]`的位置出发，只做删点操作，达到询问位置即可，每一个询问最多加$\sqrt{n}$次，回答完之后，**我们撤销本次移动左端点的所有改动，使左端点回到L[T]的位置**。
> 6、重复。

同样的，回滚莫队的时间复杂度还是$O(n\sqrt{n})$，并且我们只使用了删点操作，只有在一开始时将整个序列加入莫队中，这样就完成了我们需要的操作

[只加不删的回滚莫队](https://www.luogu.com.cn/problem/AT_joisc2014_c)

求元素权值 * 区间出现次数的最大值，加点容易实现，但是删点之后的次大值无法维护所以不能删点，因此我们需要只加不删的回滚莫队，**撤销操作**就是在桶中`(cnt数组，出现的次数)`减去出现次数，而不管答案是否改变，在下一次加点的过程中答案就得以统计了。

```cpp
#include<bits/stdc++.h>
using namespace std;
#define _ ios::sync_with_stdio(0);cin.tie(0);cout.tie(0);
#define endl '\n'
#define int long long 
#define pre(i,a,b) for(int i=a;i<=b;i++)
#define rep(i,a,b) for(int i=a;i>=b;i--)
const int N = 1e5 + 10;
int n,m;
int a[N],typ[N],cnt[N],cnt2[N],ble[N],lb[N],rb[N],inp[N];
int ans[N];//typ表示离散化后的数组，原数组数据范围太大，需要离散化
struct query
{
    int l,r,id;
    bool operator<(query w)
    {
        return (ble[l] ^ ble[w.l] ? ble[l] < ble[w.l] : r < w.r);//回滚莫队不能奇偶排序
    }//需要保证同一块的内的右端点单调递增。
}q[N];

void solve()
{
    cin >> n >> m;
    int size = pow(n,3.0 / 4.0);
    int bnum = ceil(1.0 * n / size);//这里不要忘记转换为double
    for(int i=1;i<=bnum;i++)
    {
        lb[i] = size * (i - 1) + 1;
        rb[i] = size * i;
        for(int j=(i - 1) * size + 1;j <= i * size;j ++)
            ble[j] = i;
    }
    rb[bnum] = n;
    for(int i=1;i<=n;i++)
    {
        cin >> a[i];
        inp[i] = a[i];
    }
    sort(inp + 1,inp + 1 + n);
    int tot = unique(inp + 1,inp + n + 1) - inp - 1;//去重数组，判断一共有多少不同的数
    for(int i=1;i<=n;i++)
        typ[i] = lower_bound(inp + 1,inp + tot + 1,a[i]) - (inp - 1) - 0 + 1;//离散化操作离散到以0开始
    for(int i=1;i<=m;i++)
    {
        int l,r;
        cin >> l >> r;
        q[i] = {l,r,i};
    }
    sort(q + 1,q  + 1 + m);//排序：按照左端点的块升序和右端点为两个键值排序。
    int i = 1;//枚举询问到的块
    for(int k=0;k<=bnum;k++)//枚举每一个块
    {
        int l = rb[k] + 1,r = rb[k];//初始化，将左端点初始化为块右端点加一，右端点为块右端点--空区间
        int now = 0;
        fill(cnt,cnt + 1 + n,0);//莫队本身的cnt数组，记录出现次数
        for(;ble[q[i].l] == k;i ++)//当前的询问区间在这个块内
        {
            int ql = q[i].l,qr = q[i].r,id = q[i].id,tmp;
            if(ble[ql] == ble[qr])//左右端点属于同一个块 k
            {
                tmp = 0;
                for(int j=ql;j<=qr;j++)
                    cnt2[typ[j]] = 0;//暴力扫描的 cnt，块内清零操作
                for(int j=ql;j<=qr;j++)
                {
                    ++ cnt2[typ[j]];//计算块内的数据
                    tmp = max(tmp,cnt2[typ[j]] * a[j]);
                }
                ans[id] = tmp;//在同一个块内的询问直接在块内暴力扫描
                continue;
            }
            //不在一个块内的询问  -- 只加不删的回滚莫队
            while(r < qr)//右端点不够，右端点右移
            {
                ++ r;
                ++ cnt[typ[r]];
                now = max(now,cnt[typ[r]] * a[r]); 
            }
            tmp = now;//暂时的最大值，记录撤销操作
            while(l > ql)//左端点不够，左端点左移
            {
                -- l;
                ++ cnt[typ[l]];
                now = max(now,cnt[typ[l]] * a[l]);
            }
            ans[id] = now;
            //撤销操作，对于不在同一个块内的询问，左端点可能是乱序的，需要使左端点左移在撤销
            while(l < rb[k] + 1)
            {
                -- cnt[typ[l]];
                l ++ ;
            }
            now = tmp;
        }
    }
    for(int i=1;i<=m;i++)
        cout << ans[i] << endl;
}

signed main()
{
    _;
    int t = 1;
    // cin >> t;
    while(t -- )
        solve();
}
```

[只删不加的回滚莫队](https://www.cnblogs.com/Parsnip/p/10969989.html#3719129404)

长度为`n`的数组，`m`次询问，每次询问一个区间内最小没有出现过的自然数。
维护桶中出现过的数字，那么`mex`即是询问答案，删点操作容易实现，可以顺带更新答案，但是加点操作，原来的最小值在加点的过程中出现了，无从得知新的答案，显然，一开始将整个序列加入桶中并统计答案是可行的，只删不加的回滚莫队。
撤销操作还是在桶中更新，但不管答案的变化即可。

```cpp
#include<bits/stdc++.h>
using namespace std;
#define _ ios::sync_with_stdio(0);cin.tie(0);cout.tie(0);
#define endl '\n'
#define int long long 
const int N = 2e5 + 10,M = 1020;
int n,m;
int a[N],cnt[N],mi,ans[N];
int cnt1[N],ans1,ble[N],L[N],R[N];
struct query
{
    int l,r,id;
    bool operator<(query w)
    {
        return (ble[l] ^ ble[w.l] ? ble[l] < ble[w.l] : r > w.r);
    }
}q[N];
// 删点
inline void remove(int p,int &Minval) 
{
    if ( a[p] > n+1 ) return;
    cnt[a[p]]--;
    if ( cnt[a[p]] == 0 ) Minval = min( Minval , a[p] );
}
// 撤销
inline void resume(int p)
{
    if ( a[p] > n+1 ) return;
    cnt[a[p]]++;
}
void solve()
{
    cin >> n >> m;
    for(int i=1;i<=n;i++)   
        cin >> a[i];
    for(int i=1;i<=m;i++)
    {
        cin >> q[i].l >> q[i].r;
        q[i].id = i;
    }
    //将整个序列加入莫队中，同时得到整体的答案
    for(int i=1;i<=n;i++)
        if(a[i] <= n + 1)
            cnt[a[i]] ++;
    while(cnt[ans1]) ans1 ++;
    //分块
    int size = sqrt(n);
    int bnum = n / size;
    for(int i=1;i<=bnum;i++)
    {
        if(i * size > n) break;
        L[i] = (i - 1) * size + 1;
        R[i] = i * size;
        for(int j=L[i];j<=R[i];j++)
            ble[j] = i;
    }
    R[bnum] = n;
    //比较
    sort(q + 1,q + 1 + m);
    int l = 1,r = n,last = 0;
    for(int i=1;i<=m;i++)
    {
        //处理同一区间的询问
        if(ble[q[i].l] == ble[q[i].r])
        {
            for(int j=q[i].l;j<=q[i].r;j++)
                if(a[j] <= n + 1) cnt1[a[j]] ++;
            int tmp = 0;
            while(cnt1[tmp]) tmp ++;
            ans[q[i].id] = tmp;
            for(int j=q[i].l;j<=q[i].r;j++)
                if(a[j] <= n + 1) cnt1[a[j]] --;
            continue;
        }
        //如果移动到了一个新的块
        if(ble[q[i].l] ^ last)
        {
            while(r < n) resume( ++ r);
            while(l < L[ble[q[i].l]]) remove(l ++,ans1);
            mi = ans1,last = ble[q[i].l];
        }
        //单调移动右端点
        while(r > q[i].r) remove(r -- ,mi);
        //移动左端点回答询问
        int tmp = mi,ll = l;
        while(ll < q[i].l) remove(ll ++ ,tmp);
        //回滚
        while(ll > l)   resume(--ll);
        ans[q[i].id] = tmp;
    }
    for(int i=1;i<=m;i++)
        cout << ans[i] << endl;

}
signed main()
{
    _;
    int t = 1;
    // cin >> t;
    while(t -- )
        solve();
}
```



### 莫队算法的扩展 --- 树上莫队

问题一 : 子树统计

- 在原树上跑一遍dfs序,发现一颗子树其实就是里面的一段固定区间,边跑dfs边弄子树对应的左右端点即可,这里序列的长度 = 结点的个数,实际上不必用莫队,只需要传个标记即可,复杂度O(nlogn)

![树上莫队](图片/树上莫队.jpg)

问题二 : 路径上的统计   --- [洛谷COT2 - Count on a tree II](https://www.luogu.com.cn/problem/SP10707)

![树上莫队欧拉序](图片/树上莫队欧拉序.jpg)

- 求的是 u 到 v 上有多少不同的整数,由于区间没有对应关系,所以普通的dfs序不行,这里需要用到欧拉序(特殊的dfs序,可以解决很多dfs序不能解决的问题),详细见欧拉序总结讲解,这里用的是第一种欧拉序,求树上莫队,
- 子树 : 第一种欧拉序上两个相同编号(X),之间所有的编号都出现两次,且都位于 X 子树上,
- 两点之间的路径 : 设每个点第一次最后一次出现的位置为first[x],last[x],那么对于路径x -> y,设 f[x] <= f[y] (不满足swap,均是简写),这个操作的意义在于,**如果x,y在一条链上**,则 x 一定是 y 的祖先或者等于 y,如果lca(x,y)=x,**则直接把 ( f[x],f[y] ) 的区间**扯过来用,**反之使用 ( l[x],f[y] )区间,**(不用f[x]到l[x]的路径是因为中间的点一定不在路径上,里面的点都出现过两次,但是求路径的时候我们需要找的是出现奇数次的点),**但这个区间内不包括lca(x,y),故需要最后加上**(不在同一条链上)

```c++
//由于无需考虑的点会出现两次，我们可以弄一个标记数组（标记结点是否被访问），没访问就加，访问过就删，每次操作把标记·异或个1，完美解决所有添加、删除、去双问题。
#include<bits/stdc++.h>
using namespace std;
#define maxn 200200
#define isdigit(x) ((x) >= '0' && (x) <= '9')
inline int read() {
	int res = 0;
	char c = getchar();
	while(!isdigit(c)) c = getchar();
	while(isdigit(c)) res = (res << 1) + (res << 3) + (c ^ 48), c = getchar();
	return res;
}
int aa[maxn], cnt[maxn], first[maxn], last[maxn], ans[maxn], belong[maxn], inp[maxn], vis[maxn], ncnt, l = 1, r, now, size, bnum; //莫队相关
int ord[maxn], val[maxn], head[maxn], depth[maxn], fa[maxn][30], ecnt;
int n, m;
struct edge {
	int to, next;
} e[maxn];
void adde(int u, int v) {
	e[++ecnt] = (edge){v, head[u]};
	head[u] = ecnt;
	e[++ecnt] = (edge){u, head[v]};
	head[v] = ecnt;
}
void dfs(int x) {//欧拉序的第一种写法求子树和树上莫队常用,由于结合LCA,将父节点存了下来,所以不需要再传入父节点,调用即可.
	ord[++ ncnt] = x;
	first[x] = ncnt;
	for(int k = head[x]; k; k = e[k].next) {
		int to = e[k].to;
		if(to == fa[x][0]) continue;//f[x][0]表示当前的点的第一个父节点
		depth[to] = depth[x] + 1;
		fa[to][0] = x;
		for(int i = 1; (1 << i) <= depth[to]; ++i) fa[to][i] = fa[fa[to][i - 1]][i - 1];//LCA,倍增写法
		dfs(to);
	}
	ord[++ ncnt] = x;
	last[x] = ncnt;
}
int getlca(int u, int v) {//LCA倍增思路,依据二进制不断上跳求最近公共祖节点
	if(depth[u] < depth[v]) swap(u, v);
	for(int i = 20; i + 1; --i) //先跳深度,将深度尽量压缩到一个小范围,
		if(depth[u] - (1 << i) >= depth[v]) u = fa[u][i];
	if(u == v) return u;
	for(int i = 20; i + 1; --i)//在跳父节点,判断每个点对应的二进制下的父节点是否相同.
		if(fa[u][i] != fa[v][i]) u = fa[u][i], v = fa[v][i];
	return fa[u][0];
}
struct query {
	int l, r, lca, id;
} q[maxn];
int cmp(query a, query b) {
	return (belong[a.l] ^ belong[b.l]) ? (belong[a.l] < belong[b.l]) : ((belong[a.l] & 1) ? a.r < b.r : a.r > b.r);
}
void work(int pos) {//因为节点最多出现一次,第一次出现时记作加,先计算个数在对数量修改,此时为后件,此后被标记为1,再出现减去,注意两种++, -- 的顺序不一样,因为计算的时候改变导致,仔细理解
	vis[pos] ? now -= !--cnt[val[pos]] : now += !cnt[val[pos]]++;
	vis[pos] ^= 1;//标记数组(标记节点是否被访问过,没访问过就加,访问过就删,每次操作标记 ^ 1)
}
int main() {
	n = read(); m = read();
	for(int i = 1; i <= n; ++i) 
		val[i] = inp[i] = read();
	sort(inp + 1, inp + n + 1);
	int tot = unique(inp + 1, inp + n + 1) - inp - 1;
	for(int i = 1; i <= n; ++i)
		val[i] = lower_bound(inp + 1, inp + tot + 1, val[i]) - inp;
	for(int i = 1; i < n; ++i) adde(read(), read());
	depth[1] = 1;
	dfs(1);
	size = sqrt(ncnt), bnum = ceil((double) ncnt / size);
	for(int i = 1; i <= bnum; ++i)//分块操作
		for(int j = size * (i - 1) + 1; j <= i * size; ++j) belong[j] = i;
	for(int i = 1; i <= m; ++i) {
		int L = read(), R = read(), lca = getlca(L, R);
		if(first[L] > first[R]) swap(L, R);
		if(L == lca) {//在同一条链上
			q[i].l = first[L];
			q[i].r = first[R];
		}
		else {
			q[i].l = last[L];
			q[i].r = first[R];
			q[i].lca = lca;
		}//注意存进结构体的是位置不是节点值,遍历的是两个位置之间的节点值
		q[i].id = i;
	}
	sort(q + 1, q + m + 1, cmp);//排序优化莫队
	for(int i = 1; i <= m; ++i) {
		int ql = q[i].l, qr = q[i].r, lca = q[i].lca;
		while(l < ql) work(ord[l++]);//区间删除
		while(l > ql) work(ord[--l]);//区间添加
		while(r < qr) work(ord[++r]);//区间添加
		while(r > qr) work(ord[r--]);//区间删除
		if(lca) work(lca);//这里的区间添加删除只是对于区间长度来说,对于答案的贡献需要判断是第一次出现还是第二次出现,
		ans[q[i].id] = now;
		if(lca) work(lca);
	}
	for(int i = 1; i <= m; ++i) cout << ans[i] << endl;
	return 0;
}
```

## 树链剖分

**树剖**是指通过**轻重边**剖分将树分割成多条链，然后利用数据结构来维护这些链（本质是一种优化暴力）

明确定义 ：

- 重儿子 ： 父亲节点的所有儿子中子树节点数目最多（size最大）的结点；
- 轻儿子 ： 父亲节点中除了重儿子以外的儿子；
- 重边 ： 父亲节点和重儿子连成的边；
- 轻边 ： 父亲节点和轻儿子连成的边；
- 重链 ： 由多条重边连接而成的路径；
- 轻链 ： 由多条轻边连接而成的路径。                             

![树链剖分1图](图片/树链剖分1图.png)

```cpp
//变量声明
int f[N],d[N],si[N],son[N],rk[N],top[N],id[N];
//数组依次是 ：f[N] 保存节点 u 的父亲节点  ；d[N] 保存节点 u 的深度 ；si[N] 保存以 u 为根的子树大小 ；son[N] 保存重儿子 ；rk[N] 保存当前 dfs标号在树中所对应的节点 ；top[N] 保存当前节点所在链的顶端节点 ；id[N] 保存树中每个节点剖分以后得新编号（DFS的执行顺序）
```

1、对于一个点我们首先求出它所在的子树大小，找到他的重儿子（即处理出size，son数组）

2、在`dfs`过程中顺便记录其父亲以及深度（即处理出`f，d`数组），操作1,2可以一次`dfs`实现。

```cpp
void dfs(int u,int fa,int dep)//当前节点，父节点，深度
{
    fa[u] = fa;
    d[u] = dep;
    si[u] = 1;//点本身先记一个size
    for(int i=h[u];~i;i=ne[i])
    {
        int j = e[i];
        if(j == fa) continue;
        dfs(j,u,dep + 1);//层数加一
        si[u] += si[j];//更新父节点的size
        if(si[j] > si[son[u]])
            son[u] = j;//更新重儿子
    }
}
dfs(root,0,1);
```

<img src="图片/树链剖分2.png" alt="树链剖分2" style="zoom:100%;" />

3、第二遍`dfs`，然后连接重链，同时标记每一个节点的`dfs序`,并且为了用数据结构来维护重链，在`dfs`时保证一条重链上各个节点`dfs序`连续（即处理出数组`top,id,rk`）

```cpp
void dfs2(int u,int t)//当前节点，重链顶端
{
    top[u] = t;
    id[u] = ++ cnt;//标记dfs序
    rk[cnt] = u;//序号 cnt 对应节点 u 
    if(!son[u]) return ;
    dfs2(son[u],t);
    //选择优先进入重儿子来保证一条重链上各个节点 dfs序连续，一个点和他的重儿子处于同一条重链，所以重儿子所在重链顶端还是 t
    for(int i=h[u];~i;i=ne[i])
    {
        int j = e[i];
        if(j != son[u] && j != f[u])//不是重儿子，不是父节点，当前点一定是新的重链，top即是j，从j接着dfs2,
            dfs2(j,j);//一个点位于轻链底端，那么它的top必然是自身，，，，例如 2,3 点
    }
}
```

![树链剖分3](图片/树链剖分3.png)

4、两遍`dfs`就是树链剖分的主要处理，通过`dfs`我们已经保证一条重链上各个节点`dfs`序连续，可以通过数据结构（例如线段树）来维护一条重链的信息。

性质：

- 如果（u，v)是一条轻边，那么`size(v)  < size(u) / 2`
- 从根节点到任意节点的路所经过的轻重链的个数必定都小于`logn`，时间复杂度为`O(nlog^2n)`

 [树链剖分模版](https://www.luogu.com.cn/problem/P3384)

一共 4 个操作，1、将树从`x`到`y`结点最短路径上所有节点的值都加上`z`；  2、表示求树从`x`到`y`结点最短路径上所有节点值之和；  3、表示将以`x`为根节点的子树内所有节点值都加上`z` ； 4、表示求以`x`为根节点的子树所有节点值之和。

```cpp
#include<bits/stdc++.h>
using namespace std;
#define _ ios::sync_with_stdio(0);cin.tie(0);cout.tie(0);
#define endl '\n'
#define int long long 
#define len(u) (tr[u].r - tr[u].l + 1)//！！！括号一定要加！！！
#define pa tr[u]
#define ls tr[u << 1]
#define rs tr[u << 1 | 1]
const int N = 2e5 + 10;
int h[N],e[N << 1],ne[N << 1],idx;//建图，本题的最短路径是节点数最少，边权均为 1，类似LCA，top
struct tree                       //实现快速跳跃，不断的寻找在同一条重链上的两点
{
    int l,r,sum,lazy;
}tr[N << 2];
int n,m,r,rt,mod;
int a[N];
int f[N],dep[N],son[N],si[N],top[N],id[N],rk[N],cnt;//dfs树链剖分使用
void add(int a,int b)
{
    e[idx] = b,ne[idx] = h[a],h[a] = idx ++;
}
void dfs1(int u,int fa)
{
    si[u] = 1;
    f[u] = fa;
    dep[u] = dep[fa] + 1;
    for(int i=h[u];~i;i=ne[i])
    {
        int j = e[i];
        if(j == fa) continue;
        dfs1(j,u);
        si[u] += si[j];
        if(si[j] > si[son[u]])
            son[u] = j;
    }
}
void dfs2(int u,int tp)
{
    top[u] = tp,id[u] = ++ cnt,rk[cnt] = u;
    if(!son[u])
        return ;
    dfs2(son[u],tp);
    for(int i=h[u]; ~i;i=ne[i])
    {
        int j = e[i];
        if(j != son[u] && j != f[u])
            dfs2(j,j);
    }
}
void pushup(int u)
{
    pa.sum = (ls.sum + rs.sum) % mod;
}
void build(int u,int l,int r)
{
    if(l == r)
        tr[u] = {l,r,a[rk[l]] % mod,0};//注意这里需要将对应的dfs序的时间戳转换为原数组的值
    else 
    {
        tr[u] = {l,r,0,0};
        int mid = l + r >> 1;
        build(u << 1,l,mid);
        build(u << 1 | 1,mid + 1,r);
        pushup(u);
    }
}
void pushdown(int u)
{
    ls.lazy = (ls.lazy + pa.lazy) % mod;
    rs.lazy = (rs.lazy + pa.lazy) % mod;
    ls.sum = (ls.sum + (len(u << 1) * pa.lazy % mod)) % mod;
    rs.sum = (rs.sum + (len(u << 1 | 1) * pa.lazy % mod)) % mod;
    pa.lazy = 0;
}
void modify(int u,int l,int r,int c)
{
    c %= mod;
    if(pa.l >= l && pa.r <= r)
    {
        pa.lazy = (pa.lazy + c) % mod;
        pa.sum = (pa.sum + len(u) * c % mod) % mod;
        return ;
    }
    pushdown(u);
    int mid = pa.l + pa.r >> 1;
    if(l <= mid)
        modify(u << 1,l,r,c);
    if(r > mid)
        modify(u << 1 | 1,l,r,c);
    pushup(u);
}
int query(int u,int l,int r)
{
    if(pa.l >= l && pa.r <= r)
    {
        return pa.sum % mod;
    }
    pushdown(u);
    int mid = pa.l + pa.r >> 1,s = 0;
    if(l <= mid)
        s = (s + query(u << 1,l,r)) % mod;
    if(r > mid)
        s = (s + query(u << 1 | 1,l,r)) % mod;
    return s % mod;
}
int sum(int x,int y)
{//注意这里的最短路径是指节点数最少，点有权值，边没有边权，即是每个边的边权均为 1
    int s = 0;
    while(top[x] != top[y])//类似于LCA的功能不断地向上跳x,y，直到x，y在同一条重链上，然后直接计算
    {
        if(dep[top[x]] < dep[top[y]])
            swap(x,y);//不断交换x，y，x向上跳跃，直至共重链即可直接求解区间和
        s = (s + query(1,id[top[x]],id[x])) % mod;
        x = f[top[x]];
    }
    if(id[x] > id[y])
        swap(x,y);//此时x，y在同一条重链上，直接求线段树区间和即可
    return (s + query(1,id[x],id[y])) % mod;
}
void updates(int x,int y,int c)
{
    c %= mod;
    while(top[x] != top[y])
    {
        if(dep[top[x]] < dep[top[y]])
            swap(x,y);
        modify(1,id[top[x]],id[x],c);
        x = f[top[x]];
    }
    if(id[x] > id[y])
        swap(x,y);
    modify(1,id[x],id[y],c);
}
signed main()
{
    _;
    cin >> n >> m >> r >> mod;
    for(int i=1;i<=n;i++)
        cin >> a[i];
    fill(h + 1,h + 1 + n,-1);
    for(int i=1;i<n;i++)
    {
        int u,v;
        cin >> u >> v;
        add(u,v),add(v,u);
    }
    dfs1(r,0);
    dfs2(r,r);
    build(1,1,n);
    for(int i=1;i<=m;i++)
    {
        int op,x,y,k;
        cin >> op;
        if(op == 1)
        {
            cin >> x >> y >> k;
            updates(x,y,k);
         }
        else if(op == 2)
        {
            cin >> x >> y;
            cout << sum(x,y) << endl;
        }
        else if(op == 3)
        {
            cin >> x >> y;
            modify(1,id[x],id[x] + si[x] - 1,y % mod);
        }
        else 
        {
            cin >> x;
            cout << query(1,id[x],id[x] + si[x] - 1) << endl;
        }
    }
}
```

例题

[树的统计](https://www.luogu.com.cn/problem/P2590)

[软件包管理器](https://www.luogu.com.cn/problem/P2146) 本题询问答案的时候直接使用前后差值即可，1表示安装，0表示未安装，-1为初始条件！！！初始**不能**使用 0

 [松鼠的新家](https://www.luogu.com.cn/problem/P3258)



## 线段树

![线段树](图片/线段树.bmp)

### 单点修改,区间查询

```c++
int search(int i,int l,int r){ //求区间和
    if(tree[i].l >= l && tree[i].r <= r)//如果这个区间被完全包括在目标区间里面，直接返回这个区间的值
        return tree[i].sum;
    if(tree[i].r < l || tree[i].l > r)  return 0;//如果这个区间和目标区间毫不相干，返回0
    int s = 0;
    if(tree[i * 2].r >=l )  s += search(i * 2 ,l,r);//如果这个区间的左儿子和目标区间又交集，那么搜索左儿子
    if(tree[i * 2 + 1].l <= r)  s += search(i * 2 + 1,l,r);//如果这个区间的右儿子和目标区间又交集，那么搜索右儿子
    return s;
}
int query(int u,int l,int r)//u是当前节点,求区间最大值
{
    if(tr[u].l >= l && tr[u].r <= r)
        return tr[u].v;//树中节点已经被完全包含在[l,r]中,v 即是区间的最大值
    int mid = tr[u].l + tr[u].r >> 1;
    int v = 0;
    if(l <= mid)//和左边有交集
        v = query(u << 1,l,r);//左端点不需要取max,右端点自己max即可更新答案
    if(r > mid) //和右边有交点
        v = max(v,query(u << 1 | 1,l,r));
    return v;
}
//单点修改
void modify(int u,int x,int v)//单点修改 把a[x]的值修改为v,进行加减操作可以
{
    if(tr[u].l == x && tr[u].r == x)//如果找到叶节点直接修改
        tr[u].v  = v;
    else  //否则判断到底是往左递归还是往右递归
    {
        int mid = tr[u].l + tr[u].r >> 1;//取中点
        if(x <= mid)
            modify(u << 1,x,v);
        else modify(u << 1 | 1,x,v);//u << 1 | 1 等价于u << 1 + 1,位移运算符操作后,某位肯定为 0, | 1表示加1
        //递归完成后，当前节点最大值信息一定要记得更新
        pushup(u);//从下往上回溯更新信息
        //pushup函数需要根据具体要求实现tr[u].v = max(tr[u << 1].v,tr[u << 1 | 1].v);区间最大值的写法
    }
}
```

#### 典型例题 : 求区间子段最大和

```c++
#include<bits/stdc++.h>//求区间子段最大和(连续),结构体中加入最大前缀和和最大后缀和,最大连续子段和
using namespace std;
const int N = 5e5 + 10;
int n,m;
int w[N];
struct Node
{
    int l,r;
    int sum,lmax,rmax,tmax;//区间和,最大前缀和,最大后右缀和,最大连续子段和
}tr[N * 4];
void pushup(Node &u,Node &l,Node &r)//自己(父节点),左儿子,右儿子
{
    u.sum = l.sum + r.sum;//左和 = 左和加右和
    u.lmax = max(l.lmax,l.sum + r.lmax);//左边前缀和=max(左边前缀,左边和加右边前缀)
    u.rmax = max(r.rmax,r.sum + l.rmax);//右边后缀=max(右边后缀,右边和+左边后缀)
    u.tmax = max(max(l.tmax,r.tmax),l.rmax + r.lmax);//总的最大值=max未横跨两个区间最大和,左儿子的最大后缀和和右儿子的最大前缀和
}
void pushup(int u)//儿子节点向上更新父亲节点
{
    pushup(tr[u],tr[u << 1],tr[u << 1 | 1]);
}
void build(int u,int l,int r)
{
    if(l == r)
        tr[u] = {l,r,w[r],w[r],w[r],w[r]};//子段中最少包含一个数,不能为0
    else
    {
        tr[u] = {l,r};
        int mid = l + r >> 1;
        build(u << 1,l,mid),build(u << 1 | 1,mid + 1,r);
        pushup(u);
    }
}
int modify(int u,int x,int v)
{
    if(tr[u].l == x && tr[u].r == x)//这个点是叶子节点
        tr[u] = {x,x,v,v,v,v};
    else
    {
        int mid = tr[u].l + tr[u].r >> 1;
        if(x <= mid) //单点修改最后只会落到一个区间内,不会有重叠.
            modify(u << 1,x,v);
        else 
            modify(u << 1 | 1,x,v);
        pushup(u);
    }
}
Node query(int u,int l,int r)
{
    if(tr[u].l >= l && tr[u].r <= r)//在区间内部直接返回
        return tr[u];
    else 
    {
        int mid = tr[u].l + tr[u].r >> 1;
        if(r <= mid) 
            return query(u << 1,l,r);//完全在左边
        else if(l > mid) return query(u << 1 | 1,l,r);//完全在右边
        else //在左右两边的
        {
            auto left = query(u << 1,l,r);
            auto right = query(u << 1 | 1,l,r);
            Node res;
            pushup(res,left,right);
            return res;
        }
    }
}
int main()
{
    _;
    cin >> n >> m;
    for(int i=1;i<=n;i++)
        cin >> w[i];
    build(1,1,n);
    int k,x,y;
    while(m --)
    {
        cin >> k >> x >> y;
        if(k == 1)//表示查询
        {
            if(x > y) swap(x,y);
            cout << query(1,x,y).tmax << endl;//求[x,y]中的区间和 从根节点开始递归
        }
        else modify(1,x,y);//更改操作将将A[x]该为y,从根节点开始递归
    }
    return 0;
}//5 3  1 2 -3 4 5  1 2 3  2 2 -1  1 3 2  -- > 2  -1
```

#### 线段树加差分  ---  区间修改区间查询最大公约数

- 结构体中存一个最大公约数,父区间的最大公约数 = 两个子区间的最大公约数在取最大公约数  ---> 转换为差分,将区间修改转换为点, gcd(x , y , z) = gcd(x ,y - x ,z - y),存储差分数组,并且区间加减一个数也可以转换为差分对单点的修改,故转换为单点修改区间查询.

```c++
struct Node
{
    int l,r;
    ll sum,d;
}tr[N * 4];
ll gcd(ll a,ll b)
{
    return b ? gcd(b,a % b) : a;
}
void pushup(Node &u,Node &l,Node &r)//算左右两边的信息
{//节点和两个儿子
     u.sum = l.sum + r.sum;
     u.d = gcd(l.d,r.d);
}
void pushup(int u)
{
    pushup(tr[u],tr[u << 1],tr[u << 1 | 1]);
}
void build(int u,int l,int r)
{
    if(l == r)
    {
        ll b = w[r] - w[r-1];//存进去的是差分数组,由辗转除法可知,两数的最大公约数和一个数与两数差的最大公约数一致
        tr[u] = {l,r,b,b}; 
    }
    else 
    {
        tr[u].l = l,tr[u].r = r;
        int mid = l + r >> 1;
        build(u << 1,l,mid),build(u << 1 | 1,mid + 1,r);
        pushup(u);
    }
}
void modify(int u,int x,ll v)
{
    if(tr[u].l == x && tr[u].r == x)
    {
        ll b = tr[u].sum + v;
        tr[u] = {x,x,b,b};
    }
    else 
    {      
        int mid = tr[u].l + tr[u].r >> 1;
        if(x <= mid)    
            modify(u << 1,x,v);
        else modify(u << 1 | 1,x,v);
        pushup(u);
    }
}
Node query(int u,int l,int r)
{
    if(l > r) return {0};
    if(tr[u].l >= l && tr[u].r <= r)    
        return tr[u];//在区间内部
    else
    {        
        int mid = tr[u].l + tr[u].r >> 1;
        if(r <= mid) return query(u << 1,l,r);//都在左半边
        else if(l > mid) return query(u << 1 | 1,l,r);
        else 
        {
            auto left = query(u << 1,l,r);
            auto right = query(u << 1 | 1,l,r);
            Node res;
            pushup(res,left,right);
            return res;
        }
    }
}
int main()
{
    _;
    cin >> n >> m;
    for(int i=1;i<=n;i++)
        cin >> w[i];
    build(1,1,n);
    int l,r;
    ll d;
    char op[2];
    while(m --)
    {    
        cin >> op >> l >> r;
        if(*op == 'Q')//查询
        {
            auto left = query(1,1,l);///求1到l的前缀和
            auto right = query(1,l + 1,r);//最大公约数
            cout << abs(gcd(left.sum,right.d)) << endl;//防止结果中有负数返回正的
        }  //(x,y,z) = (x,y-x,z-y)括号最大公约数 gcd(a[l],gcd(b[l+1],b[r]))
        else 
        {
            cin >> d;
            modify(1,l,d);
            if(r + 1 <= n) 
                modify(1,r + 1,-d);//差分数组的修改
        }
    }
}//5 5  1 3 5 7 9  Q 1 5  C 1 5 1  Q 1 5  C 3 3 6  Q 2 4  -- > 1 2 4
```

### 区间修改,单点查询  ---  懒标记

- 懒标记的意义在于当题目对于区间修改时如果我们对每个点暴力修改,复杂度较高,可能会超时,此时如果一个 tr[u] 完全被修改区间覆盖那直接给这个区间加上一个标记,代表这个区间的所有数都需要加上一个值,当询问的时候如果询问到了这个区间或者是这个区间的子区间再将懒标记pushdown,传到子区间里面           **询问操作前先pushdown(如果当前区间未被完全覆盖),修改完成后再pushup**

```c++
const int N = 1e5 + 10;//加和乘的懒标记,强制将顺序转换为先乘后加,即是结构体维护 sum * mul + add的形式更易操作,否则需要考虑优先级的问题直接存入的时候强制化简为这个形式.
int n,m,p;
int w[N];
struct Node
{
    int l,r;
    int sum,add,mul;//总和,加法懒标记,乘法懒标记,两个懒标记代表这个题目区间修改时有两种方式
}tr[N * 4];
void pushup(int u)
{
    tr[u].sum = (tr[u << 1].sum + tr[u << 1 | 1].sum) % p;
}
void eval(Node &t,int add,int mul)//对这个区间的改变,更新sum和懒标记
{                           //区间内的每个数都要加
    t.sum = ((ll)t.sum * mul +(ll)(t.r - t.l + 1) * add) % p;
    t.mul = (ll)t.mul * mul % p;//乘法直接将乘数直接相乘即可
    t.add = ((ll)t.add * mul + add) % p;//加法,需要先将原来的加数项乘于传入的乘数在加上传入的加数
}  
void pushdown(int u)    
{
    eval(tr[u << 1],tr[u].add,tr[u].mul);//先将根节点的懒标记传到儿子身上
    eval(tr[u << 1 | 1],tr[u].add,tr[u].mul);
    tr[u].add = 0,tr[u].mul = 1;//传到子儿子后,根节点的懒标记清空
}
                           
void build(int u,int l,int r)
{
    if(l == r)
        tr[u] = {l,r,w[r],0,1};//叶子结点,左右区间,总和,add,mul
    else
    {
        tr[u] = {l,r,0,0,1};//因为后面有pushup,所以先随便赋值即可
        int mid = l + r >> 1;
        build(u << 1,l,mid),build(u << 1 | 1,mid + 1,r);
        pushup(u);
    }
}
void modify(int u,int l,int r,int add,int mul)
{
    if(tr[u].l >= l && tr[u].r <= r)//区间被全部包含
        eval(tr[u],add,mul);
    else 
    {
        pushdown(u);//修改之前pushdown
        int mid = tr[u].l + tr[u].r >> 1;
        if(l <= mid)//左边有交集
            modify(u << 1,l,r,add,mul);
        if(r > mid) //右边有交集
            modify(u << 1 | 1,l,r,add,mul);
        pushup(u);//修改之后pushup
    }
}
ll query(int u,int l,int r)
{
    if(tr[u].l >= l && tr[u].r <= r)
        return tr[u].sum;
    pushdown(u);//先pushdown并且只pushdown到包括现在的区间就可以停止了,后面下次如果用到在继续pushdown,目前不必要
    int mid = tr[u].l + tr[u].r >> 1;//现在枚举到的区间范围太大了
    ll sum = 0;
    if(l <= mid)
        sum = query(u << 1,l,r);
    if(r > mid)
        sum = (ll)(sum + query(u << 1 | 1,l,r)) % p;
    return sum;
}
int main()
{
    _;
    cin >> n >> p;
    for(int i=1;i<=n;i++)
        cin >> w[i];
    build(1,1,n);
    cin >> m;
    while(m --)
    {
        int t,l,r,d;
        cin >> t >> l >> r;
        if(t == 1)//乘于一个数
        {
            cin >> d;
            modify(1,l,r,0,d);//没有加上的数,传入为 0 
        }
        else if(t == 2) // 加上一个数
        {
            cin >> d;
            modify(1,l,r,d,1);//没有乘于的数,传入为 1 ,乘 1 表示不变
        }
        else cout << query(1,l,r) % p << endl;//询问区间
    }
    return 0;
}
```

### 扫描线    ---  线段树的特殊用法 , 且比较唯一

#### 扫描线求重叠矩形面积

![线段树扫描线1](图片/线段树扫描线1.png)

![线段树扫描线2](图片/线段树扫描线2.png)

思路 : 使用一条垂直于 x 轴的直线,从左到右来扫描这个图形,明显,只有早碰到矩阵的左边界或者右边界的时候,这个线段所扫描到的情况才会改变,所以把所有**矩形(最初的矩形)**的入边出边按 X 值排序,根据 X 值从小到大去处理,就可以用线段树来维护扫描到的情况,如上图X1到X8是所有矩形的入边,出边的 X 坐标.

而红色部分的线段,如果碰到**矩形(最初的矩形)**的入边就把这条边加入,碰到出边就拿走,红色部分就是有线段覆盖的部分 .  要求面积.只需要知道图中的L1到L8,   线段树就是用来维护这个区间的   ; 例如X1 X2 X3均是入边,扫描到的时候加入结构体(线段树),并且计算图二中子矩形的面积,扫描到 X4 时发现它是第一个矩形的出边,先算出子矩形的面积后在删除一条线段(对应的入边),线段树维护现存于线段树中所有线段的和,去重叠之后,是相对长度和不是绝对长度和

```c++
const int N = 1e5 + 10;
int n;
struct Segment//区间用线段存下来
{
    double x,y1,y2;//存的是 x 坐标和y轴长度用于求面积
    int k;//权值,区分它是该矩形前面的边还是后面的边
    bool operator < (const Segment &t)const
    {
        return x < t.x;
    }
}seg[N * 2];//两倍n的线段,每个矩形需要存两条边
//线段树的每个节点,保存的为线段,0 号点为y[0]到y[1],依次类推
struct Node
{
    int l,r;//存的是离散后的值
    int cnt;//记录这段区间出现了几次,1 和 -1 相互抵消,代表删除
    double len;//记录这段区间的长度,即线段长度,纵坐标之差
}tr[N * 8];//2n条线,线段树开4倍
vector<double> ys;//vector存所有的y,用作离散化
int find(double y)//查找离散化后对应的下标
{//返回vector中第一个 >= y 的下标
    return lower_bound(ys.begin(),ys.end(),y) - ys.begin();
}
void pushup(int u)
{
    //例如：假设tr[1].l = 0,tr[1].r = 1;
    //      y[0]为ys[0]到ys[1]的距离, y[1]为ys[1]到ys[2]的距离
    //      tr[1].len 等于y[0]到y[1]的距离
    //      y[1] = ys[tr[1].r + 1] = ys[2], y[0] = ys[tr[1].l] = ys[0]
    
    //表示整个区间都被覆盖，该段长度就为右端点 + 1后在ys中的值 - 左端点在ys中的值
    if(tr[u].cnt)//当前cnt>0,长度可以直接算
        tr[u].len = (ys[tr[u].r + 1] - ys[tr[u].l]);//ys是原纵坐标,
    
     // 借鉴而来
    // 如果tr[u].cnt等于0其实有两种情况:
    // 1. 完全覆盖. 这种情况由modify的第一个if进入. 
    //    这时下面其实等价于把"由完整的l, r段贡献给len的部分清除掉", 
    //    而留下其他可能存在的子区间段对len的贡献
    // 2. 不完全覆盖, 这种情况由modify的else最后一行进入. 
    //    表示区间并不是完全被覆盖，可能有部分被覆盖,所以要通过儿子的信息来更新
    else if(tr[u].l != tr[u].r)//不是叶节点,用两个子儿子算
    {
        tr[u].len = tr[u << 1].len + tr[u << 1 | 1].len;
    } 
    else tr[u].len = 0;//表示为叶子节点且该线段没被覆盖，为无用线段，长度变为0
}
void modify(int u,int l,int r,int k)//表示从线段树中 l 到 r 点出现次数 + k
{
    if(tr[u].l >= l && tr[u].r <= r)//该区间被完全覆盖
    {
        tr[u].cnt += k; //该区间出现的次数加 k 
        pushup(u);//算一下结果,更新节点的 len
    }
    else 
    {
        int mid = tr[u].l + tr[u].r >> 1;
        if(l <= mid) modify(u << 1,l,r,k);//左边有交集
        if(r > mid)
            modify(u << 1 | 1,l,r,k);
        pushup(u);//向上更新
    }
}
void build(int u,int l,int r)
{
    tr[u] = {l,r,0,0};
    if(l != r)
    {
        int mid = l + r >> 1;
        build(u << 1,l,mid),build(u << 1 | 1,mid + 1,r);//因为建树的时候最开始都是0,所以不需要pushup一遍
    }
}
int main()
{
    int T = 1;
    while(cin >> n,n)
    {
        ys.clear();
        int j = 0;//表示一共 j 条线段
        for(int i=0;i<n;i++)
        {
            double x1,y1,x2,y2;
            scanf("%lf%lf%lf%lf", &x1, &y1, &x2, &y2);
            seg[j ++] = {x1,y1,y2,1};//前面一跳边的权值看做1
            seg[j ++] = {x2,y1,y2,-1};//后面一条边的权值看做-1   一个矩形转换为两条线
            ys.push_back(y1),ys.push_back(y2);//这个离散化有大小要求,故不能直接使用umap
        }
        sort(ys.begin(),ys.end());//先排序后判重
        ys.erase(unique(ys.begin(),ys.end()),ys.end());//判重去重 
        build(1,0,ys.size() - 2);//建线段树,从一号点开始,点下标从0到size()-1(区间的个数比点少一个)
		//例子：假设现在有三个不同的y轴点,分为两个线段
        //y[0] ~ y[1],y[1] ~ y[2];
        //此时ys.size()为3,ys.size() - 2 为 1;
        //此时为 build(1, 0, 1);
        //有两个点0 和 1,线段树中0号点为y[0] ~ y[1],1号点为y[1] ~ y[2];
        sort(seg,seg + n * 2);//线段排序
        double res = 0;
        for(int i=0;i<n * 2;i++)//j = n 
        {//根节点的长度即为此时有效线段长度 ，再 * x轴长度即为面积
            if(i > 0)//第一条线左边没有东西,是0   计算面积只从根节点计算 --> tr[1].len
                res += tr[1].len * (seg[i].x - seg[i - 1].x); 
            //处理一下该线段的信息，是加上该线段还是消去
            //例子：假设进行modify(1，find(10),find(15) - 1,1);
            //      假设find(10) = 0,find(15) = 1;
            //      此时为modify(1, 0, 0, 1);
            //      表示线段树中0号点出现次数加1；
            //      而线段树中0号点刚好为线段(10 ~ 15);
            //      这就是为什么要进行find(seg[i].y2) - 1 的这个-1操作
            modify(1,find(seg[i].y1),find(seg[i].y2) - 1,seg[i].k);//左区间,右区间,权值
        }//find后减 1 是因为存的是区间不是点
        printf("Test case #%d\nTotal explored area: %.2lf\n\n", T ++, res);
    }  //cout输出时可能会出现问题,小数可能h会舍去.
    return 0;
}
```

## 区间最值操作 & 区间历史最值

## 可持久数据结构

# 图论

## 树和图

> - 树的重心指的是把这个点删去后,剩余联通块中点最大值最小,满足这个条件的点是树的重心.

```c++
const int N = 100010,M = N*2;//删除树中的一个点，使得剩下的连通块中点数最大值的最小值
int h[N],e[M],ne[M],idx=0;//h存的是n个链表的链表头，e  ne  idx与链表一致
bool st[N];//idx存的是边
int ans = N;//最小的最大值
void add(int a,int b)//在a后插入b
{
	e[idx] = b,ne[idx] = h[a],h[a] = idx++;	
}
int dfs(int u)//以u为根的子树中点的数量
{
	st[u] = true;//标记一下，这点点已经被搜过了
	int sum = 1,res = 0;//res是每一个连通块的最大值
	for(int i=h[u];i!=-1;i=ne[i])
	{
		int j = e[i];
		if(!st[j]) 
		{
			int s = dfs(j);//可能会分为多个连通块 
			res = max(res,s);//把这个点删除后每一个联通块点的最大值
			sum += s;//这个连通块一共多少点算上当前u
		}  //相减的剩下的点数
	} //n - sum 代表这个指向这个点的连通块点数
	res = max(res,n-sum);//被删除的连通块和剩下的连通块
	ans = min(ans,res);
	return sum;
}
int main()
{ 
	cin >> n;
	memset(h,-1,sizeof h);//初始化，将头全指向-1，代表结尾
	for(int i=0;i<n-1;i++)
	{
		int a,b;
		cin >> a >>b;
		add(a,b),add(b,a);//无向边
	}
	dfs(1);//从第一个节点开始深搜-搜的是图的节点编号
	cout << ans << endl;
}
```

## 树的dfs序,欧拉序,时间戳

[dfs序和欧拉序 - Styx-ferryman - 博客园 (cnblogs.com)](https://www.cnblogs.com/stxy-ferryman/p/7741970.html)

### dfs 序

![树的dfs序](图片/树的dfs序.png)

树的DFS序列,也就是树的深搜索,他的概念是 : 树的每一个节点在深度优先遍历中进出栈的时间序列.树的DFS序,简单来说就是对树从根开始深搜,按搜到的时间顺序把所有的节点排队,上图的一个DFS序就是 : ` 1 4 6 6 3 9 9 3 4 7 7 2 5 5 8 8 2 1`,**树的DFS序不是唯一的**,对于一棵树进行DFS序,需要把回溯的时候的节点编号也记录一下,于是每个数字会出现两次,DFS序的长度是 2 * N,**一个数字出现两次的位置所夹的区间正好是以这个数为根的一个子树**,例如`2 8 8 5 5 2`,子树是285      (大部分都认为DFS序每个节点都直会出现一次,出现两次的是欧拉序,这里直接归到DFS序里面,方便比较)

- DFS序的性质和应用 : 把一颗子树放到一个区间里,这个性质把树状结构变成了线性结构.我们只需要在这个线性结构上进行区间修改区间查询,而不需要遍历整颗子树来做到区间修改和区间查询,主要用于树链剖分

```c++
const int maxn=1e5+10;//部分DFS序解释每个节点只出现过一次,只需要加一个st数组即可,按照两次统计能快速解决子树问题
int n;
int idx,e[maxn<<1],ne[maxn<<1],h[maxn];
int id[maxn],cnt;
stack<int> st;
void add(int x,int y)
{
    e[idx] = y,ne[idx] = h[x],h[x] = idx ++;
}
void dfs(int x,int f)
{
    id[++ cnt] = x;
    for(int i=h[x];~i;i=ne[i])
    {
        int y = e[i];
        if(y == f)
            continue;
        dfs(y,x);
    }
    id[++ cnt] = u;//访问到叶子节点了,或者回溯之后当前节点所有的节点遍历过了,将当前节点在加入id一次
//如果一个点只需要出现一次的时候这个不必加上,这个主要是叶子节点和树遍历完毕dfs之后没有子节点进不去for循环再加一个叶子节点一共两次。
}
int main()
{
    cin >> n;
    fill(h,h + 1 + n,-1);
    for(int i=1;i<n;i++)
    {
        int x,y;
        cin >> x >> y;
        add(x,y),add(y,x);
    }
    dfs(1,0);
    for(int i=1;i<=cnt;i++)
        cout << id[i] << " ";
    return 0;
}
```

### 时间戳

- 概念 : 按照深度优先遍历的过程,按每个节点第一次被访问的顺序,依次给予这些节点 1 - N 的标记,这个标记就是时间戳
- DFS序的概念是按照深度优先搜索时间顺序的节点编号序列,数组下标存的是时间 ; 时间戳是按照深搜时间顺序的时间编号序列.数组下标是节点编号,也就是 id[i] = x,其中 i 是时间,x 是节点,id 是DFS序,dfn[x] = i,i 是时间,x 是节点,dfn 是时间戳.两者有点互为反函数的意思,在tarjan算法中也有所体现.

### 欧拉序

- 定义 : 从根节点出发回到根节点为止,按深度优先遍历的顺序所经过的所有点的顺序.

![欧拉序](图片/欧拉序.png)

这个图的一个欧拉序就是 : `1 2 8 2 5 2 1 7 1 4 3 9 3 4 6 4 1`,与DFS序类似欧拉序同样不唯一,并且性质 : 每个点在欧拉序中出现的次数等于这个点的度数,因为DFS到的时候加进一次,回去的时候也加进. 所以总点数是 `n + n - 1 = 2 * n - 1`,`n` 个点,`n - 1` 条边; 

```c++
#include<bits/stdc++.h>
using namespace std;
vector<int> g[40010];
int len,a[80020];
void dfs(int u,int fa)
{
    a[++ len] = u;//先入欧拉序
    int sz = g[u].size();
    for(int i=0; i<sz; i++)
    {
        if(g[u][i] != fa)
        {
            dfs(g[u][i],u);
            a[++ len] = u;//非递归到叶节点, 叶子节点没有子节点所以不会进入for循环，叶子节点只会记录一次，在一条路递归结束一次之后会一次进行这一步操作，保证回溯的时候对应的节点记录上。实现了 1 2 8 2 5 2 1 ... 的效果,相当于从下往上走,叶子结点直接入欧拉序一次
        }
    }
}
int main()
{
    int t;
    cin >> t;
    while(t--)
    {
        int n,m;
        len = 0;
        memset(a,0,sizeof(a));
        cin >> n;
        for(int i=1; i<=n; i++)
            g[i].clear();
        for(int i=1; i<=n-1; i++)
        {
            int a,b;
            cin >> a >> b;
            g[a].push_back(b);
            g[b].push_back(a);
        }
        dfs(1,0);
        for(int i=1;i<=len;i++)
            cout << a[i] << " ";
    }
}
```

#### 欧拉序的应用

- 欧拉序主要用于求LCA问题,也即是将树上的LCA问题转换为区间的RMQ问题(最值),根据欧拉序的性质,两个点第一次出现的位置之间一定有它们的LCA,并且LCA一定是这个区间中深度最小的点,所以预处理时,就可以将树上的LCA问题变成两个节点在欧拉序中深度最小值RMQ

![欧拉序求LCA](图片/欧拉序求LCA.png)

```c++
#include<bits/stdc++.h>
using namespace std;
vector<int > g[40010];
int len,a[80020],dep[80020],pos[80020][17],dp[80020][17],vis[80020],cnt[80020];
void dfs(int u,int fa,int deep)
{
    a[++ len] = u;//欧拉序
    dep[len] = deep + 1;
    if(!vis[u])
    {
        cnt[u] = len;//时间戳,很明显时间戳的更新只有在第一次dfs的时候才会更新,后面dfs之后的肯定不会改变
        vis[u] = 1;//因为时间戳和dfs序互反,如果只是一个点统计一遍的话肯定是对应的,但是lca问题不能只统计一遍
    }
    int sz = g[u].size();
    for(int i=0; i<sz; i++)
    {
        if(g[u][i] != fa)
        {
            dfs(g[u][i],u,deep + 1);
            a[++ len] = u;//叶子节点或者已经遍历完所有的子节点当前父节点会在进欧拉序,但是对应的深度不应该更新.
            dep[len] = deep + 1;//所以递归结束之后继续赋值dep,此时dep是第一次对应的深度,不会变大
        }
    }
}

int main()
{
    int t;
    cin >> t;
    while(t--)
    {
        int n,m;//n是点数,边数为点数减一,m是询问次数,询问两个点的LCA
        len = 0;
        memset(a,0,sizeof(a)),memset(dep,0,sizeof(dep)),memset(pos,0,sizeof(pos));
        memset(dp,0,sizeof(dp)),memset(vis,0,sizeof(vis)),memset(cnt,0,sizeof(cnt));
        cin >> n >> m;
        for(int i=1; i<=n; i++)
            g[i].clear();
        for(int i=1; i<=n-1; i++)
        {
            int a,b;
            cin >> a >> b;
            g[a].push_back(b);
            g[b].push_back(a);
        }
        dfs(1,0,0);
        cout << len << endl;
        for(int i=1; i<=len; i++)
        {
            dp[i][0] = dep[i];
            pos[i][0] = i;
        }
        for(int j=1; j<=17; j++)
        {
            for(int i=1; i<=len; i++)
            {
                if(i + (1 << (j - 1)) >= len)
                    break;
                if(dp[i][j - 1] > dp[i + (1 << (j - 1))][j - 1])
                {
                    dp[i][j] = dp[i + (1 << (j - 1))][j - 1];
                    pos[i][j] = pos[i + (1 << (j - 1))][j - 1];
                }
                else
                {
                    dp[i][j] = dp[i][j-1];
                    pos[i][j] = pos[i][j-1];
                }
            }
        }
        for(int i=1; i<=m; i++)
        {
            int x,y;
            cin >> x >> y;
            int dx = cnt[x];
            int dy = cnt[y];
            if(dx > dy)
            {
                swap(dx,dy);
                swap(x,y);
            }
            int k = (int)(log((double)(dy - dx + 1)) / log(2.0));
            int p;
            if(dp[dx][k] > dp[dy - (1 << k) + 1][k])
                p = pos[dy - (1 << k) + 1][k];
            else
                p = pos[dx][k];
            cout << a[p] << endl;
        }
    }
}
```

#### 两种写法的欧拉序及dfs序总结

![欧拉序总结](图片/欧拉序总结.png)

1. 欧拉序 : 1 2 4 4 5 5 2 3 6 6 7 7 3 1         时间戳 : 1 2 3 3 5 5 2 8 9 9 11 11 8 1      节点数量为 2 * N

-  树上任意一点 x 的子树,在**第一次出现 **x 和**最后一次出现** x 之间的所有出现的数,另外有个性质是这些出现的次数和为偶数,
- 树上任意两点 x 和 y,路径上的点,为**最后一个** x 到**第一个** y 之间,出现奇数次的数,另外加上**lca**,例如:4 到 7 的树上路径,截取为4 5 5 2 3 6 6 7,出现奇次的数字为4 2 3 7,加上lca=1,故树上路径的最终节点是 4 2 1 3 7
- 两点之间的路径 : 设每个点第一次最后一次出现的位置为first[x],last[x],那么对于路径x -> y,设 f[x] <= f[y] (不满足swap,均是简写),这个操作的意义在于,如果x,y在一条链上,则 x 一定是 y 的祖先或者等于 y,如果lca(x,y)=x,则直接把 ( f[x],f[y] ) 的区间扯过来用,反之使用 ( l[x],f[y] )区间,(不用f[x]到l[x]的路径是因为中间的点一定不在路径上,里面的点都出现过两次,但是求路径的时候我们需要找的是出现奇数次的点),但这个区间内不包括lca(x,y),故需要最后加上
- 通常将树上问题,转换为区间问题求解,比如:树上莫队,求解树链上不同数的个数等等.

```c++
void dfs(int u) {
	sa[++tim] = u;//欧拉序列数组
	dfn[u] = tim;//时间戳数组
	for (son...) dfs(son);
	sa[++tim] = u;//访问到叶节点直接将叶节点入数组,或者一个节点的字节点完全被访问结束再入这个节点一次.
}
```

2. 欧拉序 : 1 2 4 2 5 2 1 3 6 3 7 3 1          时间戳 : 1 2 3 2 5 2 1 8 9 8 11 8 1,       节点数量为 2 * N - 1

- 树上任意一点的子树为,第一次出现 x和最后一次 x之间出现的所有数,出去本节点的数,例如 : 2 的子树,截取为 2 4 2 5 2,除去节点为4 5
- 树上任意两点x和y,两点的**lca**,为**第一次**出现 x 和**第一次**出现 y 区间内,**时间戳最小**的那个值,例如 : 4到7的lca,截取为4 2 5 2 1 3 6 3 7,这里的时间戳是指第一次进入节点的时间,时间戳为3 2 5 2 1 8 9 8 11,时间戳最小的是1,.找到 1 对应的节点即可
- 通常用于求 LCA,结合ST表,在O(1)的时间求解LCA,适合频繁求LCA的问题. 

```c++
void dfs(int u) {//一笔不断遍历整棵树,每个节点出现的次数
	sa[++tim] = u;
	dfn[u] = tim;
	for (son...) {
		dfs(son);
		sa[++tim] = u;
	}
}
```

3. DFS序 : 1 2 4 5 3 6 7     时间戳 : 1 2 3 4 5 6 7,仅进入节点的时候记录,

- 树上任意点 x 的子树包括x,为当前第一次出现 x 的位置到往后 sz[x] 个节点的区间,sz[x] 表示dfs时计算的以 x 为根的树大小,假设 sz[x]包含了x,例如 : 2的子树,sz[2]=3,那么截取为2 4 5,如果不包含x,取x往后,区间大小为sz[x]-1即可,即是4 5
- 树上两点x 和 y 的 LCA,需要配合树链剖分,将树链变成一块块连续的序列
- 通常结合树链剖分等进行维护树上链上的信息,比如 : 给某条链全部节点加上 1 ,求解最大值最小值.

```c++
void dfs(int u) {
	sa[++tim] = u;
	dfn[u] = tim;
	for (son...) dfs(son);
}
```

## 树上问题

### 树基础

- 适用于有根树和无根树

  > **森林**,  **生成树 : ** 一个连通无向图的生成子树,同时要求是树.也即在图的边集中选择 n - 1 条,将所有顶点连通, **无根图的叶节点 : ** 度数不超过 1 的结点,不超过 1 是因为 n = 1时的特例 ,**有根图的叶结点 : ** 没有子结点的结点.

- 只适用于有根树

  > **父节点,子节点,祖先,兄弟,后代,**, **结点的深度 : ** 到根节点的路径上的边数 ; **树的高度 : ** 所有结点的深度的最大值 ; **子树 : ** 删掉与父亲相连的边后,该结点所在的子图 ; 

- 特殊的树

  > **链 : ** 满足与任一结点相连的边不超过 ![2](data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7) 条的树称为链。**菊花图 : ** 满足存在 ![u](data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7) 使得所有除 ![u](data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7) 以外结点均与 ![u](data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7) 相连的树称为菊花。**有根二叉树 : ** 每个结点最多只有两个儿子（子结点) 的有根树称为二叉树。常常对两个子结点的顺序加以区分，分别称之为左子结点和右子结点。大多数情况下，**二叉树** 一词均指**有根二叉树**。**完整二叉树 :  ** 每个结点的子结点数量均为 0 或者 2 的二叉树。换言之，每个结点或者是树叶，或者左右子树均非空。**完全二叉树 : ** 只有最下面两层结点的度数可以小于 2，且最下面一层的结点都集中在该层最左边的连续位置上。**完美二叉树 : ** 所有叶结点的深度均相同，且所有非叶节点的子节点数量均为 2 的二叉树称为完美二叉树。

### 树的遍历 - 主要先 中 后三种

![树的遍历](C:\Users\19669\Desktop\nodeT\图片\数的遍历.png)

#### 树上DFS

> 在树上DFS指的是 : 先遍历根节点,然后分别访问根节点每个儿子的子树,可以用来求出每个节点的深度,父亲等信息..

#### 二叉树上DFS - 先序遍历

- 按照 **根,左,右** 的顺序遍历二叉树,即是从根节点出发,一直向下遍历左儿子直至到叶节点,然后寻找上一个父节点的右儿子,接着重复第一步遍历左儿子,**DFS**的思路一条路走到结尾在回溯.   结果是 **A B D H I E J C F K G**

#### 二叉树上DFS - 中序遍历

- 按照 **左,根,右** 的顺序遍历二叉树,即是先找到最左边的叶节点,然后向上遍历找到最近的父节点,遍历这个父节点的右儿子,到叶节点后继续线上回溯,找这个父节点的最近父节点遍历右儿子,递归至根节点,**中序遍历可以看成 : **二叉树每个节点,垂直投影下来(可以理解为每个节点从最左边开始垂直掉到地上),然后从左向右数,得到的结果便是中序遍历的结果,结果是 **H D I B E J A F K C G **(K 是 F 的右儿子,所以 K 在 F 后)

#### 二叉树上DFS - 后序遍历

- 按照 **左,右,根** 的顺序遍历二叉树,就像是剪葡萄,将一串葡萄剪成一颗一颗的,每次剪掉的必须是一颗不是一串.结果是 **H I D J E B K F G C A**

#### 层次遍历 BFS

- 从根节点开始,一层一层,从上到下,每层从左到右,依次写值就可以,结果是 **A B C D E F G H I J K **

```c++
#include<stdio.h>
#include<stdlib.h>
typedef struct Tree
{
 int data;					//	存放数据域
 struct Tree *lchild;			//	遍历左子树指针
 struct Tree *rchild;			//	遍历右子树指针
}Tree,*BitTree;
BitTree CreateLink()
{
	int data;
	int temp;
	BitTree T;	
	scanf("%d",&data);		//	输入数据
	temp=getchar();			//	吸收空格
	
	if(data == -1)	//	输入-1 代表此节点下子树不存数据，也就是不继续递归创建	
		return NULL;
    else
    {
		T = (BitTree)malloc(sizeof(Tree));			//		分配内存空间
		T->data = data;								//		把当前输入的数据存入当前节点指针的数据域中
		
		printf("请输入%d的左子树: ",data);		
		T->lchild = CreateLink();					//		开始递归创建左子树
		printf("请输入%d的右子树: ",data);			
		T->rchild = CreateLink();					//		开始到上一级节点的右边递归创建左右子树
		return T;							//		返回根节点
	}	
	
}
void ShowXianXu(BitTree T)			//		先序遍历二叉树
{
	if(T==NULL)						//	递归中遇到NULL，返回上一层节点
		return;
	printf("%d ",T->data);
	ShowXianXu(T->lchild);			//	递归遍历左子树
	ShowXianXu(T->rchild);			//	递归遍历右子树
}
void ShowZhongXu(BitTree T)			//		中序遍历二叉树
{
	if(T==NULL)						//	递归中遇到NULL，返回上一层节点
		return;
	ShowZhongXu(T->lchild);			//	递归遍历左子树
	printf("%d ",T->data);
	ShowZhongXu(T->rchild);			//	递归遍历右子树
	
}
void ShowHouXu(BitTree T)			//		后序遍历二叉树
{
	if(T==NULL)						//	递归中遇到NULL，返回上一层节点
		return;
	ShowHouXu(T->lchild);			//	递归遍历左子树
	ShowHouXu(T->rchild);			//	递归遍历右子树
	printf("%d ",T->data);
}
int main()
{
	BitTree S;
	printf("请输入第一个节点的数据:\n");
	S = CreateLink();			//		接受创建二叉树完成的根节点
	printf("先序遍历结果: \n");
	ShowXianXu(S);				//		先序遍历二叉树
	printf("\n中序遍历结果: \n");
	ShowZhongXu(S);				//		中序遍历二叉树	
	printf("\n后序遍历结果: \n");
	ShowHouXu(S);				//		后序遍历二叉树
	return 0;	
} 	
```

#### 反推

- 已知中序遍历和另外一个序列可以求第三个序列.前序的第一个是 `root`，后序的最后一个是 `root`。先确定根节点，然后根据中序遍历，在根左边的为左子树，根右边的为右子树。对于每一个子树可以看成一个全新的树，仍然遵循上面的规律。

#### 无根树

- 树的遍历一般为深度优先遍历,这个过程要注意的是避免重复访问节点,由于树是无环图,因此只需记录当前节点是由哪个节点访问而来,此后进入除该节点外的所有相邻结点,即可避免重复访问.

```c++
void dfs(int u, int from) {
  // 递归进入除了 from 之外的所有子结点
  // 对于出发结点，from 为空，故会访问所有相邻结点，这与期望一致
  for (int v : adj[u])
    if (v != from) {
      dfs(v, u);
    }
}
// 开始遍历时
int EMPTY_NODE = -1;  // 一个不存在的编号
int root = 0;         // 任取一个结点作为出发点
dfs(root, EMPTY_NODE);
```

### 树的直径

> 树上任意两节点之间最长的简单路径即为树的直径.可以使用两次 DFS 或者 树形DP 的方法在 O(n) 时间求出树的直径.树的直径是相当于点来说

- 两遍DFS : **定理 : ** 在一棵树上,从任意节点 y 开始进行一次 DFS,到达的距离其最远的节点 z 必为直径的一端,从节点 z 找另一端.(两次 DFS 适用于边权不为负,如果含有负权边则不能使用两次 DFS 求解).如果需要求出一条直径上的所有结点,则可以在第二次 DFS 的过程中,记录每个点的前序结点,即可从直径的一端一路向前,遍历直径上的所有结点.

```c++
const int N = 10000 + 10;
int n, c, d[N];
vector<int> E[N];
void dfs(int u, int fa) {
  for (int v : E[u]) {
    if (v == fa) continue;
    d[v] = d[u] + 1;
    if (d[v] > d[c]) c = v;
    dfs(v, u);
  }
}
int main() {
  cin >> n;
  for (int i = 1; i < n; i++) {
    int u, v;
    cin >> u >> v;
    E[u].push_back(v), E[v].push_back(u);
  }//使用vector的方式存树.
  dfs(1, 0);
  d[c] = 0, dfs(c, 0);
  cout << d[c] << endl;
  return 0;
}
```

**两遍dfs有时会出现不理解的问题，以后树的直径求解问题都是用 DP 方法求解比较稳妥，除非是求直径的两个端点**

这里提供一种只使用一个数组进行的树形 DP 方法。

我们定义 ![dp[u]](data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7)：以 `dp[u]`为根的子树中，从 `u` 出发的最长路径。那么容易得出转移方程：`dp[u] = max(dp[u],dp[v] + w(u,v))`，其中的 v 为 u 的子节点，`w(u,v)` 表示所经过边的权重。

对于树的直径，实际上是可以通过枚举从某个节点出发不同的两条路径相加的最大值求出。因此，在 DP 求解的过程中，我们只需要在更新 `dp[u]` 之前，计算 `d = max(d,dp[u] + dp[v] + w(u,v))` 即可算出直径 ![d](data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7)。

```c++
// 边权代码
const int N = 10000 + 10;

int n, d = 0;
int dp[N];
vector<int> E[N];

void dfs(int u, int fa) {
  for (int v : E[u]) {
    if (v == fa) continue;
    dfs(v, u);
    d = max(d, dp[u] + dp[v] + 1);
    dp[u] = max(dp[u], dp[v] + 1);
  }
}

int main() {
  scanf("%d", &n);
  for (int i = 1; i < n; i++) {
    int u, v;
    scanf("%d %d", &u, &v);
    E[u].push_back(v), E[v].push_back(u);
  }
  dfs(1, 0);
  printf("%d\n", d);
  return 0;
}
```

> 性质 : 若树上所有边权均为正,则树的所有直径中点重合.

- 点权代码

```cpp
void dfs(int u,int fa)
{
	for(auto L : g[u])
	{
		if(L == fa) continue;
		dfs(L,u);
		c = max(c,dp[u] + dp[L] + (d[u] - 1) * (qmi(2,m) - 1) + 1);//后续表示的是 u 节点的权值，即是边权中的边权
		dp[u] = max(dp[u],dp[L]);//这里不加表示的是将dp[u]更新为子树L向u扩展时所能提供的最大直径，最后递归结束之后在更新 u 节点的贡献
	}//
	dp[u] += (d[u] - 1) * (qmi(2,m) - 1) + 1;
}
pair<int,int> dp1[N],p;//有两个关键字的点权直径
void dfs1(int u,int fa)
{
	for(auto L : g[u])
	{
		if(L == fa) continue;
		dfs1(L,u);
		p = max(p,{dp1[u].first + dp1[L].first + (d[u] - 1),dp1[u].second + 1 + dp1[L].second});
		dp1[u] = max(dp1[u],dp1[L]);
	}
	dp1[u].first += (d[u] - 1);
	dp1[u].second += 1;
}
```

### 最近公共祖先 - LCA

- 两个点的最近公共祖先,就是这两个点的公共祖先里面,离根最远的点.
- 求解从`x`到`y`结点的最短路径上所有结点的值和 -- `dfs O(n) 处理出 每个节点的 dis(即是每个节点到根节点的最短路径长度)`，然后对于每个询问，求出`x，y`两点的`lca`，利用`lca`的性质可得`dis(x，y) = dis(x)  + dis(y) - 2 * dis(lca)`。

> 性质 : **1 . ** LCA({u}) = u,   **2 . **u 是 v 的祖先,当且仅当LCA({u,v}) = u,    **3 .**如果 u 不为 v 的祖先并且 v 不为 u 的祖先,那么 u, v 分别处于 LCA(u,v) 的两棵不同的子树中,   **4 .**前序遍历中,LCA(S) 出现在所有 S 中元素之前,后续遍历中 LCA(S) 则出现在所有 S 中元素之后,   **5 . **两点集并的最近公共祖先为两点集分别的最近公共祖先的最近公共祖先,即是$LCA(A\ \cup\ B) = LCA(LCA(A),LCA(B))$,  **6 .**两点的最近公共祖先必定处在树上两点间的最短路上,   **7 .**d(u,v) = h(u) + h(v) - 2h(LCA(u,v)), 其中 d 是树上两点点的距离, h 代表某点到树根的距离.

- 动态规划加倍增优化 : 

```c++
int e[N],ne[N],h[500010],idx;
void add(int a,int b)
{
    e[idx] = b,ne[idx] = h[a],h[a] = idx ++;
}
int dp[500010][20];//用来保存父节点,表示第 2^j 个祖先是 x 
int depth[500010]; //保存深度 
bool st[500010];//预处理出父亲切点 
void DFS(int k)
{
	//预处理出DP数组
	for(int i=1;(1<<i)<depth[k];i++)//小于是因为 0 也是一个父节点，即一个距离已经被占用
		dp[k][i] = dp[dp[k][i-1]][i-1];
	for(int i=h[k];i;i=ne[i])
    {
        int j = e[i];
		//求解直接公共祖先；
		if(st[j]) continue;
		st[j] = true; 
		depth[j] = depth[k]+1;
		dp[j][0] = k;// 2 ^ 0 表示第一个祖先
		DFS(j);
	}
	return; 
} 
int lca(int u,int v)
{
	if(depth[u] < depth[v]) swap(u,v);
	//弹节点 
	int k=log2(depth[u]-depth[v]);//返回的浮点数由int接受等价于__lg,二进制最后一个 1 是第几位
	for(int i=k;i>=0;i--)
		if(depth[dp[u][i]]>=depth[v])u=dp[u][i];
	if(u == v)
        return u;
	//查询
	k = log2(depth[u]);//返回的是浮点数,但是int接受直接下取整
	for(int i=k;i>=0;i--)
    {
		if(dp[u][i] == dp[v][i])
            continue;
		u = dp[u][i];
		v = dp[v][i];
	} 
	return dp[u][0];
}
int main()
{
	//LCA模板题
	int n,m,s;
	cin >> n >> m >> s;
	int u,v;
	for(int i=0;i<n-1;i++)
    {
		cin >> u >> v;
		add(u,v),add(v,u);
	} 
	//这里要初始化为1，避免与深度为0的0产生歧义。 
	depth[s] = 1;
	st[s] = true;
	DFS(s);
	for(int i=0;i<m;i++)
    {
		cin >> u >> v;
		cout << lca(u,v) << endl;
	}
	return 0;
} 
```

- ST表优化

```c++
int e[2*N],ne[2*N],h[N],idx;//欧拉序列
void add(int a,int b)
{
    e[idx] = b,ne[idx] = h[a],h[a] = idx ++;
}
int sq[2*N];
int d[N];//深度

bool st[N];
int has[N]; 
void DFS(int k)
{
	for(int i=h[k];i;i=ne[i])
    {
		if(st[e[i]])
            continue;
		st[e[i]] = true;
		d[e[i]] = d[k]+1;
		DFS(e[i]);
		sq[++idx] = e[i];
		has[e[i]] = idx;
		sq[++idx] = k;
	}
	return;
}
int dp[2*N][20];
int lca(int l,int r)
{
	if(l>r)swap(l,r);
	int k=log2(r-l+1);
	if(d[dp[l][k]]<d[dp[r-(1<<k)+1][k]])
		return dp[l][k];
    else
		return dp[r-(1<<k)+1][k];
}
int main()
{
	int n,m,s;
	cin >> n >> m >> s;
	int u,v;
	for(int i=0;i<n-1;i++)
    {
		cin >> u >>v;
		add(u,v),add(v,u);
	}
	idx=0;
	st[s]=true;
	d[s]=1;
	DFS(s);
	for(int i=0;i<=idx;i++)dp[i][0]=sq[i];
	for(int j=1;j<=20;j++)
    {
		for(int i=0;i+(1<<j)<=idx+1;i++)
        {//这里一定要考虑区间的边界，要满足超出区间右侧一位 
			if(d[dp[i][j-1]]<d[dp[i+(1<<j-1)][j-1]])
				dp[i][j]=dp[i][j-1];

            else
				dp[i][j]=dp[i+(1<<j-1)][j-1];
		}
	}
	has[s]=idx;
	for(int i=0;i<m;i++)
    {
		cin >> u >> v;
		cout << lca(has[u],has[v]) << endl;
	} 
	return 0;
} 
```

- tarjan 算法

```c++
//tarjan离线算法实现LCA
const int N=5000005;
int arr[N];
int find(int x)
{
	return x==arr[x]?x:arr[x]=find(arr[x]);
} 
//这里的merge不能用秩平衡定理 
void merge(int x,int y){
	x = find(x);
	y = find(y);
	arr[x] = y;
	return;
}
//兄弟链表法存储所有边 
int e[2*N],ne[2*N],h[N],idx;
inline void add(int a,int b)
{
	e[idx] = b,ne[idx] = h[a],h[a] = idx++;
}
//用来存储所有的查询以及查询的序号 
vector<int> q[N];
vector<int> qi[N];
void add_query(int u,int v,int id)
{
	q[u].push_back(v);
	q[v].push_back(u);
	qi[u].push_back(id);
	qi[v].push_back(id);
	return;
}
int st[N];
int ans[N];
void DFS(int k)
{
	//初次访问 
	st[k] = 1; 
	for(int i=h[k];i;i=ne[i])
    {
		if(st[e[i]]) continue;
		DFS(e[i]);
		//注意这里只能单向合并，可以直接设置父节点 
		merge(e[i],k);
	}
	//这里一定要注意遍历顺序是后根序。 
	//查看是否已经有可以求解的
	for(int i=0;i<q[k].size();i++)
    {
		if(st[q[k][i]]==2){
			ans[qi[k][i]]=find(q[k][i]);
		}
	}
	//遍历结束 
	st[k] = 2;
	return;
}
int main(){ 
	int n,m,s;
	cin>>n>>m>>s;
	//初始化并查集
	for(int i=1;i<=n;i++)arr[i]=i; 
	int u,v;
	for(int i=0;i<n-1;i++)
    {
		cin >> u >> v;
		add(u,v);
		add(v,u);
	}
	for(int i=0;i<m;i++)
    {
		cin >> u >> v;
		add_query(u,v,i);
	}
	DFS(s);
	for(int i=0;i<m;i++)
    {
		cout << ans[i] << endl;
	}
}

```

### 树的重心

- 对于树上的每一个点,计算所有子树中的最大的子树结点数,这个值最小的点就是树的重心.(这里所有的子树都是指无根树的子树,即包括向上的那棵子树,并且不包括整棵树自身).

> 性质 **1 .**树的重心如果不唯一,则至多有两个,且这两个重心相邻.  **2 .**以树的重心为根时,所有子树的大小都不超过整棵树大小的一半. **3. **树中的所有点到某个点的距离中,到重心的距离和是最小的;如果有两个重心,那么到他们的距离和一样. **4.  **把两棵树通过一条边相连得到的一棵新的树,那么新的树的重心在连接原来两棵树的重心的路径上. **5 .**在一棵树上添加或删除一个叶子,那么它的重心最多只移动一条边的距离.

```c++
// 这份代码默认节点编号从 1 开始，即 i ∈ [1,n]
int size[MAXN],  // 这个节点的「大小」（所有子树上节点数 + 该节点）
    weight[MAXN],  // 这个节点的「重量」
    centroid[2];   // 用于记录树的重心（存的是节点编号）
void dfs(int cur, int fa) 
{  // cur 表示当前节点 (current)
  size[cur] = 1;
  weight[cur] = 0;
  for (int i = head[cur]; i != -1; i = e[i].nxt) 
  {
        if (e[i].to != fa) 
        {  // e[i].to 表示这条有向边所通向的节点。防止访问到父节点,成环死循环
          dfs(e[i].to, cur);
          size[cur] += size[e[i].to];
          weight[cur] = max(weight[cur], size[e[i].to]);
        }
  }
  weight[cur] = max(weight[cur], n - size[cur]);
  if (weight[cur] <= n / 2) // 依照树的重心的定义统计,最多有两种这种点
    centroid[centroid[0] != 0] = cur;//防止有两个重心
}
```

## 拓扑排序

> - 主要统计入度和出度的关系,**有向无环图一定能拓扑排序**   

```c++
bool topsort()//d 表示入度,q数组存的是拓扑序
{
	int hh = 0,tt = -1;
	for(int i=1;i<=n;i++)
	{
		if(!d[i])
			q[++tt] = i;
	}//先将入度为0的点入队
	while(hh <= tt)
	{
		int t = q[hh++];
		for(int i=h[t];i!=-1;i=ne[i])
		{
			int j = e[i];
			d[j]--;//不断更新入度出度和队列中的点
			if(d[j] == 0) q[++tt] = j;
		}
	}
	return tt == n-1;//判断是否全都入队了，由此判断是否存在拓扑结构
}   //tt从0开始所以一共n-1    有向无环图
```

### 拓扑排序判断是否存在环

> 拓扑排序判断图中是否有环,有向图度数为 0 的点入度,无向图度数为 1 的点入度,并且开一个 bool 数组,防止端点遍历到端点导致错误.

```c++
bool topsort()   //如果是有向图,环内的元素就是更新后度数为不为 0 的点(为1),如果是有向图,环内的元素就是更新后度数不为 1 的点,如果不加bool数组,可能导致端点遍历过后被其他端点继续遍历,导致度数重复错误的相减,例如无向图度数可能减为 0 ,但实际上拓扑后最小为 1 
{
    pre(i,1,n)
        if(d[i] == 1)//入度为 1 的点入队
            q.push(i),st[i] = 1;//bool数组标记
    while(q.size())
    {
        auto t = q.front();
        q.pop();
        for(int i=h[t];~i;i=ne[i])
        {
            int j = e[i];
            if(st[j]) continue;
            d[j] --;
            if(d[j] == 1)
                q.push(j),st[j] = 1;
        }
    }
    return q.size() == n;//true代表无环,否则代表有环,不在队列内的点构成一个环
}
```

## 最短路

### 单源正权最短路  --- Dijkstra

> - 堆优化的 Dijkstra 算法,-mlogn,找到目前权值最小的点,用这个点更新其他点到根节点的权值,直至堆为空.

```c++
int dijkstra()
{
	memset(dist,0x3f,sizeof dist);
	dist[1]=0;
	priority_queue<PII,vector<PII>,greater<PII>> heap;//小根堆
	heap.push({0,1});//将起点放进去
	while(heap.size())//堆是空的
	{
		auto t = heap.top();//小根堆，堆顶就是最小值
		heap.pop();
		int ver = t.second,distance = t.first;
		if(st[ver]) continue;//这个点是冗余备份，即这个点已经处理过了 
		st[ver] = 1; 
		for(int i=h[ver];i!=-1;i=ne[i])
		{
			int j = e[i];
			if(dist[j] > distance + w[i])
			{
				dist[j] = distance + w[i];
				heap.push({dist[j],j});
                  pre[j] = i;//前驱数组,用于求出最短路径
			}
		}
	}
	if(dist[n] == 0x3f3f3f3f) return -1;//图是不连通的
	return dist[n]; 
}
void find(int x)	//递归输出最短路径,起点是dij算法的起始点
{
	if (pre[x] == 1) 
		cout << 1;
	else 
		find(pre[x]);
	cout << " -> " << x;
	return;
}
for (int i = 2; i <= G.vex; i++)
{
    cout << "起点 v1 到 v" << i << " 的路径为： ";
    find(i);
    cout << endl;
}
```

### 在矩阵中 Dijkstra 算法，矩阵不能过大

[图式Dijkstra](https://ac.nowcoder.com/acm/problem/268703)

定义小根堆的时候`priority_queue<array<int,3>,vector<array<int,3>>,greater<array<int,3>> q` 分别表示距离，起点和终点，后续即是常规算法

```cpp
void dijkstra(int x,int y,vector<string> &mp,vector<vector<int>> &d,vector<vector<int>> &st)
{
    priority_queue<array<int,3>,vector<array<int,3>>,greater<array<int,3>>> q;
    q.push({0,x,y});//(x,y)到当前起点距离是 0 
    st[x][y] = 1;
    d[x][y] = 0;
    while(q.size())
    {
        auto [dis,a,b] = q.top();
        q.pop();
        for(int i=0;i<4;i++)
        {
            int nx = a + dx[i],ny = b + dy[i];
            if(nx >= 0 && nx < n && ny >= 0 && ny < m && !st[nx][ny])
            {
                st[nx][ny] = 1;
                d[nx][ny] = min(d[nx][ny],d[a][b] + (mp[nx][ny] - '0'));
                q.push({d[nx][ny],nx,ny});
            }
        }
    }
}
```



### 单源负权最短路 ---- Spfa    --- 	求 负环

```c++
int spfa()//st 数组和Dijk 的区别是Dijk只遍历一遍,Spfa是判断当前点是否在队列里,需要时时更新
{//如果没有负环,点最多出现 n 次,因为使用的是更新最短距离所以没有负环的情况下每个点最多出现一次
    memset(dist,0x3f,sizeof dist);//判断负环时不必将距离初始化为0x3f3f3f3f;判断最短路需要初始化,并且不需要cnt数组
	queue<int> q;
	for(int i=1;i<=n;i++)//最短路不需要,只需要找起点入队,更新st[起点]即可
	{//判断存在负环，但不一定是1经过负环，任意一个起点都有可能
		st[i] = 1;
		q.push(i);
	}
	while(q.size())
	{
		int t = q.front();//代表从t到j点有边
		q.pop();
		st[t] = 0;
		for(int i=h[t];i!=-1;i=ne[i])
		{
			int j = e[i];
			if(dist[j] > dist[t] + w[i])
			{
				dist[j] = dist[t] + w[i];
				cnt[j] = cnt[t] + 1;
				if(cnt[j] >= n) return 1;//存在负环,判断最短路的时候不能加
				if(!st[j])
				{
					q.push(j);
					st[j] = 1;
				}
			}
		}
	}
	return 0;//表示不存在负环
}
```

### Spfa 算法 判断是否存在正环

求正环的问题可以转换为求负环的问题,只是边点权重的分配不同。同时也可以直接转换为求某点的最长路，加上一定的经验，**当点的入队次数超过最多点的2倍时，认为图中存在正环**，没有正环的情况下，入队肯定不会超过2倍，

最短路与最长路的区别  ：

```c++
//最短路
if(dist[j] > distance + w[i])//只有可以使边权变小才会更新
{
    dist[j] = distance + w[i];
    heap.push({dist[j],j});
}
//最长路 
if(dist[j] < dist[t] + wf[t] - mid * wt[i]) //只有可以使边权变大才会更新,边更新变大
```

- 01 分数规划 + 最长路 问题
- L 个点，P 条边的有向图，每个点都有一个权值 f[i] ，每条边都有一个权值 t[i]，求图中的一个环，使环上各点的权值之和 / 环上各边的权值之和最大   ------>  01 分数规划，点权和边权都存在，可以将点权放到边权上，出边入边都可，由此转换为 $\sum\text{fi - mid * }\sum\text{ti>0 }\Longrightarrow\sum\text{ (fi - mid * ti)\ >\ 0}.$，输出的是结果的最大值。对于点权 f[i] 将它附到出边上，即是 f[i] 实际上是下标为 i 的边的出发点的点权，如果重定义每个边的权值为( f[i] - mid * t[i] )，那么问题转换为转换为**权值后的图中是否存在正环**。

```c++
 const int N = 1010,M = 5010;
 int n,m;
 int wf[N];
 int h[N],e[M],wt[M],ne[M],idx;
 double dist[N];
 int q[N],cnt[N];
 bool st[N];
void add(int a,int b,int c)
{
	e[idx] = b,ne[idx] = h[a],wt[idx] = c,h[a] = idx ++;
}
bool check(double mid)
{
	//memset(dist,0,sizeof dist);最长路问题可以不用初始化,因为如果存在环一定会不断地更新,初始化与否不重要
	memset(st,0,sizeof st);
	memset(cnt,0,sizeof cnt);
	int hh = 0,tt = 0;//循环队列
	for(int i=1;i<=n;i++)
	{
		q[tt ++] = i;
		st[i] = true;
	}
	while(hh != tt)
	{
		int t = q[hh ++];
		if(hh == N) hh = 0;
		st[t] = false;
		for(int i=h[t];~i;i=ne[i])
		{
			int j = e[i];
			if(dist[j] < dist[t] + wf[t] - mid * wt[i])
			{//最长路：边更新变大，与最短路相反
				dist[j] = dist[t] + wf[t] - mid * wt[i];
				cnt[j] = cnt[t] + 1;
				if(cnt[j] >= n) return true;//正环一定可以使得满足条件
				if(!st[j])
				{
					q[tt ++] = j;
					if(tt == N) tt = 0;
					st[j] = true;
				}
			}
		}
	}
	return false;//因为题目要求的是环中的结果,所以如果没有正环,则代表一定不成立,收缩右值
}//false 包括没有达成环,和没有正环
int main()
{
	_;
	cin >> n >> m;
	memset(h,-1,sizeof h);
	for(int i=1;i<=n;i++)
		cin >> wf[i];
	while(m --)
	{
		int a,b,c;
		cin >> a >> b >> c;
		add(a,b,c);
	}
	double l = 0,r = 1010;//二分答案
	while(r - l > 1e-4)
	{
		double mid = (l + r) / 2;//mid 是二分答案,找到答案的值
		if(check(mid)) l = mid;
		else r = mid;
	}
	cout << r << endl;
	return 0;
}
```

- 单词环   前面两个字母和后面两个字母相同时，可以连接在一起，求单词环的平均长度，即是 wi 表示单词的长度，si 表示单词的个数

  $\sum\text{wi}/\sum\textbf{si 最大,}\sum\text{wi}/\sum\text{1>M , }\Rightarrow\sum\text{(Mi-M}*1)>0\Rightarrow $ 图中是否存在正环，判断无解的时候只需要判断 M = 0 的时候即可。

  等价于将前两个字母和后两个字母哈希为两个数，表示从前面的数到后面的数建一条边，最多26 * 26 = 676 个点

  ```c++
  const int N = 700,M = 100010;//点的数量，边的数量
  int n;
  int h[N],e[M],w[M],ne[M],idx;
  double dist[N];
  int q[N],cnt[N];
  bool st[N];
  void add(int a,int b,int c)
  {
  	e[idx] = b,w[idx] = c,ne[idx] = h[a],h[a] = idx ++;
  }
  bool check(double mid)
  {
  	memset(st,0,sizeof st);//dist 可以不初始化,因为如果存在正环会不断的更新距离,一定会进入if判断
  	memset(cnt,0,sizeof cnt);
  	int hh = 0,tt = 0;
  	for(int i=0;i<676;i++)
  	{
  		q[tt ++] = i;
  		st[i] = true;
  	}
  	int count = 0;	
  	while(hh != tt)
  	{
  		int t = q[hh ++];
  		if(hh == N) hh = 0;
  		st[t] = false;
  		for(int i=h[t];~i;i=ne[i])
  		{
  			int j = e[i];
  			if(dist[j] < dist[t] + w[i] - mid * 1)
  			{//最长路：边更新变大，与最短路相反
  				dist[j] = dist[t] + w[i] - mid;
  				cnt[j] = cnt[t] + 1;
  				if(++ count > 2 * n) return true;//点更新的次数大于2*n经验上就是存在正环
  				if(cnt[j] >= N) return true;
  				if(!st[j])
  				{
  					q[tt ++]  =j;
  					if(tt == N) tt = 0;
  					st[j] = true;
  				}
  			}
  		}
  	}
  	return false;
  }
  int main()
  {
  	_;
  	char  str[1010];
  	while(cin >> n,n)
  	{
  		memset(h,-1,sizeof h);
  		idx = 0 ;
  		for(int i=0;i<n;i++)
  		{
  			cin >> str;
  			int len = strlen(str);
  			if(len >= 2)
  			{
  				int left = (str[0] - 'a') * 26 + str[1] - 'a';//将前两个字母哈希为数值
  				int right = (str[len - 2] - 'a') * 26 + str[len - 1] - 'a';//将后两个字母哈希为数值
  				add(left,right,len);//前两个字母和后两个字母间建边,权值是字符长度
  			}
  		}
  		if(!check(0)) cout << "NO solution" << endl;//判断无解
  		else 
  		{
  			double l = 0,r = 1000;//二分答案
  			while(r - l > 1e-4)
  			{
  				double mid = (l + r) / 2;
  				if(check(mid)) l = mid ;
  				else r = mid;
  			}
  			cout << r << endl;
  		}
  	}
  	return 0;
  }//3   intercommunicational   alkylbenzenesulfonate   tetraiodophenolphthalein ---> 0
  ```

  

## 多源最短路 -- floyd

```c++
void floyd()
{
	for(int k=1;k<=n;k++)
		for(int i=1;i<=n;i++)
			for(int j=1;j<=n;j++)//这样的循环顺序可以保证后面的数据更新是
				d[i][j]=min(d[i][j],d[i][k]+d[k][j]);//用到的前面的数据是更新过的
}//读入的时候需要去重边,保存较小的边
if(d[a][b]>INF/2) puts("impossible");//图不连通,判断的时候不能直接用INF,可能有负权边
```

## 最小生成树

- 最小生成树的唯一性 : 如果一条边 **不在最小生成树的边集中 ,**并且可以替换与其**权值相同,并且在最小生成树边集**的另一条边,那么这个最小生成树就是不唯一的.先统计当前权值的总个数,然后计算选上的个数,如果未完全选上,则代表枚举到的点之前已经有联通块,加上此权值的边会成环,不必全选但是路径不唯一,即是最小生成树不唯一

- ```c++
  const int N = 200010, mod = 1e9+7;
  int T, n, m;
  struct node{
  	int x, y, w;
  }a[N];
  int ans, pre[N];
  int sum;
  bool cmp(node a, node b){
  	return a.w < b.w;
  }
  int find(int x){
  	if(pre[x] != x) pre[x] = find(pre[x]);
  	return pre[x];
  }
  int kruskal()
  {
  	sort(a+1, a+m+1, cmp);	
  	for(int i=1;i<=m;i++)
  	{
  		int r = i;		
  		while(r <= m && a[r].w == a[i].w) r++;//记录当前枚举到的权值
  		r --;		
  		int cnt = 0; //记录可拿边的个数 
  		for(int j=i;j<=r;j++)
  		{
  			int x = a[j].x, y = a[j].y, w = a[j].w;
  			if(find(x) != find(y)) cnt++; //可拿边个数++ 此时不合并
  		}		
  		for(int j=i;j<=r;j++)
  		{
  			int x = a[j].x, y = a[j].y, w = a[j].w;
  			if(find(x) != find(y)) pre[find(x)] = find(y), sum += w, cnt --; //用掉了，cnt-- 
  		}		
  		i = r;		
  		if(cnt) return 0; //最后还剩余可拿边，最小生成树不唯一 
  	}
  	return 1;
  }
  signed main(){
  	Ios;
  	cin >> T;
  	while(T --)
  	{
  		cin >> n >> m;
  		ans = 0, sum = 0;
  		for(int i=1;i<=n;i++) pre[i] = i;		
  		for(int i=1;i<=m;i++)
  		{
  			int x, y, w;cin >> x >> y >> w;
  			a[i] = {x, y, w};
  		}		
  		if(!kruskal()) cout << "Not Unique!\n" ;
  		else cout << sum << endl;
  	}	
  	return 0;
  }
  ```

### Prim 

> - o(n^2)-稠密图-一般是无向图,边多点少,找到集合外距离最近的点用t更新其他点到集合的距离

```c++
int prim()//g数组是邻接矩阵,存的是两点之间的最小边权
{
	memset(dist,0x3f,sizeof dist);
	int res = 0;//最小生成树里面所有边的长度之和
	for(int i=0;i<n;i++)
	{
		int t = -1;
		for(int j=1;j<=n;j++)
			if(!st[j] && (t==-1||dist[t]>dist[j]))
				t = j;
		if(i && dist[t] == INF) return INF;//不是第一个点，并且距离集合最近的点是正无穷，代表不连通
		if(i) res += dist[t];//只要不是第一个点加上距离   dist表示这个点到集合的问题
		for(int j=1;j<=n;j++)//先加和在更新，防止自环
			dist[j] = min(dist[j],g[t][j]);//到集合的距离		
		st[t] = true;
	}
	return res;//t == INF 不连通的图
}
```

### Kruskal

> - 稀疏图-先对边递增排序-o（mlongn),点多边少

```c++
struct Edge//结构体存储所有边	
{
	int a,b,w;	
	bool operator< (const Edge &W) const
 	{
		return w < W.w;//重载小于号，方便排序
	}
}edges[N];
int find(int x)
{
	if(p[x] != x) p[x] = find(p[x]);
	return p[x];
}
//核心代码
sort(edges,edges+m);
for(int i=1;i<=n;i++) p[i] = i;
int res = 0,cnt=0;
for(int i=0;i<m;i++)
{
    int a = edges[i].a,b = edges[i].b,w = edges[i].w;
    a = find(a),b = find(b);//并查集找根节点
    if(a!=b) //a b 未连通
    {
        p[a] = b;//合并
        res += w;
        cnt ++;//存了多少条边
    }
}//cnt < n - 1,代表的是图不连通,并查集里面的点小于n个,根节点未计入
```

### 次小生成树

- **非严格次小生成树 :**在无向图中,边权最小的满足边权和 **大于等于** 最小生成树边权的生成树,依次枚举未被选上的边(u->v),找到这条边为端点的最小生成树路径内的边权最大的边,用为选中的边代替权值最大的边,$\ M^{`}\ =\ M\ +\ w\ -\ w^{`}.对所有替换到的答案\ M^{`}\ 取最小值即可$,

- 严格次小生成树 : 边权最小的满足边权和 **严格大于** 最小生成树边权和的生成树.替换的边要严格大于路径中最大边权的权值.

- ```c++
  const int N = 510,M = 10010;
  int n,m;
  struct Edge
  {
  	int a,b,w;
  	bool f;//表示是不是非树边
  	bool operator<(const Edge &t)const
  	{
  		return w < t.w;
  	}
  }edge[M];
  int p[N],dist[N][N];
  int h[N],e[N*2],w[N*2],ne[N*2],idx;//用来建树
  void add(int a,itn b,int w)
  {
  	e[dix] = b,w[idx] = c,ne[idx] = h[a],h[a] = idx ++;
  }
  int find(int x)
  {
  	if(x != p[x])
  		p[x] = find(p[x]);
  	return p[x];
  }
  void dfs(int u,int fa,int maxd,int d[])//maxd存的是从根节点到当前点的边权最大值
  {
  	d[u] = maxd;
  	for(int i=h[u];~i;i=ne[i])
  	{
  		int j = e[i];
  		if(j != fa)
  		{
  			dfs(j,u,max(maxd,w[i]),d);//维护的时候更新一下最大值即可
  		}
  	}
  }
  int main()
  {
  	_;
  	cin >> n >> m;
  	memset(h,-1,sizeof h);
  	for(int i=0;i<m;i++)
  	{
  		int a,b,w;
  		cin >> a >> b >> w;
  		edge[i] = {a,b,w};
  	}
  	sort(edge,edge + m);
  	for(int i=1;i<=n;i++) p[i] = i;
  	ll sum = 0;
  	for(int i=0;i<m;i++)
  	{
  		int a = edge[i].a,b = edge[i].b,w = edge[i].w;
  		int pa = find(a),pb = find(b);
  		if(pa != pb)
  		{
  			p[pa] = pb;
  			sum += w;
  			add(a,b,w),add(b,a,w);//建树，无向边
  			edge[i].f = 1;//标记是树边
  		}
  	}
  	for(int i=1;i<=n;i++)
  		dfs(i,-1,0,dist[i]);//传入父节点是因为是双向边防止死循环
  	for(int i=0;i<m;i++)
  	{
  		if(!edge[i].f)//是非树边
  		{
  			int a = edge[i].a,b = edge[i].b,w = edge[i].w;
  			if(w > dist[a][b])
  			{
  				res = min(res,sum + w - dist[a][b]);
  			}
  		}
  	}
  	cout << res << endl;
  	return 0;
  }//4 4   1 2 100   2 4 200   2 3 250   3 4 100  ----> 450
  ```

> 优化 : 求解方法基本一致 : 使用倍增的思路维护,差别在于如果找到的最大边权和当前枚举的边权一致,使用严格次大值来替换枚举到的权值.

```c++
const int INF = 0x3fffffff;
const long long INF64 = 0x3fffffffffffffffLL;
struct Edge {
  int u, v, val;
  bool operator<(const Edge &other) const { return val < other.val; }
};
Edge e[300010];
bool used[300010];
int n, m;
long long sum;
class Tr {
 private:
  struct Edge {
    int to, nxt, val;
  } e[600010];
  int cnt, head[100010];
  int pnt[100010][22];
  int dpth[100010];
  // 到祖先的路径上边权最大的边
  int maxx[100010][22];
  // 到祖先的路径上边权次大的边，若不存在则为 -INF
  int minn[100010][22];
 public:
  void addedge(int u, int v, int val) {
    e[++cnt] = (Edge){v, head[u], val};
    head[u] = cnt;
  }
  void insedge(int u, int v, int val) {//无向图
    addedge(u, v, val);
    addedge(v, u, val);
  }
  void dfs(int now, int fa) {
    dpth[now] = dpth[fa] + 1;
    pnt[now][0] = fa;
    minn[now][0] = -INF;
    for (int i = 1; (1 << i) <= dpth[now]; i++) {
      pnt[now][i] = pnt[pnt[now][i - 1]][i - 1];
      int kk[4] = {maxx[now][i - 1], maxx[pnt[now][i - 1]][i - 1],
                   minn[now][i - 1], minn[pnt[now][i - 1]][i - 1]};
      // 从四个值中取得最大值
      std::sort(kk, kk + 4);
      maxx[now][i] = kk[3];
      // 取得严格次大值
      int ptr = 2;
      while (ptr >= 0 && kk[ptr] == kk[3]) ptr--;
      minn[now][i] = (ptr == -1 ? -INF : kk[ptr]);
    }
    for (int i = head[now]; i; i = e[i].nxt) {
      if (e[i].to != fa) {
        maxx[e[i].to][0] = e[i].val;
        dfs(e[i].to, now);
      }
    }
  }
  int lca(int a, int b) {
    if (dpth[a] < dpth[b]) std::swap(a, b);
    for (int i = 21; i >= 0; i--)
      if (dpth[pnt[a][i]] >= dpth[b]) a = pnt[a][i];
    if (a == b) return a;
    for (int i = 21; i >= 0; i--) {
      if (pnt[a][i] != pnt[b][i]) {
        a = pnt[a][i];
        b = pnt[b][i];
      }
    }
    return pnt[a][0];
  }
  int query(int a, int b, int val) {
    int res = -INF;
    for (int i = 21; i >= 0; i--) {
      if (dpth[pnt[a][i]] >= dpth[b]) {
        if (val != maxx[a][i])
          res = std::max(res, maxx[a][i]);
        else
          res = std::max(res, minn[a][i]);
        a = pnt[a][i];
      }
    }
    return res;
  }
} tr;
int fa[100010];
int find(int x) { return fa[x] == x ? x : fa[x] = find(fa[x]); }
void Kruskal() {
  int tot = 0;
  std::sort(e + 1, e + m + 1);
  for (int i = 1; i <= n; i++) fa[i] = i;

  for (int i = 1; i <= m; i++) {
    int a = find(e[i].u);
    int b = find(e[i].v);
    if (a != b) {
      fa[a] = b;
      tot++;
      tr.insedge(e[i].u, e[i].v, e[i].val);
      sum += e[i].val;
      used[i] = 1;
    }
    if (tot == n - 1) break;
  }
}
int main() {
	_;
  std::cin >> n >> m;
  for (int i = 1; i <= m; i++) {
    int u, v, val;
    std::cin >> u >> v >> val;
    e[i] = (Edge){u, v, val};
  }
  Kruskal();
  long long ans = INF64;
  tr.dfs(1, 0);
  for (int i = 1; i <= m; i++) {
    if (!used[i]) {
      int _lca = tr.lca(e[i].u, e[i].v);
      // 找到路径上不等于 e[i].val 的最大边权
      long long tmpa = tr.query(e[i].u, _lca, e[i].val);
      long long tmpb = tr.query(e[i].v, _lca, e[i].val);
      // 这样的边可能不存在，只在这样的边存在时更新答案
      if (std::max(tmpa, tmpb) > -INF)
        ans = std::min(ans, sum - std::max(tmpa, tmpb) + e[i].val);
    }
  }
  std::cout << (ans == INF64 ? -1 : ans) << '\n';  // 次小生成树不存在时输出 -1
  return 0;
}
```

## 斯坦纳树

- 与最小生成树的区别在于可以外加点使得所有点连通的距离和最小
- **定理 :** 对于 a,b,c 三个点加入额外点 P 使 a+b+c 最小,(a,b,c分别为PA,PB,PC), 若三角形ABC内角均小于$ 120^{\circ},那么\ P\ 就是使边PA.PB,PC两两之间$$成\ 120^{\circ}\ 的点的角都是\ 120^{\circ}\ 的点,如果三角形ABC的一个角大于或等于\ 120^{\circ}\ 那么\ P\ 点与这个点重合.$

> $问题推广 : 数学上表述为: 给定 n 个点\ A_{1},A_{2},\dots A_{n},试求连接此\ n\ 个点,总长最短的直线连接系统,并且任意两点都可以由系统中的直线段$$组成的折线连接起来.此新问题称为\ ^*斯坦纳树问题^*\ .在给定\ n\ 个点的情形,最多将有\ n-2\ 个复接点(斯坦纳点).过每一个斯坦纳点,至多有$$三条边通过.若为三条边,则他们两两相交成120^{\circ}角,若为两条边,则此斯坦纳点必为某一一给定的点,且此两条边交成的角比大于或等于120^{\circ}  $

## 差分约束

- 对于最短路问题,队列的作用是将遍历的点存起来,栈也可以实现这个功能,队列是先进先出,栈是先进后出,对于存在一个负环的问题,由于栈的先进后出,会使此后遍历到的点尽可能是环内的点,减少不必要遍历,同时**当点入队的次数超过 ( 2 * 点数 ) 次时,一般认为图中含有环**(一般是不断超时加的优化),某个点入队次数超过 n 次,也被认为含环,最短路找负环,最长路找正环.

- ```c++
  //最短路问题更新
  if(dist[j] > dist[t] + w[i]) // j = e[i]
  {
      dist[j] = dist[t] + w[i];
      cnt[j] = cnt[t] + 1;
      if(cnt[j] >= n)
          return true;
      if(!st[j])
      {
          q[tt ++] = j;
          if(tt == N) tt = 0;
          st[j] = true;
      }
  }
  // 最长路更新
  if(dist[j] < dist[t] + wf[t] - mid * wt[i]) // j = e[i]
  {//最长路：边更新变大，与最短路相反
      dist[j] = dist[t] + wf[t] - mid * wt[i];
      cnt[j] = cnt[t] + 1;
      if(cnt[j] >= n) return true;
      if(!st[j])
      {
          q[tt ++] = j;
          if(tt == N) tt = 0;
          st[j] = true;
      }
  }
  //最短路和最长路的区别主要在于更新的时候大小于号的区别
  ```

- 差分约束 : 用来求不等式的可行解,形如 : $X_{i}\ \le\ X_{j}\ + \ C_{k}$的方程组的一组可行解, **源点 **需要满足的条件 : 从源点出发,一定可以走到所有的边. 如果无解一定是走到了 $X_{i}\ <\ X_{i}$ 的情况  $\Longrightarrow$ 存在负环($1. 先将每个不等式\ X_{i}\ \le\ X_{j}\ + \ C_{K}\ 转换为一条从\ X_{i}走到\ X_{j}\ 长度为\ C_{K}\ 的一条边\ 2.找到一个超级源点,使得$$该源点一定可以遍历到所有的边\ 3.从源点求一遍单源最短路,①如果存在负环,则原不等式无解,②如果没有负环,则dist\ 就是原不等式的一个可行解$)

-  求最值(指的是每一个变量的最值)  **1.** 求**最小值**,则对应求**最长路**    **2.**求**最大值**,则对应求**最短路.**  (主要转换为$X_{i}\ \le\ c\ 其中\ c\ 是一个常数,这类的不等式. $)

- 方法 : 建立一个超级源点 0 ,然后建立$0\to i,长度是\ c\ 的边即可.以求X_{i}的^*最大值^*为例:求所有从X_{i}出发,构成的不等式链X_{i} \le X_{j} + C_{1} \le X_{k}+C_{2}+C_{1} $$\le \dots \le C_{1}+C_{2}+\dots 所计算出的上界,最终X_{i}的最大值等于所有上界的$**最小值**,每一条链都转换为一条最长路,求最长路的最小值.结果便是$X_{i}$的**最大值**

- ```c++
  int q[N],cnt[N];//cnt用来求正环,q是队列
  bool st[N];//是否在队列里
  void add(int a,int b,int c)
  {
  	e[idx] = b,w[idx] = c,ne[idx] = h[a],h[a] = idx++;
  }
  bool spfa()
  {
  	int hh = 0,tt = 1;
  	memset(dist,-0x3f,sizeof dist);//最长路，需要初始化为负无穷
  	dist[0] = 0;//超级源点
  	q[0] = 0;
  	st[0] = true;
  	while(hh != tt)//虽然栈存求负环spfa会更快，但是在求一般的问题时，栈会十分么慢，最长路这种无环的时候更新从后往前会造成许多浪费，本题队列超时，需改为栈尝试
  	{
  		//int t = q[hh ++];
  		int t = q[-- tt];
  		//if(hh == N) hh = 0;
  		st[t] = false;
  		for(int i=h[t];~i;i=ne[i])
  		{
  			int j = e[i];
  			if(dist[j] < dist[t] + w[i])
  			{
  				dist[j] = dist[t] + w[i];
  				cnt[j] =  cnt[t] + 1;
  				if(cnt[j] >= n + 1)//包括 0 号点，一共 n + 1 个点
  					return false;
  				if(!st[j])
  				{
  					q[tt ++] = j;
  					//if(tt == N) tt = 0;
  					st[j] = true;
  				}
  			}
  		}
  	}
  	return true;
  }
  int main()
  {
  	_;
  	cin >> n >> m;
  	memset(h,-1,sizeof h);
  	while(m --)
  	{
  		int x,a,b;
  		cin >> x >> a >> b;
  		if(x == 1)
  			add(b,a,0),add(a,b,0);
  		else if(x == 2)//从 a 向 b 连一条长度为 1 的边
  			add(a,b,1);
  		else if(x == 3)
  			add(b,a,0);//从 b 向 a 连一条长度为 0 的边
  		else if(x == 4) 
  			add(b,a,1);
  		else add(a,b,0);
  	}
  	for(int i=1;i<=n;i++) add(0,i,1);//源点到每个点的距离为 1 ，源点的值为0，每个人大于1
  	if(!spfa())
  		cout << -1 << endl;//有负环无解
  	else
  	{
  		ll res = 0;
  		for(int i=1;i<=n;i++) res += dist[i];
  		cout << res << endl;
  	}
  }//5 7  1 1 2  2 3 2  4 4 1  3 4 5  5 4 5  2 3 5  4 5 1  ---> 11
  ```

  

## 二分图

### 染色法

> - 染色法判断是不是二分图,二分图一定不含奇数环,不含奇数环的是二分图

```c++
//链式前向星建边,略,无向图,边是点的二倍
bool dfs(int u,int c)
{
	color[u] = c;//记录当前点的颜色是c
	for(int i = h[u];i!=-1;i=ne[i])//遍历点的领点
	{
		int j = e[i];
		if(!color[j])//没有染色
		{
			if(!dfs(j,3-c)) return false;
		}//没有染色，染成另外一种颜色1或2
		else if(color[j]==c)
			return false;//边的端点颜色一样
	}
	return true;
}
bool flag = true;//表示染色是不是有矛盾
for(int i=1;i<=n;i++)//染色
    if(!color[i])
    {
        if(!dfs(i,1))
        {
            flag = false;
            break;
        }
    }
//flag = 1,表示是二分图,否则不是二分图
```

### 匈牙利算法 - 只能建单项边

> - 求二分图最大匹配数,如果现在匹配的两个点都是单独的,就匹配在一起,否则就遍历其中一个点的其他匹配可否更换,,如果可以更换就更换,否则匹配失败 

```c++
int match[N];//右边的相对应的点//match[j]=a,表示女孩j的现有配对男友是a
bool st[N];//st[]数组我称为临时预定数组，st[j]=a表示一轮模拟匹配中，女孩j被男孩a预定了。
bool find(int x)//这个函数的作用是用来判断,如果加入x来参与模拟配对,会不会使匹配数增多
{	 //遍历自己喜欢的女孩
	for(int i=h[x];i!=-1;i=ne[i])//左半部分
	{
		int j = e[i];
		if(!st[j])//如果在这一轮模拟匹配中,这个女孩尚未被遍历
		{
			st[j] = true;//更新状态
			if(match[j]==0||find(match[j]))//右半部分对应的点未匹配左半部分或者右半部分匹配的左边有其他选择，
			{ //如果女孩j没有男朋友，或者她原来的男朋友能够预定其它喜欢的女孩。配对成功,更新match
				match[j] = x;
				return true;
			}	
		}
	}//自己中意的全部都被预定了。配对失败。
	return false;
}
for(int i=1;i<=n1;i++)
{//因为每次模拟匹配的预定情况都是不一样的所以每轮模拟都要初始化
    memset(st,false,sizeof st);//将右半部分清空，保证只匹配一次,macth不能清楚,所以不会影响匹配结果 n
    if(find(i)) res++;
}
```

## 有向图的强连通分量

- 对于一个有向图。连通分量 ：对于分量中的任意两点 u，v，必然可以从 u 走到 v，且从 v 走到 u 。一般按照DFS的顺序来求解。（强连通分量 ： 极大连通分量）。
- 作用 ：令一个有向图缩点为有向无环图（PAG），也即是拓扑图（缩点指的是将所有的连通分量缩成一个点，即是将环变为点），这样求最短路或者最长路可以直接递推来求解。O（n + m）
- 分类 ： 1、树枝边 ： x  是 y 的一个父节点。2、前向边：x 是 y 的祖先节点。3、后向边：x 是 y 的子节点，不必是直接子节点。4、横叉边：x 和 y 不在同一个分支，由 x 搜到另一个分支的边，这个 y 此时已经被搜过了。
- 解释 : 如果按照DFS从左到右的顺序搜索，由 x 搜到左边的点这时左边的点被搜过是横叉边，如果搜到右边的点，未搜过的，是树枝边。
- 判断 ： 如果一个点在强连通分量（SCC）中：1、存在后向边指向祖先节点（后向边一定向上走）。2、它走到一个横叉边上，且横叉边的点走到了祖先节点。

### Tarjan算法求SCC

实现方法 ：

​		引入一个时间戳的概念，按照DFS的搜索顺序给每一个节点打上时间戳，即是第几个搜索到的点，对于每个点定义两个时间戳，dfn[u] 表示遍历到 u 的时间戳，low[u] 从 u 开始走所能遍历到的最小时间戳，如果 u 是它所在的强连通分量的最高点，等价于 dfn[u] == low[u]，

# 数学知识

## 期望

E(x + y) = E(x) + E(y) ，无论`x`和`y`是否存在一些互斥或者其他关系影响，这个公式都是满足的，**和的期望等于期望的和！！！！**

## 素数筛法

### 线性筛

```c++
void get_primes(int n)
{
	for(int i=1;i<=n;i++)
	{
		if(!st[i])
		{
			primes[cnt++] = i;
			for(int j=i+i;j<=n;j+=i) st[j] = true;
		}
	}
}
```

### 欧拉筛

```c++
void get_primes(int n)
{
	for(int i=2;i<=n;i++)
	{
		if(!st[i])
        {
            primes[cnt++]=i;
            lcm = lcm * qmi(i,(int)(log(n) / log(i))) % mod;//求 1 - n 之间所有数的 LCM 
        }
		for(int j=0;primes[j]<=n/i;j++)
		{
			st[primes[j]*i] = true;//把每个合数用最小质因子筛掉即可
			if(i%primes[j]==0) break;//primes[j]一定是i的最小质因子
		}
	}
}
```

## 约数

### 约数个数

> - $对于一个大于1的正整数n可以分解质因数\mathbf{n}=\prod_{\mathrm{i=1}}^{\mathrm{k}}\mathbf{p}_{\mathrm{i}}^{\alpha_{\mathrm{i}}}\ =\mathbf{p}_{\mathrm{I}}^{\mathrm{a_{1}}}\ *\mathbf{p}_{\mathrm{2}}^{\mathrm{a_{2}}}\cdots\mathbf{p}_{\mathrm{k}}^{\mathrm{a_{k}}},则约数的个数为{\mathfrak{f}}({\mathfrak{n}})=\prod_{i=1}^{{\mathfrak{k}}}{\big(}a_{i}+1{\big)}=(a_{1}+1){\big(}a_{2}+1{\big)}\cdot\cdot\cdot{\big(}a_{\mathbf{k}}+1{\big)},$
> - $其中\alpha_{1},\alpha_{2}\cdots是P_{1},P_{2}\cdots P_{k}的指数   $

```c++
unordered_map<int,int> primes;//哈希表,存素数,将大值映射为小值
while(n--)
{
    int x;
    cin >> x;
    for(int i=2;i<=x/i;i++)//试除法判断这个数的质因子有什么,存入primes里面
        while(x % i ==  0)
        {
            x /= i;
            primes[i] ++;//存质因子个数2
        }
    if(x > 1) primes[x] ++;//这个约数较大超过了sqrt(x) 
}
ll res = 1;
for(auto prime : primes) res = res * (prime.second + 1) % mod;//计算约数个数
```

### 约数求和

> - $对于一个大于1的正整数n可以分解质因数\mathbf{n}=\prod_{\mathrm{i=1}}^{\mathrm{k}}\mathbf{p}_{\mathrm{i}}^{\alpha_{\mathrm{i}}}\ =\mathbf{p}_{\mathrm{I}}^{\mathrm{a_{1}}}\ *\mathbf{p}_{\mathrm{2}}^{\mathrm{a_{2}}}\cdots\mathbf{p}_{\mathrm{k}}^{\mathrm{a_{k}}},则余数之和sum等于$
> - $sum = ((P_{1}^0+P_{1}^1+P_{1}^2\cdots+P_{1}^{a_{1}}) * (P_{2}^0+P_{2}^1+P_{2}^2\cdots+P_{2}^{a_{2}}) * \cdots * (P_{k}^0+P_{k}^1+P_{k}^2\cdots+P_{k}^{a_{k}}))$

```c++
unordered_map<int,int> primes;//哈希表
while(n--)
{
    int x;
    cin >> x;
    for(int i=2;i<=x/i;i++)
        while(x%i==0)
        {
            x /= i;
            primes[i]++;//存质因子个数
        }
    if(x > 1) primes[x]++;//这个约数较大超过了sqrt(x) 
}
ll res = 1;
for(auto prime : primes)
{	
    int p = prime.first,a = prime.second;
    ll t = 1;
    while(a--)
        t = (t * p + 1) % mod;//求得是对于每一个质因子的部分和
    res = res * t % mod;//结果是所有部分和 t 的乘积.
}	
```

## 裴蜀定理





## 抽屉原理(鸽巢原理)  [CSDN](https://blog.csdn.net/Destinymiao/article/details/81392751)

常被用于证明存在性证明和求最坏情况下的解。

- 原理 1 ：把多于 n + 1 个的物体放到 n 个抽屉里，则至少有一个抽屉里的东西不少于两件
- 原理 2 ：把多于 mn + 1 （n 不为0）个物品放进 n 个抽屉里，则至少有一个抽屉里有不少于 （m + 1) 的物体
- 原理 3 :  把无穷多件物体放进 n 个抽屉里，则至少有一个抽屉里 有无穷个物体

第二抽屉原理 ： 把（mn - 1）个物体放进 n 个抽屉里，其中必有一个抽屉中至多有 （m - 1）个物体（例如 ： 将3  * 5 - 1 = 14 个物体放进 5 个抽屉里，则必定有一个抽屉中的物体数少于等于 3 - 1 = 2 个）

> 拉姆塞理论

6个点,无三点共线,每两点用蓝色或者红色线段连接起来,练好之后一定有一个 三角形的三边同色.  --- >  6个人,或者有三个人之前就认识或者三个人之前不认识

## 博弈论

- 公平组合游戏(ICG) -- SG 函数

ICG   --- 有两名玩家，两名玩家轮流操作，在一个有限集合内任选一个进行比较，改变游戏当前局面，一个局面的合法操作，只取决于游戏本身且固定存在，与玩家次序或者其他任何因素无关，无法操作者，即操作结合为空，输掉比赛，另一方获胜。

定义 sg(x)  := mex( { sg(y) | x -> y} )，其中 x 和 y 表示的都是某种状态，x -> y 在这里表示 x 状态可以通过**一次**操作**到达** y 状态，称 sg(x) = 0 的状态 x 为**必败态**，相反，如果 sg(x) != 0，则称 x 为**必胜态**，说明此时存在策略使自己必定取胜（即每次轮到自己都会转移到 sg 为 0 的状态）（即是博弈图，每个状态能到达的状态都是一个点，转移的过程是一个边）

-   定义函数:`SG(x)=mex(sg(y)|x->y)`, 此时:

1.   $sg(x)=0$, 为必败态. 
     1.   当$X$没有下一步转移态时, $sg(x)=0$, 当前手必输
     2.   当$X$有下一步转移态, 此时 $mex({y\ |\ x\ ->y})$ 均大于 0, 也就是说下一手的人能再次把状态转移到$sg(x^/)=0$, 经过若干 turns 后, 当前手仍必败.
2.   $sg(x)>0$, 为必胜态
     1.   当前手一定可以把状态转换为 $sg(x^/)=0$ 给下一 player.

-   然后打表找 sg 规律

-   对于多个 ICG 组合的游戏:

有定理:$sg(x_1+x_2+......+x_n)=sg(x_1 ) \^sg(x_2) \^...... \^xg(x_n)$



NIM 游戏  ：  $\text{定义 Nim 和}=a_1\oplus a_2\oplus\ldots\oplus a_n\text{。}$ 当且仅当 Nim 和为 0 时,该状态为必败状态.,否则为必胜状态。无



![ICG](图片/NIm转移求sg.png)

如果每堆石子操作的时候只能从特定的一些数中取出

```c++
 //最多有100堆石子,每堆石子的个数最多是10000个
 const int N = 110, M = 10010;
 int n, m;
 //用s[]来表示可以操作的	集合,用f[]来表示SG函数的值
 int s[N], f[M];
 int sg(int x)
 {
     if(f[x] != -1) return f[x];
     //用一个哈希表来存当前他可以到达的局面
     unordered_set<int> S;
     //使用深度优先来进行搜索
     for(int i = 0; i < m; i ++)
     {
         //从集合中取出所有可以移动的石子个数,进行遍历
         int sum = s[i];
         //将下一个状态的sg函数加入到S中
         if(x >= sum) S.insert(sg(x - sum));
         
     }
     //退出for循环后我们就得到了当前的状态x可以到达的所有的其他的状态的sg函数
     //从小到达来枚举所有的自然数,来求得当前状态的sg函数
     for(int i = 0; ; i ++)
     {
         //如果这个自然数不在后面的状态中,就得到了当前状态的sg函数,并返回给上一层调用的地方
         if(!S.count(i)) return f[x] = i;//即是mex
     }
 }
 int main()
 {
     cin >> m;     //读入集合中m个数,每次取出只能从这些数中选
     for(int i = 0 ; i < m; i ++) cin >> s[i]; 
     cin >> n;//读入n表示有n堆石子
     int res = 0;
     memset(f, -1, sizeof f);
     for(int i = 0; i < n; i ++)
     { 
         int x;//输入每堆石子的个数
         cin >> x;
         //这里使用记忆化搜索
         //异或上每堆石子的初始SG函数的值
         res ^= sg(x);
     }
     if(res) puts("Yes");
     else puts("No");
 }
```

- Sprague - Grundy 定理 : 当且仅当 $\operatorname{SG}(s_1)\oplus\operatorname{SG}(s_2)\oplus\ldots\oplus\operatorname{SG}(s_n)\neq0$ 时，这个游戏是先手必胜的。同时，这是一个组合游戏的游戏状态 x 的 sg 值。

递归查找 mex , 主要是找到 sg 函数在特定题目中的规律.

```c++
int mex(auto v) // v可以是vector、set等容器 ,是可以到达的状态的 sg 函数
{
    unordered_set<int> S;
    for (auto e : v)
        S.insert(e);
    for (int i = 0;; ++i)
        if (S.find(i) == S.end())
            return i;
}
```



## 欧拉函数

> - 定义 : 对于一个正整数 n,n 的欧拉函数 $\Phi(n),$表示小于等于 n 与 n 互质的正整数的个数.
> - 性质 1 : 如果 n 是质数,那么 $\Phi(n) = n - 1,$因为只有 n 本身与它不互质.
> - 性质 2 : 如果 p 和 q 是质数,那么 $\Phi(p * q) =  \Phi(p) *  \Phi(q) = (p - 1) * (q - 1).$
> - 性质 3 : 如果 p 是质数,那么$\Phi({\bf p}^{\bf k})={\bf p}^{\bf k}-{\bf p}^{\bf k-1}(p,2p,3p\cdots p^{k-1}p一共p^{k-1}个能够整除p^k的,所以答案为p^k-p^{k-1})$
> - 性质 4 :$对于任意正整数 n ,\Phi({\bf n})={\bf n}({\bf1}-\textstyle{\frac{1}{{\bf p}_{1}}})({\bf1}-\textstyle{\frac{1}{{\bf p}_{2}}})({\bf1}-\textstyle{\frac{1}{{\bf p}_{3}}}).....({\bf1}-\textstyle{\frac{1}{{\bf p}_{n}}})$
> - 其余性质 : 若\ a 为质数,b mod a = 0,则$\Phi(a * b) = \Phi(b) * a,若\ a\ 和\ b\ 互质,\Phi(a * b) = \Phi(a) * \Phi(b),若\ n\ 为一个正整数,那么\Sigma_{\mathrm{d|n}}=\mathbf{n},其中\ d\ |\ n\ 表示\ d\ 整除\ n$

```c++
for(int i=2;i<=n;i++)
{//prime[0]表示素数个数
    if(!vis[i])//没有被访问，也就是没有被筛掉,说明是素数 
    {
        vis[i] = 1;
        prime[++ prime[0]]=i;
        phi[i]=i-1;
    }
    for(int j=1;j<=prime[0]&&i*prime[j]<=n;j++)
    {
        vis[i * prime[j]]=true;
        if(i % prime[j] == 0)//a % b == 0，那么phi[a * b] = b * phi[a] 
        {
            phi[i * prime[j]] = phi[i] * prime[j];
            break;
        }
        else phi[i * prime[j]] = phi[i] * (prime[j] - 1);//两者互素,素数的欧拉函数是 n-1
    }
}
```

## 快速幂

```c++
int qmi(int a,int k,int p)
{
	int res=1;
	while(k)
	{
		if(k&1) res = (ll)res * a % p;// k & 1 -> 将k换为二进制且求最后一位0或1
		k >>= 1;//右移运算符，二进制删除最后一位
		a = (ll)a * a % p;//实现平方操作
	}
	return res;
}
//如果数据范围比较大,可能会爆longlong,此时需要优化或者使用__int128
ll qmul(ll a,ll k,ll b) //或者使用 __int128
{
	ll res = 0;
	while(k)
	{
		if(k & 1) res = (res + a) % b;
		a = (a + a) % b;
		k >>= 1;
	}
	return res ;
} 
ll qmi(ll a,ll k,ll b)//这题 L 的范围太大，可能会爆 ll ,len(L) = 9,如果数十分大，可能爆 18 ll
{
	ll res = 1;
	while(k)
	{
		if(k & 1)
			res = qmul(res,a,b);
		a = qmul(a,a,b);
		k >>= 1;		
	}
	return res;
}
```

## 逆元

> - 逆元就是在 mod 的意义下,不能直接除以一个数,而是要乘以它的逆元,比如 $a * b\equiv1(mod\  p),那么a,b互为模\ n\ 意义下的逆元,例如要计算\ x / a\ 就可以改成\ x\ *\ b\ \%\ p  $

- 扩展欧几里得求逆元 

  > $a*b\equiv 1(mod\ p),变形为a*b+k*p=1,用扩展欧几里得求出b,同时也说明只有a和p互素的情况下才有逆元$

```c++
LL exgcd(LL a,LL b,LL &x,LL &y)//扩展欧几里得算法 ,求出来的结果是方程变换后右值等于 gcd 的系数 x y
{
	if(b == 0)
	{
		x = 1,y = 0;
		return a;
	}
	LL ret = exgcd(b,a%b,y,x);
	y -= a / b * x;
	return ret;
}
LL getInv(int a,int mod)//求a在mod下的逆元，不存在逆元返回-1 
{
	LL x,y;
	LL d = exgcd(a,mod,x,y);//这个计算出来的结果是方程式恰好等于gcd(a,mod),所以最终的结果gcd != 1,代表无逆元
	return d == 1 ? (x % mod + mod) % mod : -1;//求符合条件的最小正整数逆元解
}
```

- 费马小定理 / 欧拉定理

  > $费马小定理:若\ p\ 为素数,则有a^{p-1}\equiv1(mod\ p),a^{p-2}*a\equiv1(mod\ p),即a^{p-2}就是\ a\ 在mod\ p\ 意义下的逆元 $
  >
  > $欧拉定理:若a和p互素,则有a^{\varphi(p)}\equiv1(mod\ p)(费马小定理的一般形式),易得a^{\varphi(p)-1}就是a\ 在mod\ p\ 意义下的逆元$

```c++
LL qkpow(LL a,LL p,LL mod)
{
	LL t = 1,tt = a % mod;
	while(p)
	{
		if(p & 1)t = t * tt % mod;
		tt = tt * tt % mod;
		p >>= 1;
	}
	return t;
}
LL getInv(LL a,LL mod)
{
	return qkpow(a,mod-2,mod);
}

```

- 递推求逆元 - 模数必须是素数才能使用,主要用在模数是不大的素数且需要多次求解不同的逆元,例如卢卡斯定理

> `p` 是模数,`i`是待求的逆元,我们求得是$i^{-1},在mod\ p意义下的逆元,设t=M/i,k=M\%i,则有t*i+k\equiv 
>  0(mod\ M),-t*i$$ \equiv k(mod\ M),对上式同除i*k,-t*inv[k]\equiv inv[i](mod\ M),即是inv[i]=(M-M/i)*inv[M\%i]\%M;求得是正数,所以$$前面有额外加上的M,inv[1] = 1$

```c++
LL inv[mod+5];
void getInv(LL mod)
{
	inv[1] = 1;//初始化
	for(int i=2;i<mod;i++)
		inv[i] = (mod - mod / i) * inv[mod % i] % mod;//加上的mod保证求出来的逆元都是最小正整数.
}
LL inv(LL i)
{
	if(i == 1) return 1;
	return (mod - mod / i) * inv(mod % i) % mod;
}

```

## 扩展欧几里得定理

```c++
LL exgcd(LL a,LL b,LL &x,LL &y)//扩展欧几里得算法 ,求出来的结果是方程变换后右值等于 gcd 的系数 x y
{
	if(b == 0)
	{
		x = 1,y = 0;
		return a;
	}
	LL ret = exgcd(b,a%b,y,x);
	y -= a / b * x;
	return ret;
}//ax + by = m,当且仅当 m % d == 0的时候有整数解
```

部分题可能有负数,模数和结果都可能是负数,如果要求的是最小正整数解,模数一定要取绝对值  ( x % abs(mod) + abs(mod) ) % abs(mod)

### 扩展中国剩余定理

```c++
//差距在于大范围，会爆 long long ,所以使用__int128接受一下结果计算后再转换为long long,__int128没有输入和输出的函数
#include <bits/stdc++.h>
using namespace std;
typedef __int128 ll;
const int N = 1e5 + 10;
ll x, y, d; int n;

void exgcd(ll &x, ll &y, ll a, ll b) 
{
    if(!b) d = a, x = 1, y = 0;
    else exgcd(y, x, b, a % b), y -= a / b * x;
}

ll lcm(ll a, ll b) 
{
    return a / __gcd(a, b) * b;
}
__int128 A1, b, A2, B;

void merge() 
{
    exgcd(x, y, A1, A2);
    ll c = B - b;
    if(c % d) puts("-1"), exit(0);
    x = x * c / d % (A2 / d);//x * c / d是代表当前值的解，通解是x = x0 + a2 / d,所以最小解就是 % 
    if(x < 0) x += A2 / d;
    ll mod = lcm(A1, A2);//下一步需要将A1变为A1和A2的最小公倍数
    b = (A1 * x + b) % mod; // x = a1*x+b + k*[a1,a2]，[]是最小公倍数 -> x = x0 + ka
    if(b < 0) b += mod;
    A1 = mod;//a1 a2的最小公倍数      //上式 x0即是更新后的m ，a即是更新后的a1
    
}

int main() {
    cin >> n;
    for(int i = 1 ; i <= n ; ++ i) 
    {
        long long _A, _B;
        cin>>_A>>_B, A2 = _A, B = _B;
        if(i > 1) merge();
        else A1 = A2, b = B;
    }
    printf("%lld\n", (long long)(b % A1));
	return 0;
}
//// x mod ai = mi, x = k1 * a1 + m1 , 通解 : k1 = k1 + k * a2 / d ,
/*表达整数的奇怪方式 X 同余与 mi (mod ai) 求最小X*/  
//  x = k1*a1+m1        k = k1 + k * a2 / d 通解，代入  
#include <iostream>  
using namespace std;  
#define ll long long  
  
ll exgcd(ll a,ll b,ll &x,ll&y)//x y加引用是因为后续要传回主函数中  
{  
    if(!b)  
    {  
        x = 1,y = 0;  
        return a;  
    }  
    ll d = exgcd(b,a % b,y,x);  
    y -= a / b * x;  
    return d;  
}  
  
int main()  
{  
    int n;  
    cin >> n;  
    bool has_answer = true;//判断是否有解  
    ll a1,m1;  
    cin >> a1 >> m1;  
    for(int i=0;i<n-1;i++)  
    {//不断求解 k1a1 - kiai = mi - m1的系数k1  ki  
        ll a2,m2;  
        cin >> a2 >> m2;  
        ll k1,k2;  
        ll d = exgcd(a1,a2,k1,k2);//exgcd求系数  
        if((m2 - m1) % d)//判断是否有解，斐蜀定理-有解一定是最大公约数的倍数  
        {//取余结果不是0证明不是倍数，无解             
            has_answer = false;  
            break;  
        }  
        k1 *= (m2 - m1) / d;//此时求得解是为d是对应的解  
        ll t = a2 / d;    //保证k变得足够小，最小正整数解 k = k1 + a2 / d  通解  
        k1 =  (k1 % t + t) % t;//将k1变为最小的正整数解，防止溢出，所以变成最小的就是 % 一下  
        m1 = a1 * k1 + m1;   // x = a1*k1+m1 + k*[a1,a2]，[]是最小公倍数 -> x = x0 + ka  
        a1 = abs(a1 / d * a2);//a1 a2的最小公倍数      //上式 x0即是更新后的m ，a即是更新后的a1  
      }//防止负数的干扰，输出符合条件的最小的正整数解  
    //最终化为一个式子 x = ka1 + m1 解即是 m1 % a1 + a1 ) % a1     
    if(has_answer) cout << (m1 % a1 + a1) % a1 << endl;  
    else puts("-1"); 
}
```





## 矩阵乘法

### 求斐波那契数列的前 n 列和(大数据 n)

$ A = \begin{bmatrix}
 0 & 1 & 0\\
 1 & 1 & 1\\
 0 & 0 & 1
\end{bmatrix} ,\begin{pmatrix}
 f[n+1] & f[n+2] & s[n+1]
\end{pmatrix}=\begin{pmatrix}
 f[n] & f[n+1] & s[n]
\end{pmatrix}*\begin{bmatrix}
 0 & 1 & 0\\
 1 & 1 & 1\\
 0 & 0 & 1
\end{bmatrix},结合递推可知可以通过第 n 项矩阵来求解$$第 n + 1项矩阵,且由于矩阵没有交换律但是有结合律,所以直接先计算出A^{n},然后和初始矩阵相乘即可快速得到结果,矩阵乘法加快速幂.$$直接初始化f1即可求得fn$

```c++
const int N = 3;
int n,m;
void mul(int c[],int a[],int b[][N])
{
	int temp[N] = {0};
	for(int i=0;i<N;i++)
	{
		for(int j=0;j<N;j++)
		{
			temp[i] = (temp[i] + (ll)a[j] * b[j][i]) % m;
		}
	}
	memcpy(c,temp,sizeof temp);//尺寸只能用全局变量或者矩阵变量，传 c 的话是指针长度不是数组长度
}
void mul(int c[][N],int a[][N],int b[][N])//矩阵乘法  二维乘二维
{
	int temp[N][N] = {0};
	for(int i=0;i<N;i++)
	{
		for(int j=0;j<N;j++)
		{
			for(int k=0;k<N;k++)
			{
				temp[i][j] = (temp[i][j] + (ll)a[i][k] * b[k][j]) % m;
			}
		}
	}
	memcpy(c,temp,sizeof temp);
}
int main()
{
	_;
	cin >> n >> m;
	int f1[3] = {1,1,1};//f1 = 1,f2 = 1 斐波那契,第 n 项数据,第 n + 1 项数组,前 n 项和.
	int a[N][N] = 
	{
		{0,1,0},
		{1,1,1},
		{0,0,1}
	};// A ,即是快速幂的二维数组.
	n --;//求得是 n - 1 次方,因为第一项便是初始值,只需要 n-1 幂指数即可
	while(n)
	{
		if(n & 1) mul(f1,f1,a);// res = res * a (a 是矩阵),理解是从后面乘的,不满足交换律,但满足结合律所以能直接结合后面两个
		mul(a,a,a);// a = a * a;两个函数的第一维参数都是为了将改变的值复制过来,因为是局部变量,全局变量不必考虑
		n >>= 1;
	}
	cout << f1[2] << endl; //注意区分,初始化的时候f[2]是前 n 项和,f[1]是第 n+1 斐波那契数列,f[0]是第 n 项斐波那契.
}// 5  1000   --->  12
```



# DP

## 简介

## 基础类DP

## 记忆化搜索

## 背包DP

### 01 背包

- 主要是倒序计算体积

```c++
for(int i=1;i<=n;i++) cin >> v[i] >> w[i];//  n 是物品个数,m 是体积
for(int i=1;i<=n;i++)
    for(int j=m;j>=v[i];j--)//倒序，保证此时已经是选了i-1个物体，防止误更新问题出现
        f1[j] = max(f1[j],f1[j-v[i]] + w[i]);
cout << f1[m] << endl;
```

### 完全背包 

-  每个物品有无数个,这个时候需要正序循环

```c++
for(int i=1;i<=n;i++) cin >> v[i] >> w[i]; // n,m 的意义同上
for(int i=1;i<=n;i++)
    for(int j=v[i];j<=m;j++)//正序，保证物品无限个，后面的数据及时更新
        f2[j] = max(f2[j],f2[j-v[i]] + w[i]);
cout << f2[m] << endl;
```

### 多重背包

- 每个物品有 x 个,如何选择使得总价值最大--- 二进制优化后由于二进制可以组成任意数,转换为 01 背包,一次选一二进制组.

```c++
cin >> n >> m;
int cnt =0;
for(int i=1;i<=n;i++)
{
    int a,b,s;
    cin >> a >> b >> s;
    int k = 1;
    while(k<=s)
    {
        cnt ++,v[cnt] = a * k,w[cnt] = b * k,s -= k,k *= 2; 
    }
    if(s>0)//C
    {
        cnt ++,v[cnt] = a * s,w[cnt] = b * s;
    }
}
n = cnt;//打包转换为01背包
for(int i=1;i<=n;i++)
    for(int j = m;j>=v[i];j--)
        f3[j] = max(f3[j],f3[j-v[i]]+w[i]);
cout << f3[m] << endl;
```

### 分组背包问题

- 每个组内的物品最多选一个或者不选,求最大价值.

```c++
cin >> n >> m;
for(int i=1;i<=n;i++)
{
    cin >> s[i];
    for(int j=0;j<s[i];j++)
        cin >> v[i][j] >> w[i][j];//具体输入根据题目要求,价值和体积可能分开输入可能一起输入
}
for(int i=1;i<=n;i++)
    for(int j=m;j>=0;j--)//倒序,代表每组只有选或不选,保证当前数据是 i-1 的时候的数据
        for(int k=0;k<s[i];k++)//遍历组内的物品,每次都是在 i-1 的基础上选或不选
            if(v[i][k] <= j)//只有这个物品能放进去才有更新的意义
                f[j] = max(f[j],f[j - v[i][k]] + w[i][k]);
cout << f[m] << endl;
```

## 区间DP

## 树形DP

### 基础

- [没有上司的舞会](https://www.luogu.com.cn/problem/P1352)  一个人去他的上司不能去,求最大开心指数

```c++
void dfs(int u)
{
    dp[u][1] = happy[u];
    for(int i=h[u];~i;i=ne[i])
    {
        int j = e[i];
        dfs(j);
        dp[u][1] += dp[j][0];
        dp[u][0] += max(dp[j][1],dp[j][0]); 
        
    }
}//注意建边的顺序,add模板的建边是后传入是先传入的儿子
cout << max(dp[root][1],dp[root][0]) << endl;//root是入度为 0 的点
```

- [HDU 2196 Computer](https://vjudge.net/problem/HDU-2196)      [POI FAR-FarmCraft](https://www.luogu.com.cn/problem/P3574)  

### 树上背包 

- 树上背包就是背包问题和树形DP的结合,例如 [落谷CTSC1997选课](https://www.luogu.com.cn/problem/P2014) ,有 n 门课程,ai 为对应的学分,每门课有 0 或 1门先修课,有先修课需要先学完先修课,m 门最多的学分   ---->  转换为 01 背包

```c++
int dp[N][N],w[N],n,m;//f[][][]表示遍历u号点的前i棵树,选了j门课的最大学分,倒序优化一维
vector<int> a[N];
int dfs(int u)
{
    int p = 1;
    dp[u][1] = w[u];
    for(auto L : a[u])//遍历子vector,物品组
    {
        int si = dfs(L); // 注意下面两重循环的上界和下界
        for(int i=min(p,m+1);i;i--)//体积组倒序
        {// 只考虑已经合并过的子树，以及选的课程数超过 m+1 的状态没有意义
            for(int j=1;j<=si&&i+j<=m+1;j++)//体积组,选了多少门
                dp[u][i + j] = max(dp[u][i + j],dp[u][i] + dp[L][j]);
        }
        p += si;
    }
    return p;
}
dfs(0);
cout << dp[0][m + 1] << endl; //m + 1 是因为加入了 0 号课程,学分是 0 
```

- [JSOI2018 潜入行动](https://loj.ac/p/2546)  [SDOI2017苹果树](https://loj.ac/p/2268)   [cf Mex Tree]([Problem - D - Codeforces](https://codeforces.com/contest/1830/problem/D))

### 换根DP

- 换根DP是一种用来求树上各点道其他点的距离之和的算法,通常不会指定根节点,并且根节点的变化会对一些值,例如子节点深度和,点权和等产生影响,通常需要两遍DFS,第一次DFS预处理诸如深度,点权和之类的信息,第二次DFS开始进行换根DFS

  > - 用于求出树上所有点为根节点时到其他点的距离之和.
  > - 假设 a为根节点，b为直系子节点，那么对于 b 所在子树对 a 的贡献为 dis[b] + size[b] ,其中 dis[b] 为 **b** 到以 **b** 为根节点的子树中所有点的距离之和。size[b]为以 **b** 为根节点的子树中的点的个数，其实很好理解，就相当于以 b为根节点中的所有路径长度全部 +1，然后就到达了 a 节点。

  ```c++
  int dis[N];//第一次dfs每个节点到其子节点距离之和 
  int size[N];//每个节点下子节点个数（包括这个节点本身） 
  int dp[N];//最终结果  
  bool vis[N];
  vector<int> vec[N];
  void dfs(int x)
  {
  	vis[x] = true;
  	int sum = 0;
  	for(int i=0;i<vec[x].size();i++)
      {
  		int y = vec[x][i];
  		if(!vis[y])
          {
  			dfs(y);
  			sum += size[y];
  			dis[x] += dis[y]+size[y];
  		}
  	}
  	size[x] = sum+1;
  	return ;
  }
  ```

  > - 换根dp,换根DP的思想就是**把所有与根相连的节点通过一定的操作将其变为根**,依然利用上述节点 a,b,将根节点从  a 换到 b ,dp[b] = dp[a] - size[b] + (n - size[b]),−size[b] 表示从 b 引申出来的 size[b] 条路径长度全部 −1,n−size[b] 表示从 a 引申出来的不包含 b 的其他路径长度全部 +1

  ```c++
  memset(vis,0,sizeof(vis));//dfs时使用过一次，因此需要清空
  dp[a]=dis[a];//此处a指代上面自己找的dfs起点，一般情况下a=1
  void Dp(int x)
  {
  	vis[x] = true;
  	for(int i=0;i<vec[x].size();i++)
      {
  		int y = vec[x][i];
  		if(!vis[y])
          {
  			dp[y] = dp[x] - size[y] + n - size[y];
  			Dp(y);
  		}
  	}
  }
  ```

  - [例题](https://www.luogu.com.cn/problem/P3478),             给出一个 n 个点的树,请求出一个节点使得以这个点为根节点时,所有节点的深度之和最大.         [牛客 : Tree](https://ac.nowcoder.com/acm/contest/4479/C)      求深度之和最小       [树?数!](https://ac.nowcoder.com/acm/contest/64600/E)







# 字符串

## KMP

```c++
//求next过程,这个板子中将字符串下标转换为 从 1 开始,注意
for(int i=2,j=0;i<=n;i++)
{      //需要特别注意i是从1开始计数的，j是从0开始
    while(j && p[i] != p[j+1]) j = ne[j];//未到起始，且匹配失败，回答前缀的结尾，快速匹配
    if(p[i] == p[j+1]) j++;//匹配成功，前缀结尾终点后移
    ne[i] = j;//更新后缀对应的前缀结尾
}
//KMP匹配过程
for(int i=1,j=0;i<=m;i++)// i 遍历s所有数组
{
    while(j && s[i] != p[j+1]) j = ne[j];// j没有退回起点且匹配失败， 下标从 1 开始，0相当于在空着，起来一个判断作业
    if(s[i] == p[j+1]) j++;
    if(j == n)
    {
        //匹配成功
        cout << i-j << " ";//i从1开始i-n即是开始位置
        j = ne[j];//匹配结束，更新最大后缀对应的前缀结尾，快速匹配前一部分字符
    }
}
//KMP 判断字符串是否是由子字符串重复循环构成
if(len%(len-next[len])==0)//len表示字符串 s 的长度,就是字符串第一位到next[len]位与len-next[len]位到第len位是匹配的
	printf("%d\n",len/(len-next[len]));//判断,len-next[len] 即是重复子字符串的长度
```

## 字符串哈希 Hash

> - 自动溢出 ull

```c++
p[0] = 1; // 不要将某个字符串映射为0,所以p[0]初始化为 1,映射的最小值为 1
cin >> n >> m >> str + 1;//n 是字符长度,m 是询问个数,str 是输入字符串,char str[N];
for(int i=1;i<=n;i++)//n 为字符长度
{
    p[i] = p[i-1]*P;// P = 131 或 13331,p,h 数组都是 ull 的数据类型
    h[i] = h[i-1]*P+str[i] - 'A' + 1;//str 是读入的字符串,下标从 1 开始,将字符转换为 1 - 26,减小范围
}
//获取哈希结果
ull get(int l,int r)
{   //公式，相当于将1到l-1这串字符，前移到长度与r对齐，不足补-，这样相减就是l到r的区间哈希
	return h[r] - h[l-1]*p[r-l+1];
}
```

> - 二维哈希 : 取两个 base,
> - 主要用于统计 b矩阵在 a矩阵中的出现次数

```c++
const ULL base1 = 13331;
const ULL base2 = 23333;
ULL p1[N],p2[N];
void pre()
{
    p1[0] = p2[0] = 1;//与一维hash一样,不要将字符串映射为 0,p1,p2[0] = 1;
    for(int i=1;i<N;i++)
        p1[i] = p1[i-1] * base1,//记录乘了多少次base
        p2[i] = p2[i-1] * base2;
}
int T,n1,m1,n2,m2;
char a[N][N],b[N][N];
ULL h[N][N];//h[i][j]表示(1,1,i,j)子矩阵的哈希值,类似与前缀和求子矩阵的和
void init()
{//二维哈希的初始化类似与二维前缀和s[i][j] = s[i][j-1] + s[i-1][j] - s[i-1][j-1] + a[i][j],二维哈希乘于base
    for(int i=1;i<=n1;i++)
        for(int j=1;j<=m1;j++)
            h[i][j] = h[i][j-1] * base2 + h[i-1][j] * base1
                - h[i-1][j-1] * base1 * base2 + (a[i][j] - 'A' + 1);
}//减去的值乘两个base是因为加的时候重叠了,两个base都乘了,所以减去的时候要都乘
ULL get_hash(int x,int y,int lx,int ly)
{//计算子矩阵和,联系一维哈希和二维前缀和的计算,
    return  +h[x+lx-1][y+ly-1]//右下角
            -h[x+lx-1][y-1]*p2[ly]
            -h[x-1][y+ly-1]*p1[lx]//p数组存的是计算前缀和时,乘了多少次base
            +h[x-1][y-1]*p1[lx]*p2[ly];//左上角
}
int main()
{
    pre();
    cin >> T;
    while(T--)
    {
        cin >> n1 >> m1;
        for(int i=1;i<=n1;i++) cin >> a[i] + 1;
        cin >> n2 >> m2;
        for(int i=1;i<=n2;i++) cin >> b[i] + 1;
        init();
        ULL t = 0;
        for(int i=1;i<=n2;i++)
            for(int j=1;j<=m2;j++)
                t += (b[i][j]-'A'+1) * p1[n2-i] * p2[m2-j];//注意下标
        int ans=0;
        for(int i=1;i<=n1-n2+1;i++)
            for(int j=1;j<=m1-m2+1;j++)
                if(get_hash(i,j,n2,m2)==t)//i,j表示起点,求的是大小为n,m的子矩阵,判断是否出现过,所以get里面右下角
                    ans++;       //是[x+la-1][y+ly-1],后面的过程类似于二维矩阵求子矩阵和
        cout << ans << endl;
    }
    return 0;
 
 //如果是一个大的子矩阵求的是若干个小矩阵在这个大矩阵里面有没有出现过,可以  使用二分优化(前提是所有子矩阵的大小一致)
    init();//大矩阵
    for (int i = 1; i <= n - a + 1; i++)
         for (int j = 1; j <= m - b + 1; j++)
              ans[++ct] = get_hash(i, j, a, b);//预处理出来所有的大小为 a * b 的矩阵
     while(q --)
     {
         cin >> str + 1;//处理询问小矩阵
         for(int i=1;i<=a;i++)
         {
             for(int j=1;j<=b;j++)
             {
                 h[i][j] = (ll) h[i][j-1] * base2 + h[i-1][j] * base1
                - h[i-1][j-1] * base1 * base2 + (str[i][j] - 'A' + 1);
             }
         }//binary_search  STL二分,返回值只有0 或 1,不能返回下标,返回类型是bool的
         puts(std::binary_search(ans + 1, ans + ct + 1, h[a][b]) ? "1" : "0");
     }
}
```

- 第二种方法 : 二维哈希先求出每一行的子串,在将行看做点求矩阵哈希

```c++
for (int i = 1; i <= n; ++i)
    for (int j = 1; j <= m; ++j)
    {
        scanf("%d", &x);
        h[i][j] = ((ull)h[i][j - 1] * 131 + x);
    }
//第一步获得每行子串的哈希表
for (int i = 1; i <= n; ++i)
    for (int j = 1; j <= m; ++j)
        h[i][j] = (h[i][j] + (ull)h[i - 1][j] * 233);
//第二步获得子矩阵的哈希值
```

> - 双哈希,一般不使用自动溢出即是 ull 类型,会被卡,手动取模,双哈希本质上就是两遍一维哈希,不再使用自动溢出

```c++
const ULL MOD1 = 402653189;
const ULL MOD2 = 805306457;
const ULL BASE1 = 13331;
const ULL BASE2 = 23333;
//备用 模数&base
//const ULL MOD1 = 1610612741;
//const ULL MOD2 = 1222827239;
//const ULL BASE1 = 1331;
//const ULL BASE2 = 131;
ULL p[N][2];
void init()
{
    p[0][0] = p[0][1] = 1;//字符串不映射到 0 
    for(int i=1;i<N;i++) p[i][0] = p[i-1][0] * BASE1 % MOD1;
    for(int i=1;i<N;i++) p[i][1] = p[i-1][1] * BASE2 % MOD2;
}//如果字符串长度确定,for 循环不必循环到N,循环到字符串的长度即可
struct myHash
{
    ULL h[N][2];
    void init(char s[],int n = 0)
    {555
        n = strlen(s),h[n][0] = h[n][1] = 0;
        for(int i=1;i<=n;i++)//两遍hash
            h[i][0] = (h[i-1][0] * BASE1 % MOD1 + s[i] - 'A' + 1) % MOD1;
        for(int i=1;i<=n;i++) 
            h[i][1] = (h[i-1][1] * BASE2 % MOD2 + s[i] - 'A' + 1) % MOD2;
    }
    upr get_hash(int pos,int length)//r=pos,l = l + length +1,
    {//返回哈希的结果,和一维哈希计算结果一样,只是两种哈希模数和基数
        return {
            ((h[pos][0] - h[pos-length][0] * p[length][0] % MOD1) + MOD1) % MOD1,
            ((h[pos][1] - h[pos-length][1] * p[lengt h][1] % MOD2) + MOD2) % MOD2
        };
    }
};
upr make_hash(char s[])
{
    int n = strlen(s);
    upr ans;
    for(int i=n-1;i>=0;i--)
    {
        ans.fi = (ans.fi * BASE1 % MOD1 + s[i] - 'A' + 1) % MOD1;
        ans.se = (ans.se * BASE2 % MOD2 + s[i] - 'A' + 1) % MOD2;
    }
    return ans;
}
```

## manacher算法求最大回文串

- 直接暴力复杂度较高,从中间向两边扩展不能确定对称中心是在字符上还是字符中间,所以采用每两个字符加入一个字符例如' #',将所有的字符对称中心转换到符号 '#' 上,结尾 0 和开头使用不同的字符防止匹配上越界

```c++
string str,s;
int dp[N];
void solve()
{
    cin >> str;
    int n = str.size();
    s += ' ';//防止越界
    for(int i=0;i<n;i++)
    {
        s += '#';//没两个字符中间加上一个辅助字符,强制将对称中心转到字符上,而不是字符中间
        s += str[i];
    }
    s += '#';
    s += '!';//最后再加一个!,防止和开头匹配上导致越界
    int pos = 0,r = 0;//pos是中点,r是回文字符串的最右端点,pos是r对应的中点 
    n = s.size();
    for(int i=1;i<n;i++)//pos < i
    {
        if(i < r)//求最右端点,与左端点对称的点和,这段区间已经匹配上了
            dp[i] = min(dp[(pos<<1)-i],r - i);
        else dp[i] = 1;
        while(s[i-dp[i]] == s[i + dp[i]])//因为终点的字符是独特的,所以不用担心越界问题,结尾一定匹配不上
            dp[i] ++;//如果两边的字符能匹配上,dp数组可以匹配的长度加 1 
        if(i + dp[i] > r)//匹配的长度大于最大右端点了,更新右端点
            r = i + dp[i],pos = i;//更新右端点和中点
    }
    int ans = 0;
    for(int i=1;i<=n;i++)
        ans = max(ans,dp[i] - 1);//找最长回文
    cout << ans << endl;
}//每个答案减一是因为dp代表的含义是一边但是算上的有#,所以是最大回文,但是结束的时候一定是#,多计算了一个#
signed main()
{
    ios
    int t=1;
    // cin >> t;
    while(t--)
          solve();
    return 0;
}
```

## AC 自动机

```cpp
struct sam
{
	typedef struct node
	{
		int son[26], fail;
		int &operator[](int x) { return son[x]; }
	} node;

	node t[N];
	int tot, q[N], head, tail, d[N], ans[N], hd[N], nxt[N];//d数组是子串出现多少次，t数组是fail数组
	char s[N];
	char s1[N];
	void insert(char *s, int id)
	{
		int cur = 0, len = strlen(s);
		for (int i = 0; i < len; i++)
		{
			if (!t[cur][s[i] - 'a'])
				t[cur][s[i] - 'a'] = ++tot;
			cur = t[cur][s[i] - 'a'];
		}
		nxt[id] = hd[cur];
		hd[cur] = id;
	}

	void build()
	{
		head = 1;
		tail = 0;
		for (int i = 0; i < 26; i++)
			if (t[0][i])
				q[++tail] = t[0][i];
		while (head <= tail)
		{
			int x = q[head++];
			for (int i = 0; i < 26; i++)
			{
				if (t[x][i])
					t[t[x][i]].fail = t[t[x].fail][i], q[++tail] = t[x][i];
				else
					t[x][i] = t[t[x].fail][i];
			}
		}
	}
	void solve()
	{
		int cur = 0, len = strlen(s);
		for (int i = 0; i < len; i++)
		{
			cur = t[cur][s[i] - 'a'];
			d[cur]++;
		}
		for (int i = tot; i >= 1; i--)
		{
			for (int j = hd[q[i]]; j; j = nxt[j])
				ans[j] = d[q[i]];
			d[t[q[i]].fail] += d[q[i]];
		}
	}
} sa;
//先 insert  在 build 求fail数组，在solve求个数
//一边先和 哈希或者kmp 结合

#include <bits/stdc++.h>
using i64 = long long;

const int N = 1e5 + 10, mod = 998244353, INF = 1e18;
const int base = 1331, hashmod = 1610612741;

int n;
struct sam
{
	typedef struct node
	{
		int son[26], fail;
		int &operator[](int x) { return son[x]; }
	} node;

	node t[N];
	int tot, q[N], head, tail, d[N], ans[N], hd[N], nxt[N];//d数组是子串出现多少次，t数组是fail数组
	char s[N];
	char s1[N];
	void insert(char *s, int id)
	{
		int cur = 0, len = strlen(s);
		for (int i = 0; i < len; i++)
		{
			if (!t[cur][s[i] - 'a'])
				t[cur][s[i] - 'a'] = ++tot;
			cur = t[cur][s[i] - 'a'];
		}
		nxt[id] = hd[cur];
		hd[cur] = id;
	}

	void build()
	{
		head = 1;
		tail = 0;
		for (int i = 0; i < 26; i++)
			if (t[0][i])
				q[++tail] = t[0][i];
		while (head <= tail)
		{
			int x = q[head++];
			for (int i = 0; i < 26; i++)
			{
				if (t[x][i])
					t[t[x][i]].fail = t[t[x].fail][i], q[++tail] = t[x][i];
				else
					t[x][i] = t[t[x].fail][i];
			}
		}
	}
	void solve()
	{
		int cur = 0, len = strlen(s);
		for (int i = 0; i < len; i++)
		{
			cur = t[cur][s[i] - 'a'];
			d[cur]++;
		}
		for (int i = tot; i >= 1; i--)
		{
			for (int j = hd[q[i]]; j; j = nxt[j])
				ans[j] = d[q[i]];
			d[t[q[i]].fail] += d[q[i]];
		}
	}
} sa[12];
char b[N];
bool f1[N];
i64 ha[N];
i64 p[N];
i64 get(int l, int r)
{
	return ((ha[r] - ha[l - 1] * p[r - l + 1] % hashmod) % hashmod + hashmod) % hashmod;
}
int now = 0;

void solve()
{
	std::cin >> n;
	// now = 0;
	now++;
	std::cin >> sa[now].s;
	std::string c;
	std::cin >> c;
	int m = c.length();
	c = " " + c;
	i64 h = 0;
	for (int i = 1; i <= m; i++)
	{
		h = h * base % hashmod + c[i];
		h %= hashmod;
	}
	for (int i = 1; i <= n; i++)
	{
		std::cin >> sa[now].s1;
		sa[now].insert(sa[now].s1, i);
		std::string st;
		std::cin >> st;
		int len = st.length();
		st = " " + st;
		bool flag = 0;
		ha[0] = 0;
		for (int j = 1; j <= len; j++)
		{
			ha[j] = ha[j - 1] * base % hashmod + st[j];
			ha[j] %= hashmod;
			if (j >= m)
			{
				if (get(j - m + 1, j) == h)
				{
					flag = 1;
					break;
				}
			}
		}
		f1[i] = flag;
	}
	sa[now].build();
	sa[now].solve();
	for (int i = 1; i <= n; i++)
	{
		if (sa[now].ans[i] && f1[i])
		{
			std::cout << i << " ";
		}
	}
	std::cout << '\n';
}
signed main()
{
	std::ios::sync_with_stdio(0);
	std::cin.tie(0);
	std::cout.tie(0);
	int t = 1;
	std::cin >> t;
	p[0] = 1;
	for (int i = 1; i < N; i++)
		p[i] = p[i - 1] * base % hashmod;
	while (t--)
	{
		solve();
	}
}
```



# jiangly板子

## 自动取模机

```c++
#define i64 long long 
template<class T>
constexpr T power(T a, i64 b) {
    T res = 1;
    for (; b; b /= 2, a *= a) {
        if (b % 2) {
            res *= a;
        }
    }
    return res;
}
constexpr i64 mul(i64 a, i64 b, i64 p) {
    i64 res = a * b - (i64)(1.L * a * b / p) * p;
    res %= p;
    if (res < 0) {
        res += p;
    }
    return res;
}
template<i64 P>
struct MLong {//longlong 自动取模机
    i64 x;
    constexpr MLong() : x{} {}
    constexpr MLong(i64 x) : x{norm(x % getMod())} {}
    
    static i64 Mod;
    constexpr static i64 getMod() {
        if (P > 0) {
            return P;
        } else {
            return Mod;
        }
    }
    constexpr static void setMod(i64 Mod_) {
        Mod = Mod_;
    }
    constexpr i64 norm(i64 x) const {
        if (x < 0) {
            x += getMod();
        }
        if (x >= getMod()) {
            x -= getMod();
        }
        return x;
    }
    constexpr i64 val() const {
        return x;
    }
    explicit constexpr operator i64() const {
        return x;
    }
    constexpr MLong operator-() const {
        MLong res;
        res.x = norm(getMod() - x);
        return res;
    }
    constexpr MLong inv() const {
        assert(x != 0);
        return power(*this, getMod() - 2);
    }
    constexpr MLong &operator*=(MLong rhs) & {
        x = mul(x, rhs.x, getMod());
        return *this;
    }
    constexpr MLong &operator+=(MLong rhs) & {
        x = norm(x + rhs.x);
        return *this;
    }
    constexpr MLong &operator-=(MLong rhs) & {
        x = norm(x - rhs.x);
        return *this;
    }
    constexpr MLong &operator/=(MLong rhs) & {
        return *this *= rhs.inv();
    }
    friend constexpr MLong operator*(MLong lhs, MLong rhs) {
        MLong res = lhs;
        res *= rhs;
        return res;
    }
    friend constexpr MLong operator+(MLong lhs, MLong rhs) {
        MLong res = lhs;
        res += rhs;
        return res;
    }
    friend constexpr MLong operator-(MLong lhs, MLong rhs) {
        MLong res = lhs;
        res -= rhs;
        return res;
    }
    friend constexpr MLong operator/(MLong lhs, MLong rhs) {
        MLong res = lhs;
        res /= rhs;
        return res;
    }
    friend constexpr std::istream &operator>>(std::istream &is, MLong &a) {
        i64 v;
        is >> v;
        a = MLong(v);
        return is;
    }
    friend constexpr std::ostream &operator<<(std::ostream &os, const MLong &a) {
        return os << a.val();
    }
    friend constexpr bool operator==(MLong lhs, MLong rhs) {
        return lhs.val() == rhs.val();
    }
    friend constexpr bool operator!=(MLong lhs, MLong rhs) {
        return lhs.val() != rhs.val();
    }
};
template<>
i64 MLong<0LL>::Mod = (i64)(1E18) + 9;
template<int P>
struct MInt {//int 自动取模机
    int x;
    constexpr MInt() : x{} {}
    constexpr MInt(i64 x) : x{norm(x % getMod())} {}
    
    static int Mod;
    constexpr static int getMod() {
        if (P > 0) {
            return P;
        } else {
            return Mod;
        }
    }
    constexpr static void setMod(int Mod_) {
        Mod = Mod_;
    }
    constexpr int norm(int x) const {
        if (x < 0) {
            x += getMod();
        }
        if (x >= getMod()) {
            x -= getMod();
        }
        return x;
    }
    constexpr int val() const {
        return x;
    }
    explicit constexpr operator int() const {
        return x;
    }
    constexpr MInt operator-() const {
        MInt res;
        res.x = norm(getMod() - x);
        return res;
    }
    constexpr MInt inv() const {
        assert(x != 0);
        return power(*this, getMod() - 2);
    }
    constexpr MInt &operator*=(MInt rhs) & {
        x = 1LL * x * rhs.x % getMod();
        return *this;
    }
    constexpr MInt &operator+=(MInt rhs) & {
        x = norm(x + rhs.x);
        return *this;
    }
    constexpr MInt &operator-=(MInt rhs) & {
        x = norm(x - rhs.x);
        return *this;
    }
    constexpr MInt &operator/=(MInt rhs) & {
        return *this *= rhs.inv();
    }
    friend constexpr MInt operator*(MInt lhs, MInt rhs) {
        MInt res = lhs;
        res *= rhs;
        return res;
    }
    friend constexpr MInt operator+(MInt lhs, MInt rhs) {
        MInt res = lhs;
        res += rhs;
        return res;
    }
    friend constexpr MInt operator-(MInt lhs, MInt rhs) {
        MInt res = lhs;
        res -= rhs;
        return res;
    }
    friend constexpr MInt operator/(MInt lhs, MInt rhs) {
        MInt res = lhs;
        res /= rhs;
        return res;
    }
    friend constexpr std::istream &operator>>(std::istream &is, MInt &a) {
        i64 v;
        is >> v;
        a = MInt(v);
        return is;
    }
    friend constexpr std::ostream &operator<<(std::ostream &os, const MInt &a) {
        return os << a.val();
    }
    friend constexpr bool operator==(MInt lhs, MInt rhs) {
        return lhs.val() == rhs.val();
    }
    friend constexpr bool operator!=(MInt lhs, MInt rhs) {
        return lhs.val() != rhs.val();
    }
};
template<>
int MInt<0>::Mod = 998244353;
template<int V, int P>
constexpr MInt<P> CInv = MInt<P>(V).inv();
constexpr int P = 998244353;//自动取模机的模数
using Z = MInt<P>;//自动取模机的重定义,使用过程中会自动取模
void solve() {
    string s;
    cin >> s;
    int n = s.size();
    Z ways = 1;//自动取模,不需要管模数的问题
    int ans = 0;
    for (int l = 0, r = 0; l < n; l = r) {
        while (r < n && s[l] == s[r]) {
            r ++;
        }
        ans += r - l - 1;
        ways *= r - l;//类变量 ways 自动取模
    }
    for (int i = 1; i <= ans; i++) 
        ways *= i;
}
```

