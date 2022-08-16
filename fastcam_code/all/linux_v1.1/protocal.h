#ifndef __PROTOCAL_H__
#define __PROTOCAL_H__

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <sys/types.h>
#include <fcntl.h>

#define MAXLENGTH 1024

struct DataStruct
{
    unsigned char ti;

    unsigned char data[MAXLENGTH];

}__attribute__((packed));




struct FrameStruct
{
    unsigned char head;

    unsigned int source_address;

    unsigned int destin_address;

    unsigned short length;

    struct DataStruct data_struct;

    unsigned char tail;
    
}__attribute__((packed));


typedef struct FrameStruct Frame;

typedef struct DataStruct Data;


#define FRAME_LENGTH    1024

#define HEAD    0xEA;
#define TAIL    0x16;


/*在linux下测试，ip都是127.0.0.1
后面更换设备的时候把宏定义换掉即可
*/
// #define SA      0xAC140A07 //这里换成了172.20.10.7，我的wifi
#define SA      0XC0A808B4 //这里是192.168.8.180.安工的wifi
#define DA      0XC0A80875 //192.168.8.117 安工的wifi
// #define SA      0x7F000001
// #define DA      0x7F000001

#define SA_RESPONSE     0x7F000001
#define DA_RESPONSE     0x7F000001

#define PORT    54321

#define TI_READLOG_REQ  0X01
#define TI_READLOG_RES  0X02
#define TI_READLOG_ACK  0x03

#define TI_READVIDEO_REQ    0X04
#define TI_READVIDEO_RES    0X05
#define TI_READVIDEO_ACK    0X06

#define TI_PARAM_CHANGE_ACK 0X0F

#define LENGTH_READLOG_REQ  0X0001
#define LENGTH_READLOG_RES  0X0001
#define LENGTH_READLOG_ACK  0X0001

#define LENGTH_READVIDEO_REQ 0X001C
#define LENGTH_READVIDEO_RES 0X001C
#define LENGTH_READVIDEO_ACK 0X0001

#define LENGTH_PARAMCHANGE_ACK 0X0001

#define LENGTH_INT_READLOG_REQ  1
#define LENGTH_INT_READLOG_RES  1
#define LENGTH_INT_READLOG_ACK  1

#define LENGTH_INT_READVIDEO_REQ    28
#define LENGTH_INT_READVIDEO_RES    28
#define LENGTH_INT_READVIDEO_ACK    1

#define LENGTH_INT_PARAM_CHANGE_ACK 1

#define TOTAL_LENGTH_INT_READLOG_REQ  13
#define TOTAL_LENGTH_INT_READLOG_RES  13
#define TOTAL_LENGTH_INT_READLOG_ACK  13

#define TOTAL_LENGTH_INT_READVIDEO_REQ  40
#define TOTAL_LENGTH_INT_READVIDEO_RES  40
#define TOTAL_LENGTH_INT_READVIDEO_ACK  13

#define TOTAL_LENGTH_PARAM_CHANG_ACK    13

#define LENGTH_FILE_NAME    27

int Int2String(int num, char* buf, int count);

char* int2char(unsigned int number);

int GetFrameLength(Frame* frame);

void PutFrame2Buffer(Frame* frame, unsigned char* buffer, int frame_length_int);

void Response2Serv(int socket);

void ReadLogReqFrameAssemble(Frame* frame_readlog_req);

void ReadLogResFrameAssemble(Frame* frame_readlog_res);

void ServerParamSolve(int socket, unsigned char* buffer_recv, int frame_length_total);

int LengthAssembel(unsigned char length_high, unsigned char length_low);

void GetReadLogReqBuffer(int socket, Frame* frame_readlog_req);

void GetReadVideoReqBuffer(int client_socket);

void GetReadVideoACKBuffer(int client_socket);

void RecvReadLogResponse(int client_socket);

void RecvReadVideoResResponse(int client_socket);

void GetReadLogAckBuffer(int client_socket);

void ReadLogAckFrameAssemble(Frame* frame_readlog_ack);

void ReadVideoReqFrameAssemble(Frame* frame_video_req);

void ReadVideoResFrameAssembel(Frame* frame_video_res, unsigned char* buffer);

int GetFrameLengthFromBuffer(unsigned char* buffer);

void ParamChangeAckFrameAssemble(Frame* frame);

void PutParmaChangeFrame2Buffer(int socket);

#endif