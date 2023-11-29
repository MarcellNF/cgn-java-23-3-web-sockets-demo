package de.neuefische.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Service
public class ChatService extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions;
    private final ObjectMapper objectMapper;

    public ChatService() {
        sessions = new HashSet<>();
        objectMapper = new ObjectMapper();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        sessions.add(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);
        CustomTextMessage customTextMessage = new CustomTextMessage((String) message.getPayload(), Instant.now().toString());
        TextMessage textMessageToSend = new TextMessage(objectMapper.writeValueAsString(customTextMessage));
        for(WebSocketSession s : sessions){
            if (!s.equals(session)) {
                s.sendMessage(textMessageToSend);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sessions.remove(session);
    }
}

record CustomTextMessage(
        String message,
        String timestamp
) {
}
