`f[i][j]`表示从 i 节点开始**向下**找距离为 j 以内的权值和，从叶子节点开始向上回溯时更新

`d[i][j]`表示从 i 节点开始上下找距离为 j 以内的权值和，由父节点向子节点递归解决，`d[i][j] += d[fa][j - 1] - d[i][j - 2]`

其中 d 初始值为 f，状态转移方程意义为 距离 fa 为 j - 1 内的点，有上下两部分，加上减下，所以减去` d[i][j - 2]`

```cpp
void dfs1(int u,int v)
{
    for(int i=0;i<=k;i++)
        f[u][i] = c[u];
    for(auto L : g[u])
    {
        if(L == v) continue;
        dfs1(L,u);
        for(int i=1;i<=k;i++)
            f[u][i] += f[L][i - 1];
    }
}
void dfs2(int u,int v)//注意此步是由父节点求子界节点，对于 u 的子 L d[L][j] = d[L][j] + d[u][j - 1] - f[L][j - 2]
{//u 通过 j - 1 步，则 L 向上会通过 j 步到达，但是向下部分多算，减去 f[L][j - 2]
    for(auto L : g[u])
    {
        if(L == v) continue;
        for(int i=1;i<=k;i++)
        {
            d[L][i] += d[u][i - 1];
            if(i >= 2)
                d[L][i] -= f[L][i - 2];
        }
        dfs2(L,u);
    }
}
```

