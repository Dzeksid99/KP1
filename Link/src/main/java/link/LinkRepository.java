package link;

import java.util.HashMap;
import java.util.Map;

public class LinkRepository {
    private final Map<String, Link> storage = new HashMap<>();

    public synchronized void save(Link link) {
        storage.put(link.getShortUrl(), link);
    }

    public synchronized Link findByShortUrl(String shortUrl) {
        return storage.get(shortUrl);
    }

    public synchronized void delete(String shortUrl) {
        storage.remove(shortUrl);
    }

    public synchronized Map<String, Link> findAll() {
        return new HashMap<>(storage);
    }
}
