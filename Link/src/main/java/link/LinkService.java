package link;

import config.Config;
import server.ClckClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

public class LinkService {

    private final LinkRepository linkRepository;

    public LinkService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    public String createShortLink(String originalUrl, UUID userId,
                                  int requestedHours, int requestedClicks) throws IOException {
        int finalHours = Math.min(requestedHours, Config.getMaxTimeHours());
        int finalClicks = Math.max(requestedClicks, Config.getMinClicks());
//        String realShortUrl = ClckClient.shorten(originalUrl);
        String realShortUrl = ClckClient.shorten(originalUrl + "?rnd=" + System.currentTimeMillis());

        LocalDateTime expireTime = LocalDateTime.now().plusHours(finalHours);
        Link link = new Link(realShortUrl, originalUrl, userId, expireTime, finalClicks);
        linkRepository.save(link);

        return realShortUrl;
    }

    public String go(String shortUrl) {
        Link link = linkRepository.findByShortUrl(shortUrl);
        if (link == null) {
            return null;
        }
        if (link.isExpired()) {
            return null;
        }
        if (link.isMaxClicksReached()) {
            return null;
        }

        link.incrementClicks();

        return link.getShortUrl();
    }

    public boolean editLimit(String shortUrl, UUID userId, int newLimit) {
        Link link = linkRepository.findByShortUrl(shortUrl);
        if (link == null) return false;
        if (!link.getUserId().equals(userId)) return false;

        int finalLimit = Math.max(newLimit, Config.getMinClicks());
        link.setMaxClicks(finalLimit);
        return true;
    }

    public boolean deleteLink(String shortUrl, UUID userId) {
        Link link = linkRepository.findByShortUrl(shortUrl);
        if (link == null) return false;
        if (!link.getUserId().equals(userId)) return false;

        linkRepository.delete(shortUrl);
        return true;
    }
}
