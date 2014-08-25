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

import com.google.common.util.concurrent.ListenableFuture;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.Identity;

import javax.annotation.Nonnull;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This component executes {@link org.zanata.async.AsyncTask} instances. It is
 * generally more advisable to use the
 * {@link org.zanata.service.AsyncTaskManagerService} component when running
 * asynchronous tasks.
 *
 * @author Carlos Munoz <a
 *         href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@Name("taskExecutor")
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class TaskExecutor {
    
    private static final Runnable NO_OP_RUNNABLE = new Runnable() {
        @Override
        public void run() {
            // Nothing to do
        }
    };

    @In
    private AsyncTaskHandleManager asyncTaskHandleManager;

    private ExecutorService slowTaskExecutor;
    private ExecutorService normalTaskExecutor;

    @Create
    public void init() {
        slowTaskExecutor = Executors.newFixedThreadPool(1);
        normalTaskExecutor = Executors.newFixedThreadPool(3);
    }

    @Destroy
    public void cleanup() {
        slowTaskExecutor.shutdown();
        normalTaskExecutor.shutdown();
    }

    /**
     * Executes an asynchronous task in the background.
     *
     * @param task
     *            The task to execute.
     * @param onComplete
     * @return The task handle to keep track of the executed task.
     * @throws RuntimeException
     *             If the provided task value is null.
     */
//    public <V, H extends AsyncTaskHandle<V>> AsyncTaskHandle<V> startTask(
//            @Nonnull final AsyncTask<V, H> task, final Runnable onComplete) {
//        H handle = task.getHandle();
//        Identity identity = Identity.instance();
//        executor.execute(new AsynchronousTaskEnvironmentWrapper(task,
//                onComplete, identity.getPrincipal(), identity.getSubject(),
//                identity.getCredentials().getUsername()));
//        return handle;
//    }

    public <V, H extends AsyncTaskHandle<V>> ListenableFuture<V>
            startTask(final @Nonnull AsyncTask<V, H> task,
                    final AsyncTaskHandle<V> handle,
                    final Runnable onComplete) {

        final AsyncTaskResult<V> taskFuture = new AsyncTaskResult<V>();
        Identity identity = Identity.instance();
        normalTaskExecutor.execute(new AsynchronousTaskEnvironmentWrapper(task,
                handle, taskFuture,
                onComplete, identity.getPrincipal(), identity.getSubject(),
                identity.getCredentials().getUsername()));
        return taskFuture;
    }

    public <V, H extends AsyncTaskHandle<V>> ListenableFuture<V>
            startTask(final @Nonnull AsyncTask<V, H> task,
                    final AsyncTaskHandle<V> handle) {
        return startTask(task, handle, NO_OP_RUNNABLE);
    }
}
