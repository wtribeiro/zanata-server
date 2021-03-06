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
package org.zanata.page.projectversion.versionsettings;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.zanata.page.projectversion.VersionBasePage;
import org.zanata.util.LanguageList;

import java.util.List;

/**
 * This class represents the project version settings tab for languages.
 *
 * @author Damian Jansen
 * <a href="mailto:djansen@redhat.com">djansen@redhat.com</a>
 */
@Slf4j
public class VersionLanguagesTab extends VersionBasePage {

    private By languagesSettingForm = By.id("settings-languages-form");
    private By activeLocales = By.id("active-locales-list");
    private By inactiveLocales = By.id("available-locales-list");
    private By disabledLocalesFilter = By.id("settings-languages-form:available-locales-filter-input");

    public VersionLanguagesTab(WebDriver driver) {
        super(driver);
    }

    public VersionLanguagesTab waitForLocaleListVisible() {
        log.info("Wait for locale list visible");
        waitForAMoment().until(new Function<WebDriver, Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return waitForWebElement(languagesSettingForm)
                        .findElement(By.className("list--slat"))
                        .isDisplayed();
            }
        });
        return new VersionLanguagesTab(getDriver());
    }

    /**
     * Get a list of locales enabled in this version
     *
     * @return String list of language/locale names
     */
    public List<String> getEnabledLocaleList() {
        log.info("Query enabled locales list");
        return LanguageList.getListedLocales(waitForWebElement(activeLocales));
    }

    public VersionLanguagesTab waitForLanguagesContains(String language) {
        log.info("Wait for languages contains {}", language);
        waitForLanguageEntryExpected(language, true);
        return new VersionLanguagesTab(getDriver());
    }

    private void waitForLanguageEntryExpected(final String language,
                                              final boolean exists) {
        waitForAMoment().until(new Function<WebDriver, Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return getEnabledLocaleList().contains(language) == exists;
            }
        });
    }

    public VersionLanguagesTab enterSearchLanguage(String localeQuery) {
        log.info("Enter language search {}", localeQuery);
        waitForWebElement(disabledLocalesFilter).sendKeys(localeQuery);
        return new VersionLanguagesTab(getDriver());
    }

    public VersionLanguagesTab removeLocale(final String localeId) {
        log.info("Click Disable on {}", localeId);
        String message = "can not find locale - " + localeId;
        waitForAMoment().withMessage(message).until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver driver) {
                return LanguageList.toggleLanguageInList(
                        getDriver().findElement(activeLocales), localeId);
            }
        });

        refreshPageUntil(this, new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver driver) {
                return !getEnabledLocaleList().contains(localeId);
            }
        });

        return new VersionLanguagesTab(getDriver());
    }

    public VersionLanguagesTab addLocale(final String localeId) {
        log.info("Click Enable on {}", localeId);
        String message = "can not find locale - " + localeId;
        waitForAMoment().withMessage(message).until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver driver) {
                return LanguageList.toggleLanguageInList(
                        getDriver().findElement(inactiveLocales), localeId);
            }
        });

        refreshPageUntil(this, new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver driver) {
                return getEnabledLocaleList().contains(localeId);
            }
        });

        return new VersionLanguagesTab(getDriver());
    }

}
