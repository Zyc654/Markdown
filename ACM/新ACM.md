此板子只有部分内容，原因是“ACM”中图片使用的是绝对路径无法正常转移使用

---

## 后缀数组（SA）    

- 学习掌握 SA
- 解决 LCP 问题（最长公共前缀）

约定 ： 字符串下标从 `1`开始，长度为`n`，“`后缀 i`”代指以第 `i`个字符开头的后缀，存储时用`i`代表字符串 s 的后缀 `s[i...n]`。

- 需要的数组介绍

```cpp
sa[i]//表示将所有后缀排序后第 i 小的后缀的编号，也就是后缀数组，编号数组
rk[i]//表示后缀 i 的排名，是重要的辅助数组，后文也称排名数组 rk   按照 LCP 的长度排序的
```
- 性质 ：

`sa[rk[i]] = rk[sa[i]] = i`
![后缀数组解释](...\images\后缀数组解释.png)

### 求后缀数组

#### 暴力做法    $O(n^2logn)$

直接暴力`sort`的做法，枚举每一种后缀数组。

#### 倍增做法     $O(nlog^2n)$

首先对字符串`s`的所有长度为 1 的子串进行排序，得到排序后的编号数组$sa_1 和排名数组 rk_1$。

倍增过程

​	1.用两个长度为 1 的子串的排名，即 $rk_1[i] 和 rk_1[i + 1] $ ，作为排序的第一第二关键字，就可以对字符串 ![s](data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7) 的每个长度为 2 的子串：{s[i...min(i + 1,n)] | $i\in[1,n] $}![\{s[i\dots \min(i+1, n)]\ |\ i \in [1,\ n]\}](data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7) 进行排序，得到 $sa_2 和 rk_2$；

​	2.用两个长度为 `w` 的子串的排名，即 $rk_1[i] 和 rk_1[i + w / 2] $ ，作为排序的第一第二关键字，就可以对字符串 ![s](data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7) 的每个长度为`w`的子串：{s[i...min(i + w - 1,n)] | $i\in[1,n] $}![\{s[i\dots \min(i+1, n)]\ |\ i \in [1,\ n]\}](data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7) 进行排序，得到 $sa_w 和 rk_w$；其中当`i + w > n时`,$kr_w[i + w]视为无穷小$；

   $rk_w[i]即是子串s[i...i+w-1]的排名，这样当 w\geqslant n时，得到的编号数组sa_w，就是需要的后缀数组$

![倍增做法](..\images/后缀数组倍增算法.png)

```cpp
int n,w,sa[N],rk[N << 1],oldrk[N << 1];//为了防止rk[i + w]越界，开两倍数组
string s;
inline bool cmp(int x,int y,int w)//第x，y个后缀数组，w表示拼接长度
{
    return (rk[x] != rk[y] ? (rk[x + w] < rk[y + w]) : (rk[x] < rk[y]));
}
void solve()
{
    cin >> s;
    n = s.size();s = " " + s;
    for(int i=1;i<=n;i++)
        sa[i] = i,rk[i] = s[i];
    for(w = 1,w < n;w <<= 1)
    {
        sort(sa + 1, sa + n + 1, [](int x, int y) {
      return rk[x] == rk[y] ? rk[x + w] < rk[y + w] : rk[x] < rk[y];
    });  // 这里用到了 lambda,因为cmp中有额外的参数 w，不会传入直接使用lambda
        memcpy(oldrk,rk,sizeof(rk));//由于计算 rk 时原来的 rk 会被覆盖，先复制
        for(int p=0,i=1;i<=n;i++)
        {
            if(oldrk[sa[i]] == oldrk[sa[i - 1]] && oldrk[sa[i] + w] == oldrk[sa[i - 1] + w])
                	rk[sa[i]] = p;
           else rk[sa[i]] = ++p;//若两个子串相同，对应的 rk 也应相同，去重
        }
    }
    for(int i=1;i<=n;i++)
        	cout << sa[i] << " \n"[i == n];
}

```

#### $O(nlogn) 做法$

前置知识 ： **计数排序**，**基数排序**      $O(n) 排序 O(logn) 倍增$   

基数排序把数字依次按照由**低位到高位**依次排序，排序时只看当前位。对于每一位排序时，因为上一位已经是有序的，所以这一位相等或符合大小条件时就不用交换位置，如果不符合大小条件就交换，实现可以用”桶”来做。

由于计算后缀数组的过程中排序的关键字是排名，值域为$O(n)$，并且是一个双关键字的排序，可以使用基数排序优化到$O(n)$

```cpp
int n,sa[N],rk[N << 1],oldrk[N << 1],id[N],cnt[N];
string s;
void solve()
{
    cin >> s;
    n = s.size();s = " " + s;
    int m = 127;
    for(int i=1;i<=n;i++)
        ++ cnt[rk[i] = s[i]];
    for(int i=1;i<=m;i++)
        cnt[i] += cnt[i - 1];
    for(int i=n;i>=1;i--)
        sa[cnt[rk[i]] --] = i;
    memcpy(oldrk + 1,rk + 1,n * sizeof(int));
    for(int p=0,i=1;i<=n;i++)
    {
        if(oldrk[sa[i]] == oldrk[sa[i - 1]])
            rk[sa[i]] = p;
       	else rk[sa[i]] = ++ p;
    }
    for(int w=1;w<n;w <<= 1,m = n)
    {
        //对第二关键字 : id[i] + w 进行计数排序
        memset(cnt,0,sizeof(cnt));
        memcpy(id + 1,sa + 1,n * sizeof(int));//id 先保存一份sa的备份相当于oldsa
        for(int i=1;i<=n;i++) ++ cnt[rk[id[i] + w]];
        for(int i=1;i<=m;i++) cnt[i] += cnt[i - 1];
        for(int i=n;i>=1;i--) sa[cnt[rk[id[i] + w]] --] = id[i];
        //第一关键字: id[i]进行计数排序
        memset(cnt,0,sizeof(cnt));
        memcpy(id + 1,sa + 1,n * sizeof(int));
        for(int i=1;i<=n;i++) ++ cnt[rk[id[i]]];
        for(int i=1;i<=m;i++) cnt[i] += cnt[i - 1];
        for(int i=n;i>=1;i--) sa[cnt[rk[id[i]]] --] = id[i];
        
        memcpy(oldrk + 1,rk + 1,n * sizeof(int));
        for(int p=0,i=1;i<=n;i++)
        {
            if(oldrk[sa[i]] == oldrk[sa[i - 1]] && oldrk[sa[i] + w] == oldrk[sa[i - 1] + w])
                rk[sa[i]] = p;
           else rk[sa[i]] = ++ p;
        }
    }
    for(int i=1;i<=n;i++)
        cout << sa[i] << " \n"[i == n];
}
```

- 第二关键字无需基数排序   第二关键字的实质其实就是把超过字符串范围的$sa_1$放到`sa`数组头部，剩下的依原顺序放入，因此我们完全可以直接完成而不是基数排序
- 优化计数排序的值域     计算完`rk`只会留下一个`p`，这个`p`就是当前排序的值域，可以来跑基数排序而不再用`n`

```cpp
int sa[N],rk[N],oldrk[N],id[N],w,cnt[N],key[N];//id数组相当于  oldsa 数组
string s;// key[i] = rk[id[i]] 作为基数排序的第一关键字组
bool cmp(int x,int y,int w)
{
    return oldrk[x] == oldrk[y] && oldrk[x + w] == oldrk[y + w];
}
void solve()
{
    cin >> s;
    int n = s.size();s = " " + s;
    int m = 127,p,i,w;
    pre(i,1,n)
        rk[i] = s[i],++ cnt[rk[i]];
   	pre(i,1,m)
        cnt[i] += cnt[i - 1];
    rep(i,n,1)
        sa[cnt[rk[i]] --] = i;//以上都是模版，不需要动
     for (w = 1;; w <<= 1, m = p) {  // m=p 就是优化计数排序值域,w 是倍增的长度
    for (p = 0, i = n; i > n - w; --i) id[++p] = i;//在小，子串不到结尾不是后缀数组
    for (i = 1; i <= n; ++i)
      if (sa[i] > w) id[++p] = sa[i] - w;

    memset(cnt, 0, sizeof(cnt));
    for (i = 1; i <= n; ++i) ++cnt[key[i] = rk[id[i]]];
    // 注意这里px[i] != i，因为rk没有更新，是上一轮的排名数组

    for (i = 1; i <= m; ++i) cnt[i] += cnt[i - 1];
    for (i = n; i >= 1; --i) sa[cnt[key[i]]--] = id[i];
    memcpy(oldrk + 1, rk + 1, n * sizeof(int));
    for (p = 0, i = 1; i <= n; ++i)
      rk[sa[i]] = cmp(sa[i], sa[i - 1], w) ? p : ++p;
    if (p == n) {
      break;
    }
  }
    pre(i,1,n)
        cout << sa[i] << " \n"[i == n];
}
```

