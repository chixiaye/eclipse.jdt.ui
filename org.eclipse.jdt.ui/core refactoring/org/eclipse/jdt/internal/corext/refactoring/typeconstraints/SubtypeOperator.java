/*******************************************************************************
 * Copyright (c) 2000, 2002 International Business Machines Corp. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v0.5 
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v05.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.jdt.internal.corext.refactoring.typeconstraints;


public final class SubtypeOperator extends ConstraintOperator{
	
	public static final String OPERATOR_STRING= "<="; //$NON-NLS-1$

	private static final SubtypeOperator fgInstance= new SubtypeOperator(); 
	
	public static SubtypeOperator create(){
		return fgInstance;
	}
	
	//we don't need more than 1 instance
	private SubtypeOperator() {
		super(OPERATOR_STRING);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.refactoring.typeconstraints.ConstraintOperator#isSatisfied(org.eclipse.jdt.internal.corext.refactoring.typeconstraints.ConstraintVariable, org.eclipse.jdt.internal.corext.refactoring.typeconstraints.ConstraintVariable)
	 */
	public boolean isSatisfied(ConstraintVariable var1, ConstraintVariable var2) {
		return var1.isSubtypeOf(var2);
	}
}
