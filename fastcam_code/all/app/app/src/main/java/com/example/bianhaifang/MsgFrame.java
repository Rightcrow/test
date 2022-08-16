package com.example.bianhaifang;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class MsgFrame {
    CRCCheck crcCheck;
    ConnectThread connectThread;
    int SA_DA_len;
    private final byte start_flag =(byte)0xEA;
    private final byte end_flag =(byte)0x16;
    public byte[] SA  = {0};
    public byte[] DA  = {0};
    private final byte ctr_heart_test  = (byte) 0x80;
    private final byte ctr_heart_ack  = (byte) 0x00;
    private final byte ctr_parameter = (byte) 0x81;
    private final byte ctr_parameter_ack  = (byte) 0x01;
    private final byte ctr_real_msg_query = (byte) 0x82;
    private final byte ctr_real_msg_query_ack  = (byte) 0x02;
    private final byte ctr_command = (byte) 0x83;
    private final byte ctr_command_ack  = (byte) 0x03;
    private final byte ctr_file_services = (byte) 0x84;
    private final byte ctr_file_services_ack  = (byte) 0x04;
    private final byte ctr_clock = (byte) 0x87;
    private final byte ctr_clock_ack  = (byte) 0x07;

    private final byte ti_heart_test  = (byte) 0x01;
    private final byte ti_heart_ack  = (byte) 0x02;
    private final byte ti_parameter_search  = (byte) 0x05;
    private final byte ti_parameter_search_ack  = (byte) 0x06;
    private final byte ti_parameter_set  = (byte) 0x07;
    private final byte ti_parameter_set_ack  = (byte) 0x08;
    private final byte ti_real_msg_query = (byte) 0x09;
    private final byte ti_real_msg_query_ack  = (byte) 0x0A;
    private final byte ti_ctr_command = (byte) 0x0B;
    private final byte ti_ctr_command_ack  = (byte) 0x0C;
    private final byte ti_mulu_query  = (byte) 0x0D;
    private final byte ti_mulu_query_ack  = (byte) 0x0E;
    private final byte ti_mulu_msg_transport  = (byte) 0x0F;
    private final byte ti_mulu_msg_transport_ack  = (byte) 0x10;
    private final byte ti_file_write_query  = (byte) 0x11;
    private final byte ti_file_write_query_ack  = (byte) 0x12;
    private final byte ti_file_write_data  = (byte) 0x13;
    private final byte ti_file_write_data_ack  = (byte) 0x14;
    private final byte ti_file_write_active  = (byte) 0x15;
    private final byte ti_file_write_active_ack  = (byte) 0x16;
    private final byte ti_file_read_query  = (byte) 0x17;
    private final byte ti_file_read_query_ack  = (byte) 0x18;
    private final byte ti_file_read_data  = (byte) 0x19;
    private final byte ti_file_read_data_ack  = (byte) 0x1A;
    private final byte ti_clock_right_time  = (byte) 0x1B;
    private final byte ti_clock_right_time_ack  = (byte) 0x1C;
    private final byte ti_clock_query  = (byte) 0x1D;
    private final byte ti_clock_query_ack  = (byte) 0x1E;

    public byte[] err;
    public byte[] pv;
    public byte[] PID;
    public byte[] cn;
    public byte[] did;

    public MsgFrame(Context context){
        String local_ip = getWifiRouteIPAddress(context);
        SA = local_ip.getBytes();
        String remote_ip = "192.168.0.3";
        DA = remote_ip.getBytes();
        SA_DA_len = SA.length + DA.length;
        Log.e("SA_DA_len ", String.valueOf(SA_DA_len));
    }

    public byte[] getheart_ack_err()
    { return err; };
    public byte[] getparameter_ack_err()
    { return err; };
    public byte[] getparameter_ack_pv()
    { return pv; };
    public byte[] getparameter_set_ack_pid()
    { return PID; };
    public byte[] getctr_command_ack_cn()
    { return cn; };
    public byte[] getmulu_query_ack_did()
    { return did; };
    /**
     * wifi获取 已连接网络路由  路由ip地址
     * @param context
     * @return
     */
    private static String getWifiRouteIPAddress(Context context) {
        WifiManager wifi_service = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();
        //        WifiInfo wifiinfo = wifi_service.getConnectionInfo();
        //        System.out.println("Wifi info----->" + wifiinfo.getIpAddress());
        //        System.out.println("DHCP info gateway----->" + Formatter.formatIpAddress(dhcpInfo.gateway));
        //        System.out.println("DHCP info netmask----->" + Formatter.formatIpAddress(dhcpInfo.netmask));
        //DhcpInfo中的ipAddress是一个int型的变量，通过Formatter将其转化为字符串IP地址
        String routeIp = Formatter.formatIpAddress(dhcpInfo.ipAddress);
        Log.e("route ip", "wifi route ip：" + routeIp);
        return routeIp;
    }
    void SolveParam(byte[] rxxBuff){
        int len = 1 + SA_DA_len+1+2+1;
        switch(rxxBuff[len-4]){
            case ctr_heart_ack:
                if((rxxBuff[len-1]) == ti_heart_ack){
                    Log.e("heart_ack ", "rcv"+(rxxBuff[len-1]));
                    err[0] = rxxBuff[len];
                }
                break;
            case ctr_parameter_ack:
                if((rxxBuff[len-1]) == ti_parameter_search_ack){
                    Log.e("parameter_search_ack ", "rcv"+(rxxBuff[len-1]));
                    err[0] = rxxBuff[len];
                    pv[0] = rxxBuff[len + 5];
                    pv[1] = rxxBuff[len + 9];
                    pv[2] = rxxBuff[len + 13];
                    pv[3] = rxxBuff[len + 17];
                    pv[4] = rxxBuff[len + 21];
                    pv[5] = rxxBuff[len + 25];
                    pv[6] = rxxBuff[len + 29];
                    pv[7] = rxxBuff[len + 33];
                }
                if((rxxBuff[len-1]) == ti_parameter_set_ack){
                    Log.e("parameter_search_ack ", "rcv"+(rxxBuff[len-1]));
                    err[0] = rxxBuff[len];
                    PID[1] = rxxBuff[len+2];
                    PID[2] = rxxBuff[len+3];
                    PID[3] = rxxBuff[len+4];
                    PID[4] = rxxBuff[len+5];
                    PID[5] = rxxBuff[len+6];
                    PID[6] = rxxBuff[len+7];
                    PID[7] = rxxBuff[len+8];
                    PID[8] = rxxBuff[len+9];
                    PID[9] = rxxBuff[len+10];
                    PID[10] = rxxBuff[len+11];
                    PID[11] = rxxBuff[len+12];
                    PID[12] = rxxBuff[len+13];
                    PID[13] = rxxBuff[len+14];
                    PID[14] = rxxBuff[len+15];
                    PID[15] = rxxBuff[len+16];

                }
                break;
            case ctr_real_msg_query_ack:
                break;
            case ctr_command_ack:
                if((rxxBuff[len-1]) == ti_ctr_command_ack){
                    Log.e("parameter_search_ack ", "rcv"+(rxxBuff[len-1]));
                    err[0] = rxxBuff[len];
                    cn[0] = rxxBuff[len+1];
                    cn[1] = rxxBuff[len+2];
                }
                break;
            case ctr_file_services_ack:
                if((rxxBuff[len-1]) == ti_mulu_query_ack){
                    Log.e("parameter_search_ack ", "rcv"+(rxxBuff[len-1]));
                    err[0] = rxxBuff[len];
                    did[0] = rxxBuff[len+1];
                    did[1] = rxxBuff[len+2];
                    did[0] = rxxBuff[len+3];
                    did[1] = rxxBuff[len+4];
                }
                break;
            case ctr_clock_ack:
                break;
        }

    }
    public ByteBuffer ACK_heart_test(){
        ByteBuffer buff = ByteBuffer.allocate(256);
        ByteBuffer buff_crc = ByteBuffer.allocate(128);

        buff_crc.put(SA);
        buff_crc.put(DA);
        buff_crc.put(ctr_heart_ack);
        buff_crc.put((byte) 0x01);
        buff_crc.put((byte) 0x00);
        buff_crc.put(ti_heart_ack);
        buff_crc.flip();
        byte[] buff1 = new byte[buff_crc.limit()-buff_crc.position()];
        for(int i = 0; i < buff_crc.limit()-buff_crc.position();i++){
            buff1[i] = buff_crc.get(i);
        }

        buff.put(start_flag);
        buff.put(buff1);
        buff.put(CRCCheck.CRC16(buff1));
        buff.put(end_flag);
        buff.flip();
        byte[] buff2 = new byte[buff.limit()-buff.position()];
        for(int i = 0; i < buff.limit()-buff.position();i++){
            buff2[i] = buff.get(i);
        }

        return buff;
    }
    public ByteBuffer send_heart_test(){
        ByteBuffer buff = ByteBuffer.allocate(256);
        ByteBuffer buff_crc = ByteBuffer.allocate(128);

        buff_crc.put(SA);
        buff_crc.put(DA);
        buff_crc.put(ctr_heart_test);
        buff_crc.put((byte) 0x01);
        buff_crc.put((byte) 0x00);
        buff_crc.put(ti_heart_test);
        buff_crc.flip();
        byte[] buff1 = new byte[buff_crc.limit()-buff_crc.position()];
        for(int i = 0; i < buff_crc.limit()-buff_crc.position();i++){
            buff1[i] = buff_crc.get(i);
        }

        buff.put(start_flag);
        buff.put(buff1);
        buff.put(CRCCheck.CRC16(buff1));
        buff.put(end_flag);
        buff.flip();
        byte[] buff2 = new byte[buff.limit()-buff.position()];
        for(int i = 0; i < buff.limit()-buff.position();i++){
            buff2[i] = buff.get(i);
        }
        Log.e("tx", "buff：" + Arrays.toString(buff2));
        return buff;
    }
    public ByteBuffer parameter_query(){
        ByteBuffer buff = ByteBuffer.allocate(256);
        ByteBuffer buff_crc = ByteBuffer.allocate(128);
        int pn = 0;

        byte[] pid = {0,1,0,2,0,3,0,4,0,5};
        Log.e("tx", "databuff：" + Arrays.toString(pid));
        pn = (pid.length)/2;

        buff_crc.put(SA);
        buff_crc.put(DA);
        buff_crc.put(ctr_parameter);
        buff_crc.put((byte) 0x01);
        buff_crc.put((byte) 0x00);
        buff_crc.put(ti_parameter_search);
        buff.put((byte) pn);
        buff.put(pid);
        buff_crc.flip();
        byte[] buff1 = new byte[buff_crc.limit()-buff_crc.position()];
        for(int i = 0; i < buff_crc.limit()-buff_crc.position();i++){
            buff1[i] = buff_crc.get(i);
        }

        buff.put(start_flag);
        buff.put(buff1);
        buff.put(CRCCheck.CRC16(buff1));
        buff.put(end_flag);
        buff.flip();
        byte[] buff2 = new byte[buff.limit()-buff.position()];
        for(int i = 0; i < buff.limit()-buff.position();i++){
            buff2[i] = buff.get(i);
        }
        Log.e("tx", "buff：" + Arrays.toString(buff2));
        return buff;
    }
    public ByteBuffer parameter_set(){
        ByteBuffer buff = ByteBuffer.allocate(256);
        ByteBuffer buff_crc = ByteBuffer.allocate(128);
        int pn = 0;
        byte[] pl = {0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01};
        byte[] pv = {0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,};
        byte[] pid = {0x00,0x01,0x00,0x02,0x00,0x03,0x00,0x04,0x00,0x05,0x00,0x06,0x00,0x07,0x00,0x08};//2字节PID数据只是举例
        Log.e("tx", "databuff：" + Arrays.toString(pid));
        pn = (pid.length)/2;//pid个数

        buff_crc.put(SA);
        buff_crc.put(DA);
        buff_crc.put(ctr_parameter);
        buff_crc.put((byte) 0x01);
        buff_crc.put((byte) 0x00);
        buff_crc.put(ti_parameter_set);
        buff.put((byte) pn);
        buff.put(pid[0]);
        buff.put(pid[1]);
        buff.put(pl[0]);
        buff.put(pv);
        buff.put(pid[2]);
        buff.put(pid[3]);
        buff.put(pl[1]);
        buff.put(pv);
        buff.put(pid[4]);
        buff.put(pid[5]);
        buff.put(pl[2]);
        buff.put(pv);
        buff.put(pid[6]);
        buff.put(pid[7]);
        buff.put(pl[3]);
        buff.put(pv);
        buff.put(pid[8]);
        buff.put(pid[9]);
        buff.put(pl[4]);
        buff.put(pv);
        buff.put(pid[10]);
        buff.put(pid[11]);
        buff.put(pl[5]);
        buff.put(pv);
        buff.put(pid[12]);
        buff.put(pid[13]);
        buff.put(pl[6]);
        buff.put(pv);
        buff.put(pid[14]);
        buff.put(pid[15]);
        buff.put(pl[7]);
        buff.put(pv);
        buff_crc.flip();
        byte[] buff1 = new byte[buff_crc.limit()-buff_crc.position()];
        for(int i = 0; i < buff_crc.limit()-buff_crc.position();i++){
            buff1[i] = buff_crc.get(i);
        }

        buff.put(start_flag);
        buff.put(buff1);
        buff.put(CRCCheck.CRC16(buff1));
        buff.put(end_flag);
        buff.flip();
        byte[] buff2 = new byte[buff.limit()-buff.position()];
        for(int i = 0; i < buff.limit()-buff.position();i++){
            buff2[i] = buff.get(i);
        }
        Log.e("tx", "buff：" + Arrays.toString(buff2));
        return buff;
    }

    public ByteBuffer real_msg_query(){
        ByteBuffer buff = ByteBuffer.allocate(256);
        ByteBuffer buff_crc = ByteBuffer.allocate(128);

        byte DT = 0x01;
        buff_crc.put(SA);
        buff_crc.put(DA);
        buff_crc.put(ctr_real_msg_query);
        buff_crc.put((byte) 0x01);
        buff_crc.put((byte) 0x00);
        buff_crc.put(ti_real_msg_query);
        buff.put(DT);
        buff_crc.flip();
        byte[] buff1 = new byte[buff_crc.limit()-buff_crc.position()];
        for(int i = 0; i < buff_crc.limit()-buff_crc.position();i++){
            buff1[i] = buff_crc.get(i);
        }

        buff.put(start_flag);
        buff.put(buff1);
        buff.put(CRCCheck.CRC16(buff1));
        buff.put(end_flag);
        buff.flip();
        byte[] buff2 = new byte[buff.limit()-buff.position()];
        for(int i = 0; i < buff.limit()-buff.position();i++){
            buff2[i] = buff.get(i);
        }
        Log.e("tx", "buff：" + Arrays.toString(buff2));
        return buff;
    }

    public ByteBuffer ctr_command(){
        ByteBuffer buff = ByteBuffer.allocate(256);
        ByteBuffer buff_crc = ByteBuffer.allocate(128);

        byte[] CN = {0x01,0x02};
        buff_crc.put(SA);
        buff_crc.put(DA);
        buff_crc.put(ctr_command);
        buff_crc.put((byte) 0x01);
        buff_crc.put((byte) 0x00);
        buff_crc.put(ti_ctr_command);
        buff.put(CN[0]);
        buff.put(CN[1]);
        buff_crc.flip();
        byte[] buff1 = new byte[buff_crc.limit()-buff_crc.position()];
        for(int i = 0; i < buff_crc.limit()-buff_crc.position();i++){
            buff1[i] = buff_crc.get(i);
        }

        buff.put(start_flag);
        buff.put(buff1);
        buff.put(CRCCheck.CRC16(buff1));
        buff.put(end_flag);
        buff.flip();
        byte[] buff2 = new byte[buff.limit()-buff.position()];
        for(int i = 0; i < buff.limit()-buff.position();i++){
            buff2[i] = buff.get(i);
        }
        Log.e("tx", "buff：" + Arrays.toString(buff2));
        return buff;
    }

    public ByteBuffer file_services(){
        ByteBuffer buff = ByteBuffer.allocate(256);
        ByteBuffer buff_crc = ByteBuffer.allocate(128);
        String mulu = "/1";
        byte[] DIR = mulu.getBytes();//目录名
        int DNL = DIR.length;//目录长度
        byte[] DID = {0x01,0x02,0x03,0x04};//目录ID
        buff_crc.put(SA);
        buff_crc.put(DA);
        buff_crc.put(ctr_file_services);
        buff_crc.put((byte) 0x01);
        buff_crc.put((byte) 0x00);
        buff_crc.put(ti_mulu_query);
        buff.put(DID);
        buff.put((byte) DNL);
        buff.put(DIR);
        buff_crc.flip();
        byte[] buff1 = new byte[buff_crc.limit()-buff_crc.position()];
        for(int i = 0; i < buff_crc.limit()-buff_crc.position();i++){
            buff1[i] = buff_crc.get(i);
        }

        buff.put(start_flag);
        buff.put(buff1);
        buff.put(CRCCheck.CRC16(buff1));
        buff.put(end_flag);
        buff.flip();
        byte[] buff2 = new byte[buff.limit()-buff.position()];
        for(int i = 0; i < buff.limit()-buff.position();i++){
            buff2[i] = buff.get(i);
        }
        Log.e("tx", "buff：" + Arrays.toString(buff2));
        return buff;
    }
}

