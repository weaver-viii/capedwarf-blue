/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
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

package org.jboss.capedwarf.datastore;

import com.google.appengine.api.datastore.Key;

/**
 * Single checker.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class SingleTxChecker implements TxChecker {
    private boolean invalid;
    private Key root;

    public boolean isInvalid(Key currentRoot, Key key) {
        // do not check if already invalid
        if (invalid == false) {
            if (root == null) {
                root = currentRoot;
                TxTrackerUtil.track(currentRoot);
            } else if (root.equals(currentRoot) == false) {
                invalid = true;
            }
        }
        return invalid;
    }

    public void beforeCompletion() {
        TxTrackerUtil.beforeCompletion(root);
    }

    public void afterCompletion(int status) {
        TxTrackerUtil.afterCompletion(status, root);
    }
}
