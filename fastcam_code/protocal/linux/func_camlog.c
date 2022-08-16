#include "protocal.h"

void ReadLogReqFrameAssemble(Frame* frame_readlog_req)
{

    Frame frame_temp;

    frame_temp.head = HEAD;

    frame_temp.source_address = SA;

    frame_temp.destin_address = DA;

    frame_temp.length = LENGTH_READLOG_REQ;

    frame_temp.data_struct.ti = TI_READLOG_REQ;

    frame_temp.tail = TAIL;

    memcpy(frame_readlog_req, &frame_temp, sizeof(frame_temp));

}

void ReadLogResFrameAssemble(Frame* frame_readlog_res)
{
    Frame frame_temp;

    frame_temp.head = HEAD;

    frame_temp.source_address = SA_RESPONSE;

    frame_temp.destin_address = DA_RESPONSE;

    frame_temp.length = LENGTH_READLOG_RES;

    frame_temp.data_struct.ti = TI_READLOG_RES;

    frame_temp.tail = TAIL;

    memcpy(frame_readlog_res, &frame_temp, sizeof(frame_temp));

}

void ReadLogAckFrameAssemble(Frame* frame_readlog_ack)
{
    Frame frame_temp;

    frame_temp.head = HEAD;

    frame_temp.source_address = SA_RESPONSE;

    frame_temp.destin_address = DA_RESPONSE;

    frame_temp.length = LENGTH_READLOG_ACK;

    frame_temp.data_struct.ti = TI_READLOG_ACK;

    frame_temp.tail = TAIL;

    memcpy(frame_readlog_ack, &frame_temp, sizeof(frame_temp));
}


/*为了简化client的流程写的*/
void GetReadLogReqBuffer(int socket, Frame* frame_readlog_req)
{
    /*组帧*/
    ReadLogReqFrameAssemble(frame_readlog_req);

    /*获取length的int值*/
    int frame_length_int = GetFrameLength(frame_readlog_req);

    /*计算报文总长*/
    int frame_total_length_int = 12 + frame_length_int;

    /*用来存报文的buffer*/
    unsigned char buffer_readlog_req[frame_total_length_int];

    /*把报文放到buffer里*/
    PutFrame2Buffer(frame_readlog_req, buffer_readlog_req, frame_length_int);

    printf("ready to send read log request\n");

    for(int count = 0; count < 12 + frame_length_int; ++count)
    {
        printf("read log requset buffer[%d] = %02hX\n", count, buffer_readlog_req[count]);
    }

    /*向serv发送读取文件目录请求报文*/
    write(socket, buffer_readlog_req, sizeof(buffer_readlog_req));

    printf("read log requset has been send\n");
    printf("\n");
    
}




/*client读取serv回传的目录请求应答*/
void RecvReadLogResponse(int client_socket)
{
    unsigned char buffer[TOTAL_LENGTH_INT_READLOG_RES] = {0x00};

    read(client_socket, buffer, sizeof(buffer));

    for(int count = 0; count < TOTAL_LENGTH_INT_READLOG_RES; ++count)
    {
        printf("read log response buffer[%d] = %02hX\n", count, buffer[count]);

    }

}

/*client给serv发送应答确认*/
void GetReadLogAckBuffer(int client_socket)
{
    Frame frame_readlog_ack;

    /*组帧*/
    ReadLogAckFrameAssemble(&frame_readlog_ack);

    unsigned char buffer[TOTAL_LENGTH_INT_READLOG_ACK];

    PutFrame2Buffer(&frame_readlog_ack, buffer, LENGTH_INT_READLOG_ACK);

    printf("ready to send ACK to serv\n");

    for(int count = 0; count < TOTAL_LENGTH_INT_READLOG_ACK; ++count)
    {
        printf("ACK buffer[%d] = %02hX\n", count, buffer[count]);
    }

    write(client_socket, buffer, sizeof(buffer));

}