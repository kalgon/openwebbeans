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
package org.apache.webbeans.corespi.scanner;


import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.webbeans.config.OpenWebBeansConfiguration;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.corespi.se.BeansXmlAnnotationDB;
import org.apache.webbeans.corespi.se.DefaultBDABeansXmlScanner;
import org.apache.webbeans.exception.WebBeansDeploymentException;
import org.apache.webbeans.logger.WebBeansLogger;
import org.apache.webbeans.spi.BDABeansXmlScanner;
import org.apache.webbeans.spi.ScannerService;
import org.apache.webbeans.util.ClassUtil;
import org.scannotation.AnnotationDB;

public abstract class AbstractMetaDataDiscovery implements ScannerService
{
    protected final WebBeansLogger logger = WebBeansLogger.getLogger(getClass());

    /** Location of the beans.xml files. */
    private final Set<URL> webBeansXmlLocations = new HashSet<URL>();

    //private Map<String, InputStream> EJB_XML_LOCATIONS = new HashMap<String, InputStream>();

    /** Annotation Database */
    private AnnotationDB annotationDB;

    protected boolean isBDAScannerEnabled = false;
    protected BDABeansXmlScanner bdaBeansXmlScanner;

    protected AbstractMetaDataDiscovery()
    {
        try
        {
            annotationDB = new AnnotationDB();
            annotationDB.setScanClassAnnotations(true);
            annotationDB.crossReferenceMetaAnnotations();
            annotationDB.setScanFieldAnnotations(false);
            annotationDB.setScanMethodAnnotations(false);
            annotationDB.setScanParameterAnnotations(false);
        }
        catch(Exception e)
        {
            throw new WebBeansDeploymentException(e);
        }                
    }
    
    /**
     * Configure the Web Beans Container with deployment information and fills
     * annotation database and beans.xml stream database.
     * 
     * @throws org.apache.webbeans.exception.WebBeansConfigurationException if any run time exception occurs
     */
    public void scan() throws WebBeansDeploymentException
    {
        try
        {
            configure();
        }
        catch (Exception e)
        {
            throw new WebBeansDeploymentException(e);
        }
    }
    
    
    abstract protected void configure() throws Exception;
    
    public void init(Object object)
    {
        // set per BDA beans.xml flag here because setting it in constructor
        // occurs before
        // properties are loaded.
        String usage = WebBeansContext.getInstance().getOpenWebBeansConfiguration().getProperty(OpenWebBeansConfiguration.USE_BDA_BEANSXML_SCANNER);
        this.isBDAScannerEnabled = Boolean.parseBoolean(usage);
        if (isBDAScannerEnabled)
        {
            annotationDB = new BeansXmlAnnotationDB();
            ((BeansXmlAnnotationDB)annotationDB).setBdaBeansXmlScanner(this);

            bdaBeansXmlScanner = new DefaultBDABeansXmlScanner();
        }
    }

    /**
     * @return the aNNOTATION_DB
     */
    protected AnnotationDB getAnnotationDB()
    {
        return annotationDB;
    }

    /**
     * add the given beans.xml path to the locations list 
     * @param file location path
     */
    protected void addWebBeansXmlLocation(URL file)
    {
        if(this.logger.wblWillLogInfo())
        {
            this.logger.info("added beans.xml marker: " + file.getFile());
        }
        webBeansXmlLocations.add(file);
    }

    /* (non-Javadoc)
     * @see org.apache.webbeans.corespi.ScannerService#getBeanClasses()
     */
    @Override
    public Set<Class<?>> getBeanClasses()
    {
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        Map<String,Set<String>> index = this.annotationDB.getClassIndex();
        
        if(index != null)
        {
            Set<String> strSet = index.keySet();
            if(strSet != null)
            {
                for(String str : strSet)
                {
                    classSet.add(ClassUtil.getClassFromName(str));   
                }
            }   
        }    
        
        return classSet;
    }    


    /* (non-Javadoc)
     * @see org.apache.webbeans.corespi.ScannerService#getBeanXmls()
     */
    @Override
    public Set<URL> getBeanXmls()
    {
        return Collections.unmodifiableSet(webBeansXmlLocations);
    }

    @Override
    public BDABeansXmlScanner getBDABeansXmlScanner()
    {
        return bdaBeansXmlScanner;
    }

    @Override
    public boolean isBDABeansXmlScanningEnabled()
    {
        return isBDAScannerEnabled;
    }
}
