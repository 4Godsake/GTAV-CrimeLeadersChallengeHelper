package com.demo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.demo.vo.MessageTemplate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private ServerSocket serverSocket;
    //所有客户端输出流
    private Map<String,PrintWriter> allOut;
    //所有的马甲
    private Map<String,String> allNickname;
    //线程池
    private ExecutorService threadPool;

    public Server(){
        try{
            serverSocket = new ServerSocket(8088);
            allOut = new HashMap<String,PrintWriter>();
            allNickname = new HashMap<String,String>();
            threadPool = Executors.newFixedThreadPool(40);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*
     * 将输出流存入共享集合，与下面两个方法互斥，保证同步安全
     */
    private synchronized void addOut(String ip ,PrintWriter out){
        allOut.put(ip,out);
    }
    private synchronized void removeOut(String ip ,PrintWriter out){
        allOut.remove(ip,out);
    }
    /**
     * @description 添加删除马甲列表
     * @param
     * @return
     * @author renzexuan
     * @date 2020/12/14 17:34
     */
    private synchronized void addNickName(String ip ,String nickName){
        allNickname.put(ip ,nickName);
    }
    private synchronized void removeNickName(String ip){
        allNickname.remove(ip);
    }
    /**
     * @description 发送信息的主方法
     * @param
     * @return
     * @author renzexuan
     * @date 2020/12/14 17:35
     */
    private synchronized void sendMessage(String message, String command, String nickname){
        MessageTemplate respMessage = new MessageTemplate();
        respMessage.setMessage(message);
        respMessage.setCommand(command);
        respMessage.setNickname(nickname);
        respMessage.setTime(DateUtil.date());
        String messageJson = JSON.toJSONString(respMessage);
        for(Map.Entry<String, PrintWriter> entry : allOut.entrySet()){
            entry.getValue().println(messageJson);
        }
    }
    private synchronized void sendMessage(MessageTemplate respMessage){
        String messageJson = JSON.toJSONString(respMessage);
        for(Map.Entry<String, PrintWriter> entry : allOut.entrySet()){
            entry.getValue().println(messageJson);
        }
    }
    /**
     * @description 延迟检测
     * @param
     * @return
     * @author renzexuan
     * @date 2020/12/14 14:08
     */
    private synchronized void pingMessage(String message, String command, String ip, Date time){
        MessageTemplate respMessage = new MessageTemplate();
        respMessage.setMessage(message);
        respMessage.setCommand(command);
        respMessage.setTime(time);
        respMessage.setNickname("server");
        String messageJson = JSON.toJSONString(respMessage);
        allOut.get(ip).println(messageJson);
    }
    private synchronized void sendSingleMessage(MessageTemplate respMessage,String ip){
        String messageJson = JSON.toJSONString(respMessage);
        allOut.get(ip).println(messageJson);
    }

    /*
     * 线程体：用于并发处理不同客户端的交互
     *
     */
    class ClientHandler implements Runnable{

        private Socket socket;

        //构造函数设置为public
        public ClientHandler(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            PrintWriter pw = null;
            String clientIp = null;
            try {
                clientIp = socket.getInetAddress().toString();
                System.out.println(clientIp);
                OutputStream os = socket.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                pw = new PrintWriter(osw, true);
                //存入共享集合
                //allOut.add(pw);
                addOut(clientIp,pw);
                sendMessage("someone just connect, 当前连接数:"+ allOut.size(),"do nothing","server");

                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                String messageJson = null;
                MessageTemplate message = new MessageTemplate();
                MessageTemplate respMessage = new MessageTemplate();
                while((messageJson = br.readLine()) != null){
                     message = JSON.parseObject(messageJson,MessageTemplate.class);
                    //for(PrintWriter o: allOut){
                    //    o.println(message);
                    if (message.getCommand().equals("SET_NICKNAME")){//新增马甲
                        addNickName(clientIp,message.getNickname());
                        System.out.println("新增马甲："+message.getNickname());
                    }else if(message.getCommand().equals("QUIT")){//全体强退
                        System.out.println(message.getNickname()+" client send quit");
                        sendMessage(message.getNickname()+" press F5, quiting","QUIT_ALL","server");
                    }else if(message.getCommand().equals("PING_GET")){//延迟检测
                        System.out.println(message.getNickname()+" client send ping");
                        pingMessage("pong","PING",clientIp,message.getTime());
                    }else if(message.getCommand().equals("LIST")){//查看当前在线
                        BeanUtil.copyProperties(message, respMessage);
                        StringBuffer tempNick = new StringBuffer();
                        tempNick.append("当前在线："+ allNickname.size()+"\n");
                        for(Map.Entry<String, String> entry : allNickname.entrySet()){
                            tempNick.append(entry.getValue()+"\n");
                        }
                        respMessage.setNickname("server");
                        respMessage.setMessage(tempNick.toString());
                        sendSingleMessage(respMessage, clientIp);
                    }
                    else {
                        BeanUtil.copyProperties(message, respMessage);
                        respMessage.setCommand("do nothing");
                        sendMessage(respMessage);
                        System.out.println(message.getNickname()+"："+message.getMessage()+"\n当前用户数量："+allOut.size());
                    }

                }
            }catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }finally{
                //当客户端断线时，要将输出流从集合中删除
                //allOut.remove(pw);
                removeOut(clientIp,pw);
                removeNickName(clientIp);
                sendMessage("someone disconnected 当前连接数:"+ allOut.size(),"do nothing","server");
                if(socket != null){
                    try{
                        socket.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public void start(){
        try{
            //循环监听客户端的连接
            while(true){

                System.out.println("等待客户端连接。。。");
                Socket socket = serverSocket.accept();
                System.out.println("客户端已连接！");

                ClientHandler handler = new ClientHandler(socket);
                //启动一个线程来完成针对该客户端的交互
                threadPool.execute(handler);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Server server = new Server();
        server.start();
    }

}