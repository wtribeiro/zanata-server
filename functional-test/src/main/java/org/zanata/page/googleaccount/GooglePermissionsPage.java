/*
 * Copyright 2013, Red Hat, Inc. and individual contributors as indicated by the
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
package org.zanata.page.googleaccount;

import com.google.common.base.Predicate;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.zanata.page.AbstractPage;
import org.zanata.page.account.EditProfilePage;

/**
 * @author Damian Jansen <a
 *         href="mailto:djansen@redhat.com">djansen@redhat.com</a>
 */
public class GooglePermissionsPage extends AbstractPage {
    public GooglePermissionsPage(WebDriver driver) {
        super(driver);
    }

    public EditProfilePage acceptPermissions() {
        try {
            waitForTenSec().until(new Predicate<WebDriver>() {
                @Override
                public boolean apply(WebDriver driver) {
                    System.out.println("Waiting for access button");
                    return getDriver()
                            .findElement(By.id("submit_approve_access"))
                            .isEnabled();
                }
            });
        } catch (TimeoutException te) {
            if (getDriver().findElements(By.id("profile-form:user-create-new"))
                    .size() > 0) {
                // We've somehow passed the "Accept Permissions" step -
                // ok, let's just continue
                return new EditProfilePage(getDriver());
            }

        }
        getDriver().findElement(By.id("submit_approve_access")).click();
        return new EditProfilePage(getDriver());
    }
}
