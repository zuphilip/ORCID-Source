package org.orcid.listener;

import java.util.Map;

import javax.annotation.Resource;
import javax.jms.Message;

import org.orcid.utils.listener.LastModifiedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * This class forms the basis of the message drive data dump and SOLR index
 * updater. It is intended to be a stand alone application but can sit alongside
 * other modules in dev, QA and sandbox.
 * 
 * Example use of a message:
 * 
 * LOG.debug("Recieved last updated message");
 * map.forEach((k,v)->LOG.debug(k+"->"+v));
 * 
 * //alternative LastModifiedMessage m = new LastModifiedMessage(map);
 * LOG.debug(m.getOrcid());
 * 
 * @author tom
 *
 */
@Component
public class UpdatedOrcidListener extends BaseListener {

    Logger LOG = LoggerFactory.getLogger(UpdatedOrcidListener.class);

    @Value("${org.orcid.persistence.messaging.topic.updateOrcids}")
    private String updateOrcidsTopicName;

    @Resource
    public UpdatedOrcidExpringQueue<UpdatedOrcidWorker> cacheQueue;

    /**
     * Queues incoming messages for processing, eventually handled by
     * UpdatedOrcidWorker
     * 
     * @param message
     */
    @JmsListener(destination = "Consumer.Summaries.VirtualTopic.Updates", concurrency = "6")
    public void onMessage(Message message) {
        Map<String, String> map = getMapFromMessage(message);
        LastModifiedMessage lastModifiedMessage = new LastModifiedMessage(map);
        LOG.info("Recieved " + updateOrcidsTopicName + " message for orcid " + lastModifiedMessage.getOrcid() + " " + lastModifiedMessage.getLastUpdated());
        LastModifiedMessage existingMessage = cacheQueue.getCache().getIfPresent(lastModifiedMessage.getOrcid());
        if (existingMessage == null || lastModifiedMessage.getLastUpdated().after(existingMessage.getLastUpdated())) {
            cacheQueue.getCache().put(lastModifiedMessage.getOrcid(), lastModifiedMessage);
        }
    }
    
}
