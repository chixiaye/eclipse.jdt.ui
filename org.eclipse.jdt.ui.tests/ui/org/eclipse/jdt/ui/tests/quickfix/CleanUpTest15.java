/*******************************************************************************
 * Copyright (c) 2021, 2023 Red Hat Inc. and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Red Hat Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.ui.tests.quickfix;

import org.junit.Rule;
import org.junit.Test;

import org.eclipse.core.runtime.CoreException;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;

import org.eclipse.jdt.internal.corext.fix.CleanUpConstants;

import org.eclipse.jdt.ui.tests.core.rules.Java15ProjectTestSetup;
import org.eclipse.jdt.ui.tests.core.rules.ProjectTestSetup;

/**
 * Tests the cleanup features related to Java 16.
 */
public class CleanUpTest15 extends CleanUpTestCase {
	@Rule
	public ProjectTestSetup projectSetup= new Java15ProjectTestSetup(false);

	@Override
	protected IJavaProject getProject() {
		return projectSetup.getProject();
	}

	@Override
	protected IClasspathEntry[] getDefaultClasspath() throws CoreException {
		return projectSetup.getDefaultClasspath();
	}

	@Test
	public void testConcatToTextBlock() throws Exception {
		IPackageFragment pack1= fSourceFolder.createPackageFragment("test1", false, null);
		String sample= "" //
				+ "package test1;\n" //
				+ "\n" //
				+ "public class E {\n" //
				+ "    public void testSimple() {\n"
				+ "        // comment 1\n" //
				+ "        String x = \"\" + //$NON-NLS-1$\n" //
    	        + "            \"public void foo() {\\n\" + //$NON-NLS-1$\n" //
    	        + "            \"    System.out.println(\\\"abc\\\");\\n\" + //$NON-NLS-1$\n" //
    	        + "            \"}\\n\"; //$NON-NLS-1$ // comment 2\n" //
    	        + "    }\n" //
    	        + "\n" //
				+ "    public void testTrailingSpacesAndInnerNewlines() {\n"
				+ "        String x = \"\" +\n" //
    	        + "            \"public \\nvoid foo() {  \\n\" +\n" //
    	        + "            \"    System.out.println\\\\(\\\"abc\\\");\\n\" +\n" //
    	        + "            \"}\\n\";\n" //
    	        + "    }\n" //
    	        + "\n" //
    	        + "    public void testLineContinuationAndTripleQuotes() {\n" //
				+ "        String x = \"\" +\n" //
    	        + "            \"abcdef\" +\n" //
    	        + "            \"ghijkl\\\"\\\"\\\"\\\"123\\\"\\\"\\\"\" +\n" //
    	        + "            \"mnop\\\\\";\n" //
    	        + "    }\n" //
    	        + "\n" //
    	        + "    public void testNoChange() {\n" //
				+ "        StringBuffer buf = new StringBuffer();\n" //
    	        + "        buf.append(\"abcdef\\n\");\n" //
    	        + "        buf.append(\"123456\\n\");\n" //
    	        + "        buf.append(\"ghijkl\\n\");\n" //
    	        + "        String k = buf.toString();\n" //
    	        + "    }\n" //
				+ "}\n";

		ICompilationUnit cu1= pack1.createCompilationUnit("E.java", sample, false, null);

		enable(CleanUpConstants.STRINGCONCAT_TO_TEXTBLOCK);

		String expected1= "" //
				+ "package test1;\n" //
				+ "\n" //
				+ "public class E {\n" //
				+ "    public void testSimple() {\n" //
				+ "        // comment 1\n" //
				+ "        String x = \"\"\"\n" //
    	        + "        \tpublic void foo() {\n" //
    	        + "        \t    System.out.println(\"abc\");\n" //
    	        + "        \t}\n" //
    	        + "        \t\"\"\"; //$NON-NLS-1$ // comment 2\n" //
    	        + "    }\n" //
    	        + "\n" //
				+ "    public void testTrailingSpacesAndInnerNewlines() {\n" //
				+ "        String x = \"\"\"\n" //
    	        + "        \tpublic\\s\n"
    	        + "        \tvoid foo() {\\s\\s\n" //
    	        + "        \t    System.out.println\\\\(\"abc\");\n" //
    	        + "        \t}\n" //
    	        + "        \t\"\"\";\n" //
    	        + "    }\n" //
    	        + "\n" //
    	        + "    public void testLineContinuationAndTripleQuotes() {\n" //
				+ "        String x = \"\"\"\n" //
    	        + "        \tabcdef\\\n" //
    	        + "        \tghijkl\\\"\"\"\\\"123\\\"\"\"\\\n" //
    	        + "        \tmnop\\\\\"\"\";\n" //
    	        + "    }\n" //
    	        + "\n" //
    	        + "    public void testNoChange() {\n" //
				+ "        StringBuffer buf = new StringBuffer();\n" //
    	        + "        buf.append(\"abcdef\\n\");\n" //
    	        + "        buf.append(\"123456\\n\");\n" //
    	        + "        buf.append(\"ghijkl\\n\");\n" //
    	        + "        String k = buf.toString();\n" //
    	        + "    }\n" //
				+ "}\n";

		assertRefactoringResultAsExpected(new ICompilationUnit[] { cu1 }, new String[] { expected1 }, null);
	}

