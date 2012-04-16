/*
 * Copyright 2012, Red Hat, Inc. and individual contributors as indicated by the
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
package org.zanata.hibernate.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;
import org.zanata.model.HIterationProject;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
public class GroupSearchBridge implements FieldBridge
{
   public static final String SLUG_FIELD = ".slug";
   public static final String DESC_FIELD = ".description";
   public static final String NAME_FIELD = ".name";
   public static final String PROJECT_FIELD = "project";

   @Override
   public void set(String name, Object value, Document document, LuceneOptions luceneOptions)
   {
      HIterationProject project = (HIterationProject) value;

      addStringField(name + SLUG_FIELD, project.getSlug(), document, luceneOptions);
      addStringField(name + DESC_FIELD, project.getDescription(), document, luceneOptions);
      addStringField(name + NAME_FIELD, project.getName(), document, luceneOptions);
   }

   private void addStringField(String fieldName, String fieldValue, Document luceneDocument, LuceneOptions luceneOptions)
   {
      Field field = new Field(fieldName, fieldValue, luceneOptions.getStore(), luceneOptions.getIndex(), luceneOptions.getTermVector());
      field.setBoost(luceneOptions.getBoost());
      luceneDocument.add(field);
   }
}