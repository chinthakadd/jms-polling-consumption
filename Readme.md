## What is this Proof of Concept?

- JMS has to modes of message consumption, Synchronous and Asynchronous
- Asynchronous message consumption is typically implemented with Service Activation style pattern. Spring implements this using @JmsListener
- However, what if I want to apply some back-pressure on the message consumption and only read messages after a certain delay. Is this something supported by JMS providers like IBM MQ?
- Well, the answer is yes. You can implement it, however you want to implement it using the concept of `MessageSelector`s.
- `MessageSelector`s are a SQL092 Compliant Query that can pass to the MQ as a Consumer Channel as it requests for messages
- While typical message selectors are used to compare against custom header values to divide queue messages into different consumer channels, the concept can be used for the back-pressure use-case.
- In order to apply this, you need to use the `JMSTimestamp` attribute and build a dynamic message selector that forces MQ to only push messages that are older than a certain timestamp.
- But traditional JmsListener approach does not allow this level of dynamism of message selectors.
- So I was forced down the synchronous path of using JmsTemplate.receive() and build a Polling Strategy that can consume message with a dynamic time based message selector.
- This Proof of Concept proves that using JmsTemplate and create a small PollingConsumer that can show-case this. It has an implementation of a Multi-threaded polling consumer that use JmsTemplate.execute() to periodically receive messages that are cooled-off within MQ for a certain period of time. It also perform transacted sessions and commit/rollback messages. MessageSelectors are periodically updated (updated every 30 seconds) to ensure that we dont invoke too many MessageConsumer handlers on MQ side within a given connection. Spring's CachingConnectionFactory is used to cache sessions therefore optimizing the performance.

## How To Run

- Run ./mq.sh to start the MQ Broker
- Start the Application
- Send messages by invoking /send
- The PollingConsumer is configured to poll messages after 2 minute delay. See it in action.

## References

The following references were helpful to complete this proof of concept.

- How to run MQ Manager in Docker
  https://developer.ibm.com/tutorials/mq-connect-app-queue-manager-containers/

- Basics about IBM MQ
  https://developer.ibm.com/articles/mq-fundamentals/

- Message Selectors
  https://www.ibm.com/docs/en/ibm-mq/9.1?topic=messages-message-selectors-in-jms

