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

import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.StringType;
import org.zanata.model.stats.StatCount;

/**
 * Hibernate type for the {@link org.zanata.model.stats.StatCount.Unit} enum.
 *
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
public class StatCountUnitType extends
        AbstractSingleColumnStandardBasicType<StatCount.Unit> implements
        LiteralType<StatCount.Unit> {

    public static final String TYPE_NAME = "statCountUnitType";
    private static final SingleCharEnumTypeDescriptor<StatCount.Unit> javaTypeDescriptor =
            new SingleCharEnumTypeDescriptor<StatCount.Unit>(
                    StatCount.Unit.class);

    public StatCountUnitType() {
        super(StringType.INSTANCE.getSqlTypeDescriptor(), javaTypeDescriptor);
    }

    @Override
    public String getName() {
        return TYPE_NAME;
    }

    @Override
    public String objectToSQLString(StatCount.Unit value, Dialect dialect)
            throws Exception {
        return "\'" + toString(value) + "\'";
    }
}
