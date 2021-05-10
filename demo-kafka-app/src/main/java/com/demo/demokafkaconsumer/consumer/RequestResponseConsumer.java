package com.demo.demokafkaconsumer.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RequestResponseConsumer {

  @KafkaListener(topics = "${app.kafka.multi-schema-topic}")
  public void consumerTaskMessage(SpecificRecordBase taskMessage) {
    log.info("### Consumed message: {}", taskMessage);
  }
}
