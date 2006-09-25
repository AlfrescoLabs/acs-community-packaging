/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.web.templating.xforms;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import org.alfresco.web.templating.*;
import org.chiba.xml.util.DOMUtil;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.alfresco.service.cmr.repository.NodeRef;

public class XSLTOutputMethod
    implements TemplateOutputMethod
{

    private static final Log LOGGER = LogFactory.getLog(XSLTOutputMethod.class);

    private final NodeRef nodeRef;

    public XSLTOutputMethod(final NodeRef nodeRef)
    {
	this.nodeRef = nodeRef;
    }

    public void generate(final Document xmlContent,
			 final TemplateType tt,
			 final String sandBoxUrl,
			 final Writer out)
	throws ParserConfigurationException,
	       TransformerConfigurationException,
	       TransformerException,
	       SAXException,
	       IOException
    {
	TransformerFactory tf = TransformerFactory.newInstance();
	final TemplatingService ts = TemplatingService.getInstance();
	DOMSource source = new DOMSource(ts.parseXML(this.nodeRef));
	final Templates templates = tf.newTemplates(source);
	final Transformer t = templates.newTransformer();
	t.setURIResolver(new URIResolver()
	{
	    public Source resolve(final String href, final String base)
	        throws TransformerException
	    {
	        URI uri = null;
	        try
	        {
		    uri = new URI(sandBoxUrl + href);
	        }
	        catch (URISyntaxException e)
	        {
		    throw new TransformerException("unable to create uri " + sandBoxUrl + href, e);
	        }
	        try
	        {
		    LOGGER.debug("loading " + uri);
		    final Document d = ts.parseXML(uri.toURL().openStream());
		    LOGGER.debug("loaded " + ts.writeXMLToString(d));
		    return new DOMSource(d);
	        }
	        catch (Exception e)
	        {
		    LOGGER.warn(e);
		    throw new TransformerException("unable to load " + uri, e);
	        }
	    }
	});
	t.setParameter("avm_store_url", sandBoxUrl);
	LOGGER.debug("setting parameter avm_store_url=" + sandBoxUrl);
	final StreamResult result = new StreamResult(out);
	try
	{
	    t.transform(new DOMSource(xmlContent), result);
	}
	catch (TransformerException e)
	{
	    LOGGER.error(e.getMessageAndLocation());
	    throw e;
	}
    }
}
