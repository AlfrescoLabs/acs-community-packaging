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
package org.alfresco.web.templating;

import org.w3c.dom.Document;
import java.util.List;
import java.io.Serializable;
//import org.alfresco.service.cmr.repository.NodeRef;

public interface TemplateType
    extends Serializable
{

    public String getName();

    public Document getSchema();

//    public void setSchemaNodeRef(final NodeRef nodeRef);
//    
//    public NodeRef getSchemaNodeRef();

    public Document getSampleXml(final String rootTagName);

    public List<TemplateInputMethod> getInputMethods();

    public void addOutputMethod(TemplateOutputMethod output);

    public List<TemplateOutputMethod> getOutputMethods();
}
