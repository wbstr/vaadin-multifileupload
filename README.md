vaadin-multifileupload
======================
MultiFileUpload is an Add-on UI Component for the Vaadin 7 framework.

MultiFileUpload provides a nice feedback for monitoring the upload progress. It is a wrapper component, which creates an instance of com.vaadin.ui.Upload or a modified version of the org.vaadin.easyuploads.MultiUpload, depends on the browser is supported or not. 
MultiFileUpload components are in the same view, use a shared UploadStateWindow for displaying an upload queue and a progress indicator for each field. 
In browsers which not supported, this field behaves like the stock Upload component with the shared window functionality.
The idea comes from the EasyUploads Vaadin addon and the demo of the File Upload in the Vaadin Sampler. 

Supported HTML5 capable browsers for multiple upload: Firefox, Chrome, Safari 
Supported browsers for single upload: all 

With MultiFileUpload you can do the following: 
-Select multiple files at once to upload if your browser support it. 
-Cancel the current upload or remove files from the upload queue. 
-Force the MultiFileUpload to behave like the stock Upload component with the shared window functionality. 

It's easy to use! 
