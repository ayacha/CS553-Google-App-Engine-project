/**
 * 
 */
package com.ksingh14.gae.gcs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.taskdefs.Sleep;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.appengine.api.*;

/**
 * @author KaranJeet
 *
 */
@SuppressWarnings("serial")
public class UploadController extends HttpServlet{

	 private static final Logger log =
		      Logger.getLogger(FileUpload.class.getName());
	 
	 private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
     .initialRetryDelayMillis(10)
     .retryMaxAttempts(10)
     .totalRetryPeriodMillis(15000)
     .build());

	@Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {try {
            ServletFileUpload upload = new ServletFileUpload();
            res.setContentType("text/plain");
            log.warning("reached upload controller doPost");
            FileItemIterator iterator = upload.getItemIterator(req);
            //log.warning("req.tostring = "+req.toString());
            final List<FileItemStream> list = new ArrayList<FileItemStream>();
            log.warning("List population started");
            while(iterator.hasNext())
            {
            	list.add(iterator.next());
            }
            log.warning("List completed started");
            
            for(int i=0;i<1;i++)
            {
            Thread thread = ThreadManager.createThreadForCurrentRequest(new Runnable() {
            	  public void run() {
            	    try {
            	      log.warning("Thread started");
            	      log.warning("list size = "+list.size());
            	      
            	      for(int i=0;i<list.size();i++)
            	      {            	    	  
            	    	  log.warning("starting upload");
                          FileItemStream item = list.get(i);
                          InputStream stream = item.openStream();
                          log.warning("reached iterator");
                          if (item.isFormField()) {
                            log.warning("Got a form field: " + item.getFieldName());
                          } else {
                            log.warning("Got an uploaded file: " + item.getFieldName() +
                                        ", name = " + item.getName());

                            GcsOutputChannel outputChannel =
                          	        gcsService.createOrReplace(getFileName(item.getName()), GcsFileOptions.getDefaultInstance());
                          	copy(stream, Channels.newOutputStream(outputChannel));
                          	
                          }
                        }
            	    } catch (Exception ex) {
            	    	log.warning(ex.getMessage());
            	      throw new RuntimeException("Interrupted in loop:", ex);
            	    }
            	  }
            	});
            thread.start();
            }
            TimeUnit.SECONDS.sleep(5);

           /* while (iterator.hasNext()) {
              FileItemStream item = iterator.next();
              InputStream stream = item.openStream();
              log.warning("reached iterator");
              if (item.isFormField()) {
                log.warning("Got a form field: " + item.getFieldName());
              } else {
                log.warning("Got an uploaded file: " + item.getFieldName() +
                            ", name = " + item.getName());

                GcsOutputChannel outputChannel =
              	        gcsService.createOrReplace(getFileName(item.getName()), GcsFileOptions.getDefaultInstance());
              	copy(stream, Channels.newOutputStream(outputChannel));
              	
              }
            }*/
            log.warning("exited upload controller doPost");
          } catch (Exception ex) {
        	  log.warning("exception - "+ex.getMessage());
            throw new ServletException(ex);
          }
        }
	
	private GcsFilename getFileName(String filename) {
	    return new GcsFilename("ass3gcs", "test/"+filename);
	  }

	  /**
	   * Transfer the data from the inputStream to the outputStream. Then close both streams.
	   */
	  private void copy(InputStream input, OutputStream output) throws IOException {
		  int size = 1024*1024;
	    try {
	      byte[] buffer = new byte[size];
	      int bytesRead = input.read(buffer);
	      while (bytesRead != -1) {
	        output.write(buffer, 0, bytesRead);
	        bytesRead = input.read(buffer);
	      }
	    } finally {
	      input.close();
	      output.close();
	    }
	  }
}
