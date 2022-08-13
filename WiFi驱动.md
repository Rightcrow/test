1. 驱动编译：
    config设置编译器、内核路径、ARCH=arm
    ARCH选择内核的架构(arm x86等)

2. 根据驱动配置相应的linux config

3. 修改sdio的驱动程序就在：内核/driver/mmc/host/[name].c

4. 修改设备数要找到config中对应的dtsi文件

5. 当驱动加载不成功，通过dmesg查看记录

6. 若缺少以来，可先查看system.map，是否有对应符号连接存在

7. 若存在对应符号链接，可以通过grep查找对应.c文件中对应函数，使用别的函数进行 
    替换。