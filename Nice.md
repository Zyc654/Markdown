## Git

配置 `ssh` 之后注意不要使用`https`的`url`，修改为

```cpp
git remote add origin https://github.com/你的用户名/仓库名.git
git remote set-url origin git@github.com:xxxx/xxx.git//分别为username和仓库名
git remote -v
```

设置邮箱采用`github`给定的隐私邮箱

```cpp
git config --global user.email "xxx"
git config user.name "xxx" //使用自己username
```

切换分支

```cpp
git branch -m xxx1 xxx2 // 切换为 xxx2
git branch -vv
```

建议先拉取下来远端在`push`

```cpp
git pull --rebase origin master 
git push origin branch
```





## 八股

[coding-guide](https://www.yuque.com/coding-guide)

