package chat;

import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.gemini.core.client.request_response.content.Content;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Data
public class User {
    private final long userId;
    private final Map<Long, List<Content>> historyChat = new ConcurrentHashMap<>();
    private String userName;

    public User(String userName, long userId) {
        this.userName = userName;
        this.userId = userId;
    }

    /**
     *
     * @param chatID
     * @param content
     * @return
     */
    public boolean addContent(long chatID, @NonNull Content content) {
        historyChat.computeIfAbsent(chatID, k -> {
            log.info("Creating new chat with ID: {}", chatID);
            return new ArrayList<>();
        });

        log.info("Adding content to chat ID {}: {}", chatID, content);
        return historyChat.get(chatID).add(content);
    }

    public long createNewChat() {
        long chatId;
        do {
            chatId = Instant.now().toEpochMilli();
        } while (historyChat.containsKey(chatId));

        historyChat.put(chatId, new ArrayList<>());
        log.info("New chat created with ID: {}", chatId);
        return chatId;
    }

    public List<Content> getChatHistory(long chatID) {
        return historyChat.getOrDefault(chatID, new ArrayList<>());
    }
}
