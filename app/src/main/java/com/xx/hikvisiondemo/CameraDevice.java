package com.xx.hikvisiondemo;

/**
 * 设备实体类
 */
public class CameraDevice {

    /**
     * IP地址
     */
    private String ip;
    /**
     * 端口
     */
    private int port;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String passWord;
    /**
     * 通道号：0 主码流，1子码流, 2三码流
     */
    private int channel;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public CameraDevice() {
    }

    public CameraDevice(String ip, int port, String userName, String passWord, int channel) {
        this.ip = ip;
        this.port = port;
        this.userName = userName;
        this.passWord = passWord;
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "[IP=" + ip + "; PORT=" + port + "; USERNAME=" + userName + "; PASSWORD=" + passWord + "; CHANNEL=" + channel + ";]";
    }

}
