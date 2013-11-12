package edu.uwm.cs361;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.uwm.cs361.entities.Charge;
import edu.uwm.cs361.entities.User;
import edu.uwm.cs361.util.UserConstants;

@SuppressWarnings("serial")
public class StudentChargesServlet extends HttpServlet {	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException	{
		List<String> errors = new ArrayList<String>();

		String[] classlist = {"Cooking For Dummies","Class 2","Class 3"}; 
		Charge[] charges = new Charge[classlist.length];
		
		PersistenceManager pm = getPersistenceManager();
		List<User> users = (List<User>) pm.newQuery(User.class).execute();
		User[] students;
		int numStudents = 0, count = 0;
		try {
			numStudents = 0;
			count = 0;
			for (User user : users) {
				if (user.getUser_type()==UserConstants.STUDENT_NUM) {
					numStudents++;			
				}
			}
			students = new User[numStudents];
			for (User user : users) {
				if (user.getUser_type()==UserConstants.STUDENT_NUM) {
					students[count] = user;
					++count;
				}
			}
		} finally {
			pm.close();
		}
		pm = getPersistenceManager();
		Charge charge;
		for (int i=0; i<students.length; i++) {
			for (int j=0; j<classlist.length; j++) {
				String[] date_string = req.getParameter(students[i].getUser_id()+"_"+classlist[j]+"_deadline").split("-"); // String[month, day, year]
				int[] date_int = new int[date_string.length]; // Int[3]
				for (int k=0; k<date_string.length; k++) {
					date_int[k] = Integer.parseInt(date_string[k]); // Int[month, day, year]
				}
				Date deadline = new Date(date_int[2],(date_int[0])-1,date_int[1]); // Date(year, month-1, day)
				charge = new Charge(Double.parseDouble(req.getParameter(students[i].getUser_id()+"_"+classlist[j]+"_charge")), deadline ,""); // Charge(amount, deadline, reason)
				charges[j] = charge;
				students[i].setCharges(charges);
				int day = charges[j].getDeadline().getDate();
				int month = charges[j].getDeadline().getMonth()+1;
				int year = charges[j].getDeadline().getYear();
				System.out.println(charges[j].getAmount());
				System.out.println(month+"-"+day+"-"+year);
			}
		}
		
		try {
			if (errors.size() > 0) {
				req.setAttribute("errors", errors);
				req.getRequestDispatcher("studentCharges.jsp").forward(req, resp);
			} else {
				for(User student : students) {
					pm.makePersistent(student);					
				}
				resp.sendRedirect("studentCharges.jsp");
			}
		} catch (ServletException e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
	}
	
	private PersistenceManager getPersistenceManager() {
		return JDOHelper.getPersistenceManagerFactory("transactions-optional").getPersistenceManager();
	}
}
