/*
 * Copyright 2010-2013, CloudBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cloudbees.sdk.extensibility.lifecycle;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base implementation for components that run periodical background task.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class PeriodicService implements Startable {
    @Override
    public void start() throws Exception {
        new Timer()
                .schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                try {
                                    PeriodicService.this.doRun();
                                } catch (Throwable e) {
                                    LOGGER.log(Level.SEVERE, "Periodic task failed", e);
                                }
                            }
                        },
                        getInitialDelay(),
                        getPeriod());
    }

    /**
     * Executes the periodic task.
     */
    protected abstract void run() throws Exception;

    private final AtomicBoolean inProgress = new AtomicBoolean();

    /**
     * Run from the web interface.
     */
    public void doRun() throws Exception {
        if (inProgress.compareAndSet(false, true)) {
            try {
                run();
            } finally {
                inProgress.set(false);
            }
        } else {
            throw new IllegalStateException("Another run is already in progress");
        }
    }

    /**
     * Cycle of execution, in milliseconds.
     */
    protected abstract long getPeriod();

    /**
     * Initial delay in milliseconds to run the first execution, in milliseconds.
     */
    protected long getInitialDelay() {
        return getPeriod();
    }

    private static final Logger LOGGER = Logger.getLogger(PeriodicService.class.getName());
}
