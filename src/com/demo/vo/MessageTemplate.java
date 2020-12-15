package com.demo.vo;

import java.util.Date;

public class MessageTemplate {

    //发送时间
    Date time;
    //发送方昵称
    String nickname;
    //消息内容
    String message;
    //指令
    String command;

    public MessageTemplate() {
    }


    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
