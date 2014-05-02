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

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;

/**
 * Single character type descriptor for enumerations. This descriptor only works
 * for enumerations where all element names start with a different character.
 *
 * @author Carlos Munoz <a
 *         href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
public class SingleCharEnumTypeDescriptor<E extends Enum> extends
        AbstractTypeDescriptor<E> {

    public SingleCharEnumTypeDescriptor(Class<E> type) {
        super(type);
    }

    @Override
    public String toString(E value) {
        return value.name().toString().substring(0, 1);
    }

    @Override
    public E fromString(String string) {
        if(string == null) {
            return null;
        }
        else {
            for(Enum enumConst : super.getJavaTypeClass().getEnumConstants()) {
                // Return the first that matches the first character.
                // This is why it only works for enums in which all values start
                // with different characters
                if( enumConst.name().startsWith(string) ) {
                    return (E)enumConst;
                }
            }
        }
        return null; // TODO Should this throw IllegalArgException instead?
    }

    @Override
    public <X> X unwrap(E value, Class<X> type, WrapperOptions options) {
        if(value == null) {
            return null;
        }
        else if (String.class.isAssignableFrom(type)) {
            return (X) toString(value);
        }
        throw unknownUnwrap(type);
    }

    @Override
    public <X> E wrap(X value, WrapperOptions options) {
        if(value == null) {
            return null;
        }
        else if( String.class.isInstance(value) ) {
            return fromString((String)value);
        }
        throw unknownWrap(value.getClass());
    }
}
