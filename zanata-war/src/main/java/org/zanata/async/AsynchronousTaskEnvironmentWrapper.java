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
package org.zanata.async;

import java.security.Principal;
import java.util.concurrent.Future;

import javax.security.auth.Subject;

import com.google.common.util.concurrent.MoreExecutors;
import lombok.AllArgsConstructor;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.security.RunAsOperation;

import lombok.extern.slf4j.Slf4j;
import org.zanata.action.AuthenticationEvents;
import org.zanata.dao.AccountDAO;
import org.zanata.model.HAccount;
import org.zanata.security.ZanataJpaIdentityStore;
import org.zanata.util.ServiceLocator;

/**
 * This class executes a Runnable Process asynchronously. Do not use this class
 * directly. Use {@link org.zanata.async.TaskExecutor} instead as this is just a
 * wrapper to make sure Seam can run the task in the background.
 * {@link TaskExecutor} is able to do this as well as return an instance of the
 * task handle to keep track of the task's progress.
 * @author Carlos Munoz <a
 *         href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@AllArgsConstructor
@Slf4j
public class AsynchronousTaskEnvironmentWrapper extends RunAsOperation
    implements Runnable {

    private AsyncTask task;
    private AsyncTaskHandle handle;
    private AsyncTaskResult result;
    private Runnable onComplete;
    private final Principal runAsPpal;
    private final Subject runAsSubject;
    private final String username;

    @Override
    public void execute() {
        AsyncUtils.outject(handle, ScopeType.EVENT);

        try {
            task.setFuture(result);
            if( handle != null ) {
                task.setHandle(handle);
                handle.startTiming();
            }
            prepareSecurityContext(username);
            Object returnValue = task.call();
            result.set(returnValue);
        } catch (Throwable t) {
            result.setException(t);
            log.error(
                "Exception when executing an asynchronous task.", t);
        } finally {
            if( handle != null ) {
                handle.finishTiming();
            }
        }
        onComplete.run();
    }

    @Override
    public Principal getPrincipal() {
        return runAsPpal;
    }

    @Override
    public Subject getSubject() {
        return runAsSubject;
    }

    /**
     * Prepares the Drools security context so that it contains all the
     * necessary facts for security checking.
     */
    private static void prepareSecurityContext(String username) {
        /*
         * TODO This should be changed to not need the username. There should be
         * a way to simulate a login for async tasks, or at least to inherit the
         * caller's context
         */
        if( username != null ) {
            // Only if it's an authenticated task should it try and do this
            // injection
            AccountDAO accountDAO =
                    ServiceLocator.instance().getInstance(AccountDAO.class);
            ZanataJpaIdentityStore idStore =
                    ServiceLocator.instance().getInstance(
                            ZanataJpaIdentityStore.class);
            HAccount authenticatedAccount = accountDAO.getByUsername(username);
            idStore.setAuthenticateUser(authenticatedAccount);
        }
    }
}
