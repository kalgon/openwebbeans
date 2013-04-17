/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.webbeans.container;

import java.lang.annotation.Annotation;
import javax.enterprise.inject.spi.BeanManager;

import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.util.AnnotationUtil;

public abstract class AbstractBeanManager implements BeanManager
{

    protected abstract WebBeansContext getWebBeansContext();
    
    public boolean areInterceptorBindingsEquivalent(Annotation annotation1, Annotation annotation2)
    {
        return AnnotationUtil.isCdiAnnotationEqual(annotation1, annotation2);
    }

    public boolean areQualifiersEquivalent(Annotation annotation1, Annotation annotation2)
    {
        return AnnotationUtil.isCdiAnnotationEqual(annotation1, annotation2);
    }

    public int getInterceptorBindingHashCode(Annotation annotation)
    {
        return AnnotationUtil.getCdiAnnotationHashCode(annotation);
    }

    public int getQualifierHashCode(Annotation annotation)
    {
        return AnnotationUtil.getCdiAnnotationHashCode(annotation);
    }
}