/*
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.zanata.search;

import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Sean Flanigan <a
 *         href="mailto:sflaniga@redhat.com">sflaniga@redhat.com</a>
 *
 */
@Test(groups = { "unit-tests" })
public class LevenshteinUtilTest {
    private static final double DELTA = 0.0001;

    public void testVarious() {
        String s1 = "one two";
        String s2 = "one two three four five";
        String s3 = "one";
        String s4 = "dbnoicgjnedbitnhjudbioe";

        double similarity = LevenshteinUtil.getSimilarity(s1, s1);
        Assert.assertTrue(similarity > 0.999f);

        similarity = LevenshteinUtil.getSimilarity(s1, s2);
        Assert.assertTrue(similarity > 0.3f);
        Assert.assertTrue(similarity < 0.4f);

        similarity = LevenshteinUtil.getSimilarity(s1, s3);
        Assert.assertTrue(similarity > 0.4f);
        Assert.assertTrue(similarity < 0.5f);

        similarity = LevenshteinUtil.getSimilarity(s1, s4);
        Assert.assertTrue(similarity < 0.3f);
    }

    public void testPoint996Similarity() {
        String s1 =
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";
        String s2 =
                "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";

        double similarity = LevenshteinUtil.getSimilarity(s1, s2);
        Assert.assertTrue(similarity > 0.99f);
        Assert.assertTrue(similarity < 1.0f);
    }

    public void testDifferentSizedLists() {
        List<String> strings1 = Arrays.asList("1234567890", "abcdefghij");
        List<String> strings2 = Arrays.asList("1234567890abcdefghij");
        double similarity = LevenshteinUtil.getSimilarity(strings1, strings2);
        Assert.assertTrue(similarity > 0.3);
        Assert.assertTrue(similarity < 0.4);
    }

    public void testSimilarLists() {
        List<String> strings1 = Arrays.asList("1234567890", "abcdefghij");
        List<String> strings2 = Arrays.asList("123456789", "bcdefghij");
        double similarity = LevenshteinUtil.getSimilarity(strings1, strings2);
        Assert.assertEquals(similarity, 0.9, DELTA);
    }

    public void testMisorderedLists() {
        List<String> strings1 = Arrays.asList("1234567890", "abcdefghij");
        List<String> strings2 = Arrays.asList("abcdefghij", "1234567890");
        double similarity = LevenshteinUtil.getSimilarity(strings1, strings2);
        Assert.assertEquals(similarity, 0.0, DELTA);
    }

    public void testIdenticalLists() {
        List<String> strings1 = Arrays.asList("one", "two");
        List<String> strings2 = Arrays.asList("one", "two");
        double similarity = LevenshteinUtil.getSimilarity(strings1, strings2);
        Assert.assertEquals(similarity, 1.0, DELTA);
    }

}
