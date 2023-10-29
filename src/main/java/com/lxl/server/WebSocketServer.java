package com.lxl.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author LiuXiaolong
 * @Description test-websocket
 * @DateTime 2023/10/20  21:31
 **/

@Slf4j
@ServerEndpoint("/chat-server/{username}")
@Component
public class WebSocketServer {


    /**
     * 记录连接
     */
    private static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();


    /**
     * 建立连接的时候使用的方法
     * @param session
     * @param userName
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String userName){
        sessionMap.put(userName,session);
        log.info("有新的用户加入,username={}，当前在线的用户已经有{}人",userName,sessionMap.size());
        updateOnlineCount();
    }


    @OnClose
    public void onClose(Session session,@PathParam("username") String username){
        sessionMap.remove(username);
        log.info("有连接关闭，移除username={}用户的session,当前在线人数为{}",username,sessionMap.size());
    }

    /**
     * 收到客户端消息之后调用的方法
     * 中转消息
     * @param message
     * @param session
     * @param username
     */
    @OnMessage
    public void onMessage(String message,Session session,@PathParam("username") String username){
        log.info("服务端收到用户username={}的消息:{}",username,message);
        JSONObject messageMapJsonObject = JSON.parseObject(message);
        String to = messageMapJsonObject.getString("to");//to表示发送给哪一个用户
        String text = messageMapJsonObject.getString("text");//text表示要发送的文本
        Session toSession = sessionMap.get(to);//根据用户名to获取发送的session,在通过toSession发送消息

        if (!ObjectUtils.isEmpty(toSession)){
            //构建一下发送的消息
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("from",username);
            jsonObject.put("text",text);
            String messageBeSend = jsonObject.toJSONString();
            this.sendMessage(messageBeSend,toSession);
            log.info("用户username={},发送给用户username={}消息,text={}",username,to,text);
        }else {
            log.error("用户username={},发送给用户username={}消息,发送失败，目标的用户好像没在线",username,to);
        }
    }

    @OnError
    public void onError(Session session,Throwable error){
        log.error("发送出现异常,sessionID={}",session.getId());
        error.printStackTrace();
    }

    private void sendMessage(String messageBeSend, Session toSession) {
        log.info("服务端给客户端【{}】发送消息",toSession.getId());
        try {
            toSession.getBasicRemote().sendText(messageBeSend);
        } catch (IOException e) {
//            throw new RuntimeException(e);
            log.info("服务端给客户端【{}】发送消息失败,{}",toSession.getId(),e.toString());
        }
    }


    /**
     * 给所有的session发送消息
     * @param message
     */
    private void sendAllMessage(String message) {
        sessionMap.forEach((userName,session)->{
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
//                throw new RuntimeException(e);
                log.info("服务端给用户【{}】客户端【{}】发送消息失败,{}",userName,session.getId(),e.toString());
            }
            log.info("服务端给用户【{}】客户端【{}】发送消息【{}】成功",userName,session.getId(),message);
        });
    }


    /**
     * 更新在线人数
     */
    private void updateOnlineCount() {
        Map<String, Object> res=  new HashMap<>();
        List<Map<String,Object>> arr = new ArrayList<>();
        sessionMap.keySet().forEach(key->{
            HashMap<String, Object> tempMap = new HashMap<>();
            tempMap.put("username",key);
            arr.add(tempMap);
        });
        res.put("users",arr);
        sendAllMessage(JSON.toJSONString(res));//发送给所有用户，用以更新用户列表
    }

}
