package com.kafka.producer.content.analytics.service;

import com.kafka.producer.content.analytics.models.Analytics;
import com.kafka.producer.content.analytics.service.ports.in.AnalyticsPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.UUID;

@Service
public class AnalyticsService implements AnalyticsPort {

    @Autowired
    private KafkaTemplate<String, Analytics> kafkaTemplate;


    @Override
    public void sendAnalytics() {
        Analytics analytics = new Analytics();
        analytics.randomizeAnalytics();
        ListenableFuture<SendResult<String, Analytics>> future =
                kafkaTemplate.send("analytics", UUID.randomUUID().toString(), analytics);

        future.addCallback(
                new ListenableFutureCallback<>() {

                    @Override
                    public void onSuccess(SendResult<String, Analytics> result) {
                        System.out.println(
                                "Sent message=["
                                        + analytics
                                        + "] with offset=["
                                        + result.getRecordMetadata().offset()
                                        + "]");
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        System.out.println(
                                "Unable to send message=[" + analytics + "] due to : " + ex.getMessage());
                    }
                });
    }
}
