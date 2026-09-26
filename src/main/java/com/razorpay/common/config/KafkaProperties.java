package com.razorpay.common.config;

import com.razorpay.common.enums.EventAggregateType;
import com.razorpay.common.exception.ResourceNotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "app.kafka")
@Getter
@Setter
public class KafkaProperties {

    private Map<String, String> topics = new HashMap<>();

    public String topicFor(EventAggregateType eventAggregateType) {
        String topic = topics.get(eventAggregateType.name().toLowerCase());
        if (topic == null) throw new ResourceNotFoundException("Topic Name", eventAggregateType.name().toLowerCase());
        return topic;
    }

}
