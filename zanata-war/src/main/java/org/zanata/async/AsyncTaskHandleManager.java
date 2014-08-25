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
package org.zanata.async;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;

/**
 * Handles the lifecycle of Async task handles
 * @author Carlos Munoz <a
 *         href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@Name("asyncTaskHandleManager")
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class AsyncTaskHandleManager {

    private Map<Serializable, AsyncTaskHandle> handlesByKey = Maps
            .newConcurrentMap();

    // Cache of recently completed tasks
    private Cache<Serializable, AsyncTaskHandle> finishedTasks = CacheBuilder
            .newBuilder().expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    public AsyncTaskHandle newTaskHandle() {
        return newTaskHandle(AsyncTaskHandle.class);
    }

    public <H extends AsyncTaskHandle> H newTaskHandle(Class<H> handleClass) {
        H newHandle = null;
        // Try a constructor with a string first
        try {
            newHandle = handleClass.getConstructor(String.class)
                    .newInstance(UUID.randomUUID().toString());
        }
        catch (Exception e) {
            // Do nothing
        }

        if( newHandle == null ) {
            // Try the no-arg constructor then
            try {
                newHandle = handleClass.newInstance();
            }
            catch (Exception e) {
                throw new RuntimeException("Error building a new task handle", e);
            }
        }

        handlesByKey.put(newHandle.getId(), newHandle);
        return newHandle;
    }

    synchronized void taskFinished(AsyncTaskHandle taskHandle) {
        AsyncTaskHandle handle = handlesByKey.remove(taskHandle.getId());
        if (handle != null) {
            finishedTasks.put(handle.getId(), handle);
        }
    }

    public AsyncTaskHandle getHandleById(String id) {
        if (handlesByKey.containsKey(id)) {
            return handlesByKey.get(id);
        }
        return finishedTasks.getIfPresent(id);
    }
}
