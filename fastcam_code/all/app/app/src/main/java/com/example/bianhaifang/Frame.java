package com.example.bianhaifang;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PrivilegedAction;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import android.util.Log;

import kotlin.jvm.internal.PropertyReference0Impl;

public class Frame
{
    private final byte HEAD = (byte) 0XEA;
    
    private final byte TAIL = 0x16;
    
    /*这里的IP是要跟随环境来更换的*/
//    private int SA = 0x7F000001; //172.20.10.7
//
//    private int DA = 0x7F000001;

    private final int SA = 0xC0A80875; //192.168.8.117
    
    private final int DA = 0xC0A808A9; //192.168.8.169
    
//    private int SA_RESPONSE = 0X7F000001;
//
//    private int DA_RESPONSE = 0X7F000001;

    private final byte TI_READLOG_REQ = 0X01;

    private final byte TI_READLOG_RES = 0X02;

    private final byte TI_READLOG_ACK = 0X03;

    private final byte TI_READVIDEO_REQ = 0X04;

    private final byte TI_READVIDEO_RES = 0X05;

    private final byte TI_READVIDEO_ACK = 0X06;

    private final byte TI_PHOTO_CONTROL = 0X0F;

    private final byte TI_VIDEO_CONTROL = 0X10;

    private final short LENGTH_RES = 0X0001;

    private final short LENGTH_REQ = 0X0001;

    private final short LENGTH_ACK = 0X0001;

    private final short LENGTH_VIDEO_REQ = 0X001C;

    private final short LENGTH_VIDEO_RES = 0X001C;

    private final short LENGTH_VIDEO_ACK = 0X0001;

    private final short LENGTH_PARAM_CHANGE = 0x0002;

    private final short LENGTH_PHOTO_CONTROL = 0X0001;

    private final short LENGTH_VIDEO_CONTROL = 0X0001;

    private int FRAME_BUF_LENGTH = 13;


    class FrameStruct
    {
        byte head;

        int source_address;

        int destin_address;

        short length;

        DataStruct dataStruct = new DataStruct();

        byte tail;
    }

    class DataStruct
    {
        byte ti;

        public byte[] date = {0x00};
    }


    byte[] ReadLogResFrameAssemble()
    {
        FrameStruct frame_struct = new FrameStruct();
        
        frame_struct.head = HEAD;
        
        frame_struct.source_address = SA;

        frame_struct.destin_address = DA;

        frame_struct.length = LENGTH_RES;

        frame_struct.dataStruct.ti = TI_READLOG_RES;

        frame_struct.tail = TAIL;

        byte[] buffer = Frame2Buffer(frame_struct, 1);

        return buffer;
    }


    byte[] ReadLogReqFrameAssemble()
    {
        FrameStruct frame_struct = new FrameStruct();

        frame_struct.head = HEAD;

        frame_struct.source_address = SA;

        frame_struct.destin_address = DA;

        frame_struct.length = LENGTH_REQ;

        frame_struct.dataStruct.ti = TI_READLOG_REQ;

        frame_struct.tail = TAIL;

        byte[] buffer = Frame2Buffer(frame_struct, 1);

        return buffer;
    }

    byte[] ReadLogACKFrameAssemble()
    {
        FrameStruct frame_struct = new FrameStruct();

        frame_struct.head = HEAD;

        frame_struct.source_address = SA;

        frame_struct.destin_address = DA;

        frame_struct.length = LENGTH_ACK;

        frame_struct.dataStruct.ti = TI_READLOG_ACK;

        frame_struct.tail = TAIL;

        byte[] buffer = Frame2Buffer(frame_struct, 1);

        return buffer;
    }

    byte[] ReadVideoAckFrameAssemble(String string)
    {
        byte[] buffer = string.getBytes();

        FrameStruct frame_struct = new FrameStruct();

        frame_struct.head = HEAD;

        frame_struct.source_address = SA;

        frame_struct.destin_address = DA;

        frame_struct.length = LENGTH_VIDEO_ACK;

        frame_struct.dataStruct.ti = TI_READVIDEO_ACK;

        frame_struct.dataStruct.date =buffer.clone();

        frame_struct.tail = TAIL;

        byte[] buffer_rst = Frame2Buffer(frame_struct, 1);

        return buffer_rst;
    }


    byte[] ReadVideoReqFrameAssemble(String string)
    {
        byte[] buffer = string.getBytes();

        FrameStruct frame_struct = new FrameStruct();

        frame_struct.head = HEAD;

        frame_struct.source_address = SA;

        frame_struct.destin_address = DA;

        frame_struct.length = LENGTH_VIDEO_REQ;

        frame_struct.dataStruct.ti = TI_READVIDEO_REQ;

        frame_struct.dataStruct.date =buffer.clone();

        frame_struct.tail = TAIL;

        byte[] buffer_rst = Frame2Buffer(frame_struct, 28);

        return buffer_rst;
    }

