package com.taffy.neko.service;

import com.taffy.neko.Result.R;

public interface AIService {
    String chatWithAI(String content);

    R<?> getKnowledgeList();


}
