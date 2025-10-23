package egovframework.theimc.common.Converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.springframework.stereotype.Component;

import egovframework.theimc.common.utils.CryptoUtils;

@Component
@Converter(autoApply = false)
public class EncryptConverter implements AttributeConverter<String, String> {

  @Override
  public String convertToDatabaseColumn(String attribute) {
    if (attribute == null) {
      return null;
    }
    return CryptoUtils.encrypt(attribute);
  }

  @Override
  public String convertToEntityAttribute(String dbData) {
    return CryptoUtils.decrypt(dbData);
  }

}