	@Test
	public void testConcatToTextBlock2() throws Exception {
		IPackageFragment pack1= fSourceFolder.createPackageFragment("test1", false, null);
		String sample= "" //
				+ "package test1;\n" //
				+ "\n" //
				+ "public class E {\n" //
				+ "    public void foo() {\n" //
				+ "        // comment 1\n" //
				+ "        StringBuffer buf= new StringBuffer(\"intro string\\n\"); //$NON-NLS-1$\n" //
				+ "        buf.append(\"public void foo() {\\n\"); //$NON-NLS-1$\n" //
				+ "        buf.append(\"    return null;\\n\"); //$NON-NLS-1$\n" //
				+ "        buf.append(\"}\\n\"); //$NON-NLS-1$\n" //
				+ "        buf.append(\"\\n\"); //$NON-NLS-1$\n" //
				+ "        System.out.println(buf.toString());\n" //
				+ "        System.out.println(buf.toString() + \"abc\");\n" //
				+ "        // comment 2\n" //
				+ "        buf = new StringBuffer(\"intro string 2\\n\");\n" //
				+ "        buf.append(\"some string\\n\");\n" //
				+ "        buf.append(\"    another string\\n\");\n" //
				+ "        // comment 3\n" //
				+ "        String k = buf.toString();\n" //
				+ "        // comment 4\n" //
				+ "        StringBuilder buf2= new StringBuilder();\n" //
				+ "        buf2.append(\"public String metaPhone(final String txt2){\\n\");\n" //
				+ "        buf2.append(\"    return null;\\n\");\n" //
				+ "        buf2.append(\"}\\n\");\n" //
				+ "        buf2.append(\"\\n\");\n" //
				+ "        // comment 5\n" //
				+ "        k = buf2.toString();\n" //
				+ "        System.out.println(buf2.toString());\n" //
				+ "        StringBuilder buf3= new StringBuilder();\n" //
				+ "        buf3.append(\"public void foo() {\\n\");\n" //
				+ "        buf3.append(\"    return null;\\n\");\n" //
				+ "        buf3.append(\"}\\n\");\n" //
				+ "        buf3.append(\"\\n\");\n" //
				+ "        // comment 6\n" //
				+ "        k = buf3.toString();\n" //
				+ "\n" //
				+ "        String x = \"abc\\n\" +\n"
				+ "            \"def\\n\" +\n" //
				+ "            \"ghi\\n\";\n" //
				+ "    }\n" //
				+ "}";

		ICompilationUnit cu1= pack1.createCompilationUnit("E.java", sample, false, null);

		enable(CleanUpConstants.STRINGCONCAT_TO_TEXTBLOCK);
		enable(CleanUpConstants.STRINGCONCAT_STRINGBUFFER_STRINGBUILDER);

		String expected1= "" //
				+ "package test1;\n" //
				+ "\n" //
				+ "public class E {\n" //
				+ "    public void foo() {\n" //
				+ "        // comment 1\n" //
				+ "        String str = \"\"\"\n" //
				+ "        \tintro string\n"
				+ "        \tpublic void foo() {\n" //
				+ "        \t    return null;\n" //
				+ "        \t}\n" //
				+ "        \t\n" //
				+ "        \t\"\"\"; //$NON-NLS-1$\n" //
				+ "        System.out.println(str);\n" //
				+ "        System.out.println(str + \"abc\");\n" //
				+ "        // comment 2\n" //
				+ "        String str1 = \"\"\"\n" //
				+ "        \tintro string 2\n" //
				+ "        \tsome string\n" //
				+ "        \t    another string\n" //
				+ "        \t\"\"\";\n" //
				+ "        // comment 3\n" //
				+ "        String k = str1;\n" //
				+ "        // comment 4\n" //
				+ "        String str2 = \"\"\"\n" //
				+ "        \tpublic String metaPhone(final String txt2){\n" //
				+ "        \t    return null;\n" //
				+ "        \t}\n" //
				+ "        \t\n" //
				+ "        \t\"\"\";\n" //
				+ "        // comment 5\n" //
				+ "        k = str2;\n" //
				+ "        System.out.println(str2);\n" //
				+ "        // comment 6\n" //
				+ "        k = \"\"\"\n" //
				+ "        \tpublic void foo() {\n" //
				+ "        \t    return null;\n" //
				+ "        \t}\n" //
				+ "        \t\n" //
				+ "        \t\"\"\";\n" //
				+ "\n" //
				+ "        String x = \"\"\"\n" //
				+ "        \tabc\n" //
				+ "        \tdef\n" //
				+ "        \tghi\n" //
				+ "        \t\"\"\";\n" //
				+ "    }\n" //
				+ "}";

		assertRefactoringResultAsExpected(new ICompilationUnit[] { cu1 }, new String[] { expected1 }, null);
	}

