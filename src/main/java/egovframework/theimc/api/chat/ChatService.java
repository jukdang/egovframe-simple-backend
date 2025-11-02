package egovframework.theimc.api.chat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class ChatService {
  private Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();

  public List<ChatRoom> findAllRooms() {
    return new ArrayList<>(chatRooms.values());
  }

  public ChatRoom findRoomByName(String roomName) {
    return chatRooms.get(roomName);
  }

  public ChatRoom createRoom(String name) {
    String roomId = UUID.randomUUID().toString();
    ChatRoom room = new ChatRoom(roomId, name, new HashSet<>());
    chatRooms.put(name, room);
    return room;
  }

}
