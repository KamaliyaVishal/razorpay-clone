package com.razorpay.merchant.cache;

import java.util.Optional;

public interface ApiKeyCache {

    Optional<ApiKeyCacheEntry> get(String keyId);

    void put(String keyId, ApiKeyCacheEntry apiKeyCacheEntry);

    void evict(String keyId);

}
