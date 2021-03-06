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
package org.zanata.util;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.mail.internet.MimeMultipart;

import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.Uninterruptibles;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Patrick Huang
 *         <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@Slf4j
public class HasEmailRule extends ExternalResource {
    private volatile static Wiser wiser;

    @Override
    protected void before() throws Throwable {
        super.before();
        if (HasEmailRule.wiser == null) {
            String port = PropertiesHolder.getProperty("smtp.port");
            HasEmailRule.wiser = new Wiser(Integer.parseInt(port));
            HasEmailRule.wiser.start();
            // NB we never call wiser.stop() because we want the email
            // server to stay running for all tests in this VM
        }
    }

    @Override
    protected void after() {
        wiser.getMessages().clear();
        super.after();
    }

    public List<WiserMessage> getMessages() {
        return wiser.getMessages();
    }

    /**
     * Email may arrive a little bit late therefore this method can be used to
     * poll and wait until timeout.
     *
     * @param expectedEmailNum
     *            expected arriving email numbers
     * @param timeoutDuration
     *            timeout duration
     * @param timeoutUnit
     *            timeout time unit
     * @return true if the expected number of emails has arrived or false if it
     *         fails within the timeout period
     */
    public boolean emailsArrivedWithinTimeout(final int expectedEmailNum,
            final long timeoutDuration, final TimeUnit timeoutUnit) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        // poll every half second
        final int sleepFor = 500;
        final TimeUnit sleepUnit = TimeUnit.MILLISECONDS;
        final long sleepTime = sleepUnit.convert(sleepFor, sleepUnit);
        final long timeoutTime = sleepUnit.convert(timeoutDuration, timeoutUnit);
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                long slept = 0;
                while (wiser.getMessages().size() < expectedEmailNum
                        && slept < timeoutTime) {

                    log.debug("current arrived email:{}", wiser.getMessages()
                            .size());
                    Uninterruptibles.sleepUninterruptibly(sleepFor, sleepUnit);
                    slept += sleepTime;
                }
                countDownLatch.countDown();
            }
        };
        Executors.newFixedThreadPool(1).submit(runnable);
        try {
            return countDownLatch.await(timeoutDuration, timeoutUnit);
        } catch (InterruptedException e) {
            log.warn("interrupted", e);
            return wiser.getMessages().size() == expectedEmailNum;
        }
    }

    public static String getEmailContent(WiserMessage wiserMessage) {
        try {
            return ((MimeMultipart) wiserMessage.getMimeMessage().getContent())
                    .getBodyPart(0).getContent().toString();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }
}
