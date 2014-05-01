/*
 * Copyright 2014, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.jms.QueueConnection;
import org.zanata.dao.ProjectDAO;
import org.zanata.events.TextFlowTargetStateEvent;
import org.zanata.service.TranslationStateCache;

/**
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@Name("testQueueListenerService")
@Scope(ScopeType.APPLICATION)
@Startup
public class TestQueueListenerService implements MessageListener {

    private QueueSession queueSession;

    private MessageConsumer messageConsumer;

    @Create
    public void init() {
        try {
            Context ctx = new InitialContext();
            queueSession = QueueConnection.instance().createQueueSession(false,
                    Session.DUPS_OK_ACKNOWLEDGE);
            Queue queue = (Queue)ctx.lookup("queue/test");
//            queueReceiver = queueSession.createReceiver(queue);
            messageConsumer = queueSession.createConsumer(queue,
                    "JMSType = '" + TextFlowTargetStateEvent.class.getName() + "'");
            messageConsumer.setMessageListener(this);
//            queueReceiver.setMessageListener(this);
        }
        catch (NamingException e) {
            throw new RuntimeException("myListener initialization failed", e);
        }
        catch (JMSException e) {
            throw new RuntimeException("myListener initialization failed", e);
        }
    }

    @Override
    public void onMessage(Message message) {
        // Make sure Seam is live
        Lifecycle.beginCall();

        try {
            TranslationStateCache service = (TranslationStateCache)Component.getInstance("translationStateCacheImpl");
            System.out.println("Got a message: " + message.toString());
            service.textFlowStateUpdated( (TextFlowTargetStateEvent)((ObjectMessage)message).getObject() );
        }
        catch (JMSException e) {
            e.printStackTrace();
        }
        finally {
            Lifecycle.endCall();
        }
    }

    @Destroy
    public void destroy() {
        if( messageConsumer != null ) {
            try {
                messageConsumer.close();
            }
            catch (JMSException e) {
                // Proceed
            }
        }
        if( queueSession != null ) {
            try {
                queueSession.close();
            }
            catch (JMSException e) {
                // Proceed
            }
        }
    }
}