	@Test
	public void testNoConcatToTextBlock() throws Exception {
		IPackageFragment pack1= fSourceFolder.createPackageFragment("test1", false, null);
		String sample= "" //
				+ "package test1;\n" //
				+ "\n" //
				+ "public class E {\n" //
    	        + "    public void testNotThreeStrings() {\n" //
				+ "        String x = \n" //
    	        + "            \"abcdef\" +\n" //
    	        + "            \"ghijkl\";" //
    	        + "    }\n" //
    	        + "\n" //
    	        + "    public void testNotAllLiterals() {\n" //
				+ "        String x = \"\" +\n" //
    	        + "            \"abcdef\" +\n" //
    	        + "            \"ghijkl\" +\n" //
    	        + "            String.valueOf(true)\n;"
    	        + "    }\n" //
    	        + "\n" //
      	        + "    public void testNotAllLiterals2(String a) {\n" //
				+ "        String x = \"\" +\n" //
    	        + "            \"abcdef\" +\n" //
    	        + "            \"ghijkl\" +\n" //
    	        + "            a\n;"
    	        + "    }\n" //
    	        + "\n" //
   	            + "    public void testNotAllStrings() {\n" //
				+ "        String x = \"\" +\n" //
    	        + "            \"abcdef\" +\n" //
    	        + "            \"ghijkl\" +\n" //
    	        + "            3;\n;"
    	        + "    }\n" //
    	        + "\n" //
   	            + "    public void testInconsistentNLS() {\n" //
				+ "        String x = \"\" +\n" //
    	        + "            \"abcdef\" +\n" //
    	        + "            \"ghijkl\" + //$NON-NLS-1$\n" //
    	        + "            \"mnop\";\n" //
    	        + "    }\n" //
				+ "}\n";
		ICompilationUnit cu1= pack1.createCompilationUnit("E.java", sample, false, null);

		enable(CleanUpConstants.STRINGCONCAT_TO_TEXTBLOCK);

		assertRefactoringHasNoChange(new ICompilationUnit[] { cu1 });
	}

