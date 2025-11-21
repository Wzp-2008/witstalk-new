package cn.wzpmc.handler;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class SignalingHandler extends TextWebSocketHandler {

    // 房间映射：roomId -> 房间内的会话集合
    private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();
    // 用户-房间映射：sessionId -> roomId
    private final Map<String, String> userRoomMap = new ConcurrentHashMap<>();
    // 用户ID-会话映射：userId -> WebSocketSession（用于识别用户）
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        JSONObject signal = JSONObject.parseObject(payload, JSONObject.class);
        String type = signal.getString("type");

        System.out.println("收到信令：" + type + "，内容：" + payload); // 后端日志，便于排查

        switch (type) {
            case "JOIN_ROOM":
                String roomId = signal.getString("roomId");
                String userId = signal.getString("userId");
                joinRoom(roomId, session, userId);
                break;
            case "LEAVE_ROOM":
                leaveRoom(session);
                break;
            case "OFFER":
            case "ANSWER":
            case "ICE_CANDIDATE":
                forwardSignal(session, signal);
                break;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        leaveRoom(session);
    }

    // 加入房间
    private void joinRoom(String roomId, WebSocketSession session, String userId) throws IOException {
        // 1. 将用户添加到房间
        rooms.computeIfAbsent(roomId, k -> new CopyOnWriteArraySet<>()).add(session);
        userRoomMap.put(session.getId(), roomId);
        userSessions.put(userId, session); // 记录用户ID与会话的映射

        // 2. 广播"新用户加入"到房间内其他用户
        JSONObject joinMsg = new JSONObject();
        joinMsg.put("type", "USER_JOIN");
        joinMsg.put("userId", userId); // 关键：传递新用户ID
        broadcastToRoom(roomId, joinMsg.toString(), session); // 排除自己

        System.out.println("用户 " + userId + " 加入房间 " + roomId);
    }

    // 离开房间
    private void leaveRoom(WebSocketSession session) throws IOException {
        String roomId = userRoomMap.remove(session.getId());
        if (roomId == null) return;

        // 找到离开的用户ID
        String leaveUserId = null;
        for (Map.Entry<String, WebSocketSession> entry : userSessions.entrySet()) {
            if (entry.getValue().equals(session)) {
                leaveUserId = entry.getKey();
                break;
            }
        }
        userSessions.remove(leaveUserId);

        // 从房间移除会话
        Set<WebSocketSession> roomSessions = rooms.get(roomId);
        if (roomSessions != null) {
            roomSessions.remove(session);
            // 广播"用户离开"
            JSONObject leaveMsg = new JSONObject();
            leaveMsg.put("type", "USER_LEAVE");
            leaveMsg.put("userId", leaveUserId);
            broadcastToRoom(roomId, leaveMsg.toString(), session);

            if (roomSessions.isEmpty()) {
                rooms.remove(roomId);
                System.out.println("房间 " + roomId + " 已空，自动删除");
            }
        }
        System.out.println("用户 " + leaveUserId + " 离开房间 " + roomId);
    }

    // 转发信令（OFFER/ANSWER/ICE_CANDIDATE）
    private void forwardSignal(WebSocketSession sender, JSONObject signal) throws IOException {
        String roomId = userRoomMap.get(sender.getId());
        if (roomId == null) return;

        // 添加上发送者ID（前端需要知道是谁发的）
        String senderId = null;
        for (Map.Entry<String, WebSocketSession> entry : userSessions.entrySet()) {
            if (entry.getValue().equals(sender)) {
                senderId = entry.getKey();
                break;
            }
        }
        signal.put("senderId", senderId);

        // 转发到房间内其他用户
        broadcastToRoom(roomId, signal.toString(), sender);
    }

    // 核心方法：向房间内所有用户广播消息（排除发送者）
    private void broadcastToRoom(String roomId, String message, WebSocketSession excludeSession) throws IOException {
        Set<WebSocketSession> sessions = rooms.get(roomId);
        if (sessions == null) {
            System.out.println("房间 " + roomId + " 不存在，广播失败");
            return;
        }

        System.out.println("向房间 " + roomId + " 广播消息：" + message); // 后端日志
        for (WebSocketSession session : sessions) {
            if (session.isOpen() && !session.equals(excludeSession)) {
                session.sendMessage(new TextMessage(message));
            }
        }
    }
}
