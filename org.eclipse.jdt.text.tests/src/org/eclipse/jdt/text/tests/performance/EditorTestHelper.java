/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.text.tests.performance;


import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.text.tests.Accessor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.reconciler.AbstractReconciler;
import org.eclipse.jface.text.source.SourceViewer;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.ITypeNameRequestor;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;

import org.eclipse.jdt.internal.corext.util.AllTypesCache;

import org.eclipse.jdt.internal.ui.text.JavaReconciler;


/**
 * @since 3.1
 */
public class EditorTestHelper {

	private static class Requestor implements ITypeNameRequestor {
		public void acceptClass(char[] packageName, char[] simpleTypeName, char[][] enclosingTypeNames, String path) {
		}
		public void acceptInterface(char[] packageName, char[] simpleTypeName, char[][] enclosingTypeNames, String path) {
		}
	}
	
	public static IEditorPart openInEditor(IFile file, boolean runEventLoop) throws PartInitException {
		IEditorPart part= IDE.openEditor(getActivePage(), file);
		if (runEventLoop)
			runEventQueue(part);
		return part;
	}

	public static IEditorPart openInEditor(IFile file, String editorId, boolean runEventLoop) throws PartInitException {
		IEditorPart part= IDE.openEditor(getActivePage(), file, editorId);
		if (runEventLoop)
			runEventQueue(part);
		return part;
	}

	public static IDocument getDocument(ITextEditor editor) {
		IDocumentProvider provider= editor.getDocumentProvider();
		IEditorInput input= editor.getEditorInput();
		return provider.getDocument(input);
	}

	public static void revertEditor(ITextEditor editor, boolean runEventQueue) {
		editor.doRevertToSaved();
		if (runEventQueue)
			runEventQueue(editor);
	}
	
	public static void closeEditor(IEditorPart editor) {
		IWorkbenchPage page= getActivePage();
		if (page != null)
			page.closeEditor(editor, false);
	}
	
	public static void closeAllEditors() {
		IWorkbenchPage page= getActivePage();
		if (page != null)
			page.closeAllEditors(false);
	}
	
	public static void runEventQueue() {
		IWorkbenchWindow window= getActiveWorkbenchWindow();
		if (window != null)
			runEventQueue(window.getShell());
	}
	
	public static void runEventQueue(IWorkbenchPart part) {
		runEventQueue(part.getSite().getShell());
	}
	
	public static void runEventQueue(Shell shell) {
		while (shell.getDisplay().readAndDispatch());
	}
	
