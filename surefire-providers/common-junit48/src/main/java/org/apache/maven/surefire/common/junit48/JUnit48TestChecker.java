package org.apache.maven.surefire.common.junit48;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.maven.surefire.NonAbstractClassFilter;
import org.apache.maven.surefire.common.junit4.JUnit4TestChecker;
import org.apache.maven.surefire.util.ReflectionUtils;
import org.apache.maven.surefire.util.ScannerFilter;
import org.junit.experimental.runners.Enclosed;

/**
 * @author Geoff Denning
 */
public class JUnit48TestChecker
    implements ScannerFilter
{
    private final NonAbstractClassFilter nonAbstractClassFilter;

    private final Class runWith;

    private final JUnit4TestChecker jUnit4TestChecker;


    public JUnit48TestChecker( ClassLoader testClassLoader )
    {
        this.jUnit4TestChecker = new JUnit4TestChecker( testClassLoader );
        this.runWith = getJUnitClass( testClassLoader, org.junit.runner.RunWith.class.getName() );
        this.nonAbstractClassFilter = new NonAbstractClassFilter();
    }

    public boolean accept( Class testClass )
    {
        return jUnit4TestChecker.accept( testClass ) || isValidJUnit48Test( testClass );
    }

    @SuppressWarnings( { "unchecked" } )
    private boolean isValidJUnit48Test( Class testClass )
    {
        Annotation runWithAnnotation = null;
        if ( runWith != null )
        {
            runWithAnnotation = testClass.getAnnotation( runWith );
        }

        // If class is marked @RunWith(Enclosed.class) then the top-level class can be abstract
        if ( !nonAbstractClassFilter.accept( testClass )
             || ( runWithAnnotation != null && runWithAnnotation.equals(Enclosed.class)) )
        {
            return false;
        }

        if ( runWithAnnotation != null )
        {
            return true;
        }

        Class classToCheck = testClass;
        while ( classToCheck != null )
        {
            if ( checkforTestAnnotatedMethod( classToCheck ) )
            {
                return true;
            }
            classToCheck = classToCheck.getSuperclass();
        }
        return false;
    }

    private boolean checkforTestAnnotatedMethod( Class testClass )
    {
        for ( Method lMethod : testClass.getDeclaredMethods() )
        {
            for ( Annotation lAnnotation : lMethod.getAnnotations() )
            {
                if ( org.junit.Test.class.isAssignableFrom( lAnnotation.annotationType() ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    private Class getJUnitClass( ClassLoader classLoader, String className )
    {
        return ReflectionUtils.tryLoadClass( classLoader, className );
    }

}
