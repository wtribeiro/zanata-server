/*
 * Copyright 2010, Red Hat, Inc. and individual contributors
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
package org.zanata.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.zanata.common.HasContents;
import org.zanata.model.HTextFlowTarget;
import org.zanata.model.HTextFlowTargetHistory;

import static org.zanata.common.HasContents.MAX_PLURALS;


@Name("textFlowTargetHistoryDAO")
@AutoCreate
@Scope(ScopeType.STATELESS)
public class TextFlowTargetHistoryDAO extends AbstractDAOImpl<HTextFlowTargetHistory, Long>
{
   
   public TextFlowTargetHistoryDAO()
   {
      super(HTextFlowTargetHistory.class);
   }

   public TextFlowTargetHistoryDAO(Session session)
   {
      super(HTextFlowTargetHistory.class, session);
   }

   public boolean findContentInHistory(HTextFlowTarget target, List<String> contents)
   {

      Criteria criteria = getSession().createCriteria(HTextFlowTargetHistory.class);
      criteria.add(Restrictions.eq("textFlowTarget", target));

      int paramPos = 1;
      int contentIdx = 0;
      for( String c : contents )
      {
         // Only for the max number of plurals
         if( contentIdx < MAX_PLURALS)
         {
            String contentField = "content" + contentIdx++;
            if( c == null )
            {
               criteria.add(Restrictions.isNull(contentField));
            }
            else
            {
               criteria.add(Restrictions.eq(contentField, c));
            }
         }
      }
      // Fill the rest of the parameter values with null
      while( contentIdx < MAX_PLURALS )
      {
         String contentField = "content" + contentIdx++;
         criteria.add(Restrictions.isNull(contentField));
      }

      criteria.setProjection(Projections.rowCount());
      return (Integer)criteria.uniqueResult() != 0;
   }

   public boolean findConflictInHistory(HTextFlowTarget target, Integer verNum, String username)
   {
      Query query = getSession().createQuery("select count(*) from HTextFlowTargetHistory t where t.textFlowTarget.id =:id and t.textFlowRevision > :ver and t.lastModifiedBy.account.username != :username");
      query.setParameter("id", target.getId());
      query.setParameter("ver", verNum);
      query.setParameter("username", username);
      Long count = (Long) query.uniqueResult();
      return count != 0;
   }

}
