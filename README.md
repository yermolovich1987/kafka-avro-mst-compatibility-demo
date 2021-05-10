# kafka-avro-mst-compatibility-demo

Project to test multi schema topics (Schema References) and schema compatibility.

For multi schema topic the new approach with Schema References is used. The compatibility is set to FULL_TRANSITIVE.

To represent the problem with forward compatibility run the next sequence of the commands from the root folder of the project:

* Run environment (Kafka, Schema Registry, etc.) via Docker Compose:
  `docker-compose up -d`
* Run new schema registration in the main project:
  `cd ./demo-kafka-app && ./gradlew assemble registerSchemasTask configSubjectTask && cd ../`
* Register new version of the schema with optional field from separate subproject:
  `cd ./new-schema-version-registrator && ./gradlew assemble registerSchemasTask configSubjectTask && cd ../`
* Run test that will try to publish Message with the old version of the schema
  `gradle test --tests com.demo.demokafkaconsumer.TestEventPublisher`

The test will fail with Serialization exception since it will try to reference the field from new schema version that is
not present in the old version:
`Error serializing Avro message
org.apache.kafka.common.errors.SerializationException: Error serializing Avro message
Caused by: java.lang.IndexOutOfBoundsException: Invalid index: 3`.

This problem occurs just for producer. It is caused by the next points:
1) For Schema Reference usage whe need to set "use.latest.version=true". For more details see https://docs.confluent.io/platform/current/schema-registry/serdes-develop/serdes-avro.html#referenced-schemas-avro.
2) When use.latest.version is set, then the latest schema is taken from Schema Registry, and not from generated class. This is done here => io.confluent.kafka.serializers.AbstractKafkaAvroSerializer#serializeImpl.
3) Then from this fetched schema the list of fields is prepared with the order index of each field. Since the schema is loaded from the Schema Repository, then the index of field could differ from the generated class. This is done here: org.apache.avro.generic.GenericDatumWriter#writeRecord.
4) Then using the provided field index value is take from the DTO and written to result array of bytes. So if the order of fields is changed or if we add new field, then we will got an exception here. This logic is located here: org.apache.avro.specific.SpecificDatumWriter#writeField 


  



