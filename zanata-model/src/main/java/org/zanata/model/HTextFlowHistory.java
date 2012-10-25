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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import org.hibernate.validator.NotEmpty;
import com.google.common.base.Objects;

import lombok.Setter;

@Entity
@Setter
@org.hibernate.annotations.Entity(mutable = false)
public class HTextFlowHistory extends HTextContainer implements Serializable, ITextFlowHistory
{

   private static final long serialVersionUID = 1L;

   private Long id;
   private Integer revision;
   private HTextFlow textFlow;
   private String content0;
   private String content1;
   private String content2;
   private String content3;
   private String content4;
   private String content5;
   private boolean obsolete;

   private Integer pos;

   public HTextFlowHistory()
   {
   }

   public HTextFlowHistory(HTextFlow textFlow)
   {
      this.revision = textFlow.getRevision();
      this.textFlow = textFlow;
      this.setContents(textFlow.getContents());
   }

   @Id
   @GeneratedValue
   public Long getId()
   {
      return id;
   }

   protected void setId(Long id)
   {
      this.id = id;
   }

   // TODO PERF @NaturalId(mutable=false) for better criteria caching
   @NaturalId
   @Override
   public Integer getRevision()
   {
      return revision;
   }

   public void setRevision(Integer revision)
   {
      this.revision = revision;
   }

   // TODO PERF @NaturalId(mutable=false) for better criteria caching
   @NaturalId
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "tf_id")
   public HTextFlow getTextFlow()
   {
      return textFlow;
   }

   public void setTextFlow(HTextFlow textFlow)
   {
      this.textFlow = textFlow;
   }

   @NotEmpty
   @Override
   @Transient
   public List<String> getContents()
   {
      List<String> contents = new ArrayList<String>();
      boolean populating = false;
      for( int i = MAX_PLURALS-1; i >= 0; i-- )
      {
         String c = this.getContent(i);
         if( c != null )
         {
            populating = true;
         }

         if( populating )
         {
            contents.add(0, c);
         }
      }
      return contents;
   }

   public void setContents(List<String> contents)
   {
      if(!Objects.equal(contents, this.getContents()))
      {
         for( int i=0; i<contents.size(); i++ )
         {
            this.setContent(i, contents.get(i));
         }
      }
   }

   private String getContent(int idx)
   {
      switch (idx)
      {
         case 0:
            return content0;

         case 1:
            return content1;

         case 2:
            return content2;

         case 3:
            return content3;

         case 4:
            return content4;

         case 5:
            return content5;

         default:
            throw new RuntimeException("Invalid Content index: " + idx);
      }
   }

   private void setContent(int idx, String content)
   {
      switch (idx)
      {
         case 0:
            content0 = content;
            break;

         case 1:
            content1 = content;
            break;

         case 2:
            content2 = content;
            break;

         case 3:
            content3 = content;
            break;

         case 4:
            content4 = content;
            break;

         case 5:
            content5 = content;
            break;

         default:
            throw new RuntimeException("Invalid Content index: " + idx);
      }
   }

   protected String getContent0()
   {
      return content0;
   }

   protected String getContent1()
   {
      return content1;
   }

   protected String getContent2()
   {
      return content2;
   }

   protected String getContent3()
   {
      return content3;
   }

   protected String getContent4()
   {
      return content4;
   }

   protected String getContent5()
   {
      return content5;
   }

   @Override
   public Integer getPos()
   {
      return pos;
   }

   public void setPos(Integer pos)
   {
      this.pos = pos;
   }

   @Override
   public boolean isObsolete()
   {
      return obsolete;
   }

   public void setObsolete(boolean obsolete)
   {
      this.obsolete = obsolete;
   }
   
   /**
    * Determines whether a Text Flow has changed when compared to this
    * history object.
    * Currently, this method only checks for changes in the revision number.
    * 
    * @param current The current Text Flow state. 
    * @return True, if the revision number in the Text Flow has changed.
    * False, otherwise.
    */
   public boolean hasChanged(HTextFlow current)
   {
      return !Objects.equal(current.getRevision(), this.getRevision());
   }

}
