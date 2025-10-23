package egovframework.theimc.common.Converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Converter(autoApply = false)
public class passwordConverter implements AttributeConverter<String, String> {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public String convertToDatabaseColumn(String attribute) {
    if (attribute == null) {
      return null;
    }
    return passwordEncoder.encode(attribute);
  }

  @Override
  public String convertToEntityAttribute(String dbData) {
    return dbData;
  }

}
