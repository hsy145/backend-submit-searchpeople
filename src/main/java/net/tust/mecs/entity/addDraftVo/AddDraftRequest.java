package net.tust.mecs.entity.addDraftVo;

import lombok.Data;
import net.tust.mecs.entity.Article;

import java.util.List;

@Data
public class AddDraftRequest {
    private List<Article> articles;
}