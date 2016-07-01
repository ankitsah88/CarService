package web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;

import to.OrderTo;
import dbconnection.IntializeDataBaseWithData;

/**
 * Servlet implementation class Service
 */
@WebServlet(urlPatterns = "/Service/*", loadOnStartup = 1, asyncSupported = true)
public class Service extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Service() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		System.out.println("My servlet has been initialized");
		IntializeDataBaseWithData.getInstance();
		System.out.println("Done");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Map<String, Object> data = new HashMap<String, Object>();
		IntializeDataBaseWithData iDB = IntializeDataBaseWithData.getInstance();
		String jsonStr = null;
		ObjectMapper mapperObj = new ObjectMapper();
		if (request.getParameter("color") != null) {
			data.put("color", request.getParameter("color"));
		}

		if (request.getRequestURL().toString().indexOf("startRide") != -1) {
			// user wants to start ride
			jsonStr = mapperObj.writeValueAsString(iDB.startRide(request
					.getParameter("tId")));
		} else {
			if (request.getRequestURL().toString().indexOf("findCar") != -1) {
				if (request.getParameter("location") != null) {
					data.put("location", request.getParameter("location"));
				} else {

					// without location can't find the car
					throw new ServletException("location not found");
				}
				if (request.getParameter("isBook") != null) {
					if (request.getParameter("userId") == null) {
						// without user can't book the car
						throw new ServletException("UserId not found");
					}
					data.put("userId", request.getParameter("userId"));
					data.put("isBook", request.getParameter("isBook"));

					// for booking a car
					OrderTo carTo = iDB.bookCar(data);
					if (carTo == null) {
						jsonStr = "Cannot find any car available at this time. We regret for inconvenience";
					} else {
						jsonStr = mapperObj.writeValueAsString(carTo);
					}
				} else {
					jsonStr = mapperObj.writeValueAsString(iDB.getCar(data));
				}

			} else if (request.getRequestURL().toString().indexOf("findAllCar") != -1) {
				// find all available cars
				jsonStr = mapperObj.writeValueAsString(iDB.getAllCar(data));
			} else if (request.getRequestURL().toString().indexOf("endRide") != -1) {
				data.put("tId", request.getParameter("tId"));
				if (request.getParameter("location") != null) {
					data.put("location", request.getParameter("location"));
				} else {
					// without location can't find the car
					throw new ServletException("location not found");
				}
				// in case user wants to end the ride
				jsonStr = mapperObj.writeValueAsString(iDB.endRide(data));
			}
		}
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(jsonStr);
		out.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
