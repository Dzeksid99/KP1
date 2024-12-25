package link;

import java.time.LocalDateTime;
import java.util.UUID;

public class Link {
    private final String shortUrl;
    private final String originalUrl;
    private final UUID userId;
    private LocalDateTime expireTime;
    private int maxClicks;
    private int currentClicks;

    public Link(String shortUrl, String originalUrl, UUID userId,
                LocalDateTime expireTime, int maxClicks) {
        this.shortUrl = shortUrl;
        this.originalUrl = originalUrl;
        this.userId = userId;
        this.expireTime = expireTime;
        this.maxClicks = maxClicks;
        this.currentClicks = 0;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public UUID getUserId() {
        return userId;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public int getMaxClicks() {
        return maxClicks;
    }

    public int getCurrentClicks() {
        return currentClicks;
    }

    public void setMaxClicks(int maxClicks) {
        this.maxClicks = maxClicks;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public void incrementClicks() {
        currentClicks++;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireTime);
    }

    public boolean isMaxClicksReached() {
        return currentClicks >= maxClicks;
    }
}
