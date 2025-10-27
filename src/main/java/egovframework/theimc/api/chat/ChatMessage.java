package egovframework.theimc.api.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
  private String roomId;
  private String sender;
  private String message;
  private MessageType type;

  public enum MessageType {
    ENTER, TALK, LEAVE
  }

}
