#include "protocal.h"

void ParamChangeAckFrameAssemble(Frame* frame)
{
    Frame frame_temp;

    frame_temp.head = HEAD;

    frame_temp.source_address = SA;

    frame_temp.destin_address = DA;

    frame_temp.length = LENGTH_PARAMCHANGE_ACK;

    frame_temp.data_struct.ti = TI_PARAM_CHANGE_ACK;

    frame_temp.tail = TAIL;

    memcpy(frame, &frame_temp, sizeof(frame_temp));
}

void PutParmaChangeFrame2Buffer(int socket)
{
    Frame frame;

    unsigned char buffer_temp[TOTAL_LENGTH_PARAM_CHANG_ACK];

    ParamChangeAckFrameAssemble(&frame);

    PutFrame2Buffer(&frame, buffer_temp, LENGTH_INT_PARAM_CHANGE_ACK);

    write(socket, buffer_temp, sizeof(buffer_temp));
}