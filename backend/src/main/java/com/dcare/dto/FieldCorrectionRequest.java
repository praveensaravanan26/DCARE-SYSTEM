package com.dcare.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class FieldCorrectionRequest {
    private UUID fieldId;
    @NotBlank
    private String fieldName;
    @NotBlank
    private String correctedValue;
    private String note;

    public FieldCorrectionRequest() {}

    public UUID getFieldId() { return fieldId; }
    public void setFieldId(UUID fieldId) { this.fieldId = fieldId; }
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    public String getCorrectedValue() { return correctedValue; }
    public void setCorrectedValue(String correctedValue) { this.correctedValue = correctedValue; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
