<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="java.util.logging.Logger"%>
<%
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
%>


<html>
    <head>
        <title>Upload Test</title>
    </head>
    <body onload="checkStatus();">
        <form name="uploadForm" action="<%= blobstoreService.createUploadUrl("/upload") %>" method="post" enctype="multipart/form-data">
            <input type="file" name="myFile" multiple="multiple">
            <input type="submit" value="Submit" onclick="fileUpload();">
        </form>
        
        <form action="/UploadController" enctype="multipart/form-data" method="post">  
 			<input type="file" name="file" multiple="multiple">  
  			<input type="submit" value="Submit">
		</form>
        <br />
        <p id="statusP"></p>
        <br/>
        <a href='http://storage.googleapis.com/ass3gcs/hash.xml'>View HashTable</a>
        <script type="text/javascript">
        function viewHashMap()
        {
        	window.location.replace("/viewHash");
        }
        
        function checkStatus()
        {
        	var status = getQueryVariable("status");
        	 if(status!=null)
        		 document.getElementById('statusP').innerHTML = status;
        }
        
        function getQueryVariable(variable)
        {
               var query = window.location.search.substring(1);
               var vars = query.split("&");
               for (var i=0;i<vars.length;i++) {
                       var pair = vars[i].split("=");
                       if(pair[0] == variable){return pair[1];}
               }
               return();
        }
        
        function fileUpload() {
            if (window.File && window.FileList) {
                var fd = new FormData();
                var files = document.getElementById('myFile').files;
                for (var i = 0; i < files.length; i++) {  
                  fd.append("file"+i, files[i]);
                }
                var xhr = new XMLHttpRequest();
                xhr.open("POST", document.getElementById('uploadForm').action);
                xhr.send(fd);
              } else {
                document.getElementById('uploadForm').submit();   //no html5
              }
          }
        </script>
    </body>
</html>