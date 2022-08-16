#include "protocal.h"

unsigned char video_name[LENGTH_FILE_NAME];

/*用于serv针对client发来的报文进行解帧*/
void ServerParamSolve(int socket, unsigned char* buffer_recv, int frame_length_total)
{
    printf("ready to start ServerParamSolve\n");

    if (buffer_recv[11] == 0x01)
    {
        printf("ready to send Reaponse frame\n");

        Frame frame_readlog_res;

        ReadLogResFrameAssemble(&frame_readlog_res);

        /*获取长度int值*/
        int frame_length_int = GetFrameLength(&frame_readlog_res);

        /*获取报文总长*/
        int frame_total_length_int = 12 + frame_length_int;

        /*把报文放到buffer里*/
        unsigned char buffer_readlog_res[frame_total_length_int];

        PutFrame2Buffer(&frame_readlog_res, buffer_readlog_res, frame_length_int);

        write(socket, buffer_readlog_res, sizeof(buffer_readlog_res));

        return;
    }

    /*收到的是应答确认的报文，把文件传给app*/
    if (buffer_recv[11] == 0x03)
    {
        printf("ready to send file\n");

        unsigned char buffer[MAXLENGTH];

        memset(buffer, '\0', sizeof(buffer));

        int list_fd = open("./list.dat", O_RDONLY);

        if(list_fd > -1)
        {
            while(read(list_fd, buffer, 1024) > 0)
            {
                write(socket, buffer, strlen(buffer));

                /*保证下一次用来装填文件内容的buffer是空的*/
                memset(buffer, '\0', MAXLENGTH);
            }

            printf("file read has done\n");

            strcpy(buffer, "afz");
            
            write(socket, buffer, strlen(buffer));

            memset(buffer, '\0', 1024);

            printf("end sight has been set\n");
        }
        else
        {
            printf("list.dat open failed\n");
        }

        close(list_fd);

        return;
    }

    /*收到app的video请求报文，回传video请求应答*/
    if(buffer_recv[11] == 0x04)
    {
        memset(video_name, '\0', sizeof(video_name));

        /*在这里添加读取报文中的文件名，并确认文件在系统中存在的操作*/
        unsigned char buffer_name[LENGTH_FILE_NAME];

        memset(buffer_name, '\0', LENGTH_FILE_NAME);

        memcpy(buffer_name, buffer_recv + 12, LENGTH_FILE_NAME);

        printf("buffer_name[0] = %c\n", buffer_name[0]);

        int video_file_fd = open(buffer_name, O_RDONLY);

        if (video_file_fd > -1)
        {
            memcpy(video_name, buffer_name, sizeof(buffer_name));

            printf("video_name[0] = %c\n", video_name[0]);

            Frame frame_video_res;

            ReadVideoResFrameAssembel(&frame_video_res, buffer_recv);  

            unsigned char buffer[TOTAL_LENGTH_INT_READVIDEO_RES];

            PutFrame2Buffer(&frame_video_res, buffer, LENGTH_INT_READVIDEO_RES);

            write(socket, buffer, sizeof(buffer));

            printf("Got the video file\n");
        }
        else
        {
            printf("Do not have the video file get this name\n");
        }

        close(video_file_fd);
        
        return;
    }

    /*收到应答确认，向APP传输视频文件*/
    if(buffer_recv[11] == 0x06)
    {
        printf("start transmit video file\n");

        unsigned char buffer_video[MAXLENGTH];

        memset(buffer_video, '\0', MAXLENGTH);

        printf("ready to open file\n");
        int video_fd = open(video_name, O_RDONLY);
        printf("open file done \n");

        if (video_fd > -1)
        {
            printf("ready to send file \n");


            while(read(video_fd, buffer_video, MAXLENGTH) > 0)
            {
                              
                write(socket, buffer_video, MAXLENGTH);

                memset(buffer_video, '\0', MAXLENGTH);
            }
            printf("send file done\n");

            strcpy(buffer_video, "afz");
            send(video_fd, buffer_video, strlen(buffer_video), 0);
            memset(buffer_video, '\0', 1024);

            close(video_fd);

            printf("write video file done\n");
        }
        else
        {
            printf("got video file failed\n");

            close(video_fd);
        }

        memset(video_name, '\0', sizeof(video_name));     

        return;
    }

    if (buffer_recv[11] == 0x07)
    {
        /*尚未添加功能函数*/
        PutParmaChangeFrame2Buffer(socket);

        printf("param ack has been send\n");
        printf("has recv the mode change frame\n");

        return;
    }

    if (buffer_recv[11] == 0x08)
    {
        /*尚未添加功能函数*/
        PutParmaChangeFrame2Buffer(socket);

        printf("param ack has been send\n");
        printf("has recv the image size frame\n");

        return;
    }


    if (buffer_recv[11] == 0x09)
    {
        /*尚未添加功能函数*/
        PutParmaChangeFrame2Buffer(socket);
        
        printf("param ack has been send\n");
        printf("has recv the video size frame\n");

        return;
    }


    if (buffer_recv[11] == 0x0A)
    {
        /*尚未添加功能函数*/
        PutParmaChangeFrame2Buffer(socket);
        
        printf("param ack has been send\n");
        printf("has recv the number of photo frame\n");

        return;
    }


    if (buffer_recv[11] == 0x0B)
    {
        /*尚未添加功能函数*/
        PutParmaChangeFrame2Buffer(socket);
        
        printf("param ack has been send\n");
        printf("has recv the video time frame\n");
        return;
    }


    if (buffer_recv[11] == 0x0C)
    {
        /*尚未添加功能函数*/
        PutParmaChangeFrame2Buffer(socket);
        
        printf("param ack has been send\n");
        printf("has recv the interval time frame\n");
        return;
    }


    if (buffer_recv[11] == 0x0D)
    {
        /*尚未添加功能函数*/
        PutParmaChangeFrame2Buffer(socket);
        
        printf("param ack has been send\n");
        printf("has recv the PIR sensitive frame\n");
        return;
    }


    if (buffer_recv[11] == 0x0E)
    {
        /*尚未添加功能函数*/
        PutParmaChangeFrame2Buffer(socket);
        
        printf("param ack has been send\n");
        printf("has recv the time stamp frame\n");
        return;
    }

    if (buffer_recv[11] = 0x0F)
    {

        printf("photo control has been set\n");
        return;
    
    }

    if (buffer_recv[11] = 0x10)
    {
        printf("video control has been set\n");
        return;
    
    }

    printf("ServerParamSolve has been excuted\n");
    return;
}



