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
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import org.zanata.common.ContentState;
import com.google.common.base.Objects;

import lombok.Setter;

@Entity
@org.hibernate.annotations.Entity(mutable = false)
@Setter
public class HTextFlowTargetHistory extends HTextContainer implements Serializable, ITextFlowTargetHistory
{

   private static final long serialVersionUID = 1L;

   private Long id;

   private HTextFlowTarget textFlowTarget;

   private Integer versionNum;

   private String content0;

   private String content1;

   private String content2;

   private String content3;

   private String content4;

   private String content5;

   private Date lastChanged;

   private HPerson lastModifiedBy;

   private ContentState state;

   private Integer textFlowRevision;

   public HTextFlowTargetHistory()
   {
   }

   public HTextFlowTargetHistory(HTextFlowTarget target)
   {
      this.lastChanged = target.getLastChanged();
      this.lastModifiedBy = target.getLastModifiedBy();
      this.state = target.getState();
      this.textFlowRevision = target.getTextFlowRevision();
      this.textFlowTarget = target;
      this.versionNum = target.getVersionNum();
      this.setContents(target.getContents());
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
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "target_id")
   public HTextFlowTarget getTextFlowTarget()
   {
      return textFlowTarget;
   }

   public void setTextFlowTarget(HTextFlowTarget textFlowTarget)
   {
      this.textFlowTarget = textFlowTarget;
   }

   @Override
   // TODO PERF @NaturalId(mutable=false) for better criteria caching
   @NaturalId
   public Integer getVersionNum()
   {
      return versionNum;
   }

   public void setVersionNum(Integer versionNum)
   {
      this.versionNum = versionNum;
   }

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

   public Date getLastChanged()
   {
      return lastChanged;
   };

   public void setLastChanged(Date lastChanged)
   {
      this.lastChanged = lastChanged;
   }

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "last_modified_by_id", nullable = true)
   @Override
   public HPerson getLastModifiedBy()
   {
      return lastModifiedBy;
   }

   public void setLastModifiedBy(HPerson lastModifiedBy)
   {
      this.lastModifiedBy = lastModifiedBy;
   }

   @Override
   public ContentState getState()
   {
      return state;
   }

   public void setState(ContentState state)
   {
      this.state = state;
   }

   @Override
   @Column(name = "tf_revision")
   public Integer getTextFlowRevision()
   {
      return textFlowRevision;
   }

   public void setTextFlowRevision(Integer textFlowRevision)
   {
      this.textFlowRevision = textFlowRevision;
   }
   
   /**
    * Determines whether a Text Flow Target has changed when compared to this
    * history object.
    * 
    * @param current The current Text Flow Target state. 
    * @return True, if any of the Text Flow Target fields have changed from the
    * state recorded in this History object. False, otherwise.
    */
   public boolean hasChanged(HTextFlowTarget current)
   {
      return    !Objects.equal(current.getContents(), this.getContents())
             || !Objects.equal(current.getLastChanged(), this.lastChanged)
             || !Objects.equal(current.getLastModifiedBy(), this.lastModifiedBy)
             || !Objects.equal(current.getState(), this.state)
             || !Objects.equal(current.getTextFlowRevision(), this.textFlowRevision)
             || !Objects.equal(current.getLastChanged(), this.lastChanged)
             || !Objects.equal(current.getTextFlow().getId(), this.textFlowTarget.getId())
             || !Objects.equal(current.getVersionNum(), this.versionNum);
   }
}
