#include "protocal.h"

/*视频读取报文组帧，要加上文件名*/
void ReadVideoReqFrameAssemble(Frame* frame_video_req)
{
    Frame frame_temp;

    frame_temp.head = HEAD;

    frame_temp.source_address = SA;

    frame_temp.destin_address = DA;

    frame_temp.length = LENGTH_READVIDEO_REQ;

    frame_temp.data_struct.ti = TI_READVIDEO_REQ;

    /*文件名*/
    frame_temp.data_struct.data[0] = 'v';

    frame_temp.data_struct.data[1] = 'i';

    frame_temp.data_struct.data[2] = 'd';

    frame_temp.data_struct.data[3] = 'e';

    frame_temp.data_struct.data[4] = 'o';

    frame_temp.data_struct.data[5] = '_';

    frame_temp.data_struct.data[6] = '1';

    frame_temp.data_struct.data[7] = '9';

    frame_temp.data_struct.data[8] = '7';

    frame_temp.data_struct.data[9] = '0';

    frame_temp.data_struct.data[10] = '0';

    frame_temp.data_struct.data[11] = '1';

    frame_temp.data_struct.data[12] = '0';

    frame_temp.data_struct.data[13] = '1';

    frame_temp.data_struct.data[14] = '_';

    frame_temp.data_struct.data[15] = '0';

    frame_temp.data_struct.data[16] = '9';

    frame_temp.data_struct.data[17] = '_';

    frame_temp.data_struct.data[18] = '4';

    frame_temp.data_struct.data[19] = '9';

    frame_temp.data_struct.data[20] = '_';

    frame_temp.data_struct.data[21] = '3';

    frame_temp.data_struct.data[22] = '1';

    frame_temp.data_struct.data[23] = '.';

    frame_temp.data_struct.data[24] = 'd';

    frame_temp.data_struct.data[25] = 'a';

    frame_temp.data_struct.data[26] = 't';

    frame_temp.tail = TAIL;

    printf("ready to assign the struct\n");

    printf("size of frame_temp = %lu\n", sizeof(frame_temp));

    // *frame_video_req = frame_temp;

    memcpy(frame_video_req, &frame_temp, sizeof(frame_temp));

}

/*视频读取请求应答报文组帧*/
void ReadVideoResFrameAssembel(Frame* frame_video_res, unsigned char* buffer)
{

    int frame_length_int = GetFrameLengthFromBuffer(buffer);

    Frame frame_temp;

    frame_temp.head = HEAD;

    frame_temp.source_address = SA_RESPONSE;

    frame_temp.destin_address = DA_RESPONSE;

    frame_temp.length = LENGTH_READVIDEO_RES;

    frame_temp.data_struct.ti = TI_READVIDEO_RES;

    for(int count = 0; count < (frame_length_int - 1); ++count)
    {
        frame_temp.data_struct.data[count] = buffer[12 + count];
    }

    frame_temp.tail = TAIL;

    memcpy(frame_video_res, &frame_temp, sizeof(frame_temp));

}

/*读取视频请求应答确认报文组帧*/
void ReadVideoACKFrameAssemble(Frame* frame_readvideo_ack)
{
    Frame frame_temp;

    frame_temp.head = HEAD;

    frame_temp.source_address = SA;

    frame_temp.destin_address = DA;

    frame_temp.length = LENGTH_READVIDEO_ACK;

    frame_temp.data_struct.ti = TI_READVIDEO_ACK;

    frame_temp.tail = TAIL;

    memcpy(frame_readvideo_ack, &frame_temp, sizeof(frame_temp));
}

/*client读取serv回传的视频请求应答*/
void RecvReadVideoResResponse(int client_socket)
{
    unsigned char buffer[TOTAL_LENGTH_INT_READVIDEO_RES] = {0x00};

    read(client_socket, buffer, sizeof(buffer));

    for(int count = 0; count < TOTAL_LENGTH_INT_READVIDEO_RES; ++count)
    {
        printf("read video response buffer[%d] = %02hX\n", count, buffer[count]);

    }
}


/*client发给serv视频读取请求*/
void GetReadVideoReqBuffer(int client_socket)
{
    Frame frame_readvideo_req;

    /*组帧,已添加文件名*/
    ReadVideoReqFrameAssemble(&frame_readvideo_req);

    unsigned char buffer[TOTAL_LENGTH_INT_READVIDEO_REQ];

    PutFrame2Buffer(&frame_readvideo_req, buffer, LENGTH_INT_READVIDEO_REQ);

    printf("ready to send video reqest to serv\n");

    for(int count = 0; count < TOTAL_LENGTH_INT_READVIDEO_REQ; ++count)
    {
        printf("video REQ[%d] = %02hX\n", count, buffer[count]);
    }

    printf("sizeof buffer = %lu\n", sizeof(buffer));

    write(client_socket, buffer, sizeof(buffer));

}

/*client发给*/
void GetReadVideoACKBuffer(int client_socket)
{
    Frame frame_readvideo_ack;

    /*组帧*/
    ReadVideoACKFrameAssemble(&frame_readvideo_ack);

    unsigned char buffer[TOTAL_LENGTH_INT_READLOG_ACK];

    PutFrame2Buffer(&frame_readvideo_ack, buffer, LENGTH_INT_READVIDEO_ACK);

    printf("ready to send video ack to serv\n");

    for(int count = 0; count < TOTAL_LENGTH_INT_READVIDEO_ACK; ++count)
    {
        printf("video ACK[%d] = %02hX\n", count, buffer[count]);
    }

    write(client_socket, buffer, sizeof(buffer));

}