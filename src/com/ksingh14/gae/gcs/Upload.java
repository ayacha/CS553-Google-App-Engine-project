package com.ksingh14.gae.gcs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.memcache.AsyncMemcacheService;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@SuppressWarnings("serial")
public class Upload extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    private String BUCKETNAME = "ass3gcs";
    private static final Logger log = Logger.getLogger(Upload.class.getName());
    private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
    .initialRetryDelayMillis(10)
    .retryMaxAttempts(10)
    .totalRetryPeriodMillis(15000)
    .build());
    
    @SuppressWarnings("null")
	@Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {

    	try
    	{
    	Map<String, List<BlobInfo>> blobsData = blobstoreService.getBlobInfos(req);
    	HashMap<String, Object> hash = new HashMap<String, Object>();
        
        // Using the asynchronous cache
        AsyncMemcacheService asyncCache = MemcacheServiceFactory.getAsyncMemcacheService();
        asyncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
        //Object[] keys = blobsData.keySet().toArray();
        //List<String> keys = new ArrayList<String>(blobsData.keySet());
    	for (String key : blobsData.keySet())
    	//for(int i=0;i<keys.length/2;i++)
    	    {
    			//Object key = keys[i];
    			List<BlobInfo> blobList = blobsData.get(key);
    	        for(BlobInfo blob:blobList.subList(0, blobList.size()))
    	        {
    	        	if(blob.getSize()>100*1024)
    	        	{
    	            byte[] b = new byte[(int)blob.getSize()];
    	            BlobstoreInputStream in = new BlobstoreInputStream(blob.getBlobKey());
    	            in.read(b);

    	            GcsService gcsService = GcsServiceFactory.createGcsService();
    	            GcsFilename filename = new GcsFilename(BUCKETNAME, "test/"+blob.getFilename());
    	            GcsFileOptions options = new GcsFileOptions.Builder()
    	            .mimeType(blob.getContentType())
    	            .acl("authenticated-read")
    	            .addUserMetadata("myfield1", "my field value")
    	            .build();
    	            hash.put(blob.getFilename(), blob.getBlobKey().getKeyString());
    	            gcsService.createOrReplace(filename, options,ByteBuffer.wrap(b));
    	            Future<Object> futureValue = asyncCache.get(blob.getBlobKey().getKeyString());
    	            byte[] value = (byte[]) futureValue.get(); // read from cache
    	            asyncCache.put(blob.getFilename(), blob.getBlobKey().getKeyString()); // populate cache
    	            in.close();
    	        	}
    	        	else
    	        	{
    	        		byte[] b = new byte[(int)blob.getSize()];
        	            BlobstoreInputStream in = new BlobstoreInputStream(blob.getBlobKey());
        	            in.read(b);
        	            Future<Object> futureValue = asyncCache.get(blob.getBlobKey().getKeyString());
        	            byte[] value = (byte[]) futureValue.get(); // read from cache
        	            //if (value == null) 
        	            
        	            hash.put(blob.getFilename(), new String(b,"UTF-8"));
        	            asyncCache.put(blob.getFilename(), new String(b,"UTF-8")); // populate cache
        	            in.close();
    	        	}
    	        }
    	    }
    	
    	XStream xStream = new XStream(new DomDriver());
    	xStream.alias("hash", java.util.HashMap.class);
    	String xml = xStream.toXML(hash);
    	GcsOutputChannel outputChannel =
    	        gcsService.createOrReplace(new GcsFilename(BUCKETNAME, "hash.xml"), GcsFileOptions.getDefaultInstance());
    	copy(IOUtils.toInputStream(xml,"UTF-8"), Channels.newOutputStream(outputChannel),xml.length()+1);
    	res.sendRedirect("/index.jsp?status=Successful");
    	}	
    	catch(Exception ex)
    	{
    		res.sendRedirect("/index.jsp?status=FAIL");
    		log.warning(ex.getMessage());
    	}
        //Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
        //BlobKey blobKey = blobs.get("myFile");

        //if (blobKey == null) {
        //    res.sendRedirect("/");
        //} else {
        //    res.sendRedirect("/serve?blob-key=" + blobKey.getKeyString());
        //}
    }
    
    private void copy(InputStream input, OutputStream output, int size) throws IOException {
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