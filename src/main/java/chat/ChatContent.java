package chat;

import lombok.Builder;
import org.gemini.core.client.request_response.content.Content;

/**
 * Current class exactly the same as Content byt have an additional field {@code timeStamp}
 * @param timeStamp
 * @param contents
 */
@Builder
public record  ChatContent(long timeStamp, Content contents) {
}
