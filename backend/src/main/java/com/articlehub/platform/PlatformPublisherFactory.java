package com.articlehub.platform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PlatformPublisherFactory {
    private final Map<String, PlatformPublisher> publishers;

    @Autowired
    public PlatformPublisherFactory(List<PlatformPublisher> publisherList) {
        this.publishers = publisherList.stream()
            .collect(Collectors.toMap(PlatformPublisher::getPlatformName, p -> p));
    }

    public PlatformPublisher getPublisher(String platform) {
        PlatformPublisher publisher = publishers.get(platform);
        if (publisher == null) {
            throw new IllegalArgumentException("Unsupported platform: " + platform);
        }
        return publisher;
    }

    public boolean isSupported(String platform) {
        return publishers.containsKey(platform);
    }
}
