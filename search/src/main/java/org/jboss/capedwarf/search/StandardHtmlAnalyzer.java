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

package org.jboss.capedwarf.search;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.lucene.analysis.charfilter.HTMLStripCharFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;

/**
 * @author <a href="mailto:mluksa@redhat.com">Marko Luksa</a>
 */
public final class StandardHtmlAnalyzer extends AnalyzerWrapper {
    private StandardAnalyzer standardAnalyzer;

    public StandardHtmlAnalyzer() {
        super(GLOBAL_REUSE_STRATEGY);
        this.standardAnalyzer = new StandardAnalyzer(CharArraySet.EMPTY_SET);
    }

    @Override
    protected Reader wrapReader(String fieldName, Reader reader) {
        return super.wrapReader(fieldName, new HTMLStripCharFilter(reader));
    }

    protected Analyzer getWrappedAnalyzer(String fieldName) {
        return standardAnalyzer;
    }
}
