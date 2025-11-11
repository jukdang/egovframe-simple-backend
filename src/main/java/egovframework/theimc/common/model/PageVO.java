package egovframework.theimc.common.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageVO {
  private int page = 1;
  private int size = 10;

  public int getOffset() {
    return (page - 1) * size;
  }
}
