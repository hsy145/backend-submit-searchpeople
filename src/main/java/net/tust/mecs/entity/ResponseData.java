package net.tust.mecs.entity;

import lombok.Data;

@Data
public class ResponseData {
    private String type;
    private String media_id;
    private long created_at;
}