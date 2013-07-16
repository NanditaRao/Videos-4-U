
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.columbia.db.RDSManager;

/**
 * Servlet implementation class DeleteServlet
 */

public class DeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static RDSManager rdsManager;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteServlet() {
		super();
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// delete from bucket
		
		AWSCredentials credentials = new PropertiesCredentials(
				DeleteServlet.class
						.getResourceAsStream("AwsCredentials.properties"));
		AmazonS3Client s3 = new AmazonS3Client(credentials);
		String videoName = (String) request.getParameter("videoName")
				.replaceAll("_", " ");

		
		if (videoName != null) {
			s3.deleteObject("ra2616.cloud2.bucket", videoName);

			// delete from database
			rdsManager = new RDSManager();
			rdsManager.deleteVideo(videoName);
		}
		getServletContext().getRequestDispatcher("/myYouTube.jsp").forward(
				request, response);
	}

}
