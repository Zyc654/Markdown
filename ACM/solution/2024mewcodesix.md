[2024牛客暑期多校训练营6 - 空気力学の詩 - 博客园 (cnblogs.com)](https://www.cnblogs.com/cjjsb/p/18337327)

## [$A$](https://ac.nowcoder.com/acm/contest/81601/A)  Cake

先手优先取 `1`占比较多的 , 后手优先取 `1` 占比较少的 , 博弈的思想 ,通过回溯的时候确定选取的方式 ,

 注意每一个数据的答案要和自己能取到的最大值比较一下 , 判断从前到后是否可以取到当前数据 

```cpp
auto dfs = [&](auto self,int u,int fa,int sum,int total,int op) -> void
{
    if(u == 1)
        ans[u] = 1e9;
    else ans[u] = 1.0 * sum / total;
    bool flag = 1;
    if(op) f[u] = 1e9;//后手操作,回溯时取最小值 
    else f[u] = 0;//先手取最大值 1 占比最多
    for(auto [x,y] : g[u])
    {
        if(x != fa)
        {
            flag = 0;
            self(self,x,u,sum + y,total + 1, (op ^ 1));
            if(op) f[u] = min(f[u],f[x]);
            else f[u] = max(f[u],f[x]);
        }
    }
    if(flag) f[u] = ans[u];//叶子节点不必回溯直接等于当前权值
    else f[u] = min(f[u],ans[u]);//和当前点的权值取最小,从前到后走不到这么大,去除不合理状态
};
```



---

## [$F Challenge NPC_2$](https://ac.nowcoder.com/acm/contest/81601/F)

遇到模拟多画图 ! ! ! !

通过观察可得只有在一个菊花图的时候必定无解 , 其余情况下都有解 , 对于每一个非菊花图将偶数层节点和奇数层节点分开存储 , 先将偶数层节点加到 `ans` 中 , 在将奇数层节点加到 `ans` 中 , 可以满足这部分一定是可以的

对于直径上两个点的菊花图 , 此时一个点加入到 `one` , 另一个加到 `two` 中即可

对于直径上三个点的菊花图 , 先将中间的点加到 'one' 另外 加到 `two` 其实也是先偶后奇的原则

对于一个点直接加到`ans` 中即可

输出的时候注意 : 先 `one` 在 `ans` 在 `two` , 思考如果先 `one` 在 `two` 在 `ans` , 如果只有一个直径为 3 的菊花图和其他正常图 , 输出顺序会出现原图中的边 , 而不是补图中的边 , 先 `one` 在 `ans` 在 `two`可以很好解决这个问题 .

