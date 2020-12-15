package com.demo;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.demo.hotkey.CtrlE;
import com.demo.hotkey.F5;
import com.demo.hotkey.GlobalHotkeyResourceManagement;
import com.demo.vo.MessageTemplate;
import org.fusesource.jansi.AnsiConsole;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

public class Client {
    public static PrintWriter printWriter;
    public static String nickName;
    private Socket socket;


    public Client(){
        String IP_TENCENT_CLOUD = "120.53.227.170";
        String IP_LOCAL = "localhost";
        try {
            socket = new Socket(IP_LOCAL, 8088);
        } catch (Exception e) {
            System.out.println("服务连接失败，请检查服务状态");
        }
        GlobalHotkeyResourceManagement.initResources();
        GlobalHotkeyResourceManagement.addListener();
        F5.register();
        CtrlE.register();
    }

    private class ServerHandler implements Runnable{

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try{
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                String messageJson = null;
                MessageTemplate message = new MessageTemplate();
                while(true){
                    messageJson = br.readLine();
                    message = JSON.parseObject(messageJson,MessageTemplate.class);
                    //输出消息内容
                    AnsiConsole.out.println(DateUtil.format(message.getTime(),"HH:mm:ss")+"|"+ansi().fg(GREEN).a(message.getNickname()).reset()+": "+message.getMessage());
                    if (message.getCommand().equals("QUIT_ALL")){
                        //quit后逻辑
                        AnsiConsole.out.println(ansi().fg(RED).a("quiting now").reset());
                    }else if (message.getCommand().equals("PING")){
                        Date date_now = DateUtil.date();
                        long delay = DateUtil.between(date_now, message.getTime(), DateUnit.MS);
                        AnsiConsole.out.println(ansi().fg(BLUE).a("ping：").reset()+""+delay);
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void start(){
        AnsiConsole.systemInstall();
        try{
            ServerHandler handler = new ServerHandler();
            Thread t = new Thread(handler);
            t.setDaemon(true);
            t.start();

            OutputStream out = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
            PrintWriter pw = new PrintWriter(osw, true);
            printWriter = pw;
            //创建Scanner读取用户输入内容
            Scanner scanner = new Scanner(System.in);
            Thread.sleep(1000);
            AnsiConsole.out.println(ansi().fg(BLUE).a("请输入昵称：").reset());
            nickName = scanner.nextLine();
            AnsiConsole.out.println(ansi().fg(BLUE).a("您将使用以下昵称："+nickName).reset());
            MessageTemplate message = new MessageTemplate();
            //将昵称传到服务器放入列表里
            message.setNickname(nickName);
            message.setMessage("");
            message.setCommand("SET_NICKNAME");
            pw.println(JSON.toJSONString(message));
            while(true){
                //设置消息的昵称以及消息内容
                message.setMessage(scanner.nextLine());
                message.setTime(DateUtil.date());
                //如果消息内容是F5，设置消息指令信息为“quit”
                if (message.getMessage().equals("/F5")){
                    message.setCommand("QUIT");
                }else if (message.getMessage().equals("/ping")){
                    message.setCommand("PING_GET");
                }else if (message.getMessage().equals("/list")){
                    message.setCommand("LIST");
                }
                else{
                    message.setCommand("nothing");
                }
//                pw.println(scanner.nextLine());
                pw.println(JSON.toJSONString(message));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(socket != null){
                try{
                    socket.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Client client = new Client();
        client.start();
    }

}