
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.columbia.db.RDSManager;

/**
 * Servlet implementation class RateServlet
 */

public class RateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static RDSManager rdsManager;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RateServlet() {
		super();

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		
		rdsManager = new RDSManager();
		String videoName = (String) request.getParameter("rateVideoName")
				.replaceAll("_", " ");
		int rating = 0;

		if (videoName != null) {

			if (request.getParameter("rating") != null) {
				rating = Integer.parseInt(request.getParameter("rating"));
			}

			rdsManager.rateVideo(videoName, rating);

		}
		getServletContext().getRequestDispatcher("/myYouTube.jsp").forward(
				request, response);
	}

}
