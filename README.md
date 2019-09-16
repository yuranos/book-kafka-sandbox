# book-kafka-sandbox
Created to reproduce a [bug](https://github.com/spring-projects/spring-kafka/issues/1234).

It's very easy to reproduce. The only prerequisite is that you create a topic with a lot of partitions. In my case it's 32.
I run kafka_2.12-2.2.0 but I managed to reproduce it with 0.10.1 at least, so it should cover a wide range of versions and is not about the broker.

You can create them via:

```./kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 32 --topic input```


Then run:

```./kafka-console-producer.sh --broker-list localhost:9092 --topic input```

...and type in several messages very quickly, like so:
```
>1
>2
>3
>4
>5
```

