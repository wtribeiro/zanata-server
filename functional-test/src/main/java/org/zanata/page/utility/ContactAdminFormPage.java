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
package org.zanata.page.utility;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.zanata.page.BasePage;

/**
 * @author Patrick Huang <a
 *         href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@Slf4j
public class ContactAdminFormPage extends BasePage {

    private By subjectField = By.id("contactAdminForm:subjectField:subject");
    private By messageField = By.id("contactAdminForm:messageField:message");
    private By sendButton = By.id("send");

    public ContactAdminFormPage(WebDriver driver) {
        super(driver);
    }

    public ContactAdminFormPage inputSubject(String subject) {
        log.info("Enter subject {}", subject);
        waitForWebElement(subjectField).clear();
        waitForWebElement(subjectField).sendKeys(subject);
        return new ContactAdminFormPage(getDriver());
    }

    public ContactAdminFormPage inputMessage(String message) {
        log.info("Enter message {}", message);
        waitForWebElement(messageField).sendKeys(message);
        return new ContactAdminFormPage(getDriver());
    }

    /**
     * Send the message to the administrator
     * Requires a page type to return to
     * @param pageClass type of page to return to
     * @return new page type P
     */
    public <P> P send(Class<P> pageClass) {
        log.info("Click Send");
        waitForWebElement(sendButton).click();
        return PageFactory.initElements(getDriver(), pageClass);
    }
}
