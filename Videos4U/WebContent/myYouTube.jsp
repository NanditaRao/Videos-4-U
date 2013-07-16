<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="com.amazonaws.*"%>
<%@ page import="com.amazonaws.auth.*"%>
<%@ page import="com.amazonaws.services.ec2.*"%>
<%@ page import="com.amazonaws.services.ec2.model.*"%>
<%@ page import="com.amazonaws.services.s3.*"%>
<%@ page import="com.amazonaws.services.s3.model.*"%>
<%@ page import="com.amazonaws.services.simpledb.*"%>
<%@ page import="com.amazonaws.services.simpledb.model.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.columbia.db.RDSManager"%>


<%!// Share the client objects across threads to
    // avoid creating new clients for each web request
    private AmazonEC2      ec2;
    private AmazonS3        s3;
    private AmazonSimpleDB sdb;
    private RDSManager     rdsMgr;%>

<%
    /*
     * AWS Elastic Beanstalk checks your application's health by periodically
     * sending an HTTP HEAD request to a resource in your application. By
     * default, this is the root or default resource in your application,
     * but can be configured for each environment.
     *
     * Here, we report success as long as the app server is up, but skip
     * generating the whole page since this is a HEAD request only. You
     * can employ more sophisticated health checks in your application.
     */
    if (request.getMethod().equals("HEAD")) return;
%>

<%
    if (ec2 == null) {
        AWSCredentials credentials = new PropertiesCredentials(
            getClass().getClassLoader().getResourceAsStream("AwsCredentials.properties"));
        ec2 = new AmazonEC2Client(credentials);
        s3  = new AmazonS3Client(credentials);
        sdb = new AmazonSimpleDBClient(credentials);
        rdsMgr = new RDSManager();
    }
	
%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-type" content="text/html; charset=utf-8">
<title>Welcome to myYouTube!</title>
<link rel="stylesheet" href="styles/styles.css" type="text/css"
	media="screen">
<script type="text/javascript"
	src="https://s3.amazonaws.com/ronan.jwplayer/jwplayer/jwplayer.js">
	</script>
<script type="text/javascript">jwplayer.key="IFhVDTGUAS2rMT6B2r9dmwEzCZv17bBbXti9RA==";</script>
<script type="text/javascript">
img=new Image();
img.src= "http://images.webestools.com/buttons.php?frm=2&amp;btn_type=26&amp;txt=Play";
</script>
<script type="text/javascript">
img=new Image();
img.src= "http://images.webestools.com/buttons.php?frm=2&amp;btn_type=26&amp;txt=Delete";
</script>
<script type="text/javascript">
img=new Image();
img.src= "http://images.webestools.com/buttons.php?frm=2&amp;btn_type=24&amp;txt=Upload";
</script>
</head>
<body >


	<div id="content" class="container" align="center">
		<br />
		<h1>
			<font color="black"><big><b>MyYouTube</b></big>
		</h1>
		<br />

		<div id="myplayer">Loading the player ...</div>

		<script type="text/javascript">		
		
		jwplayer('myplayer').setup({
		 
		                file: "https://s3.amazonaws.com/ra2616.cloud2.bucket/HowToUseRDS.mp4",	
		                image: "images/LOGO.png",
		                height:300,		
		                width:450,		
		                allowfullscreen: true,		
		                title: "Video!!!!!!!",		
		               modes: [{type: "flash", src:"https://s3.amazonaws.com/ronan.jwplayer/jwplayer/jwplayer.swf"},	 
		                 {type: "html5", config:{file:"http://dj87f1qgzp8sd.cloudfront.net/HowToUseRDS.mp4",title: "Welcome",provider:"video"}} ], 	
		              
		                provider: "rtmp",		
		                streamer: "rtmp://s2fzoi0fa4g1dm.cloudfront.net/cfx/st",
		                listbar : {
		                	position: 'bottom',
		                	size:85
		                },
		            	autostart: true
		                           }); 
		
		

		</script>



		<h3><span style="background-color:#FCDC3B; color:#EE0000">${requestScope.message}</span></h3>
		<h2>Upload:</h2>
		File to be uploaded: <br />

		<form action="UploadServlet" method="post"
			enctype="multipart/form-data">
			<input type="file" name="file" size="50" /> <input type="image" src="http://images.webestools.com/buttons.php?frm=1&amp;btn_type=24&amp;txt=Upload"
				value="Submit" />

		</form>

		<h2>List of the videos</h2>
		<table >
			<%
				String p ="https://s3.amazonaws.com/";
						
						String bucket_name = "ra2616.cloud2.bucket";
						LinkedList<String> videos = rdsMgr.getVideo();
						if( videos != null) {
							for(int i1 =0; i1 < videos.size(); i1++)
							{
								String url =p + bucket_name+"/"+ videos.get(i1).replace(" ","+");
			%>

			<tr>

				<% if(videos.get(i1).contains(".mp4") || videos.get(i1).contains(".flv")){%>
				<td>
					<p>
						<b><font face="Calibri" size="5" ><%=videos.get(i1)  %></font></b></p>
				</td>

				<td>
				<img style="border:0px;" src="http://images.webestools.com/buttons.php?frm=1&amp;btn_type=26&amp;txt=Play" onclick="jwplayer('myplayer').load({file: '<%=url %>', image:'images/LOGO.png', title:'<%= videos.get(i1) %>'});jwplayer('myplayer').play();" alt="Play" /></td>

				<td><form action="DeleteServlet" method="post"
						enctype="multipart/">
						<input type="hidden" name="videoName"
							value=<%= videos.get(i1).replaceAll(" ", "_") %>>
							<input
							type="image" src="http://images.webestools.com/buttons.php?frm=1&amp;btn_type=26&amp;txt=Delete" name="Delete" value="Submit">
					</form></td>

				<td><p>
						&nbsp;&nbsp;
						Current Rating: <%=rdsMgr.getObjectRating(videos.get(i1))%>
						&nbsp;
					</p></td>

				<td><form action="RateServlet" method="post"
						enctype="multipart/">
						<input type="hidden" name="rateVideoName"
							value=<%= videos.get(i1).replaceAll(" ", "_") %>> </td>
						<tr><td>Select Rating	<input type = "radio"
							name="rating" value ="1" onchange="this.form.submit()"/>1
							<input type = "radio"
							name="rating" value ="2" onchange="this.form.submit()"/>2
							<input type = "radio"
							name="rating" value ="3" onchange="this.form.submit()"/>3
							<input type = "radio"
							name="rating" value ="4" onchange="this.form.submit()"/>4
							<input type = "radio"
							name="rating" value ="5" onchange="this.form.submit()"/>5
							
						</td>
						
					</form> <%} 					   
					   
					   }%>
			</tr>
			<% } %>
		</table>


	</div>
</body>
</html>