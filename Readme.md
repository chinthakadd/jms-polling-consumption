## What is this Proof of Concept?

I am trying to check how to build a Polling Consumer for IBM MQ (JMS) instead of using Listener approach.

Why?

- With MessageListener, you cannot dynamically set a MessageSelector
- When you want to read messages after a cool-off period instead of reading it immediately from the queue, you can apply
  a JMSTimestamp based MessageSelector to do that.
- However, to apply such a dynamic selector, you need to poll for messages instead of listening to it.
- This Proof of Concept proves that using JmsTemplate and create a small PollingConsumer that can show-case this.

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

