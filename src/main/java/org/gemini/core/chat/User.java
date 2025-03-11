package org.gemini.core.chat;

import lombok.Data;
import lombok.Getter;
import org.gemini.core.client.request_response.content.Content;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
public class User {
    private final long userId;
    private final List<Content> historyChat = new ArrayList<>();
    private String userName;

    public User(String userName, long userId) {
        this.userName = userName;
        this.userId = userId;
    }

    public boolean addContent(Content content) {
        if (content != null) {
            historyChat.add(content);
            return true;
        }
        return false;
    }
}
