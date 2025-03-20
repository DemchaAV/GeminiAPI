package chat;

import lombok.extern.slf4j.Slf4j;
import org.gemini.core.client.GeminiConnection;

@Slf4j
public class Chat {
    private final GeminiConnection connection;
    private final User user;

    public Chat(User user, GeminiConnection connection) {
        this.user = user;
        this.connection = connection;
        log.info("Chat instance created for user: {}", user.getUserName());
    }

}