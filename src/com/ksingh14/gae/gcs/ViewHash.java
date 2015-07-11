/**
 * 
 */
package com.ksingh14.gae.gcs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.util.logging.Level;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

/**
 * @author KaranJeet
 *
 */
public class ViewHash extends HttpServlet {

	private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
    .initialRetryDelayMillis(10)
    .retryMaxAttempts(10)
    .totalRetryPeriodMillis(15000)
    .build());
	private String BUCKETNAME = "ass3gcs";
	
	@SuppressWarnings("null")
	@Override
	  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException 
	{
		
        PrintWriter out = resp.getWriter();
        try {
        	out.println("<!DOCTYPE html>");
        	out.println("<html>     ");
        	out.println("  <head>   ");
        	out.println("    <script> ");
        	out.println("      function loadXMLDoc(url)");
        	out.println("      {                       ");
        	out.println("      var xmlhttp;            ");
        	out.println("      var txt,x,xx,i;         ");
        	out.println("      if (window.XMLHttpRequest)");
        	out.println("      {// code for IE7+, Firefox, Chrome, Opera, Safari ");
        	out.println("      xmlhttp=new XMLHttpRequest();                     ");
        	out.println("      }                                                 ");
        	out.println("      else                                              ");
        	out.println("      {// code for IE6, IE5                             ");
        	out.println("      xmlhttp=new ActiveXObject(\"Microsoft.XMLHTTP\"); ");
        	out.println("      }                                                 ");
        	out.println("      xmlhttp.onreadystatechange=function()             ");
        	out.println("      {                                                 ");
        	out.println("      if (xmlhttp.readyState==4 && xmlhttp.status==200) ");
        	out.println("      {                                                 ");
        	out.println("      txt=\"<table border='1'>                           ");
        	out.println("        <tr>                                            ");
        	out.println("          <th>Key</th>                                ");
        	out.println("          <th>Value</th>                               ");
        	out.println("        </tr>\";                                         ");
        	out.println("        x=xmlhttp.responseXML.documentElement.getElementsByTagName(\"entry\");");
        	out.println("        for (i=0;i<x.length;i                           ");
        	out.println("      {                                                 ");
        	out.println("txt=txt + \"<tr>                                         ");
        	out.println("          \";                                            ");
        	out.println("          xx=x[i].getElementsByTagName(\"string\");        ");
        	out.println("          {                                             ");
        	out.println("          try                                           ");
        	out.println("          {                                             ");
        	out.println("          txt=txt + \"<td>\" + xx[0].firstChild.nodeValue + \"</td>\";");
        	out.println("          }                                                       ");
        	out.println("          catch (er)                                              ");
        	out.println("          {                                                       ");
        	out.println("          txt=txt + \"<td> </td>\";                                 ");
        	out.println("          }                                                       ");
        	out.println("          }                                                       ");
        	out.println("          xx=x[i].getElementsByTagName(\"string\");                 ");
        	out.println("          {                                                       ");
        	out.println("          try                                                     ");
        	out.println("          {                                                       ");
        	out.println("          txt=txt + \"<td>\" + xx[0].firstChild.nodeValue + \"</td>\";");
        	out.println("          }                                                       ");
        	out.println("          catch (er)                                              ");
        	out.println("          {                                                       ");
        	out.println("          txt=txt + \"<td> </td>\";                                 ");
        	out.println("          }                                                       ");
        	out.println("          }                                                       ");
        	out.println("          txt=txt + \"                                             ");
        	out.println("        </tr>\";                                                   ");
        	out.println("        }                                                         ");
        	out.println("        txt=txt + \"                                               ");
        	out.println("      </table>\";                                                  ");
        	out.println("      document.getElementById('txtCDInfo').innerHTML=txt;         ");
        	out.println("      }                                                           ");
        	out.println("      }                                                           ");
        	out.println("      xmlhttp.open(\"GET\",url,true);                               ");
        	out.println("      xmlhttp.send();                                             ");
        	out.println("      }                                                           ");
        	out.println("    </script>                                                     ");
        	out.println("  </head>                                                         ");
        	out.println("  <body onload=\"loadXMLDoc('cd_catalog.xml')\">");
        	out.println("                                                                  ");
        	out.println("    <div id=\"txtCDInfo\">                                          ");
        	out.println("      ");
        	out.println("    </div>                                                               ");
        	out.println("                                                                         ");
        	out.println("  </body>                                                                ");
        	out.println("</html>                                                                  ");
         } finally {
            out.close();  // Always close the output writer
         }
	}
	
	  }
