This is an example project to demonstrate the problems I encountered when using JMS and Sleuth

I ran into this when I was upgrading a project from Spring Boot 1.5 to 2.1.* (alt. 2.2 .. *).

The code is running fine without Sleuth but when adding this dependency 
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
```
then the following problems arise
```
Caused by: org.springframework.beans.factory.BeanCreationException: 
    Error creating bean with name 'jmsListenerContainerFactory' defined in class path resource [com/merikan/jmsexample/jms/JmsConfig.class]: 
    Bean instantiation via factory method failed; nested exception is org.springframework.beans.BeanInstantiationException: 
    Failed to instantiate [org.springframework.jms.config.DefaultJmsListenerContainerFactory]: 
    Factory method 'jmsListenerContainerFactory' threw exception; nested exception is java.lang.IllegalStateException: 
    @Bean method JmsConfig.receiverConnectionFactory called as bean reference for type [javax.jms.ConnectionFactory] but overridden by 
    non-compatible bean instance of type [org.springframework.cloud.sleuth.instrument.messaging.LazyXAConnectionFactory]. 
    Overriding bean of same name declared in: class path resource [com/merikan/jmsexample/jms/JmsConfig.class]
Caused by: org.springframework.beans.BeanInstantiationException: Failed to instantiate [org.springframework.jms.config.DefaultJmsListenerContainerFactory]: 
    Factory method 'jmsListenerContainerFactory' threw exception; nested exception is java.lang.IllegalStateException: 
    @Bean method JmsConfig.receiverConnectionFactory called as bean reference for type [javax.jms.ConnectionFactory] but overridden by 
    non-compatible bean instance of type [org.springframework.cloud.sleuth.instrument.messaging.LazyXAConnectionFactory]. 
    Overriding bean of same name declared in: class path resource [com/merikan/jmsexample/jms/JmsConfig.class]
Caused by: java.lang.IllegalStateException: 
    @Bean method JmsConfig.receiverConnectionFactory called as bean reference for type [javax.jms.ConnectionFactory] but overridden by 
    non-compatible bean instance of type [org.springframework.cloud.sleuth.instrument.messaging.LazyXAConnectionFactory]. 
    Overriding bean of same name declared in: class path resource [com/merikan/jmsexample/jms/JmsConfig.class]
```

The `ActiveMQConnectionFactory` is no longer a `javax.jms.ConnectionFactory` instead it is a `org.springframework.cloud.sleuth.instrument.messaging.LazyXAConnectionFactory`.
Our `ActiveMQConnectionFactory` implements both `ConnectionFactory` and `XAConnectionFactory`,  
so when looking at the [Sleuth code](https://github.com/spring-cloud/spring-cloud-sleuth/blob/558900155adb1ae4a732cf450c216e2ff04a3f90/spring-cloud-sleuth-core/src/main/java/org/springframework/cloud/sleuth/instrument/messaging/TracingConnectionFactoryBeanPostProcessor.java#L73)
we can see that a `LazyXAConnectionFactory` is returned instead of `LazyConnectionFactory`. 

## Solution   
Instead of creating a bean of type `ActiveMQConnectionFactory` we can wrap it in a `CachingConnectionFactory` class instead. Sleuth will now instrument a `LazyConnectionFactory` 
since `CachingConnectionFactory` only implements `ConnectionFactory` and not `XAConnectionFactory`.
```java
@Bean
public ConnectionFactory senderConnectionFactory() {
    CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
    connectionFactory.setTargetConnectionFactory(new ActiveMQConnectionFactory(brokerUrl));
    return connectionFactory;
}
```