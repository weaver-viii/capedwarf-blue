/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
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

package org.jboss.capedwarf.common.servlet;

import io.undertow.server.session.SessionManager;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.SessionManagerFactory;
import org.jboss.capedwarf.shared.common.http.StubSessionManagerFactory;
import org.jboss.capedwarf.shared.config.AppEngineWebXml;
import org.jboss.capedwarf.shared.config.SessionType;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class DelegateSessionManagerFactory implements SessionManagerFactory {
    private final SessionManagerFactory delegate;

    public DelegateSessionManagerFactory(AppEngineWebXml appEngineWebXml) {
        final SessionType sessionType = appEngineWebXml.getSessionType();
        switch (sessionType) {
            case APPENGINE:
                delegate = new CapedwarfSessionManagerFactory(appEngineWebXml.isAsyncSessionPersistence(), appEngineWebXml.getSessionPersistenceQueueName());
                break;
            case STUB:
                delegate = new StubSessionManagerFactory();
                break;
            default:
                throw new IllegalStateException();
        }
    }

    public SessionManager createSessionManager(Deployment deployment) {
        return delegate.createSessionManager(deployment);
    }
}
