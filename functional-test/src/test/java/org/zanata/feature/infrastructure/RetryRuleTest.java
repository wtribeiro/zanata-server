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

package org.zanata.feature.infrastructure;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.zanata.feature.testharness.ZanataTestCase;
import org.zanata.feature.testharness.TestPlan.BasicAcceptanceTest;
import org.zanata.util.RetryRule;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Damian Jansen <a
 * href="mailto:djansen@redhat.com">djansen@redhat.com</a>
 */
@Category(BasicAcceptanceTest.class)
public class RetryRuleTest extends ZanataTestCase {

    @Rule
    public RetryRule retryRule = new RetryRule(2);

    @Test(timeout = ZanataTestCase.MAX_SHORT_TEST_DURATION)
    public void retryPassAfterFail() throws Exception {
        // Fail on the first execution, but pass on the second
        assertThat(retryRule.currentTry())
                .isGreaterThan(1)
                .as("Current try is greater than 1");
        // Can only pass on second execution
        assertThat(retryRule.currentTry())
                .isEqualTo(2)
                .as("This is the second try");
    }

    @Test(timeout = ZanataTestCase.MAX_SHORT_TEST_DURATION)
    public void passWillPass() throws Exception {
        assertThat(true).isTrue().as("A normal passing test will pass");
        assertThat(retryRule.currentTry())
                .isEqualTo(1)
                .as("And pass on the first try");
    }

    @Test(expected = AssertionError.class)
    public void retryFailsWhenAllTriesFail() throws Exception {
        // Fail the first execution
        if (retryRule.currentTry() == 1) {
            throw new Exception();
        }
        // Passes on the second execution, expect-fails on the third
        assertThat(retryRule.currentTry())
                .isEqualTo(2)
                .as("The execution count is correct");
        // Fails on the second execution
        throw new Exception();
    }
}