	@Test
	public void testNoConcatToTextBlock2() throws Exception {
		IPackageFragment pack1= fSourceFolder.createPackageFragment("test1", false, null);
		String sample= "" //
				+ "package test1;\n" //
				+ "\n" //
				+ "public class E {\n" //
    	        + "    public void testNoToString() {\n" //
				+ "        StringBuffer buf = new StringBuffer();\n" //
    	        + "        buf.append(\"abcdef\\n\");\n" //
    	        + "        buf.append(\"123456\\n\");\n" //
    	        + "        buf.append(\"ghijkl\\n\");\n" //
    	        + "    }\n" //
    	        + "\n" //
    	        + "    public void testExtraCallsAfter() {\n" //
				+ "        StringBuffer buf = new StringBuffer();\n" //
    	        + "        buf.append(\"abcdef\\n\");\n" //
    	        + "        buf.append(\"123456\\n\");\n" //
    	        + "        buf.append(\"ghijkl\\n\");\n" //
				+ "        String x = buf.toString();\n" //
    	        + "        buf.append(\"abcdef\\n\");\n" //
    	        + "    }\n" //
    	        + "\n" //
      	        + "    public void testExtraCallsBetween(String a) {\n" //
				+ "        StringBuffer buf = new StringBuffer();\n" //
    	        + "        buf.append(\"abcdef\\n\");\n" //
    	        + "        buf.reverse();\n" //
    	        + "        buf.append(\"ghijkl\\n\");\n" //
				+ "        String x = buf.toString();\n" //
    	        + "    }\n" //
    	        + "\n" //
   	            + "    public void testSerialCallsNotSupported() {\n" //
				+ "        StringBuffer buf = new StringBuffer();\n" //
    	        + "        buf.append(\"abcdef\\n\");\n" //
    	        + "        buf.append(\"123456\\n\");\n" //
    	        + "        buf.append(\"ghijkl\\n\").append(\"mnopqrst\\n\");\n" //
				+ "        String x = buf.toString();\n" //
    	        + "    }\n" //
    	        + "\n" //
   	            + "    public void testAppendingNonString() {\n" //
				+ "        StringBuffer buf = new StringBuffer();\n" //
    	        + "        buf.append(\"abcdef\\n\");\n" //
    	        + "        buf.append(\"123456\\n\");\n" //
    	        + "        buf.append(\"ghijkl\\n\");\n" //
    	        + "        buf.append(3);\n" //
				+ "        String x = buf.toString();\n" //
    	        + "    }\n" //
    	        + "\n" //
   	            + "    public void testInconsistentNLS() {\n" //
				+ "        StringBuffer buf = new StringBuffer();\n" //
    	        + "        buf.append(\"abcdef\\n\");\n" //
    	        + "        buf.append(\"123456\\n\"); //$NON-NLS-1$\n" //
    	        + "        buf.append(\"ghijkl\\n\");\n" //
    	        + "        buf.append(3);\n" //
				+ "        String x = buf.toString();\n" //
    	        + "    }\n" //
				+ "}\n";
		ICompilationUnit cu1= pack1.createCompilationUnit("E.java", sample, false, null);

		enable(CleanUpConstants.STRINGCONCAT_TO_TEXTBLOCK);
		enable(CleanUpConstants.STRINGCONCAT_STRINGBUFFER_STRINGBUILDER);

		assertRefactoringHasNoChange(new ICompilationUnit[] { cu1 });
	}

}
