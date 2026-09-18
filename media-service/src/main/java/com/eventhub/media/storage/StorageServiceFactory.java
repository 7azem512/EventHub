package com.eventhub.media.storage;

import com.eventhub.media.enums.StorageProvider;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class StorageServiceFactory {

    private final Map<StorageProvider, StorageService> services;

    public StorageServiceFactory(List<StorageService> storageServices) {

        this.services = new EnumMap<>(StorageProvider.class);

        for (StorageService service : storageServices) {
            services.put(service.getProvider(), service);
        }
    }

    public StorageService get(StorageProvider provider) {

        StorageService service = services.get(provider);

        if (service == null) {
            throw new IllegalStateException(
                    "Storage provider is not available: " + provider
            );
        }

        return service;
    }
}