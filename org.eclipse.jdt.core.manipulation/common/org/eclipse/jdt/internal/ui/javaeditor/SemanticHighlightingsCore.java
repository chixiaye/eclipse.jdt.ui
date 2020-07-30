/*******************************************************************************
 * Copyright (c) 2018, 2020 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * This is an implementation of an early-draft specification developed under the Java
 * Community Process (JCP) and is made available for testing and evaluation purposes
 * only. The code is not compatible with any specification of the JCP.
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Red Hat Inc. - copied and modified from SemanticHighlightings
 *******************************************************************************/
package org.eclipse.jdt.internal.ui.javaeditor;

/**
 * Semantic highlightings
 *
 * @since 1.11
 */
public class SemanticHighlightingsCore {

	/**
	 * A named preference part that controls the highlighting of static final fields.
	 */
	public static final String STATIC_FINAL_FIELD="staticFinalField"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of static fields.
	 */
	public static final String STATIC_FIELD="staticField"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of fields.
	 */
	public static final String FIELD="field"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of method declarations.
	 */
	public static final String METHOD_DECLARATION="methodDeclarationName"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of static method invocations.
	 */
	public static final String STATIC_METHOD_INVOCATION="staticMethodInvocation"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of inherited method invocations.
	 */
	public static final String INHERITED_METHOD_INVOCATION="inheritedMethodInvocation"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of annotation element references.
	 * @since 3.1
	 */
	public static final String ANNOTATION_ELEMENT_REFERENCE="annotationElementReference"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of abstract method invocations.
	 */
	public static final String ABSTRACT_METHOD_INVOCATION="abstractMethodInvocation"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of local variables.
	 */
	public static final String LOCAL_VARIABLE_DECLARATION="localVariableDeclaration"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of local variables.
	 */
	public static final String LOCAL_VARIABLE="localVariable"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of parameter variables.
	 */
	public static final String PARAMETER_VARIABLE="parameterVariable"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of deprecated members.
	 */
	public static final String DEPRECATED_MEMBER="deprecatedMember"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of type parameters.
	 * @since 3.1
	 */
	public static final String TYPE_VARIABLE="typeParameter"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of methods
	 * (invocations and declarations).
	 *
	 * @since 3.1
	 */
	public static final String METHOD="method"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of auto(un)boxed
	 * expressions.
	 *
	 * @since 3.1
	 */
	public static final String AUTOBOXING="autoboxing"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of classes.
	 *
	 * @since 3.2
	 */
	public static final String CLASS="class"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of enums.
	 *
	 * @since 3.2
	 */
	public static final String ENUM="enum"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of interfaces.
	 *
	 * @since 3.2
	 */
	public static final String INTERFACE="interface"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of annotations.
	 *
	 * @since 3.2
	 */
	public static final String ANNOTATION="annotation"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of type arguments.
	 *
	 * @since 3.2
	 */
	public static final String TYPE_ARGUMENT="typeArgument"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of numbers.
	 *
	 * @since 3.4
	 */
	public static final String NUMBER="number"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of abstract classes.
	 *
	 * @since 3.7
	 */
	public static final String ABSTRACT_CLASS="abstractClass"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of inherited fields.
	 *
	 * @since 3.8
	 */
	public static final String INHERITED_FIELD="inheritedField"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of 'var' keywords.
	 */
	public static final String VAR_KEYWORD= "varKeyword"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of 'yield' keywords.
	 */
	public static final String YIELD_KEYWORD= "yieldKeyword"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of 'record' keywords.
	 */
	public static final String RECORD_KEYWORD= "recordKeyword"; //$NON-NLS-1$

	/**
	 * A named preference part that controls the highlighting of 'sealed' and 'non-sealed' keywords.
	 */
	public static final String SEALED_KEYWORDS= "sealedKeywords"; //$NON-NLS-1$

	/**
	 * Do not instantiate
	 */
	private SemanticHighlightingsCore() {
	}
}
