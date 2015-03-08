package com.chatit.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.chatit.exceptions.AlreadyReportedException;
import com.chatit.model.Report;
import com.chatit.util.Queries;

////////////////////////////
//
// Report CRUD Operations
//
///////////////////////////

public class ReportDAO {
	
	Connection c;
	
	PreparedStatement ps = null;
	
	ResultSet rs = null;
	
	Report report;
	
	public ReportDAO(Connection c){
		this.c=c;
	}
	
	public void report(Report report) throws AlreadyReportedException, SQLException{
		
		//Store report in instance variable
		this.report = report;
		
		//Get the user IDs of reporter and reportee
		int [] uids = getUids();
		
		//Initialize the last insert id variable
		int inserted_report_id = 0;
		
		//Check if reporter has already reported the reportee
		if(!canReport(uids[0])){
			throw new AlreadyReportedException("User already reported");
		}
		
		//Check if the reportee has already been reported
		if(!isReported(uids[1])){
			
			//Create new report and set the last inserted report ID
			inserted_report_id = createNewReport(uids[1]);
			report.setRid(inserted_report_id);
			
		}else{
			
			//A report has already been filed, increment the post's downvotes
			incrementDownvote();
		}
		
		//Associate the reporter and the reportee
		createReportReporteePair(uids[0]);
		
	}
	
	private boolean canReport(int reporter) throws SQLException{
		boolean canReport = false;
		ps = c.prepareStatement(Queries.CHECK_IF_CAN_REPORT);
		ps.setInt(1, reporter);
		rs = ps.executeQuery();
		if(rs.next()){
			canReport = true;
		}	
		return canReport;
	}
	
	private boolean isReported(int reportee) throws SQLException{
		boolean isReported = false;
		
		ps = c.prepareStatement(Queries.REPORT_LOOK_UP);
		ps.setInt(1, reportee);
		rs = ps.executeQuery();
		
		if(rs.next()){
			isReported = true;
			report.setRid(rs.getInt(1));
		}
		
		return isReported;
	}
	
	private int[] getUids() throws SQLException{
		int [] uids = new int[2];
		
		ps = c.prepareStatement(Queries.FIND_IN_USER);
		ps.setString(1, report.getReporter());
		ps.setString(2, report.getReportee());
		rs = ps.executeQuery();
		
		if(rs.next()){
			uids[0] = rs.getInt(1);
			int count = rs.getInt(2);
			if(count > 1){
				rs.next();
				uids[1] = rs.getInt(1);
			}else{
				uids[1] = getUidWithSecondaryEmail(report.getReportee());
			}
		}
		
		return uids;
	}
	
	private int getUidWithSecondaryEmail(String reportee) throws SQLException{
		int uid = 0;
		ps = c.prepareStatement(Queries.FIND_IN_EMAILS);
		ps.setString(1, reportee);
		rs = ps.executeQuery();	
		if(rs.next()){
			uid = rs.getInt(1);
		}
		return uid;
	}
	
	private int createNewReport(int reportee) throws SQLException{
		ps = c.prepareStatement(Queries.NEW_REPORT);
		ps.setString(1, report.getMessage());
		ps.setInt(2, reportee);
		ps.setString(3, report.getChannel().trim());
		ps.executeUpdate();
		
		rs = ps.getResultSet();
		return rs.getInt(1);
	}
	
	private void incrementDownvote() throws SQLException{
		ps = c.prepareStatement(Queries.INCREMENT_DOWNVOTE);
		ps.setInt(1, report.getRid());
		ps.execute();
	}
	
	private void createReportReporteePair(int reporter_id) throws SQLException{
		ps = c.prepareStatement(Queries.REPORTER_REPORTEE_PAIR);
		ps.setInt(1, report.getRid());
		ps.setInt(2, reporter_id);
		ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
		ps.setInt(4, report.getType());
		ps.execute();
	}
	
	
}