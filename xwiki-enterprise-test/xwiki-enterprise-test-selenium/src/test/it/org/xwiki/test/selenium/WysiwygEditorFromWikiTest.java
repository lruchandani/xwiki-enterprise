/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
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
package org.xwiki.test.selenium;

import org.xwiki.test.selenium.framework.AbstractXWikiTestCase;
import org.xwiki.test.selenium.framework.ColibriSkinExecutor;
import org.xwiki.test.selenium.framework.XWikiTestSuite;

import junit.framework.Test;

/**
 * Tests the WYSIWYG editor (content edited in Wiki mode and then switched in WYSIWYG mode).
 * 
 * @version $Id$
 */
public class WysiwygEditorFromWikiTest extends AbstractXWikiTestCase
{
    private static final String SYNTAX = "xwiki/1.0";

    public static Test suite()
    {
        XWikiTestSuite suite = new XWikiTestSuite("Tests the wiki editor");
        suite.addTestSuite(WysiwygEditorFromWikiTest.class, ColibriSkinExecutor.class);
        return suite;
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        loginAsAdmin();
        editInWikiEditor("Test", "WysiwygEdit", SYNTAX);
    }

    public void testIndentedOrderedList() throws Exception
    {
        setFieldValue("content", "1. level 1\n11. level 2");
        clickEditPageInWysiwyg();

        assertHTMLGeneratedByWysiwyg("ol/li[text()='level 1']");
        assertHTMLGeneratedByWysiwyg("ol/ol/li[text()='level 2']");

        clickEditPageInWikiSyntaxEditor();
        assertEquals("1. level 1\n11. level 2", getFieldValue("content"));
    }

    public void testAutomaticConversionFromHashSyntaxToNumberSyntaxForOrderedLists()
    {
        setFieldValue("content", "# item 1\n# item 2");
        clickEditPageInWysiwyg();
        clickEditPageInWikiSyntaxEditor();

        assertEquals("1. item 1\n1. item 2", getFieldValue("content"));
    }

    public void testHorizontalLineBeforeTableMacro()
    {
        setFieldValue("content", "----\n\n{table}\na | b\nc | d\n{table}");
        clickEditPageInWysiwyg();
        clickEditPageInWikiSyntaxEditor();

        assertEquals("----\n\n{table}\na | b\nc | d\n{table}", getFieldValue("content"));
    }

    public void testBulletedLists() throws Exception
    {
        setFieldValue("content", "- item 1\n-- item 2\n- item 3");
        clickEditPageInWysiwyg();

        assertHTMLGeneratedByWysiwyg("ul/li[text()='item 1']");
        assertHTMLGeneratedByWysiwyg("ul/ul/li[text()='item 2']");
        assertHTMLGeneratedByWysiwyg("ul/li[text()='item 3']");

        clickEditPageInWikiSyntaxEditor();
        assertEquals("- item 1\n-- item 2\n- item 3", getFieldValue("content"));
    }

    public void testVelocityComments() throws Exception
    {
        setFieldValue("content", "## comment");
        clickEditPageInWysiwyg();

        // <div class="vcomment"> comment</div>
        assertHTMLGeneratedByWysiwyg("div[@class='vcomment'][.=' comment']");

        clickEditPageInWikiSyntaxEditor();
        assertEquals("## comment", getFieldValue("content"));
    }

    public void testAccentsInLinks() throws Exception
    {
        setFieldValue("content", "[\u00E9t\u00E9]");
        clickEditPageInWysiwyg();
        clickEditPageInWikiSyntaxEditor();
        assertEquals("[\u00E9t\u00E9]", getFieldValue("content"));

        setFieldValue("content", "[test>\u00E9t\u00E9]");
        clickEditPageInWysiwyg();
        clickEditPageInWikiSyntaxEditor();
        assertEquals("[test>\u00E9t\u00E9]", getFieldValue("content"));

        setFieldValue("content", "[\u00E9t\u00E9>test]");
        clickEditPageInWysiwyg();
        clickEditPageInWikiSyntaxEditor();
        assertEquals("[\u00E9t\u00E9>test]", getFieldValue("content"));
    }

}
