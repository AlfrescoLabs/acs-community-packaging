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
package org.alfresco.web.bean.wcm;

import java.text.MessageFormat;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.alfresco.repo.domain.PropertyValue;
import org.alfresco.service.cmr.avm.AVMService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.config.ClientConfigElement;

/**
 * @author Kevin Roast
 */
public final class AVMConstants
{
   /**
    * Private constructor
    */
   private AVMConstants()
   {
   }
   
   public static String buildAVMStagingStoreName(String store)
   {
      return store + AVMConstants.STORE_STAGING;
   }
   
   public static String buildAVMStagingPreviewStoreName(String store)
   {
      return store + AVMConstants.STORE_PREVIEW;
   }
   
   public static String buildAVMUserMainStoreName(String store, String username)
   {
      return store + '-' + username + AVMConstants.STORE_MAIN;
   }
   
   public static String buildAVMUserPreviewStoreName(String store, String username)
   {
      return store + '-' + username + AVMConstants.STORE_PREVIEW;
   }
   
   public static String buildAVMStoreRootPath(String store)
   {
      return store + ":/" + DIR_APPBASE + '/' + DIR_WEBAPPS;
   }
   
   public static String buildAVMStoreUrl(String store)
   {
      if (store.indexOf(':') != -1)
      {
         store = store.substring(0, store.indexOf(':'));
      }
      ClientConfigElement config = Application.getClientConfig(FacesContext.getCurrentInstance());
      return MessageFormat.format(PREVIEW_SANDBOX_URL, lookupStoreDNS(store), config.getWCMDomain(), config.getWCMPort());
   }
   
   public static String buildAVMAssetUrl(String store, String assetPath)
   {
      if (assetPath.startsWith('/' + DIR_APPBASE + '/' + DIR_WEBAPPS))
      {
         assetPath = assetPath.substring(('/' + DIR_APPBASE + '/' + DIR_WEBAPPS).length());
      }
      if (assetPath.length() == 0 || assetPath.charAt(0) != '/')
      {
         assetPath = '/' + assetPath;
      }
      
      ClientConfigElement config = Application.getClientConfig(FacesContext.getCurrentInstance());
      return MessageFormat.format(PREVIEW_ASSET_URL, lookupStoreDNS(store), config.getWCMDomain(), config.getWCMPort(), assetPath);
   }
   
   public static String buildAVMAssetUrl(final String avmPath)
   {
      final String[] s = avmPath.split(":");
      if (s.length != 2)
      {
         throw new IllegalArgumentException("expected exactly one ':' in " + avmPath);
      }
      return AVMConstants.buildAVMAssetUrl(s[0], s[1]);
   }
   
   public static String lookupStoreDNS(String store)
   {
      String dns = null;
      
      AVMService avmService = Repository.getServiceRegistry(FacesContext.getCurrentInstance()).getAVMService();
      Map<QName, PropertyValue> props = avmService.queryStorePropertyKey(store, QName.createQName(null, PROP_DNS + '%'));
      if (props.size() == 1)
      {
         dns = props.entrySet().iterator().next().getKey().getLocalName().substring(PROP_DNS.length());
      }
      
      return dns;
   }
   
   // names of the stores representing the layers for an AVM website
   public final static String STORE_STAGING = "-staging";
   public final static String STORE_MAIN = "-main";
   public final static String STORE_PREVIEW = "-preview";
   
   // system directories at the top level of an AVM website
   public final static String DIR_APPBASE = "appBase";
   public final static String DIR_WEBAPPS = "avm_webapps";
   
   // system property keys for sandbox identification and DNS virtualisation mapping
   public final static String PROP_SANDBOXID = ".sandbox-id.";
   public final static String PROP_SANDBOX_STAGING_MAIN = ".sandbox.staging.main";
   public final static String PROP_SANDBOX_STAGING_PREVIEW = ".sandbox.staging.preview";
   public final static String PROP_SANDBOX_AUTHOR_MAIN = ".sandbox.author.main";
   public final static String PROP_SANDBOX_AUTHOR_PREVIEW = ".sandbox.author.preview";
   public final static String PROP_DNS = ".dns.";
   public final static String PROP_WEBSITE_NAME = ".website.name";
   public final static String PROP_SANDBOX_STORE_PREFIX = ".sandbox.store.";
   public final static String SPACE_ICON_WEBSITE = "space-icon-website";
   
   // URLs for preview of sandboxes and assets
   public final static String PREVIEW_SANDBOX_URL = "http://www-{0}.avm.{1}:{2}";
   public final static String PREVIEW_ASSET_URL = "http://www-{0}.avm.{1}:{2}{3}";
}
