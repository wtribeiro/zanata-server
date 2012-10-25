/*
 * Copyright 2010, Red Hat, Inc. and individual contributors as indicated by the
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
package org.zanata.model;

import java.util.ArrayList;
import java.util.List;

import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.zanata.ZanataDbunitJpaTest;
import org.zanata.common.ContentType;
import org.zanata.common.LocaleId;
import org.zanata.dao.LocaleDAO;

/**
 * This test covers massive insertion and updates of HTextFlow and history.
 *
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@Test
public class HTextFlowHistoryMassTest extends ZanataDbunitJpaTest
{
   private LocaleDAO localeDAO;
   HLocale en_US;

   @BeforeMethod(firstTimeOnly = true)
   public void beforeMethod()
   {
      localeDAO = new LocaleDAO((Session) em.getDelegate());
      en_US = localeDAO.findByLocaleId(LocaleId.EN_US);
   }

   @Override
   protected void prepareDBUnitOperations()
   {
      beforeTestOperations.add(new DataSetOperation("org/zanata/test/model/ProjectsData.dbunit.xml", DatabaseOperation.CLEAN_INSERT));
      beforeTestOperations.add(new DataSetOperation("org/zanata/test/model/LocalesData.dbunit.xml", DatabaseOperation.CLEAN_INSERT));
   }

   private HDocument massiveInsert()
   {
      Session session = getSession();
      HDocument d = new HDocument("/path/to/document.txt", ContentType.TextPlain, en_US);
      d.setProjectIteration((HProjectIteration) session.load(HProjectIteration.class, 1L));
      d.getProjectIteration().getGroups();
      session.save(d);
      session.flush();

      // insert 10K text flows
      for( int i=0; i<10000; i++ )
      {
         HTextFlow tf = new HTextFlow(d, "mytf" + i, "hello world" + i);
         d.getTextFlows().add(tf);

      }
      session.flush();

      return d;
   }

   @Test
   public void testMassiveUpdates() throws Exception
   {
      HDocument d = massiveInsert();

      long max = -1;
      long min = Long.MAX_VALUE;
      long average = -1;
      long total = 0;

      long start;
      long end;

      for( HTextFlow t : d.getTextFlows() )
      {
         start = System.currentTimeMillis();

         Session session = newSession();
         t.setContents( t.getContent0() + " Modified 1" );
         session.flush();

         end = System.currentTimeMillis();
         long time = end - start;

         total += time;

         if( time > max )
         {
            max = time;
         }
         if( time < min )
         {
            min = time;
         }
      }

      average = total / 10000;

      System.out.println("===== First Pass ");
      System.out.println("Maximum: " + max + " ms.");
      System.out.println("Minimum: " + min + " ms.");
      System.out.println("Average: " + average + " ms.");


      newSession();
      min = Long.MAX_VALUE;
      max = -1;
      average = -1;
      total = 0;

      for( HTextFlow t : d.getTextFlows() )
      {
         start = System.currentTimeMillis();

         Session session = newSession();
         t.setContents( t.getContent0() + " Modified 2" );
         session.flush();

         end = System.currentTimeMillis();
         long time = end - start;

         total += time;

         if( time > max )
         {
            max = time;
         }
         if( time < min )
         {
            min = time;
         }
      }

      average = total / 10000;

      System.out.println("===== Second Pass ");
      System.out.println("Maximum: " + max + " ms.");
      System.out.println("Minimum: " + min + " ms.");
      System.out.println("Average: " + average + " ms.");
   }
}