    byte[] PhotoControlFrameAssemble()
    {
        FrameStruct frame_struct = new FrameStruct();

        frame_struct.head = HEAD;

        frame_struct.source_address = SA;

        frame_struct.destin_address = DA;

        frame_struct.length = LENGTH_PHOTO_CONTROL;

        frame_struct.dataStruct.ti = TI_PHOTO_CONTROL;

        frame_struct.tail = TAIL;

        byte[] buffer_rst = Frame2Buffer(frame_struct, 1);

        return buffer_rst;
    }

    byte[] VideoControlFrameAssemble()
    {
        FrameStruct frame_struct = new FrameStruct();

        frame_struct.head = HEAD;

        frame_struct.source_address = SA;

        frame_struct.destin_address = DA;

        frame_struct.length = LENGTH_VIDEO_CONTROL;

        frame_struct.dataStruct.ti = TI_VIDEO_CONTROL;

        frame_struct.tail = TAIL;

        byte[] buffer_rst = Frame2Buffer(frame_struct, 1);

        return buffer_rst;
    }




    byte[] ChangeParamFrameAssemble(String string)
    {
        byte ti = GetTi(string);

        byte value = GetValue(string);

        FrameStruct frame_struct = new FrameStruct();

        frame_struct.head = HEAD;

        frame_struct.source_address = SA;

        frame_struct.destin_address = DA;

        frame_struct.length = LENGTH_PARAM_CHANGE;

        frame_struct.dataStruct.ti = ti;

        frame_struct.dataStruct.date[0] = value;

        frame_struct.tail = TAIL;

        byte[] buffer = Frame2Buffer(frame_struct, 2);

        return buffer;
    }



    byte GetTi(String string)
    {
        String string_ti = string.substring(0, 2);

        byte byte_ti  = String2Ti(string_ti);

        return byte_ti;
    }

    byte GetValue(String string)
    {
        String string_value = string.substring(2);

        int int_value = Integer.parseInt(string_value);

        Log.e("Frame", "the int_value = " + int_value + "--------------");

        String hex = Integer.toHexString(int_value);

        Log.e("Frame", "the hex = " + hex + "--------------");

        byte byte_value = (byte) int_value;

        Log.e("Frame", "byte_value = " + byte_value + "--------------");

        return byte_value;

    }

    byte String2Ti(String string)
    {
        if(string.equals("07"))
        {
            return 0x07;
        }

        if(string.equals("08"))
        {
            return 0x08;
        }

        if(string.equals("09"))
        {
            return 0x09;
        }

        if(string.equals("0A"))
        {
            return 0x0A;
        }

        if(string.equals("0B"))
        {
            return 0x0B;
        }

        if(string.equals("0C"))
        {
            return 0x0C;
        }

        if(string.equals("0D"))
        {
            return 0x0D;
        }

        if(string.equals("0E"))
        {
            return 0x0E;
        }

        /*都不对的情况下才返回0x00*/
        return 0x00;
    }


    /*这里的length，是报文里length那一栏里，length的int值*/
    byte[] Frame2Buffer(FrameStruct frame_struct, int length)
    {
        byte[] buffer = new byte[1024];

        buffer[0] = (byte)(frame_struct.head & 0xff);

        buffer[1] = (byte) (((frame_struct.source_address) >> 24) & 0xff);

        buffer[2] = (byte) (((frame_struct.source_address) >> 16) & 0xff);

        buffer[3] = (byte) (((frame_struct.source_address) >> 8) & 0xff);

        buffer[4] = (byte) (((frame_struct.source_address)) & 0xff);

        buffer[5] = (byte) (((frame_struct.destin_address) >> 24) & 0xff);

        buffer[6] = (byte) (((frame_struct.destin_address) >> 16) & 0xff);

        buffer[7] = (byte) (((frame_struct.destin_address) >> 8) & 0xff);

        buffer[8] = (byte) ((frame_struct.destin_address) & 0xff);

        buffer[9] = (byte)(((frame_struct.length) >> 8) & 0xff);

        buffer[10] = (byte)((frame_struct.length) & 0xff);

        buffer[11] = (byte)(frame_struct.dataStruct.ti);

        for(int count = 0; count < (length - 1); ++count)
        {
            buffer[12 + count] = frame_struct.dataStruct.date[count];
        }

        buffer[11 + length] = (byte)(frame_struct.tail);

        return buffer;

    }



}
