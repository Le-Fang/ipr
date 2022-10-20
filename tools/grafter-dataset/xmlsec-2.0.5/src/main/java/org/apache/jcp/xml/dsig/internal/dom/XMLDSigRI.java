/**
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
/*
 * ===========================================================================
 *
 * (C) Copyright IBM Corp. 2003 All Rights Reserved.
 *
 * ===========================================================================
 */
/*
 * Portions copyright 2005 Sun Microsystems, Inc. All rights reserved.
 */
/*
 * $Id: XMLDSigRI.java 1673811 2015-04-15 15:04:02Z coheigea $
 */
package org.apache.jcp.xml.dsig.internal.dom;

import java.util.*;
import java.security.*;

import javax.xml.crypto.dsig.*;

/**
 * The XMLDSig RI Provider.
 *
 * @author Joyce Leung
 */

/**
 * Defines the XMLDSigRI provider.
 */

public final class XMLDSigRI extends Provider {

    static final long serialVersionUID = -5049765099299494554L;

    private static final String INFO = "Apache Santuario XMLDSig " +
        "(DOM XMLSignatureFactory; DOM KeyInfoFactory; " +
        "C14N 1.0, C14N 1.1, Exclusive C14N, Base64, Enveloped, XPath, " +
        "XPath2, XSLT TransformServices)";

    public XMLDSigRI() {
        /* We are the ApacheXMLDSig provider */
        super("ApacheXMLDSig", 2.05, INFO);
        
        final Map<Object, Object> map = new HashMap<Object, Object>();
        map.put("XMLSignatureFactory.DOM",
                "org.apache.jcp.xml.dsig.internal.dom.DOMXMLSignatureFactory");
        map.put("KeyInfoFactory.DOM",
                "org.apache.jcp.xml.dsig.internal.dom.DOMKeyInfoFactory");


        // Inclusive C14N
        map.put("TransformService." + CanonicalizationMethod.INCLUSIVE,
                "org.apache.jcp.xml.dsig.internal.dom.DOMCanonicalXMLC14NMethod");
        map.put("Alg.Alias.TransformService.INCLUSIVE",
                CanonicalizationMethod.INCLUSIVE);
        map.put("TransformService." + CanonicalizationMethod.INCLUSIVE +
                " MechanismType", "DOM");

        // InclusiveWithComments C14N
        map.put("TransformService." +
                CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
                "org.apache.jcp.xml.dsig.internal.dom.DOMCanonicalXMLC14NMethod");
        map.put("Alg.Alias.TransformService.INCLUSIVE_WITH_COMMENTS",
                CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS);
        map.put("TransformService." +
                CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS +
                " MechanismType", "DOM");

        // Inclusive C14N 1.1
        map.put("TransformService.http://www.w3.org/2006/12/xml-c14n11",
                "org.apache.jcp.xml.dsig.internal.dom.DOMCanonicalXMLC14N11Method");
        map.put("TransformService.http://www.w3.org/2006/12/xml-c14n11" +
                " MechanismType", "DOM");

        // InclusiveWithComments C14N 1.1
        map.put("TransformService.http://www.w3.org/2006/12/xml-c14n11#WithComments",
                "org.apache.jcp.xml.dsig.internal.dom.DOMCanonicalXMLC14N11Method");
        map.put("TransformService.http://www.w3.org/2006/12/xml-c14n11#WithComments" +
                " MechanismType", "DOM");

        // Exclusive C14N
        map.put("TransformService." + CanonicalizationMethod.EXCLUSIVE,
                "org.apache.jcp.xml.dsig.internal.dom.DOMExcC14NMethod");
        map.put("Alg.Alias.TransformService.EXCLUSIVE",
                CanonicalizationMethod.EXCLUSIVE);
        map.put("TransformService." + CanonicalizationMethod.EXCLUSIVE +
                " MechanismType", "DOM");

        // ExclusiveWithComments C14N
        map.put("TransformService." +
                CanonicalizationMethod.EXCLUSIVE_WITH_COMMENTS,
                "org.apache.jcp.xml.dsig.internal.dom.DOMExcC14NMethod");
        map.put("Alg.Alias.TransformService.EXCLUSIVE_WITH_COMMENTS",
                CanonicalizationMethod.EXCLUSIVE_WITH_COMMENTS);
        map.put("TransformService." +
                CanonicalizationMethod.EXCLUSIVE_WITH_COMMENTS +
                " MechanismType", "DOM");

        // Base64 Transform
        map.put("TransformService." + Transform.BASE64,
                "org.apache.jcp.xml.dsig.internal.dom.DOMBase64Transform");
        map.put("Alg.Alias.TransformService.BASE64", Transform.BASE64);
        map.put("TransformService." + Transform.BASE64 +
                " MechanismType", "DOM");

        // Enveloped Transform
        map.put("TransformService." + Transform.ENVELOPED,
                "org.apache.jcp.xml.dsig.internal.dom.DOMEnvelopedTransform");
        map.put("Alg.Alias.TransformService.ENVELOPED", Transform.ENVELOPED);
        map.put("TransformService." + Transform.ENVELOPED +
                " MechanismType", "DOM");

        // XPath2 Transform
        map.put("TransformService." + Transform.XPATH2,
                "org.apache.jcp.xml.dsig.internal.dom.DOMXPathFilter2Transform");
        map.put("Alg.Alias.TransformService.XPATH2", Transform.XPATH2);
        map.put("TransformService." + Transform.XPATH2 +
                " MechanismType", "DOM");

        // XPath Transform
        map.put("TransformService." + Transform.XPATH,
                "org.apache.jcp.xml.dsig.internal.dom.DOMXPathTransform");
        map.put("Alg.Alias.TransformService.XPATH", Transform.XPATH);
        map.put("TransformService." + Transform.XPATH +
                " MechanismType", "DOM");

        // XSLT Transform
        map.put("TransformService." + Transform.XSLT,
                "org.apache.jcp.xml.dsig.internal.dom.DOMXSLTTransform");
        map.put("Alg.Alias.TransformService.XSLT", Transform.XSLT);
        map.put("TransformService." + Transform.XSLT + " MechanismType", "DOM");

        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                putAll(map);
                return null;
            }
        });
    }
}
