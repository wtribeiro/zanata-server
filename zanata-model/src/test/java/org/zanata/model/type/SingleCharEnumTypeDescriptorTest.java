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
package org.zanata.model.type;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.zanata.model.type.SingleCharEnumTypeDescriptorTest.TestEnum.Amet;
import static org.zanata.model.type.SingleCharEnumTypeDescriptorTest.TestEnum.Dolor;
import static org.zanata.model.type.SingleCharEnumTypeDescriptorTest.TestEnum.Ipsum;
import static org.zanata.model.type.SingleCharEnumTypeDescriptorTest.TestEnum.Lorem;
import static org.zanata.model.type.SingleCharEnumTypeDescriptorTest.TestEnum.Sit;

import org.junit.Test;

/**
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
public class SingleCharEnumTypeDescriptorTest {

    @Test
    public void enumToString() {
        SingleCharEnumTypeDescriptor<TestEnum> descriptor =
                new SingleCharEnumTypeDescriptor<TestEnum>(TestEnum.class);

        assertThat(descriptor.toString(Lorem), is("L"));
        assertThat(descriptor.toString(Ipsum), is("I"));
        assertThat(descriptor.toString(Dolor), is("D"));
        assertThat(descriptor.toString(Sit), is("S"));
        assertThat(descriptor.toString(Amet), is("A"));
    }

    @Test
    public void stringToEnum() {
        SingleCharEnumTypeDescriptor<TestEnum> descriptor =
                new SingleCharEnumTypeDescriptor<TestEnum>(TestEnum.class);

        assertThat(descriptor.fromString("L"), is(Lorem));
        assertThat(descriptor.fromString("I"), is(Ipsum));
        assertThat(descriptor.fromString("D"), is(Dolor));
        assertThat(descriptor.fromString("S"), is(Sit));
        assertThat(descriptor.fromString("A"), is(Amet));
    }

    public enum TestEnum {
        Lorem, Ipsum, Dolor, Sit, Amet;
    }
}
