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

package org.zanata.service.impl;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.AuthorizationException;
import org.zanata.common.EntityStatus;
import org.zanata.dao.ProjectDAO;
import org.zanata.dao.ProjectIterationDAO;
import org.zanata.model.HLocale;
import org.zanata.model.HProject;
import org.zanata.model.HProjectIteration;
import org.zanata.security.ZanataIdentity;
import org.zanata.service.LocaleService;
import org.zanata.service.SecurityService;
import org.zanata.webtrans.server.TranslationWorkspace;
import org.zanata.webtrans.shared.NoSuchWorkspaceException;
import org.zanata.webtrans.shared.model.WorkspaceId;

/**
 * @author Patrick Huang <a
 *         href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@Name("securityServiceImpl")
@Scope(ScopeType.STATELESS)
public class SecurityServiceImpl implements SecurityService {
    @In
    private ProjectDAO projectDAO;

    @In
    private ProjectIterationDAO projectIterationDAO;

    @In
    private LocaleService localeServiceImpl;

    @In
    ZanataIdentity identity;

    @Override
    public void checkWorkspaceAction(WorkspaceId workspaceId,
            TranslationAction translationAction)
            throws NoSuchWorkspaceException {
        HProject project = checkWorkspaceStatus(workspaceId);

        HLocale locale =
                localeServiceImpl.getByLocaleId(workspaceId.getLocaleId());

        identity.checkPermission(translationAction.action(), locale, project);
    }

    @Override
    public HProject checkWorkspaceStatus(WorkspaceId workspaceId) {
        identity.checkLoggedIn();
        HProject project =
                projectDAO.getBySlug(workspaceId.getProjectIterationId()
                        .getProjectSlug());
        HProjectIteration projectIteration =
                projectIterationDAO.getBySlug(workspaceId
                        .getProjectIterationId().getProjectSlug(), workspaceId
                        .getProjectIterationId().getIterationSlug());

        if (projectIterationIsInactive(project.getStatus(),
                projectIteration.getStatus())) {
            throw new AuthorizationException("Project or version is read-only");
        }
        return project;
    }

    private static boolean projectIterationIsInactive(
            EntityStatus projectStatus, EntityStatus iterStatus) {
        return !(projectStatus.equals(EntityStatus.ACTIVE) && iterStatus
                .equals(EntityStatus.ACTIVE));
    }

    private static class SecurityCheckResultImpl implements SecurityCheckResult {
        private final HLocale hLocale;
        private final TranslationWorkspace workspace;

        private SecurityCheckResultImpl(HLocale hLocale,
                TranslationWorkspace workspace) {
            this.hLocale = hLocale;
            this.workspace = workspace;
        }

        @Override
        public HLocale getLocale() {
            return hLocale;
        }

        @Override
        public TranslationWorkspace getWorkspace() {
            return workspace;
        }
    }
}
