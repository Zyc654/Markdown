## A  [$LCT$](https://ac.nowcoder.com/acm/contest/81599/A)

先找出完全树的根节点 , 正常 `dfs` 预处理出每个节点的深度`dep` , 之后每次询问时 , 由于每次询问的节点一定时当前当前森林的根节点 , 所以最终结果一定会归宿到当前森林某个根上 , 是可以在线更新 并查集祖先实现的 , 添加一个辅助数组 `deep` , 初始值等于 `dep` 每次更新询问`a,b`时 , 更新节点的祖先节点的 `deep[fa] = max({deep[fa],deep[a],deep[b]})`,表示将`fa`的最长路更新为 根节点到 `fa` , 最终的答案就是 `deep[w] - dep[w]` 减去此时完全树的根节点到 `w` 的距离才是 `w` 的最长路径



---



## F $Good Tree$

数学规律题 不补



---



## J [$Zero$](https://ac.nowcoder.com/acm/contest/81599/J)

























