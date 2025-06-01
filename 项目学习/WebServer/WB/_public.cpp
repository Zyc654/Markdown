bool newdir(const string &pathorfilename, bool bisfilename)
{
    // /tmp/aaa/bbb/ccc/ddd
    // 检查目录是否存在，如果不存在，逐级创建子目录
    int pos = 1;      // 不要从0开始，0是根目录/。
    while (true)
    {
        int pos1 = pathorfilename.find('/', pos);
        if (pos1 == string::npos) break;
        string strpathname = pathorfilename.substr(0, pos1);  // 截取目录。

        pos = pos1 + 1;    // 位置后移。
        if (access(strpathname.c_str(), F_OK) != 0) // 如果目录不存在，创建它。
        {
            // 0755是八进制，不要写成755。
            if (mkdir(strpathname.c_str(), 0755) != 0) return false; // 如果目录不存在，创建它。
        }
    }
    // 如果pathorfilename不是文件，是目录，还需要创建最后一级子目录。
    if (bisfilename == false)
    {
        if (access(pathorfilename.c_str(), F_OK) != 0)
        {
            if (mkdir(pathorfilename.c_str(), 0755) != 0) return false;
        }
    }
    return true;
}
// 重命名文件，类似Linux系统的mv命令。
// srcfilename：原文件名，建议采用绝对路径的文件名，dstfilename：目标文件名，建议采用绝对路径的文件名。
// 返回值：true-成功；false-失败，失败的主要原因是权限不足或磁盘空间不够，如果原文件和目标文件不在同一个磁盘分区，重命名也可能失败。
// 注意，在重命名文件之前，会自动创建dstfilename参数中包含的目录。
// 在应用开发中，可以用renamefile()函数代替rename()库函数。
bool renamefile(const string &srcfilename, const string &dstfilename) {
    // 如果原文件不存在，直接返回失败。
    if (access(srcfilename.c_str(), R_OK) != 0) return false;
    // 创建目标文件的目录。
    if (newdir(dstfilename, true) == false) return false;
    // 调用操作和系统的库函数rename重命名文件。
    if (rename(srcfilename.c_str(), dstfilename.c_str()) == 0) return true;
    return false;
}