	public static void runEventQueue(long minTime) {
		long nextCheck= System.currentTimeMillis() + minTime;
		while (System.currentTimeMillis() < nextCheck) {
			runEventQueue();
			sleep(1);
		}
	}
	
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		IWorkbench workbench= PlatformUI.getWorkbench();
		IWorkbenchWindow window= workbench.getActiveWorkbenchWindow();
		// work around failures in N20041013's performance tests:
		// http://fullmoon.rtp.raleigh.ibm.com/downloads/drops/N-N20041013-200410130010/performance/html/org.eclipse.jdt.text.tests_win32perf.html
		// TODO: investigate further when working with multiple WorkbenchWindows
		if (window == null && workbench.getWorkbenchWindowCount() > 0)
			return workbench.getWorkbenchWindows()[0];
		return window;
	}

	public static IWorkbenchPage getActivePage() {
		IWorkbenchWindow window= getActiveWorkbenchWindow();
		return window != null ? window.getActivePage() : null;
	}

	public static Display getActiveDisplay() {
		IWorkbenchWindow window= getActiveWorkbenchWindow();
		return window != null ? window.getShell().getDisplay() : null;
	}

	public void joinBackgroundActivities(SourceViewer sourceViewer) throws CoreException {
		joinBackgroundActivities();
		joinReconciler(sourceViewer, 0, Long.MAX_VALUE, 500);
	}
	
	public void joinBackgroundActivities() throws CoreException {
		// Join Building
		boolean interrupted= true;
		while (interrupted) {
			try {
				Platform.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
				interrupted= false;
			} catch (InterruptedException e) {
				interrupted= true;
			}
		}
		// Join indexing
		new SearchEngine().searchAllTypeNames(
			null,
			null,
			SearchPattern.R_EXACT_MATCH,
			IJavaSearchConstants.CLASS,
			SearchEngine.createJavaSearchScope(new IJavaElement[0]),
			new Requestor(),
			IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
			null);
		// Join all types cache
		AllTypesCache.getTypes(SearchEngine.createJavaSearchScope(new IJavaElement[0]), 
			IJavaSearchConstants.CLASS, new NullProgressMonitor(), new ArrayList());
		// Join jobs
		joinJobs(0, Long.MAX_VALUE, 500);
	}
	
	public static boolean joinJobs(long minTime, long maxTime, long intervalTime) {
		long startTime= System.currentTimeMillis() + minTime;
		runEventQueue();
		while (System.currentTimeMillis() < startTime)
			runEventQueue(intervalTime);
		
		long endTime= maxTime > 0 ? System.currentTimeMillis() + maxTime : Long.MAX_VALUE;
		boolean calm= allJobsQuiet();
		while (!calm && System.currentTimeMillis() < endTime) {
			runEventQueue(intervalTime);
			calm= allJobsQuiet();
		}
//		System.out.println("--------------------------------------------------");
		return calm;
	}

	public static void sleep(int intervalTime) {
		try {
			Thread.sleep(intervalTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static boolean allJobsQuiet() {
		IJobManager jobManager= Platform.getJobManager();
		Job[] jobs= jobManager.find(null);
		for (int i= 0; i < jobs.length; i++) {
			Job job= jobs[i];
			int state= job.getState();
//			System.out.println(job.getName() + ": " + getStateName(state));
			if (state == Job.RUNNING || state == Job.WAITING) {
//				System.out.println();
				return false;
			}
		}
//		System.out.println();
		return true;
	}

//	private static String getStateName(int state) {
//		switch (state) {
//			case Job.RUNNING: return "RUNNING";
//			case Job.WAITING: return "WAITING";
//			case Job.SLEEPING: return "SLEEPING";
//			case Job.NONE: return "NONE";
//			default: return "unknown " + state;
//		}
//	}

	public static boolean showView(String viewId) throws PartInitException {
		IWorkbenchPage activePage= getActivePage();
		IViewReference view= activePage.findViewReference(viewId);
		boolean notShown= view == null;
		if (notShown)
			activePage.showView(viewId);
		return notShown;
	}

	public static boolean hideView(String viewId) {
		IWorkbenchPage activePage= getActivePage();
		IViewReference view= activePage.findViewReference(viewId);
		boolean shown= view != null;
		if (shown)
			activePage.hideView(view);
		return shown;
	}

	public static void bringToTop() {
		getActiveWorkbenchWindow().getShell().forceActive();
	}
	
	public static void forceReconcile(SourceViewer sourceViewer) {
		Accessor reconcilerAccessor= new Accessor(getReconciler(sourceViewer), AbstractReconciler.class);
		reconcilerAccessor.invoke("forceReconciling", new Object[0]);
	}
	
	public static boolean joinReconciler(SourceViewer sourceViewer, long minTime, long maxTime, long intervalTime) {
		if (minTime > 0)
			runEventQueue(minTime);
		
		long endTime= maxTime > 0 ? System.currentTimeMillis() + maxTime : Long.MAX_VALUE;
		AbstractReconciler reconciler= getReconciler(sourceViewer);
		Accessor backgroundThreadAccessor= getBackgroundThreadAccessor(reconciler);
		Accessor javaReconcilerAccessor= null;
		if (reconciler instanceof JavaReconciler)
			javaReconcilerAccessor= new Accessor(reconciler, JavaReconciler.class);
		boolean isRunning= isRunning(javaReconcilerAccessor, backgroundThreadAccessor);
		while (isRunning && System.currentTimeMillis() < endTime) {
			runEventQueue(intervalTime);
			isRunning= isRunning(javaReconcilerAccessor, backgroundThreadAccessor);
		}
		return !isRunning;
	}

	public static AbstractReconciler getReconciler(SourceViewer sourceViewer) {
		return (AbstractReconciler) new Accessor(sourceViewer, SourceViewer.class).get("fReconciler");
	}

	public static SourceViewer getSourceViewer(AbstractTextEditor editor) {
		SourceViewer sourceViewer= (SourceViewer) new Accessor(editor, AbstractTextEditor.class).invoke("getSourceViewer", new Object[0]);
		return sourceViewer;
	}

	private static Accessor getBackgroundThreadAccessor(AbstractReconciler reconciler) {
		Object backgroundThread= new Accessor(reconciler, AbstractReconciler.class).get("fThread");
		return new Accessor(backgroundThread, backgroundThread.getClass());
	}

	private static boolean isRunning(Accessor javaReconcilerAccessor, Accessor backgroundThreadAccessor) {
		return (javaReconcilerAccessor != null ? !isInitialProcessDone(javaReconcilerAccessor) : false) || isDirty(backgroundThreadAccessor) || isActive(backgroundThreadAccessor);
	}

	private static boolean isInitialProcessDone(Accessor javaReconcilerAccessor) {
		return ((Boolean) javaReconcilerAccessor.get("fIninitalProcessDone")).booleanValue();
	}

	private static boolean isDirty(Accessor backgroundThreadAccessor) {
		return ((Boolean) backgroundThreadAccessor.invoke("isDirty", new Object[0])).booleanValue();
	}

	private static boolean isActive(Accessor backgroundThreadAccessor) {
		return ((Boolean) backgroundThreadAccessor.invoke("isActive", new Object[0])).booleanValue();
	}
}
