package com.chatit.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

//Class in charge of connecting/disconnecting to database
public class DBConnect {
	private Connection dbConn = null;

	private String drivername = new String();

	private String driverclass = new String();

	private String url = new String();

	private String name = new String();

	private String passwd = new String();
	
    public DBConnect(String dbuser, Properties p) {
    	if (p == null) {
            p = setProperties(p);
        }
        drivername = p.getProperty("driver", drivername);
        driverclass = p.getProperty("class", driverclass);
        url = p.getProperty("url", url);

        name = dbuser;
        passwd = p.getProperty("password", passwd);
    }

    // //////////////////////////////////////
    //
    // ctor()
    //
    // //////////////////////////////////////

    public DBConnect() {
    }

    // //////////////////////////////////////
    //
    // connect()
    //
    // //////////////////////////////////////

    public Connection connect() {

        // Load the JDBC driver.

        try {
            Class.forName(driverclass);
        } catch (ClassNotFoundException c) {
            System.out.println("Failed to load JDBC driver: Exception "
                    + c.toString());
            return null;
        }

        System.out.println("Connecting to JDBC database");
        System.out.println("  url   : " + url);
        System.out.println("  name  : " + name);

        try {

            // //////////////////////////////////////
            // Oracle uses (url, name, passwd) form
            // of getConnection();
            // Informix uses (url) form
            // of getConnection();
            //
            // In general we say that if the name
            // and passwd fields are empty strings
            // (but not NULL strings), then only
            // use the (url) form; otherwise,
            // use the (url, name, passwd) form.
            // //////////////////////////////////////

            if (name.equals("") && passwd.equals("")) {
                System.out.println("Using getConnection(url).");
                dbConn = DriverManager.getConnection(url);
            } else {
                System.out.println("Using getConnection(url,name,passwd).");
                dbConn = DriverManager.getConnection(url, name, passwd);
            }
        } catch (Exception e) {
            System.out.println("DriverManager.getConnection: Exception " + e);
            e.printStackTrace();
            return null;
        }
        return dbConn;
    }

    // //////////////////////////////////////
    //
    // disconnect()
    //
    // //////////////////////////////////////

    public void disconnect() {
        if (dbConn != null) {
            try {
                dbConn.close();
                dbConn = null;
            } catch (Exception e) {
                System.err.println("Error closing JDBC Connection.");
                System.err.println(e);
                System.err
                        .println("Setting Connection to NULL and disconnecting.");
                dbConn = null;
            }
        }
    }
    public Properties setProperties(Properties props){
 		props = new Properties();
 		props.setProperty("url", "jdbc:postgresql://strawberryinstance.cnyaiygstcvr.us-west-1.rds.amazonaws.com:1337/ChatitDB");
 		props.setProperty("class", "org.postgresql.Driver");
 		props.setProperty("driver", "jdbc");
 		props.setProperty("user","classroomhero");
 		props.setProperty("password","halfnelson7");
 		props.setProperty("ssl","true");
 		return props;

 	}
    
 	/*public Properties setProperties(Properties props){
		props = new Properties();
		props.setProperty("url", "jdbc:postgresql://localhost:5432/chatitdb");
		props.setProperty("class", "org.postgresql.Driver");
		props.setProperty("driver", "jdbc");
		props.setProperty("user","postgres");
		props.setProperty("password","halfnelson");
		props.setProperty("ssl","true");
		return props;
,
	}*/
	
}
