/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ts.pm.jdbc;

import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg;
import com.ts.utils.win.RegistryUtils;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;
import jcifs.netbios.NbtAddress;
import org.firebirdsql.jdbc.FBDriver;

/**
 * Class PmJdbcDriver
 *
 * @author daibheid
 */
public class PmJdbcDriver implements Driver {

    private Driver jaybirdDriver = new FBDriver();
    
    @Override
    public Connection connect(String url, Properties info) throws SQLException
    {
        if(!acceptsURL(url)) {
            return null;
        }
        
        String ipAddress = "";
        
        // get the server entry from the registry
        String server = RegistryUtils.getStringValue(WinReg.HKEY_LOCAL_MACHINE, WinNT.KEY_WOW64_32KEY, "SOFTWARE\\PC Synergy\\PostalMate\\FB", "Server");
        if(null == server)
            throw new SQLException("PmJdbcDriver: Unable to read SOFTWARE\\PC Synergy\\PostalMate\\FB\\Server value");
        
        // get the path entry from the registry
        String path = RegistryUtils.getStringValue(WinReg.HKEY_LOCAL_MACHINE, WinNT.KEY_WOW64_32KEY, "SOFTWARE\\PC Synergy\\PostalMate\\FB", "Path");
        if(null == path)
            throw new SQLException("PmJdbcDriver: Unable to read SOFTWARE\\PC Synergy\\PostalMate\\FB\\Path value");
        
        // convert the server entry to a ip address
        NbtAddress localHost = null;
        try {
            localHost = NbtAddress.getLocalHost();
        }
        catch (UnknownHostException ex) {
            throw new SQLException(ex);
        }
        
        if(localHost.getHostName().equals(server)) {
            ipAddress = "localhost";
        }
        else {
            ipAddress = localHost.getHostAddress();
        }
        
        StringBuffer sb = new StringBuffer();
        sb.append("jdbc:firebirdsql:");
        sb.append(ipAddress);
        sb.append(":");
        sb.append(path);
        
        String firebirdUrl = sb.toString();
        
        return jaybirdDriver.connect(firebirdUrl, info);
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException
    {
        return url.startsWith("jdbc:postalmate:");
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException
    {
        return jaybirdDriver.getPropertyInfo(url, info);
    }

    @Override
    public int getMajorVersion()
    {
        return jaybirdDriver.getMajorVersion();
    }

    @Override
    public int getMinorVersion()
    {
        return jaybirdDriver.getMinorVersion();
    }

    @Override
    public boolean jdbcCompliant()
    {
        return jaybirdDriver.jdbcCompliant();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException
    {
        return jaybirdDriver.getParentLogger();
    }

} 

