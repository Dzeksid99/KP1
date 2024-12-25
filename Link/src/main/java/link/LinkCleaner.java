package link;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LinkCleaner extends Thread {

    private final LinkRepository linkRepository;
    private final long intervalMillis;
    private volatile boolean running = true;

    public LinkCleaner(LinkRepository linkRepository, long intervalSeconds) {
        this.linkRepository = linkRepository;
        this.intervalMillis = intervalSeconds * 1000;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(intervalMillis);

                List<String> toDelete = new ArrayList<>();
                Map<String, Link> all = linkRepository.findAll();
                for (Map.Entry<String, Link> e : all.entrySet()) {
                    Link link = e.getValue();
                    if (link.isExpired() || link.isMaxClicksReached()) {
                        toDelete.add(e.getKey());
                    }
                }
                for (String shortUrl : toDelete) {
                    linkRepository.delete(shortUrl);
                    System.out.println("[LinkCleaner] Удалена ссылка: " + shortUrl);
                }

            } catch (InterruptedException e) {
                running = false;
            }
        }
    }

    public void shutdown() {
        running = false;
        this.interrupt();
    }
}
