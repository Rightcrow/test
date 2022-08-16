#include "protocal.h"

int main()
{
    // /*创建套接字*/
    int serv_sock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    printf("serv_sock = %d\n", serv_sock);

    /*避免出现bind不了的情况*/
    int val = 1;
    int set_sock = setsockopt(serv_sock, SOL_SOCKET, SO_REUSEADDR, (void*)&val, sizeof(int));


    if (serv_sock < 0)
    {
        printf("create socket error: %s(errno: %d)\n", strerror(errno),errno);
        exit(0);
    }

    /*套接字参数结构体*/
    struct sockaddr_in serv_addr;

    memset(&serv_addr, 0, sizeof(serv_addr));

    /*配置socket参数*/
    serv_addr.sin_family = AF_INET;

    char* ret = int2char(SA);

    serv_addr.sin_addr.s_addr = inet_addr(ret);

    serv_addr.sin_port = htons(PORT);

    if(bind(serv_sock, (struct sockaddr*)&serv_addr, sizeof(serv_addr)) < 0)
    {
        printf("bind error: %s(errno: %d)\n",strerror(errno),errno);
        close(serv_sock);
        exit(0);
    }
    else
    {
        printf("bind succuss\n");
    }

    listen(serv_sock, 20);

    struct sockaddr_in communicate_addr;

    socklen_t communicate_addr_size = sizeof(communicate_addr);

    int communicate_sock = accept(serv_sock, (struct sockaddr*)&communicate_addr, &communicate_addr_size);
    
    printf("communicate_sock = %d\n", communicate_sock);

    /*准备已做好，开始接收*/
    unsigned char buffer_recv[MAXLENGTH] = {0x00};


    /*这里有两个条件，才会出发读取的数据分析
    （1）连接没有断开
    （2）我真的收到了数据*/
    while(communicate_sock > -1)
    {
        printf("\n");
        printf("wait for date\n");
        /*这里是保证我确实收到了数据，才往后执行，不然，没有数据的话，会阻塞在这里，一直死循环等待
        到有数据进来之后，再向下执行*/
        while(read(communicate_sock, buffer_recv, sizeof(buffer_recv)) <= 0)
        {

        };

        int length_serv_frame_int = LengthAssembel(buffer_recv[9], buffer_recv[10]);

        for(int count = 0; count < (12 + length_serv_frame_int); ++count)
        {
            printf("Message from client[%d]:%02hX\n", count, buffer_recv[count]);
        }
    
        /*根据收到的报文进行解析，进行下一步动作*/
        ServerParamSolve(communicate_sock, buffer_recv, (12 + length_serv_frame_int));

        memset(buffer_recv, 0x00, sizeof(buffer_recv));
        // sleep(1000);
    }

    close(serv_sock);

    close(communicate_sock);

    return 0;

}