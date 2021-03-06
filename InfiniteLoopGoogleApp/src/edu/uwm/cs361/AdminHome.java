package edu.uwm.cs361;

import java.io.IOException;
import java.util.*;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.uwm.cs361.entities.*;

@SuppressWarnings("serial")
public class AdminHome extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException	{
		req.setAttribute("balance", getBalance());
		req.getRequestDispatcher("AdminHome.jsp").forward(req, resp);
	}
	
	@SuppressWarnings("unchecked")
	private double getBalance() {
		PersistenceManager pm = getPersistenceManager();
		List<Student> students = new ArrayList<Student>();
		double balance = 0;
		try {
			students = (List<Student>) pm.newQuery(Student.class).execute();
			for (Student student : students) {
				for (Charge charge : student.getCharges()) {
					balance += charge.getAmount();
				}
			}
		} finally {
			pm.close();
		}
		
		if(balance == 0.0) {
			return balance;
		} else {
			return balance*-1;
		}
	}

	private PersistenceManager getPersistenceManager() {
		return JDOHelper.getPersistenceManagerFactory("transactions-optional").getPersistenceManager();
	}
}