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

package org.jboss.test.capedwarf.tasks.test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.capedwarf.tasks.support.PrintListener;
import org.jboss.test.capedwarf.tasks.support.PrintServlet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@RunWith(Arquillian.class)
public class TasksTestCase {
    private static final String URL = "/_ah/test";
    private static final String WEB_XML =
                    "<web>" +
                    " <listener>" +
                    "  <listener-class>" + PrintListener.class.getName() + "</listener-class>" +
                    " </listener>" +
                    " <servlet>" +
                    "  <servlet-name>PrintServlet</servlet-name>" +
                    "  <servlet-class>" + PrintServlet.class.getName() + "</servlet-class>" +
                    " </servlet>" +
                    " <servlet-mapping>" +
                    "  <servlet-name>PrintServlet</servlet-name>" +
                    "  <url-pattern>" + URL + "</url-pattern>" +
                    " </servlet-mapping>" +
                    "</web>";

    // we wait for JMS to kick-in
    private static void sleep() throws InterruptedException {
        Thread.sleep(3000L); // sleep for 3secs
    }

    @Deployment
    public static Archive getDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addClasses(PrintServlet.class, PrintListener.class)
                .setWebXML(new StringAsset(WEB_XML))
                .addAsWebInfResource("appengine-web.xml");
    }

    @Test
    public void testSmoke() throws Exception {
        final Queue queue = QueueFactory.getQueue("default");
        queue.add(TaskOptions.Builder.withUrl(URL));
        sleep();
    }

    @Test
    public void testPayload() throws Exception {
        final Queue queue = QueueFactory.getQueue("default");
        queue.add(TaskOptions.Builder.withPayload("payload").url(URL));
        sleep();
    }

    @Test
    public void testHeaders() throws Exception {
        final Queue queue = QueueFactory.getQueue("default");
        queue.add(TaskOptions.Builder.withHeader("header_key", "header_value").url(URL));
        sleep();
    }

    @Test
    public void testParams() throws Exception {
        final Queue queue = QueueFactory.getQueue("default");
        queue.add(TaskOptions.Builder.withParam("param_key", "param_value").url(URL));
        sleep();
    }

    @Test
    public void testPull() throws Exception {
        final Queue queue = QueueFactory.getQueue("default");
        TaskHandle th = queue.add(TaskOptions.Builder.withMethod(TaskOptions.Method.PULL).param("foo", "bar").payload("foobar").etaMillis(15000));
        List<TaskHandle> handles = queue.leaseTasks(30, TimeUnit.MINUTES, 100);
        Assert.assertFalse(handles.isEmpty());
        TaskHandle lh = handles.get(0);
        Assert.assertEquals(th.getName(), lh.getName());
        sleep();
    }

    @Test
    public void testPullWithTag() throws Exception {
        final Queue queue = QueueFactory.getQueue("default");
        TaskHandle th = queue.add(TaskOptions.Builder.withMethod(TaskOptions.Method.PULL).tag("barfoo").payload("foobar").etaMillis(15000));
        List<TaskHandle> handles = queue.leaseTasksByTag(30, TimeUnit.MINUTES, 100, "barfoo");
        Assert.assertFalse(handles.isEmpty());
        TaskHandle lh = handles.get(0);
        Assert.assertEquals(th.getName(), lh.getName());
        sleep();
    }
}
