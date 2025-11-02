package egovframework.theimc.api.chat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/chat")
public class ChatController {

  @Autowired
  private ChatService chatService;

  @Autowired
  private SimpMessagingTemplate messagingTemplate;

  // --- 채팅방 생성 ---
  @PostMapping("/room/create")
  public ChatRoom createRoom(@RequestParam(value = "name") String name) {
    return chatService.createRoom(name);
  }

  // --- 채팅방 목록 조회 ---
  @GetMapping("/room/list")
  public List<ChatRoom> getRooms() {
    return chatService.findAllRooms();
  }

  @GetMapping("/room/join")
  public ChatRoom getRoomByName(@RequestParam(value = "name") String name) {
      return chatService.findRoomByName(name);
  }

  // --- 메시지 수신 ---
  @MessageMapping("/chat.send/{roomId}")
  @SendTo("/topic/room/{roomId}")
  public void sendMessage(@DestinationVariable String roomId, ChatMessage message) {
    log.info(message.toString());
    messagingTemplate.convertAndSend("/topic/room/" + roomId, message);
  }
}
