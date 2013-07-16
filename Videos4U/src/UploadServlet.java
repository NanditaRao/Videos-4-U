
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.columbia.db.*;

/**
 /* Servlet implementation class UploadServlet
 */

public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String UPLOAD_DIRECTORY = "uploadDir";
	private static RDSManager rdsManager;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UploadServlet() {
		super();

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		boolean isMulti = ServletFileUpload.isMultipartContent(request);
		rdsManager = new RDSManager();

		if (isMulti) {

			DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
			AWSCredentials credentials = new PropertiesCredentials(
					UploadServlet.class
					.getResourceAsStream("AwsCredentials.properties"));

			AmazonS3Client s3 = new AmazonS3Client(credentials);

			diskFileItemFactory.setRepository(new File(System.getProperty("java.io.tmpdir")));

			ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);

			// constructs the directory path to store upload file
			String uploadPath = getServletContext().getRealPath("")
					+ File.separator + UPLOAD_DIRECTORY;
			System.out.println("uploadpath is: "+ uploadPath);
			// creates the directory if it does not exist
			File uploadDirectory = new File(uploadPath);
			if (!uploadDirectory.exists()) {
				uploadDirectory.mkdir();
			}

			try {
				// parses the request's content to extract file data
				List formItemList = servletFileUpload.parseRequest(request);
				Iterator i = formItemList.iterator();

				// iterates over form's fields
				while (i.hasNext()) {
					FileItem fileItem = (FileItem) i.next();
					// processes only fields that are not form fields
					if (!fileItem.isFormField()) {
						String fileName = new File(fileItem.getName()).getName();
						if ((!fileName.contains(".mp4")) && (!fileName.contains(".flv")) ) {
							request.setAttribute("message", "Only mp4 or flv files can be uploaded!!");
							throw new Exception("Please select .mp4 or .flv files only");

						} else {
							String filePath = uploadPath + File.separator
									+ fileName;
							File storeFile = new File(filePath);
							

							// saves the file on disk
							fileItem.write(storeFile);
                            
							int count = new Random().nextInt(100);
                            
							PutObjectRequest putObjectRequest = new PutObjectRequest(
									"ra2616.cloud2.bucket", fileName, storeFile);
                            
							CannedAccessControlList accessControlList = CannedAccessControlList.PublicReadWrite;
							putObjectRequest.setCannedAcl(accessControlList);
							s3.putObject(putObjectRequest);

							// update the MyRatingTable with video name and
							// default rating
							
							rdsManager.setVideo(fileName, 0);
						}
					}
				}
				request.setAttribute("message", "File uploaded!");
			} catch (Exception ex) {
				request.setAttribute("message", "Error!!!!!! "
						+ ex.getMessage());
			}

			getServletContext().getRequestDispatcher("/myYouTube.jsp").forward(
					request, response);
		}
	}

}
