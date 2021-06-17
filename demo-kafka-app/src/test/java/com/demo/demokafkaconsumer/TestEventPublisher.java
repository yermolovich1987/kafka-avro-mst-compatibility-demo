package com.demo.demokafkaconsumer;

import com.demo.avro.SimpleSingleMessage;
import com.demo.avro.TaskCreated;
import com.demo.avro.TaskEvent;
import org.apache.avro.specific.SpecificRecordBase;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootTest
public class TestEventPublisher {

  @Value("${app.kafka.multi-schema-topic}")
  private String multiSchemaTopic;

  @Value("${app.kafka.single-schema-topic}")
  private String singleSchemaTopic;

  @Autowired private KafkaTemplate<String, SpecificRecordBase> planEventSender;

  @Test
  public void multiSchemaTopicTest() {
    System.out.println("=== Sending test event to topic: " + multiSchemaTopic);

    planEventSender.send(
        multiSchemaTopic,
        "testEvent",
        TaskEvent.newBuilder()
            .setEvent(TaskCreated.newBuilder().setId(123).setName("someTask").build())
            .build());
  }

  // Just to check that single topic do not work for this case too.
  @Disabled
  @Test
  public void singleSchemaTopicTest() {
    System.out.println("=== Sending test event to topic: " + singleSchemaTopic);

    planEventSender.send(
        singleSchemaTopic, "testEvent", SimpleSingleMessage.newBuilder().setId(1234L).build());
  }
}
