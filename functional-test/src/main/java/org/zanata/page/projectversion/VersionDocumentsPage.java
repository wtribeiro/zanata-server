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
package org.zanata.page.projectversion;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Damian Jansen
 * <a href="mailto:djansen@redhat.com">djansen@redhat.com</a>
 */
@Slf4j
public class VersionDocumentsPage extends VersionBasePage {

    public VersionDocumentsPage(WebDriver driver) {
        super(driver);
    }

    public VersionDocumentsPage waitForSourceDocsContains(final String document) {
        log.info("Click Project link");
        waitForAMoment().until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver input) {
                return sourceDocumentsContains(document);
            }
        });
        return new VersionDocumentsPage(getDriver());
    }

    public boolean sourceDocumentsContains(String document) {
        log.info("Query source documents contains {}", document);
        return getSourceDocumentNames().contains(document);
    }

    public List<String> getSourceDocumentNames() {
        log.info("Query source documents list");
        // getText often falls into a UI change
        return waitForAMoment().until(new Function<WebDriver, List<String>>() {
            @Override
            public List<String> apply(WebDriver input) {
                List<String> fileNames = new ArrayList<String>();
                for (WebElement element : getDocumentsTabDocumentList()) {
                    fileNames.add(element.findElement(
                            By.className("list__title"))
                            .getText());
                }
                return fileNames;
            }
        });
    }

    private List<WebElement> getDocumentsTabDocumentList() {
        slightPause();
        return waitForWebElement(By.id("documents-document_list"))
                        .findElements(By.xpath("./li"));
    }

}
