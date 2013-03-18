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
package org.zanata.webtrans.shared.rpc;

/**
 * 
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 * 
 */
public class DownloadAllFilesAction implements DispatchAction<DownloadAllFilesResult>
{
   private static final long serialVersionUID = 1L;

   private String projectSlug, versionSlug, localeId;

   private boolean offlinePo;

   @SuppressWarnings("unused")
   private DownloadAllFilesAction()
   {
   }

   public DownloadAllFilesAction(String projectSlug, String versionSlug, String localeId, boolean offlinePo)
   {
      this.projectSlug = projectSlug;
      this.versionSlug = versionSlug;
      this.localeId = localeId;
      this.offlinePo = offlinePo;
   }

   public String getProjectSlug()
   {
      return projectSlug;
   }

   public String getVersionSlug()
   {
      return versionSlug;
   }

   public String getLocaleId()
   {
      return localeId;
   }

   public boolean isOfflinePo()
   {
      return offlinePo;
   }

}
