package com.schedule.briefing.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schedule.briefing.vo.ScheduleOptionInfo;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ScheduleOptionInfoConverter implements AttributeConverter<ScheduleOptionInfo, String> {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(ScheduleOptionInfo attribute) {
    try {
      return attribute == null ? null : objectMapper.writeValueAsString(attribute);
    } catch (Exception e) {
      throw new IllegalArgumentException("ScheduleOptionInfo 직렬화 실패", e);
    }
  }

  @Override
  public ScheduleOptionInfo convertToEntityAttribute(String dbData) {
    try {
      return dbData == null ? null : objectMapper.readValue(dbData, ScheduleOptionInfo.class);
    } catch (Exception e) {
      throw new IllegalArgumentException("ScheduleOptionInfo 역직렬화 실패", e);
    }
  }
}
