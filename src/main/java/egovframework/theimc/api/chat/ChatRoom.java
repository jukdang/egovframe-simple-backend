package egovframework.theimc.api.chat;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
  private String roomId;
  private String name;
  private Set<String> users = new HashSet<>();
  
}