/*把帧组好，放在发送缓冲区里*/
void PutFrame2Buffer(Frame* frame, unsigned char* buffer, int frame_length_int)
{
    
    int frame_total_length_int = 12 + frame_length_int;

    unsigned char buffer_temp[frame_total_length_int];

    buffer_temp[0] = frame->head;

    buffer_temp[1] = ((frame->source_address >> 24) & 0xff);

    buffer_temp[2] = ((frame->source_address >> 16) & 0xff);

    buffer_temp[3] = ((frame->source_address >> 8) & 0xff);

    buffer_temp[4] = ((frame->source_address) & 0xff);

    buffer_temp[5] = ((frame->destin_address >> 24) & 0xff);

    buffer_temp[6] = ((frame->destin_address >> 16) & 0xff);

    buffer_temp[7] = ((frame->destin_address >> 8) & 0xff);

    buffer_temp[8] = ((frame->destin_address) & 0xff);

    buffer_temp[9] = ((frame->length >> 8) & 0xff);

    buffer_temp[10] = (frame->length & 0xff);

    buffer_temp[11] = (frame->data_struct.ti);

    // printf("It's int the for\n");
    for(int count = 0; count < (frame_length_int - 1); ++count)
    {
        buffer_temp[12 + count] = frame->data_struct.data[count]; 
    }

    buffer_temp[11 + frame_length_int] = frame->tail;

    printf("It's in the PutFrame2Buffer frame_legth_int = %d\n", frame_length_int);

    memcpy(buffer, buffer_temp, sizeof(buffer_temp));

}


/*获取报文帧的长度*/
int GetFrameLength(Frame* frame)
{
    int count = 0;

    int length = 0;

    unsigned char frame_int_low = ((frame->length) & 0xff);

    unsigned char frame_int_high = ((frame->length >> 8) & 0xff);

    unsigned char buffer[4] = {0x00, 0x00, frame_int_high, frame_int_low};

    for(count; count < 4; ++count)
    {
        length += (buffer[count] << ((3 - count) * 8));
    }

    return length;
}

/*用于根据buffer里的内容，计算报完里length的int型值*/
int LengthAssembel(unsigned char length_high, unsigned char length_low)
{
    int count = 0;

    int length = 0;

    unsigned char a[4] = {0x00, 0x00, length_high, length_low};

    for(count; count < 4; ++count)
    {
        length += (a[count] << ((3 - count) * 8));
    }

    printf("frame length = %d\n", length);

    return length;
}


int GetFrameLengthFromBuffer(unsigned char* buffer)
{
    int length_int = LengthAssembel(buffer[9], buffer[10]);

    return length_int;
}