[P3809 后缀排序](https://www.luogu.com.cn/problem/P3809)

## 树上启发式合并

- 按秩合并  -- 将小的集合合并到大的集合中。让高度小的树成为高度较大的子树，成为启发式合并算法

```cpp
void merge(int x, int y) {
  int xx = find(x), yy = find(y);
  if (size[xx] < size[yy]) swap(xx, yy);
  fa[yy] = xx;
  size[xx] += size[yy];
}
```



## 线段树

### 学习目标：


 - [ ] 线段树和树状数组

---
- 懒标记的时候最好加上一步判断，不需要 `pushdown`的时候不要 `pushdown`，无脑操作可能会导致一些错误！！！
- 对于需要排序询问的问题，输出一定要按照原序输出！！！ 

### Problems：
1. [HH的项链](https://www.luogu.com.cn/problem/P1972)

> 树状数组做法 `: ` 将询问区间按照右端点排序，然后在处理区间问题的时候不断更新某一个值在区间内最右端的位置，这样可以使得询问的时候不会产生影响，每次先处理 `r` 较小的，当询问区间内中的数已经出现过则先将出现的位置`-1` 表示清楚，再在当前位置加一即可，同时记录当前枚举到的右值下一个点，减少重复计算量。
```cpp
int n,m;
struct NODE
{
    int l,r,id;
    bool operator<(NODE w)
    {
        return r < w.r;
    }
}q[N];
int a[N],ans[N],pos[N],tr[N];
int lowbit(int x)
{
    return x & -x;
}
void modify(int u,int x)
{
    for(int i=u;i<=n;i+=lowbit(i))
        tr[i] += x;
}
int query(int u)
{
    int res = 0;
    for(int i=u;i;i-=lowbit(i))
        res += tr[i];
    return res;
}
void solve()
{
    cin >> n;
    pre(i,1,n)
        cin >> a[i];
    cin >> m;
    pre(i,1,m)
    {
        int l,r;
        cin >> l >> r;
        q[i] = {l,r,i};
    }
    sort(q + 1,q + 1 + m);
    int now = 1;
    pre(i,1,m)
    {
        for(int j=now;j<=q[i].r;j++)
        {
            if(pos[a[j]]) modify(pos[a[j]],-1);
            modify(j,1);
            pos[a[j]] = j;
        }
        now = q[i].r + 1;
        ans[q[i].id] = query(q[i].r) - query(q[i].l - 1);
    }
    pre(i,1,m)
        cout << ans[i] << endl;
}
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
> 线段树解法 ： 线段树解法的关键点在于存储每个区间的访问顺序，修改操作直接修改为 0 或 1 即可，其中`vis[]`数组用于记录访问顺序，以便原序输出。

```cpp
sort(q+1,q+m+1,cmp);
for(rint i=1;i<=m;++i) vis[q[i].r].push_back(i); // 确定每一个问题的访问顺序 
for(rint i=1;i<=n;++i){
		if(!lst[a[i]]){
			lst[a[i]]=i;
			build(root,i);//建树相当于将这个点的值变为 1 ，无需全部建
		}
		else{
			modify(root,lst[a[i]]);//修改相当于将之前的点的值改为 0 ，去重。
			lst[a[i]]=i;
			build(root,i);
		}
		for(rint j=0;j<vis[i].size();++j){
			int tmp=vis[i][j];
			ans[q[tmp].id]=query(root,q[tmp].l,q[tmp].r);
		}
	}
```
2.[XOR on Sement](https://www.luogu.com.cn/problem/CF242E)
-  拆位线段树

因为异或并没有合并运算性质，且对应`异或`操作常见的解决方法只有`1、前缀和，1、拆位`两种解决方案，所以本题需要拆位计算，考虑到` 0 ^ 0 = 0 ，0 ^ 1 = 1，1 ^ 1 = 0`所以拆位后需要将异或的数据和原数据每位异或，记录每位`1` 的数量即可，最后求和即是每位 1 对应二进制加和
```cpp
f[l,r,i] = (r - l + 1) - f[l,r,i)//异或的值第 i 位为 1 的情况，和 1 异或相当于每一位取反，所以 1 的个数也是相减
//当前位是 0 的话异或后，原值每一位不会改变
```
> 注意本题需要判断`pushdown`的边界，数据太大，不判断会导致RE
```cpp
const int N = 1e5 + 10;
int n,m;
int a[N];
struct tree
{
    int l,r,sum[21],lazy;//每一位 1 的个数和懒标记
}tr[N << 2];
void pushup(int u)
{
    for(int i=0;i<=20;i++)
        tr[u].sum[i] = tr[u << 1].sum[i] + tr[u << 1 | 1].sum[i];
}
void pushdown(int u)
{
    if((u << 1) > (N << 2 )) return ;//不加此特判可能会导致数据越界而 RE
    int l = tr[u].l,r = tr[u].r;
    int mid = l + r >> 1;
    for(int i=0;i<=20;i++)
    {
        if((tr[u].lazy >> i) & 1)//异或的为 1 ，区间取反
        {
            tr[u << 1].sum[i] = (mid - l + 1) - tr[u << 1].sum[i];
            tr[u << 1 | 1].sum[i] = (r - mid) - tr[u << 1 | 1].sum[i];
        }
    }
    tr[u << 1].lazy ^= tr[u].lazy;
    tr[u << 1 | 1].lazy ^= tr[u].lazy;
    tr[u].lazy = 0;
}
void build(int u,int l,int r)
{
    if(l == r)
    {
        tr[u] = {l,r,0,0};
        for(int i=0;i<=20;i++)
            tr[u].sum[i] = ((a[l] >> i) & 1);
    }      
    else 
    {
        int mid = l + r >> 1;
        tr[u] = {l,r,0,0};
        build(u << 1,l,mid);
        build(u << 1 | 1,mid + 1,r);
        pushup(u);
    }  

}
void modify(int u,int l,int r,int x)
{
    if(tr[u].l >= l && tr[u].r <= r)//区间内
    {
        for(int i=0;i<=20;i++)
        {
            if((x >> i) & 1)
                tr[u].sum[i] = (tr[u].r - tr[u].l + 1) - tr[u].sum[i];
        }
        tr[u].lazy ^= x;
    }
    else 
    {
        int mid = tr[u].r + tr[u].l >> 1;
        pushdown(u);
        if(l <= mid)
            modify(u << 1,l,r,x);
        if(r > mid)
            modify(u << 1 | 1,l,r,x);
        pushup(u);
    }
}
int query(int u,int l,int r)
{
    if(tr[u].l >= l && tr[u].r <= r)
    {
        int res = 0;
        for(int i=0;i<=20;i++)
            res += (tr[u].sum[i] * (1 << i));
        return res;
    }
    pushdown(u);
    int res = 0;
    int mid = tr[u].l + tr[u].r >> 1;
    if(l <= mid)
        res += query(u << 1,l,r);
    if(r > mid)
        res += query(u << 1 | 1,l,r);
    return res;
}
void solve()
{
    cin >> n;
    pre(i,1,n)
        cin >> a[i];
    build(1,1,n);
    cin >> m;
    pre(i,1,m)
    {
        int op;
        cin >> op;
        if(op == 1)
        {
            int l,r;
            cin >> l >> r;
            cout << query(1ll,1ll,r) - query(1ll,1ll,l - 1) << endl;
        }
        else
        {
            int l,r,x;
            cin >> l >> r >> x;
            modify(1,l,r,x);
        }
    }
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
3.[取模 开方类线段树](https://www.luogu.com.cn/problem/CF438D)

对区间进行开方和取模操作都是不可以加懒标记的只能暴力对区间内每一个数都操作，但是观察可以发现开方的次数是可以非常小的，大概开`10`次左右足够，同样的取模的次数也是可以限制，有 `x mod p < x / 2 ( p < x )`，所以取模大概在 `log x` 次便足够了，所以只要标记区间的最大值判断取模次数即可优化时间
```cpp
const int N = 1e5 + 10;
int n,m;
int a[N];
struct TREE
{
    int l,r,sum,mx;
}tr[N << 2];
void pushup(int u)
{
    tr[u].sum = tr[u << 1].sum + tr[u << 1 | 1].sum;
    tr[u].mx = max(tr[u << 1].mx,tr[u << 1 | 1].mx);
}
void build(int u,int l,int r)
{
    if(l == r)
        tr[u] = {l,r,a[l],a[l]};
    else 
    {
        tr[u] = {l,r,0,0};
        int mid = l + r >> 1;
        build(u << 1,l,mid);
        build(u << 1 | 1,mid + 1,r);
        pushup(u);
    }
}
void modify(int u,int l,int r,int x)//单点修改数
{
    if(tr[u].l == l && tr[u].r == r)
    {
        tr[u].sum = x;
        tr[u].mx = x;
    }
    else 
    {
        int mid = tr[u].l + tr[u].r >> 1;
        if(l <= mid)
            modify(u << 1,l,r,x);
        if(r > mid) 
            modify(u << 1 | 1,l,r,x);
        pushup(u);
    }
    
}
void modify1(int u,int l,int r,int mod)//取模操作没有区间性，需要每个点都进行一次
{
    if(tr[u].mx < mod) return ;
    if(tr[u].l == tr[u].r)
    {
        tr[u].sum %= mod;
        tr[u].mx %= mod;
    }
    else
    {
        int mid = tr[u].l + tr[u].r >> 1;
        if(l <= mid) modify1(u << 1,l,r,mod);
        if(r > mid) modify1(u << 1 | 1,l,r,mod);
        pushup(u);
    }
}
int query(int u,int l,int r)
{
    if(tr[u].l >= l &&  tr[u].r <= r)
    {
        return tr[u].sum;
    }
    int mid = tr[u].l + tr[u].r >> 1;
    int s = 0;
    if(l <= mid)
        s +=  query(u << 1,l,r);
    if(r > mid)
        s += query(u << 1 | 1,l,r);
    return s;
}
void solve()
{
    cin >> n >> m;
    pre(i,1,n)
        cin >> a[i];
    build(1,1,n);
    while(m --)
    {
        int op;
        cin >> op;
        if(op == 1)
        {
            int l,r;
            cin >> l >> r;
            cout << query(1,l,r) << endl;
        }
        else if(op == 2)
        {
            int l,r,x;
            cin >> l >> r >> x;
            modify1(1,l,r,x);
        }
        else
        {
            int k,x;
            cin >> k >> x;
            modify(1,k,k,x);
        }
    }
}
signed main()
{
     _;
    int t = 1;
    while(t -- )
        solve();

}
```
4.[预处理 区间最小值](https://www.luogu.com.cn/problem/CF522D?contestId=152942)

询问的是区间内任意两个相同的数之间距离的最小值，没有修改操作，所以可以直接预处理出来结果之后建树，那么每次询问的就是区间的最小值，此题的关键是需要对询问区间按照左端点排序，每次询问 `1,r`区间和即可（每次询问前先进行修改操作）
修改：使用`pos`数组标记这个与这个点相同的下一个点的位置，当左指针移动至这个点之后需要将对应的`pos`点清零，代表这个点不在询问区级内，对答案不会产生影响，所以需要删除这个点的影响，又因为每个点`res`只会记录距离最近的左边值相同点，所以删除的时候只需要删除最近的右点即可（两数组相当于互逆数组，`mp，pos`即是，需要着重理解）
```cpp
const int INF = 0x7f7f7f7f7f;
const int N = 5e5 + 10;
int n,m;
int a[N],res[N],ans[N];
struct NODE
{
    int l,r,id;
    bool operator<(NODE w)
    {
        if(l != w.l)
            return l < w.l;
        return r < w.r;
    }
}q[N];
struct tree 
{
    int l,r,mi;
}tr[N << 2];
void pushup(int u)
{
    tr[u].mi = min(tr[u << 1].mi,tr[u << 1 | 1].mi);
}
void build(int u,int l,int r)
{
    if(l == r)
        tr[u] = {l,r,INF};
    else 
    {
        int mid = l + r >> 1;
        tr[u] = {l,r,INF};
        build(u << 1,l,mid);
        build(u << 1 | 1,mid + 1,r);
        pushup(u);
    }
}
void modify(int u,int l,int x)
{
    if(tr[u].l == tr[u].r)
        tr[u].mi = x;
    else 
    {
        int mid = tr[u].l + tr[u].r >> 1;
        if(l <= mid)
            modify(u << 1,l,x);
        if(l > mid)
            modify(u << 1 | 1,l,x);
        pushup(u);
    }
}
int query(int u,int l,int r)
{
    if(tr[u].l >= l && tr[u].r <= r)
        return tr[u].mi;
    int mid = tr[u].l + tr[u].r >> 1;
    int s = INF;
    if(l <= mid)
        s = min(s,query(u << 1,l,r));
    if(r > mid)
        s = min(s,query(u << 1 | 1,l,r));
    return s;
}
void solve()
{
    cin >> n >> m;
    build(1,1,n);
    map<int,int> mp;
    int pos[n + 10] = {0};//记录左指针右移时需要清零的数据位置，代表这个点不在范围内，不能使用
    pre(i,1,n)
    {
        cin >> a[i];
        res[i] = INF;
        if(mp[a[i]])//res 数组代表距离左边值相同点的最短距离，mp 数组表示值对应的下标，先使用在更新，pos数组即左对应右的右下标
            res[i] = i - mp[a[i]],pos[mp[a[i]]] = i;//当前数据向左的距离，上一个相等点对应这个点上
        mp[a[i]] = i;
        modify(1,i,res[i]);
    }
    pre(i,1,m)
    {
        int l,r;
        cin >> l >> r;
        q[i] = {l,r,i};
    }
    sort(q + 1,q + 1 + m);//只有排序之后才能使左指针合理右移，左侧不在区间内的数据对应右边的点需要清零
    int now = 1;
    pre(i,1,m)
    {
        int l = q[i].l,r = q[i].r,id = q[i].id;
        if(l > now)//左指针需要右移
        {
            for(;now < l;now ++)//表示这个点不在询问区间内，需要删除影响，即是将最近的右点的值清零，表示该右点在区间内没有左点
                modify(1,pos[now],INF);
        }
        ans[id] = (query(1,1,r) != INF ? query(1,1,r) : -1);
    }
    pre(i,1,m)
        cout << ans[i] << endl;
}

signed main()
{
    _;
    int t = 1;
    while(t -- )
        solve();

}


```

5.[树上线段树](https://codeforces.com/problemset/problem/383/C)
一般来说树上线段树会和**树链剖分**相结合，当然也会有其他的方法，暂时未学到树链剖分，本题先使用`dfs序搭配两棵线段树解决`
大意：给定一个无向图，选中一个节点给这个节点增加`val`同时给这个节点的子节点减去`val`，给子子节点增加`val`，依次类推，询问操作后的某节点的权值是多少
分析：区间修改，单点查询，对于单点查询而言，`pushup`操作可以不写，因为最后结果一定会递归到单个数据的节点，所以向上`pushup`无意义，但是`pushdown`不可少，可以直接初始化两颗线段树，对于`dfs序`中深度为奇数的点加上`val`，对于深度为偶数时减去`val`,在询问操作时，判断询问节点是奇偶即可，加上相同的树，减去不同的树即可！

```cpp
const int N = 2e5 + 10;
#define pre(i,a,b) for(int i=a;i<=b;i++)
#define pa tr[u][op]
#define ls tr[u << 1][op]
#define rs tr[u << 1 | 1][op]//宏定义使用比较方便 
int n,m;
int a[N];
int dep[N],dfn[N],id[N],si[N],cnt;//dfs序所需要的数据
int h[N],e[N << 1],ne[N << 1],idx;//建树所需要的数据
struct tree 
{
    int l,r,sum,lazy;
}tr[N << 2][2];//建两棵线段树
void add(int a,int b)
{
    e[idx] = b,ne[idx] = h[a],h[a] = idx ++;
}
void dfs(int u,int fa)//求解dfs序，将遍历顺序转换为下标
{
    dep[u] = dep[fa] + 1;
    dfn[u] = ++ cnt;//时间戳
    id[cnt] = u;//dfs序
    si[u] = 1;
    for(int i=h[u];~i;i=ne[i])
    {
        int j = e[i];
        if(j == fa) continue;
        dfs(j,u);
        si[u] += si[j];
    }
}
void pushup(int u,int op)//可以不写，本意无需pushup
{
    pa.sum = ls.sum + rs.sum;
}
void pushdown(int u,int op)//懒标记pushdown操作
{
    int mid = pa.l + pa.r >> 1;
    ls.sum += pa.lazy * (mid - pa.l + 1);
    ls.lazy += pa.lazy;
    rs.sum += pa.lazy * (pa.r - mid);
    rs.lazy += pa.lazy;
    pa.lazy = 0;
}
void build(int u,int l,int r,int  op)
{
    if(l == r)
        pa = {l,r,0,0};
    else 
    {
        pa = {l,r,0,0};//总是忘写，不写会寄！！！！！
        int mid = pa.l + pa.r >> 1;
        build(u << 1,l,mid,op);
        build(u << 1 | 1,mid + 1,r,op);
    }
}
void modify(int u,int l,int r,int x,int op)
{
    if(pa.l >= l && pa.r <= r)
    {
        pa.sum += (pa.r - pa.l + 1) * x;
        pa.lazy += x;
        return ;
    }
    pushdown(u,op);
    int mid = pa.l + pa.r >> 1;
    if(l <= mid) modify(u << 1,l,r,x,op);
    if(r > mid) modify(u << 1 | 1,l,r,x,op);
}
int query(int u,int pos,int op)
{
    if(pa.l == pa.r)
        return pa.sum;
    pushdown(u,op);
    int mid = pa.l + pa.r >> 1;
    if(pos <= mid) return query(u << 1,pos,op);
    return query(u << 1 | 1,pos,op);
}
void solve()
{
    cin >> n >> m;
    fill(h + 1,h + 1 + n,-1);
    pre(i,1,n)
        cin >> a[i];
    pre(i,2,n)
    {
        int u,v;
        cin >> u >> v;
        add(u,v),add(v,u);
    }
    dep[0] = -1;
    dfs(1,0);
    build(1,1,n,0);
    build(1,1,n,1);
    pre(i,1,m)
    {
        int op,x,y;
        cin >> op;
        if(op == 1)
        {
            cin >> x >> y;//根据dep[x]的深度来判断修改操作，奇偶性相同的为加，不同的为减，直接分开维护
            if(dep[x] & 1) modify(1,dfn[x],dfn[x] + si[x] - 1,y,1);
            else modify(1,dfn[x],dfn[x] + si[x] - 1,y,0);
        }
        else 
        {
            cin >> x;//加上奇偶性相同的树，减去不同的树。
            cout << a[x] + query(1,dfn[x],dep[x] & 1) - query(1,dfn[x],!(dep[x] & 1)) << endl;
        }
    }
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
### 树链剖分搭配线段树
**树剖**是指通过**轻重边**剖分将树分割成多条链，然后利用数据结构来维护这些链（本质是一种优化暴力）

明确定义 ：

- 重儿子 ： 父亲节点的所有儿子中子树节点数目最多（size最大）的结点；
- 轻儿子 ： 父亲节点中除了重儿子以外的儿子；
- 重边 ： 父亲节点和重儿子连成的边；
- 轻边 ： 父亲节点和轻儿子连成的边；
- 重链 ： 由多条重边连接而成的路径；
- 轻链 ： 由多条轻边连接而成的路径。                             
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
    if(dep[x] > dep[y])
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
    if(dep[x] > dep[y])
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
 树链剖分例题 ：
 [树的统计](https://www.luogu.com.cn/problem/P2590)
 [软件包管理器](https://www.luogu.com.cn/problem/P2146) 本题询问答案的时候直接使用前后差值即可，1表示安装，0表示未安装，-1为初始条件！！！初始**不能**使用 0 
 [松鼠的新家](https://www.luogu.com.cn/problem/P3258)

 [6.状态压缩配合树剖线段树](https://codeforces.com/problemset/problem/620/E)
 本题一共两个操作，将一颗子树更换颜色，求子树区间内的颜色数量 。
 需要注意颜色种类最多不超过 `60 `种，`1 << 60 < ll`，所以可以使用`状态压缩搭配或操作`实现区间的快速合并
 需要注意的是 **`pushdown` 时如果父节点的 `lazy 是 0` 的时候不能直接`pushdown` 需要跳过，因为这个时候是初始状态，且对于或操作来说直接` pushdown` 会令子节点的数据发生错误，---> 不如以后每次都加上判断条件，防止细节错误**
 ```cpp
const int N = 4e5 + 10;
//本题重点在于颜色的状态压缩，将颜色转换为 或运算，直接快速求出区间操作
int n,m;
int c[N];
int h[N],e[N << 1],ne[N << 1],idx;//建树操作
int f[N],si[N],son[N],dep[N],id[N],rk[N],top[N],cnt;//树链剖分
struct TREE
{
    int l,r;
    int sum;//状态压缩 或运算
    int lazy;//改变的颜色
}tr[N << 2];
void add(int a,int b)
{
    e[idx] = b,ne[idx] = h[a],h[a] = idx ++;
}
void dfs1(int u,int fa)
{
    dep[u] = dep[fa] + 1;
    f[u] = fa;
    si[u] = 1;
    for(int i=h[u];~i;i=ne[i])
    {
        int j = e[i];
        if(j == fa)   continue;
        dfs1(j,u);
        si[u] += si[j];
        if(si[j] > si[son[u]])
            son[u] = j;
    }
}
void dfs2(int u,int tp)
{
    top[u] = tp;
    id[u] =  ++cnt;
    rk[cnt] = u;
    if(!son[u]) return ;
    dfs2(son[u],tp);
    for(int i=h[u];~i;i=ne[i])
    {
        int j = e[i];
        if(j != f[u] && j != son[u])
            dfs2(j,j);
    }
}
void pushup(int u)
{
    pa.sum = (ls.sum | rs.sum);//转换对应二进制位置表示颜色有无
}
void pushdown(int u)
{
    if(pa.lazy == 0) return ;//一定不能少，pushdown的时候如果父节点为0是初始条件，对于或操作会出现问题
    ls.lazy = rs.lazy = pa.lazy;
    ls.sum = rs.sum = pa.lazy;
    pa.lazy = 0;
}
void build(int u,int l,int r)
{
    if(l == r)
        pa = {l,r,1ll << c[rk[l]],c[rk[l]]};
    else 
    {
        pa = {l,r,0,0};
        int mid = l + r >> 1;
        build(u << 1,l,mid);
        build(u << 1 | 1,mid + 1,r);
        pushup(u);
    }
}
void modify(int u,int l,int r,int c)
{
    if(pa.l >= l && pa.r <= r)
    {
        pa.sum = 1ll << c;
        pa.lazy = 1ll << c;
    }
    else 
    {
        int mid = pa.l + pa.r >> 1;
        pushdown(u);
        if(l <= mid)
            modify(u << 1,l,r,c);
        if(r > mid)
            modify(u << 1 | 1,l,r,c);
        pushup(u);
    }
}
int query(int u,int l,int r)
{
    if(pa.l >= l && pa.r <= r)
    {
        return pa.sum;
    }
    int mid = pa.l + pa.r >> 1;
    pushdown(u);//！！！！
    int s = 0;
    if(l <= mid)
        s |= query(u << 1,l,r);//！！！
    if(r > mid)
        s |= query(u << 1 | 1,l,r);
    return s;
}
void solve()
{
    cin >> n >> m;
    pre(i,1,n)
        cin >> c[i];
    fill(h + 1,h + 1 + n,-1);
    pre(i,2,n)
    {
        int u,v;
        cin >> u >> v;
        add(u,v),add(v,u);
    }
    dfs1(1,0);
    dfs2(1,1);
    build(1,1,n);
    pre(i,1,m)
    {
        int op,x,y;
        cin >> op >> x;
        if(op == 1)
        {
            cin >> y;
            modify(1,id[x],id[x] + si[x] - 1,y);
        }
        else 
        {
            int now = query(1,id[x],id[x] + si[x] - 1),ans = 0;
            // cout << now << " s" << endl;
            pre(i,0,62)
            {
                if((now >> i) & 1)
                    ans ++;
            }
            cout << ans << endl;

        }
    }
}
 ```
[7.单点修改，区间求max线段树](https://ac.nowcoder.com/acm/contest/67742/G)   不含区间右端点的max（左端和 - 右端和的最大值）
```cpp
const int N = 5e5 + 10,inf = -1e9;
int n,q,a[N];
struct node
{
    int sum,del,mx;
}tr[N << 2];
node merge(node l,node r)
{
    if(l.sum == inf)
        return r;
    node res;
    res.sum = l.sum + r.sum;
    res.del = l.del;//代表不选最后几个数
    res.mx = max({l.mx,l.sum + r.mx,l.sum - r.del});
    return res;
}
void build(int u,int l,int r)
{
    if(l == r)
    {
        pa = {a[l],a[l],inf};
        return ;
    }
    int mid = l + r >> 1;
    build(u << 1,l,mid);
    build(u << 1 | 1,mid + 1,r);
    pa = merge(ls,rs);
}
void modify(int u,int l,int r,int L,int R,int x)
{
    if(l >= L && r <= R)
    {
        pa = {x,x,inf};
        return ;
    }
    int mid = l + r >> 1;
    if(L <= mid)
        modify(u << 1,l,mid,L,R,x);
    if(R > mid)
        modify(u << 1 | 1,mid + 1,r,L,R,x);
    pa = merge(ls,rs);
}
node query(int u,int l,int r,int L,int R)
{
    if(l >= L && r <= R)
        return pa;
    int mid = l + r >> 1;
    node res = {inf,inf,inf};
    if(L <= mid)
        res = merge(res,query(u << 1,l,mid,L,R));
    if(R > mid)
        res = merge(res,query(u << 1 | 1,mid + 1,r,L,R));
    return res;
}
void solve()
{
    cin >> n >> q;
    pre(i,1,n)
        cin >> a[i];
    build(1,1,n);
    while(q --)
    {
        int op,l,r;
        cin >> op >> l >> r;
        // cout << op << " " << l << endl;
        if(op == 1)
            modify(1,1,n,l,l,r);
        else cout << query(1,1,n,l,r).mx << endl;
    }
}
```

 






---
### 树状数组求逆序对个数
对于每一个数只考虑该数作为逆序对中第二个数即可，最后求和即是总序列中逆序对的个数
例如 ： 5 3 4 2 1 7
刚开始先插入 5，单点更新 5 这个位置 +1，然后查询区间 1 到 4 的数，则逆序对 + （1 - 0 - 1）= 0个(第 i 个插入 - 该数前面的数 - 本身）这个数是**在它前面插入并且在它右边的数**

**树状数组求逆序对数有两种写法 ：**

- 直接按照数值进行插入，查询的时候使用树状数组总和减去小于等于次数的数即可，例 ： [珂朵莉的数列](https://ac.nowcoder.com/acm/problem/14522)  [逆序数](https://ac.nowcoder.com/acm/problem/15163)
```cpp
//直接插入 a[i] 的写法 add(a[i],1)，正序遍历
pre(i,1,n)
{//m 是数组中的最大值
    ans = query(m) - query(a[i]);// x 表示当前枚举到的数，query(m) 表示总数,query(x) 表示小于等于 x 的数，得逆序数,如果数据较大可能需要离散化 a 数组，此时 传入的 a[]就是离散化后的数组，m是离散化后的数组大小 ！！！！
    add(a[i],1);   
}
for(int i=1;i<=n;i++)//不涉及离散化这种也可以，涉及离散化还是上面好
{
    scanf("%d",&temp);
    add(temp);
    result += i - query(temp);
}
```
- 使用输入顺序进行插入计数，求逆序对的时候直接加和此数据的对应顺序即是答案(这种方法特殊在有时需要对倍数进行操作，常规写法比较麻烦）  例：[牛客月赛87](https://ac.nowcoder.com/acm/contest/73854/G) (上述珂朵莉此方法不好写)
```cpp
//使用插入读入顺序来操作，遍历的时候需要倒序
pos[a[i]] = i;  
for(int i=n;i>=1;i--)  ans += query(pos[i]),add(pos[i],1);
```
#### 树状数组求逆序对例题 ：
[牛客周赛38 G](https://ac.nowcoder.com/acm/contest/78292/G)

### 权值线段树
普通线段树维护的信息是数列的区间信息，比如区间和，区间最大值，区间最小值，在维护序列的这些信息的时候我们更关注的是这些数本身的信息，换句话说，我们要维护区间的最值或和，我们最关注的是这些数统共的信息。而权值线段树**维护一列数中数的个数**。
**权值线段树和简单线段树的区别 ：**
权值线段树维护的是桶，**按值域开空间，维护的是个数**，
普通线段树维护的是信息，**按个数开空间，维护的是待定信息**。
权值线段树的用途 ：
权值线段树可以解决**数列第 k 大 / 小的**问题，**注意 ：**只能对给定数列解决**整个数列的第 k 大 / 小**,并不能解决**数列的子区间**的第 k 大 / 小。（解决子区间问题需要用到主席树）。
权值线段树维护的是一堆桶，每个节点储存的是节点维护区间（就是值域）的数出现的次数，整棵线段树的根节点就表示整个值域有几个数，对于整列数中第 k 大/小的数，从根节点判断（按照第k大举例），如果 k 比右儿子大，说明第 k 大的数在左儿子表示的值域中，然后 k - 右儿子，继续递归
```cpp
//建树
#define ls tr[u << 1]
#define rs tr[u << 1 | 1]
void build(int u,int l,int r)
{
	tr[u] = {l,r,0};
	if(l == mid)
	{
		tr[u] = a[l];//a[l] 表示数 l 有多少个
		return ;
	}
	build(u << 1,l,mid);
	build(u << 1 | 1,mid +1,r);
	pa = mer(ls,rs);	
}
//修改
void modify(int u,int l,int r,int cnt)//表示数 k 的个数多 cnt 个 l==r=k
{
	if(pa.l == pa.r)
	{
		pa.cnt += cnt;
		return ;
	}
	int mid = pa.l + pa.r >> 1;
	if(l <= mid)
		modify(u << 1,l,r,cnt);
	if(r > mid)
		modify(u << 1 | 1,l,r,cnt);
	pa = mer(ls,rs,pa.l,pa.r);
	
}
//查询
node query(int u,int l,int r)//查询数 l 有多少个 l==r
{
	if(pa.l >= l && pa.r <= r)
		return pa;
	int mid = pa.l + pa.r >> 1;
	node res = {-1,-1};
	if(l <= mid)
		res = mer(res,query(u << 1,l,r),-1,-1);
	if(r > mid)
		res = mer(res,query(u << 1 | 1,l,r,res.l,res.r);
	return res;
}
//查询第 k 值
int kth(int u,int l,int r,int k)
{
	if(l == r) 
		return l;
	int mid = l + r >> 1;
    int Ln = sum[u << 1];//左儿子中有多少数据
    int Rn = sum[u << 1 | 1];//有儿子中有多少数据
	if(k <= Rn)
		return kth(u << 1 | 1,mid + 1,r,k);
	else return kth(u << 1,l,mid,k - Rn);
}
```
#### 权值线段树 例题 ：
[Atc 339 E](https://atcoder.jp/contests/abc339/tasks/abc339_e)
[24天梯决 排列](http://10.36.65.50:81/contest/1084/problem/L3-2)



2024 5 23
动态开点线段树













## bitset

### 学习目标：  Bitset 
### 学习 `bitset` 数组
简介 ： bitset 是标准库中的一个存储 0/1 的大小不可变容器.    bitset 就是通过固定的优化,使得一个字节的八个比特能分别储存 8 位的 0/1.int的 1/32 倍.
  允许的运算 ： `==、!=、&、|、^、~，>>、<<；注意  bitset 只能与 bitset 进行位运算，不同类需要强制类型转换`
  **attention** :  `bitset`的第 `x`位数据表示数据位 x 的状态是否可行，就是当为 x 的时候是否可以分割，所以`bitset`的大小是数据和，这样才能表示出所有状态，同时，比较的时候即是当前状态异或状态右移`x`，返回值也是`bitset`即是将对应的位置的值改变 01 值，例：`f[0] = 1，f |= (f << 2) ,即是 f[2] = 1，2 可做分割点，这里的 2 表示的是当前价值为 2 `
  ```cpp
  count() ;// 返回 true 的数量
size() ; // 返回 bitset 的大小
test(pos) ;//他和vector中的  at() 的作用是一样的,和 [] 运算符的区别是越界检查 
any() ;//若存在某一位是 true 则返回 true,否则返回 false
none() ;//若所有的位都是 false ,则返回 true,否则返回 false .
all() ;// C++11 .若所有的位都是 true则返回 true,否则返回false. 
[]操作符    s[k] 表示 s 的第 k 位，即可取值也可赋值，编号从 0 开始
输出bitset 变量名时，是直接输出全部内容的，且是倒着输出的，即是第 0 位对应最右边
  ```
- bitset 优化 01 背包分割点问题 [P1537弹珠](https://www.luogu.com.cn/problem/P1537)
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


### [bitset 扩展](https://atcoder.jp/contests/abc348/tasks/abc348_f)
```cpp
bitset<N> bt[N][M],t;//bt[j][x] 表示第 j 列出现数字 x 的情况(表示在第几行出现)，bt[j][x][k]
//为 1 表示第 k 行第 j 列存在数字 x 
//枚举每一行，到 i 的时候，将 bt[j][a[i][j]](0 <= j < m)做异或和，因为
// 是存在奇数次相同就相似，所以在异或和中，相似的行编号对应位将为 1，异或和中
// 1 的数量即为序列[1,i - 1]中与序列 i 相似的序列数量。
// 然后将第 i 行中第 j 列数字 a[i][j] 的情况更新，即 bt[j][a[i][j]]置为 1
// 复杂度优化为 O(n * n * m / 64)
int n,m;
int g[N][N];
void solve() 
{
    cin >> n >> m;
    int ans = 0;
    pre(i,1,n)
    {
        pre(j,1,m)
        {
            cin >> g[i][j];
            g[i][j] --;
        }
        t.reset();//将 t 置为 0，初始化
        pre(j,1,m)
        {// bt 表示的是第 j 列 x 出现的情况，出现过的行对应的位置是 1，bitset输出时是倒序输出的
            t ^= bt[j][g[i][j]];//bt 表示第 j 列 x 出现的情况是数组
            //后面在加索引表示每一行出现的情况
            // cout << " w " << bt[j][g[i][j]] << endl;
            bt[j][g[i][j]][i] = 1;//更新状态，第 i 行第 j 列 x 出现过了
        }
        // cout << t << endl;
        ans += t.count();//表示枚举到第 i 行的时候得到的数量，t 中 1 的个数
    }
    //相当于将每一列中 x 出现的情况都预处理出来，bitset对应的三维数组，第二维表示的 x，第一维是 列
    //第三维是 行，每次 异或 bt[j][g[i][j]]表示进入第 j 列 x 的情况，然后和之前的异或可以得到这个数在不同行的情况
    cout << ans << endl;
} 
```





## DP

### 学习目标：

#### 学习 ACM 中 DP 部分的知识，并将刷题记录下来，方便后续学习
### [状态压缩DP](https://www.cnblogs.com/mxrmxr/p/9799832.html)
（https://www.cnblogs.com/Tony-Double-Sky/p/9283254.html）
`状态压缩DP`一般是基于二进制进行的，结合位运算
- **基于连通性** DP（棋盘式）
- 集合式（表示每一个元素是否存在集合中）
##### 第一类
DP 的过程是随着阶段增长，在每个状态维度上的分界点组成了 `DP`拓展的轮廓，对于某些问题，我们需要在 `DP`的状态中记录一个集合，保存这个轮廓的详细信息，以便于进行状态转移。若集合大小不超过 N ，集合中每个元素都是小于`k`的自然数，，**则我们可以把这个集合看做一个 N 位 k 进制数。以一个[0,k^N-1]之间的十进制整数的形式作为 DP 状态的一维，这种把集合转换为整数记录在 DP 状态中的一类算法被称之为 状态压缩 DP**
[互不侵犯](https://www.luogu.com.cn/problem/P1896)
每个国王会攻击附近的 8 格，求方案数使得 m 个王国互不攻击，`n`较小，一般不是搜索就是 DP，对于 `DP`的状态递推，一个是过往使用个数，另一个是行或列的状态(**对于矩阵状压DP常用行作为状态)**，且由于行 n 较小可以直接使用二进制数来表示当前每一行状态 -- 第几行 （i），此行的状态（j），这一行已经使用的国王数（s）
```cpp
f[i][j][s] = sum(f[i - 1][k][s - gs[j]];// gs 表示预处理数据这一行放的国王数目
//f[i][j][s]表示在只考虑前 i 行时，在前 i 行(包括i)有且仅有 s 个国王，且第 i 行的情况是 j 状态时情况的总数，而 k 代表第 i - 1行国王情况的状态编号。（j，k <= n）
//核心在于需要预处理每个状态所需国王数，并且判断没一行此状态满不满足左右无相邻，直接右移一维 & 即可。
i - 1 行状态需要的判断核心代码
 if(!st[s]) continue;
if(j & s) continue;上下关系
if((s << 1) & j) continue;//右上角
if((s >> 1) & j) continue;//左上角
for(int l=k;l>=need[j];l--)
	dp[i][j][l] += dp[i - 1][s][l - need[j]];
```
[玉米田](https://www.luogu.com.cn/problem/P1879)
```cpp
dp[i][j]//表示前 i 行，且第 i 行状态为 j 的方案数
//状态压缩 DP 判断状态是否合法非常重要 上一题是判断大小这一题是判断 & 后是否和给定的数据一致
for(int i=0;i<=tot;i++)
{
    if(can[i] && (i & mp[1]) == i)// mp 表示将输入的数据转换为对应的二进制
        dp[1][i] = 1;
}
for(int i=2;i<=n;i++)
{
    for(int j=0;j<=tot;j++)
    {
        if(can[j] && (j & mp[i]) == j)
        {
            for(int s=0;s<=tot;s++)
            {
                if(!(s & j))//两个状态没有上下相邻的
                    dp[i][j] = (dp[i][j] + dp[i - 1][s]) % mod;
            }
        }
    }
}
```
[炮兵阵地](https://www.luogu.com.cn/problem/P2704)
```cpp
//本题主要在于需要减少无用状态计算量，循环层数较多，需要简化，并且需要记录每行合理的状态，dp 时先枚举 i 在 i-2 判断状态合法，在 i-1 判断状态合法
// need 数组的计算和 第一行数据的预处理省略，记住更新操作之前一定要验证状态是否合理 ！！！！！！先遍历本行状态在 i - 2 判断状态，在 i-1 判断状态，在更新！！！！！
for(int i=1;i<=cnt;i++)
{
    if((state[i] & mp[2]) == state[i])
    {
        st[2][i] = 1;
        for(int j=1;j<=cnt;j++)
        {
            if(st[1][j] && ((state[i] & state[j]) == 0))
            {
                dp[2][i][j] = max(dp[2][i][j],dp[1][j][0] + need[i]);
            }
        }
    }
}
for(int i=3;i<=n;i++)
{
    for(int j=1;j<=cnt;j++)
    {
        if((state[j] & mp[i]) == state[j])
        {
            st[i][j] = 1;
            for(int k=1;k<=cnt;k++)
            {
                if(st[i - 2][k] && ((state[j] & state[k]) == 0))
                {
                    for(int l=1;l<=cnt;l++)
                    {
                        if(st[i - 1][l] && ((state[j] & state[l]) == 0) && ((state[k] & state[l]) == 0))
                        {
                            dp[i][j][l] = max(dp[i][j][l],dp[i - 1][l][k] + need[j]);
                        }
                    }
                }
            }
        }
    }
}
```
1. [牛客周赛32F](https://ac.nowcoder.com/acm/contest/75174/F)
此题不是二进制表示了，有三个状态所以不能直接右移判断是否有合法的操作，需要模拟一个三进制，其余的大题一致，先预处理每一列的合法状态，然后在更新答案前需要判断四个方位是否合理，因为是顺序遍历所以只要和前面的比较即可
```cpp
int g(int i,int st)//状态 st 第 i 位是什么
{
    for(int j=0;j<i;j++) st /= 3;
    return st % 3;
}
int ask(int col,int st)//将 col 转变为 st 需要代价
{
    int cnt = 0;
    for(int i=0;i<n;i++)
    {
        if(g(i,st) != mp[s[i][col - 1]])
            cnt ++;
    }
    return cnt;
}
bool check(int st)//检查 st 是否有左右相同
{
    for(int i=1;i<n;i++)
    {
        if(g(i,st) == g(i - 1,st))
            return 0;
    }
    return  1;
}
bool check(int st1,int st2)
{
    for(int i=0;i<n;i++)
    {
        if(g(i,st1) == g(i,st2))
            return 0;
    }
    return 1;
}
for(int i=0;i<=tot;i++)
{
    if(check(i)) dp[1][i] = ask(1,i);
    else dp[1][i] = 1e9;
}
for(int i=2;i<=m;i++)
{
    for(int j=0;j<=tot;j++)
    {
        dp[i][j] = 1e9;
        if(check(j))
        {
            int cnt = ask(i,j);
            for(int k=0;k<=tot;k++)
            {
                if(check(j,k) && check(k))
                    dp[i][j] = min(dp[i][j],dp[i - 1][k] + cnt);
            }
        }
    }
}
```



### [区间DP](https://www.cnblogs.com/ljy-endl/p/11610549.html)    [习题](https://blog.csdn.net/qq_43472263/article/details/98337401)
区间DP 做法比较固定，`即枚举区间长度，在枚举左端点，之后枚举区间的断点进行转移。 `区间DP在分阶段划分问题时，与阶段中元素出现的顺序和由前一阶段的哪些元素合并而来有很大的关系(例：`f[i][j] = f[i][k] + f[k + 1][j]`
区间类 DP 的特点

- 合并 ：即将两个或多个部分进行整合
- 特征 ： 能将问题分解为两两合并的形式
- 求解 ：对整个问题设最优解，枚举合并点，将问题分解为左右两个部分，最后将左右两个部分的最优值进行合并得到原问题的最优值。
- 环的处理 **破环成链**，长度扩大 2 倍。枚举**每一个起点**表示一个环
```cpp
memset(dp,0,sizeof(dp))//初始dp数组
for(int len=2;len<=n;len++){//枚举区间长度
    for(int i=1;i<n;++i){//枚举区间的起点
        int j=i+len-1;//根据起点和长度得出终点
        if(j>n) break;//符合条件的终点
        for(int k=i;k<=j;++k)//枚举最优分割点
            dp[i][j]=min(dp[i][j],dp[i][k]+dp[k+1][j]+w[i][j]);//状态转移方程
    }
} 
```
[1：石子合并](https://www.luogu.com.cn/problem/P1880)
无环正解 ： 两个长度较小的区间上的信息向一个更长的区间发生**转移**，划分点 `k` 就是转移的**决策**，区间长度`len`就是 DP 的**阶段**，根据动态规划“选择最小的能覆盖状态空间的维度集合”的思想，可以只用左、右端点表示 DP 的状态。
```cpp
sum[i]//从第 1 堆到第 i 堆石子数总和
fmax[i][j],fmin[i][j]//将从第 i 堆石子合并到第 j 堆石子的最大得分，最小得分
fmax[i][j] = max{famx[i][k]+ fmax[k + 1][j] + sum[j] - sum[i - 1]}
```
**破环成链   从每一个点开始长度为 n 表示一个完整的环，求结果**
```cpp
void solve()
{
    cin >> n;
    pre(i,1,n)
        cin >> a[i],a[i + n] = a[i];//环到链
    pre(i,1,2*n)
        s[i] = s[i - 1] + a[i];
    vector<vector<int>> fi(2 * n + 1,vector<int>(2 * n + 1,inf)),fa(2 * n + 1,vector<int>(2 * n + 1,0));
    pre(i,1,2 * n) fi[i][i] = fa[i][i] = 0;
    for(int l = 2;l <= n;l++)//区间长度
    {
        for(int i=1;i + l - 1 <= 2 * n;i++)//左端点
        {
            int j = i + l - 1;//右端点
            for(int k=i;k<j;k++)
            {
                fi[i][j] = min(fi[i][j],fi[i][k] + fi[k + 1][j]);
                fa[i][j] = max(fa[i][j],fa[i][k] + fa[k + 1][j]);
            }
            fi[i][j] += s[j] - s[i - 1];
            fa[i][j] += s[j] - s[i - 1];
        }
    }
    int mi = inf,mx = 0;
    pre(i,1,n)
        mi = min(mi,fi[i][i + n - 1]),mx = max(mx,fa[i][i + n - 1]);
    cout << mi << endl << mx << endl;
}
```
[2.圆](https://ac.nowcoder.com/acm/contest/75768/D)
循环长度，循环左端点，遍历线段左端点对应的右端点，判断是否在区间内还是不相交区间
```cpp
void solve()
{
    cin >> n >> m;
    vector<vector<array<int,2>>> g(2 * n + 1);
    int s = 0;
    pre(i,1,m)
    {
        int x,y,z;
        cin >> x >> y >> z;
        if(x > y) swap(x,y);
        g[x].push_back({y,z});
        g[y].push_back({x + n,z});
        s += z;
    }
    vector<vector<int>> dp(n * 2 + 2,vector<int> (2 * n + 2,0));
    for(int i=2;i<=n * 2;i++)//区间长度
    {
        for(int l = 1;l + i - 1 <= 2 * n;l++)//区间左端点
        {
            int r = l + i - 1;//右端点
            dp[l][r] = max(dp[l + 1][r],dp[l][r - 1]);
            for(auto L : g[l])
            {
                int u = L[0],w = L[1];
                if(u > r) continue;
                if(u + 1 < r) w += dp[u + 1][r];
                if(u - 1 > l + 1)
                    w += dp[l + 1][u - 1];
                dp[l][r] = max(dp[l][r],w);
            }
        }
    }
    int ans = 0;
    pre(i,1,n)
        ans = max(dp[i][i + n - 1],ans);
    cout << s - ans << endl;
}
```
**在第二层循环内最好加一个自然状态转移**`dp[i][j] = ___(dp[i + 1][j],dp[i][j - 1]`某些情况下不加会导致错误，加上肯定无错误！！！！！！！！！！！


### 换根 DP
换根 DP 的转移在于相邻两个顶点之间，以“树上的做优节点到根节点的距离为例” `dfs`一遍可以求出一点为根的答案，关键在于**转移**，注意到：如果将根节点转移到当前根节点的相邻节点，`dp[b] = dp[a] - size[b] +  n - size[b]`，代表着将  b 节点的子儿子到 a 节点之间的距离减去，属于 a 节点但不属于 b 节点的儿子距离加一，表示到 b 节点

```cpp
void dfs(int u,int fa,int deep)
{
    for(auto L : g[u])
    {
        if(L != fa)
        {
            dfs(L,u,deep + 1);
            s[u] += s[L];
        }
    }
    sum += w[u] * deep;
    s[u] += w[u];
}
int ans ,pp;
void fun(int u,int fa)
{
    ans = min(ans,sum);
    for(auto L : g[u])
    {
        if(L != fa)
        {
            sum -= s[L];
            sum += pp - s[L];
            fun(L,u);
            sum -= pp - s[L];
            sum += s[L];
        }
    }
}
```
[Atc 348 E](https://atcoder.jp/contests/abc348/tasks/abc348_e)







## 莫队

### 学习目标：

莫队
> **普通莫队**的分块大小为$\sqrt{n}$，**带修莫队**最佳大小为$\sqrt[4]{n^3}$。
> 奇偶排序是第二关键字是左端点所在块的奇偶排序右端点
> 注意 : 莫队初始化的时候 L= 1,R = 0,否则容易出现边界问题,先左右指针移动,在时间戳移动,指针压缩时注意先增和后增 !!!
> **回滚莫队的数组需要开大一点不然就会 WA ，**而且左右指针移动的数组一定要在重复下一次操作前清零（不在同一块了）。
> 卡常：尽量不要使用STL，氧气$O~3~$优化不能少，快读不加可能会被卡常
---
卡常优化 ： 
```cpp
#pragma GCC optimize (3)
#pragma GCC optimize ("Ofast")
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
//不使用STL
```



### [普通莫队](https://www.luogu.com.cn/problem/P1494)
此题需要找出公式转换，求得是区间内取出两个数的颜色相同的概率，则 ： a + b + c + ... = L,ans = a * (a - 1) + b *(b - 1)... /( L * (L- 1)) ，即是ans = $a^2 + b^2 + c^2+... - L /  (L*(L-1))$
```c++
const int N = 1e6 +10;
int n,m;
int a[N],ble[N],cnt[N];
struct node 
{
    int l,r,id;
    bool operator<(node w)
    {
        return (ble[l] ^ ble[w.l] ? ble[l] < ble[w.l] : (ble[l] & 1) ? r < w.r : r > w.r);
    }
}q[N],s[N];
void solve()
{
    cin >> n >> m;
    int size = sqrt(n);
    pre(i,1,n)
    {
        cin >> a[i];
        ble[i] = (i - 1) / size + 1;
    }
    int len = ble[n];
    pre(i,1,m)
    {
        cin >> q[i].l >> q[i].r;
        q[i].id = i;
    }
    sort(q + 1,q + 1 + m);
    int l = 1,r = 0,ans = 0;
    pre(i,1,m)
    {
        int ql = q[i].l,qr = q[i].r,id = q[i].id;
        while(l < ql) ans -= 2 * cnt[a[l ++]] -- - 1;//左指针右移删除操作
        while(l > ql) ans += 2 * cnt[a[-- l]] ++ + 1;
        while(r < qr) ans += 2 * cnt[a[++ r]] ++ + 1;
        while(r > qr) ans -= 2 * cnt[a[r --]] -- - 1;
        if(ql == qr)
        {
            s[id] = {0,1};
            continue;
        }
        s[id].l = ans - (qr - ql + 1);
        s[id].r = (qr - ql + 1) * (qr - ql);
        int d = __gcd(s[id].l,s[id].r);
        s[id].l /= d,s[id].r /= d;
    }
    pre(i,1,m)
        cout << s[i].l << "/" << s[i].r << endl;
}
```

#### 普通莫队的扩展
[莫队+模拟可任意删除栈](https://www.luogu.com.cn/problem/CF1000F?contestId=152942)
本题非常卡常，奇偶排序，快读，氧气优化都要使用，不能使用STL
关键点在于模拟可以在任意位置删除的栈，代表存放莫队中只出现一次的元素，没有这类元素，输出0
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

### 回滚莫队
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

#### [只加不删回滚莫队](https://www.luogu.com.cn/problem/AT_joisc2014_c)

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
int ans[N];
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
    int tot = unique(inp + 1,inp + n + 1) - inp - 1;
    for(int i=1;i<=n;i++)
        typ[i] = lower_bound(inp + 1,inp + tot + 1,a[i]) - inp;//第一个大于等于前一个-最后一个小于
    for(int i=1;i<=m;i++)
    {
        int l,r;
        cin >> l >> r;
        q[i] = {l,r,i};
    }
    sort(q + 1,q  + 1 + m);//排序：按照左端点的块升序和右端点为两个键值排序。
    int i = 1;//枚举询问到的区间
    for(int k=0;k<=bnum;k++)//枚举每一个块
    {
        int l = rb[k] + 1,r = rb[k];//初始化，将左端点初始化为块右端点加一，右端点为块右端点--空区间
        int now = 0;
        fill(cnt,cnt + 1 + n,0);
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

#### [只删不加的回滚莫队](https://www.cnblogs.com/Parsnip/p/10969989.html#3719129404)

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
[模板](https://www.luogu.com.cn/problem/P5906)
求区间内两相同点之间距离的最大值
```cpp
#include<bits/stdc++.h>
using namespace std;
#define _ ios::sync_with_stdio(0);cin.tie(0);cout.tie(0);
#define endl '\n'
#define int long long 
#define pre(i,a,b) for(int i=a;i<=b;i++)
const int N = 1e6 + 10;
int n,m;
int a[N],iph[N],typ[N];
int ble[N],L[N],R[N],ans[N];
int cnt[N];
int f[N],la[N],cnt1[N],cnt2;
struct query
{
    int l,r,id;
    bool operator<(query w)
    {
        return (ble[l] ^ ble[w.l] ? ble[l] < ble[w.l] : r < w.r);
    }
}q[N];

void solve()
{
    cin >> n;
    pre(i,1,n)
    {
        cin >> a[i];
        iph[i] = a[i];
    }
    sort(iph + 1,iph + 1 + n);
    int tot = unique(iph + 1,iph + 1 + n) - iph - 1;
    pre(i,1,n)
        typ[i] = lower_bound(iph + 1,iph + 1 + tot,a[i]) - iph;
    cin >> m;
    pre(i,1,m)
    {
        cin >> q[i].l >> q[i].r;
        q[i].id = i;
    }
    int size = sqrt(n);
    int bnum = ceil(1.0 * n / size);
    pre(i,1,bnum)
    {
        L[i] = (i - 1) * size + 1,R[i] = i * size;
        pre(j,L[i],R[i])
            ble[j] = i;
    }
    R[bnum] = n;
    sort(q + 1,q + 1 + m);
    int i = 1;
    for(int k=1;k<=bnum;k++)
    {
        int l = R[k] + 1,r = R[k],now = 0;
        cnt2 = 0;
        for(;ble[q[i].l] == k;i++)
        {
            int ql = q[i].l,qr = q[i].r,id = q[i].id,tmp = 0;
            if(ble[ql] == ble[qr])
            {
                for(int j=ql;j<=qr;j++)
                    cnt[typ[j]] = 0;
                for(int j=ql;j<=qr;j++)
                {
                    if(!cnt[typ[j]])
                        cnt[typ[j]] = j;
                    else tmp = max(tmp,j - cnt[typ[j]]);
                }
                ans[id] = tmp;
                continue;
            }
            //右指针
            while(r < qr)
            {
                ++ r;
                la[typ[r]] = r;
                if(!f[typ[r]])
                    f[typ[r]] = r,cnt1[++ cnt2] = typ[r];
                now = max(now,r - f[typ[r]]);
            }
            tmp = now;
            while(l > ql)
            {
                l --;
                if(la[typ[l]])
                    now = max(now,la[typ[l]] - l);
                else la[typ[l]] = l;
            }
            ans[id] = now;
            while(l < R[k] + 1)
            {
                if(la[typ[l]] == l) la[typ[l]] = 0;
                l ++;
            }
            now = tmp;   
        }
        pre(j,1,cnt2)
                la[cnt1[j]] = f[cnt1[j]] = 0;
    }
    pre(i,1,m)
        cout << ans[i] << endl;
}

signed main()
{
    _;
    int t = 1;
    // cin >> t;
    while(t --)
        solve();
}
```
> 初始化分块的第二种方法
```cpp
	int len=sqrt(n); //块长
	for(int i=1;i<=n;i++) 
	{
		cin >> a[i];
		ble[i] = (i - 1) / len + 1; //b记录每个下标是在哪个块中的
	}
	bnum = b[n]; //总块数，块的左右区间还是同理(i - 1) * size + 1 和 i * size
```

