package net.tust.mecs.entity.submitVo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SubmitVo {
    @JsonProperty("media_id")
    private String mediaId;
}
