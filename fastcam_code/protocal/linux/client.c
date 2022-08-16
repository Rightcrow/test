#include "protocal.h"



int main()
{
    /*创建套接字*/
    int client_socket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

    if (client_socket < 0)
    {
        printf("create socket error: %s(errno: %d)\n", strerror(errno),errno);
        exit(0);
    }

    /*套接字参数结构体*/
    struct sockaddr_in client_addr;

    memset(&client_addr, 0, sizeof(client_addr));

    /*配置socket参数*/
    client_addr.sin_family = AF_INET;

    char* ret = int2char(SA);

    client_addr.sin_addr.s_addr = inet_addr(ret);

    client_addr.sin_port = htons(PORT);

    if(connect(client_socket, (struct sockaddr*)&client_addr, sizeof(client_addr)) < 0)
    {
        printf("connect error: %s(errno: %d)\n",strerror(errno),errno);
        exit(0);
    }
    else
    {
        printf("connect succuss\n");
    }

    /*读取文件目录请求组帧,把组好的帧放到buffer里*/
    Frame* frame_readlog_req = NULL;

    /*这四行是目录读取的函数*/
    // printf("ready to GetReadLogReqBuffer\n");

    // GetReadLogReqBuffer(client_socket, &frame_readlog_req);

    // RecvReadLogResponse(client_socket);

    // GetReadLogAckBuffer(client_socket);

    /*这四行是视频读取的函数*/
    printf("ready to GetReadVedioReqBuffer\n");

    /*client向serv发送读取视频请求报文*/
    GetReadVideoReqBuffer(client_socket);

    RecvReadVideoResResponse(client_socket);

    GetReadVideoACKBuffer(client_socket);

    sleep(10);
         
    close(client_socket);

    exit(0);

}