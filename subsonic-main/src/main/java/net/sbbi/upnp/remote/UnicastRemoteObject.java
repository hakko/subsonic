/*
 * ============================================================================
 *                 The Apache Software License, Version 1.1
 * ============================================================================
 *
 * Copyright (C) 2002 The Apache Software Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include the following  acknowledgment: "This product includes software
 *    developed by SuperBonBon Industries (http://www.sbbi.net/)."
 *    Alternately, this acknowledgment may appear in the software itself, if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "UPNPLib" and "SuperBonBon Industries" must not be
 *    used to endorse or promote products derived from this software without
 *    prior written permission. For written permission, please contact
 *    info@sbbi.net.
 *
 * 5. Products  derived from this software may not be called 
 *    "SuperBonBon Industries", nor may "SBBI" appear in their name, 
 *    without prior written permission of SuperBonBon Industries.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED. IN NO EVENT SHALL THE
 * APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT,INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software  consists of voluntary contributions made by many individuals
 * on behalf of SuperBonBon Industries. For more information on 
 * SuperBonBon Industries, please see <http://www.sbbi.net/>.
 */
package net.sbbi.upnp.remote;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteServer;
import java.rmi.server.RemoteStub;
import java.rmi.server.ServerCloneException;
import java.rmi.server.ServerRef;

import net.sbbi.upnp.Discovery;
import net.sbbi.upnp.devices.UPNPDevice;
import net.sbbi.upnp.devices.UPNPRootDevice;
import net.sbbi.upnp.messages.ActionMessage;
import net.sbbi.upnp.messages.UPNPMessageFactory;

/**
 * This class can be used for remote objects that need to work behind
 * an NAT firewall compatible with IGD UPNP specifications.
 * The following system properties let you setup this class : <br/>
 * 
 * net.sbbi.upnp.remote.deviceUDN=someUPNPDeviceUDN, the device identifier to
 * be used when multiple IGD upnp devices are on the network<br/>
 * net.sbbi.upnp.remote.failWhenNoDeviceFound=true|false, Property to throw an
 * exception when the object is exported and no UPNP device is found, default to false<br/>
 * net.sbbi.upnp.remote.failWhenDeviceCommEx=true|false, Property to throw an
 * exception when the object is exported and an error occurs during com with device, default to false<br/>
 * net.sbbi.upnp.remote.discoveryTimeout=4000, timeout in ms to discover upnp devices
 * default to 1500, try to increase this timeout if you can't find a present device
 * on the network<br/>
 * 
 * Each instance of this class can create a shutdown hook trigered during JVM shutdown
 * to make sure that the port opened with UPNP is closed.
 * The hook is created as soon as the port is opened on the UPNP device.<br/>
 * 
 * Migration for distributed objects is quite simple :
 * change the standard java.rmi.server.UnicastRemoteObject class extends to
 * this class and you're done.<br/>
 * 
 * If you have trouble to make the objects available from behind your router/firewall
 * make sure that you have correctly set the java.rmi.server.hostname system property with
 * an hostname matching your router/firewall IP.<br/>
 * 
 * Make also sure that your RMI Registry port is opened on the router
 * otherwise nothing will work. You can use a urn:schemas-upnp-org:device:InternetGatewayDevice:1
 * device just like this class to automate the job.
 * 
 * @author <a href="mailto:superbonbon@sbbi.net">SuperBonBon</a>
 * @version 1.0
 */

public class UnicastRemoteObject extends RemoteServer {

  /* indicate compatibility with JDK 1.1.x version of class */
  private static final long serialVersionUID = 4974527148936298034L;

  /* parameter types for server ref constructor invocation used below */
  private static Class[] portParamTypes = { int.class };

  /* parameter types for server ref constructor invocation used below */
  private static Class[] portFactoryParamTypes = {
    int.class, RMIClientSocketFactory.class, RMIServerSocketFactory.class
  };
  
  private final static Object DISCOVERY_PROCESS = new Object();
  private final static Object ANONYMOUS_PORT_LOOKUP = new Object();
  
  private UPNPDevice wanConnDevice = null;
  
  private boolean discoveryProcessCall = false;
  
  private boolean portOpened = false;

  /**
   * @serial port number on which to export object
   */
  private int port = 0;

  /**
   * @serial client-side socket factory (if any)
   */
  private RMIClientSocketFactory csf = null;

  /**
   * @serial server-side socket factory (if any) to use when
   * exporting object
   */
  private RMIServerSocketFactory ssf = null;

  /**
   * Creates and exports a new UnicastRemoteObject object using an
   * anonymous port.
   * @throws RemoteException if failed to export object
   */
  protected UnicastRemoteObject() throws RemoteException {
    // let's open a server socket with an anonymous port
    synchronized( ANONYMOUS_PORT_LOOKUP ) {
      try { 
        ServerSocket srv = new ServerSocket( 0 );
        this.port = srv.getLocalPort();
        srv.close();
      } catch ( Exception ex ) {
        throw new RemoteException( "Error occured during anonymous port assignation", ex );
      }
    }
    openPort();
    exportObject( this, port );
    
  }

  /**
   * Creates and exports a new UnicastRemoteObject object using the
   * particular supplied port.
   * @param port the port number on which the remote object receives calls
   * (if <code>port</code> is zero, an anonymous port is chosen)
   * @throws RemoteException if failed to export object
   */
  protected UnicastRemoteObject( int port ) throws RemoteException {
    this.port = port;
    openPort();
    exportObject( this, port );
  }

  /**
   * Creates and exports a new UnicastRemoteObject object using the
   * particular supplied port and socket factories.
   * @param port the port number on which the remote object receives calls
   * (if <code>port</code> is zero, an anonymous port is chosen)
   * @param csf the client-side socket factory for making calls to the
   * remote object
   * @param ssf the server-side socket factory for receiving remote calls
   * @throws RemoteException if failed to export object
   */
  protected UnicastRemoteObject( int port, RMIClientSocketFactory csf,
                                 RMIServerSocketFactory ssf ) throws RemoteException {
    this.port = port;
    this.csf = csf;
    this.ssf = ssf;
    openPort();
    exportObject( this, port, csf, ssf );
  }

  /**
   * Re-export the remote object when it is deserialized.
   */
  private void readObject( java.io.ObjectInputStream in )
                   throws java.io.IOException,
                          java.lang.ClassNotFoundException {
    in.defaultReadObject();
    reexport();
  }

  /**
   * Returns a clone of the remote object that is distinct from
   * the original.
   *
   * @exception CloneNotSupportedException if clone failed due to
   * a RemoteException.
   * @return the new remote object
   */
  public Object clone() throws CloneNotSupportedException {
    try {
      UnicastRemoteObject cloned = (UnicastRemoteObject)super.clone();
      cloned.reexport();

      return cloned;
    } catch ( RemoteException e ) {
      throw new ServerCloneException( "Clone failed", e );
    }
  }

  /**
   * Exports this UnicastRemoteObject using its initialized fields because its
   * creation bypassed running its constructors (via deserialization or cloning,
   * for example).
   */
  private void reexport() throws RemoteException {
    closePort();
    if ( ( csf == null ) && ( ssf == null ) ) {
      exportObject( this, port );
    } else {
      exportObject( this, port, csf, ssf );
    }
    openPort();
  }

  /**
   * Exports the remote object to make it available to receive incoming
   * calls using an anonymous port.
   * @param obj the remote object to be exported
   * @return remote object stub
   * @exception RemoteException if export fails
   */
  public static RemoteStub exportObject( Remote obj ) throws RemoteException {
    return (RemoteStub)exportObject( obj, 0 );
  }

  /**
   * Exports the remote object to make it available to receive incoming
   * calls, using the particular supplied port.
   * @param obj the remote object to be exported
   * @param port the port to export the object on
   * @return remote object stub
   * @exception RemoteException if export fails
   */
  public static Remote exportObject( Remote obj, int port )
                             throws RemoteException {
    // prepare arguments for server ref constructor
    Object[] args = new Object[]{ new Integer( port ) };

    return exportObject( obj, "UnicastServerRef", portParamTypes, args );
  }

  /**
   * Exports the remote object to make it available to receive incoming
   * calls, using a transport specified by the given socket factory.
   * @param obj the remote object to be exported
   * @param port the port to export the object on
   * @param csf the client-side socket factory for making calls to the
   * remote object
   * @param ssf the server-side socket factory for receiving remote calls
   * @return remote object stub
   * @exception RemoteException if export fails
   */
  public static Remote exportObject( Remote obj, int port,
                                     RMIClientSocketFactory csf,
                                     RMIServerSocketFactory ssf )
                             throws RemoteException {
    // prepare arguments for server ref constructor
    Object[] args = new Object[]{ new Integer( port ), csf, ssf };

    return exportObject( obj, "UnicastServerRef2", portFactoryParamTypes,
                         args );
  }

  /*
   * Creates an instance of given server ref type with constructor chosen by
   * indicated paramters and supplied with given arguements, and export remote
   * object with it.
   */
  private static Remote exportObject( Remote obj, String refType,
                                      Class[] params, Object[] args )
                              throws RemoteException {
    // compose name of server ref class and find it
    String refClassName = RemoteRef.packagePrefix + "." + refType;
    Class refClass;

    try {
      refClass = Class.forName( refClassName );
    } catch ( ClassNotFoundException e ) {
      throw new ExportException( "No class found for server ref type: " +
                                 refType );
    }

    if ( !ServerRef.class.isAssignableFrom( refClass ) ) {
      throw new ExportException( "Server ref class not instance of " +
                                 ServerRef.class.getName() + ": " +
                                 refClass.getName() );
    }

    // create server ref instance using given constructor and arguments
    ServerRef serverRef;

    try {
      java.lang.reflect.Constructor cons = refClass.getConstructor( params );
      serverRef = (ServerRef)cons.newInstance( args );

      // if impl does extends UnicastRemoteObject, set its ref
      if ( obj instanceof UnicastRemoteObject )
        ((UnicastRemoteObject)obj).ref = serverRef;
    } catch ( Exception e ) {
      throw new ExportException( "Exception creating instance of server ref class: " +
                                 refClass.getName(), e );
    }

    return serverRef.exportObject( obj, null );
  }


  private void openPort() throws RemoteException {
    
    discoverDevice();
   
    if ( wanConnDevice != null && !portOpened ) {
      
      net.sbbi.upnp.services.UPNPService wanIPSrv = wanConnDevice.getService( "urn:schemas-upnp-org:service:WANIPConnection:1" );
      String failStr = System.getProperty( "net.sbbi.upnp.remote.failWhenNoDeviceFound" );
      boolean fail = false;
      if ( failStr != null && failStr.equalsIgnoreCase( "true" ) ) fail = true;
      if ( wanIPSrv == null && fail ) {
        throw new RemoteException( "Device does not implement the urn:schemas-upnp-org:service:WANIPConnection:1 service" );
      } else if ( wanIPSrv == null && !fail ) {
        return;
      }
      
      failStr = System.getProperty( "net.sbbi.upnp.remote.failWhenDeviceCommEx" );
      fail = false;
      if ( failStr != null && failStr.equalsIgnoreCase( "true" ) ) fail = true;

      UPNPMessageFactory msgFactory = UPNPMessageFactory.getNewInstance( wanIPSrv );
      ActionMessage msg = msgFactory.getMessage( "AddPortMapping" );
      
      try {
      
        String localIP = InetAddress.getLocalHost().getHostAddress();
      
        msg.setInputParameter( "NewRemoteHost", "" )
           .setInputParameter( "NewExternalPort", port )
           .setInputParameter( "NewProtocol", "TCP" )
           .setInputParameter( "NewInternalPort", port )
           .setInputParameter( "NewInternalClient", localIP )
           .setInputParameter( "NewEnabled", "1" )
           .setInputParameter( "NewPortMappingDescription", "Remote Object " + this.getClass().getName() + " " + this.hashCode() )
           .setInputParameter( "NewLeaseDuration", "0" );
        msg.service();
        portOpened = true;
        
        UnicastObjectShutdownHook hook = new UnicastObjectShutdownHook( this );
        Runtime.getRuntime().addShutdownHook( hook );
      } catch ( Exception ex ) {
        if ( fail ) {
          throw new RemoteException( "Error occured during port mapping", ex ) ;
        }
      }
    }
  }
  
  /**
   * Closes the port on the UPNP router
   */
  public void closePort() {
    if ( wanConnDevice != null && portOpened ) {

      net.sbbi.upnp.services.UPNPService wanIPSrv = wanConnDevice.getService( "urn:schemas-upnp-org:service:WANIPConnection:1" );
      if ( wanIPSrv != null ) {
        UPNPMessageFactory msgFactory = UPNPMessageFactory.getNewInstance( wanIPSrv );
        ActionMessage msg = msgFactory.getMessage( "DeletePortMapping" );
        
        try {
          msg.setInputParameter( "NewRemoteHost", "" )
             .setInputParameter( "NewExternalPort", port )
             .setInputParameter( "NewProtocol", "TCP" );
          msg.service();
          portOpened = false;
        } catch ( Exception ex ) {
          // silently ignoring
        }
      }
    }
  }

  private final void discoverDevice() throws RemoteException {
    
    synchronized( DISCOVERY_PROCESS ) {
      if ( !discoveryProcessCall ) {
        discoveryProcessCall = true;
        UPNPRootDevice rootIGDDevice = null;
        String failStr = System.getProperty( "net.sbbi.upnp.remote.failWhenNoDeviceFound" );
        boolean fail = false;
        if ( failStr != null && failStr.equalsIgnoreCase( "true" ) ) fail = true;
        
        UPNPRootDevice[] devices = null;
        try {
          String timeout = System.getProperty( "net.sbbi.upnp.remote.discoveryTimeout" );
          if ( timeout != null ) {
            devices = Discovery.discover( Integer.parseInt( timeout ), "urn:schemas-upnp-org:device:InternetGatewayDevice:1" );
          } else {
            devices = Discovery.discover( "urn:schemas-upnp-org:device:InternetGatewayDevice:1" );
          }
        } catch ( IOException ex ) {
          throw new RemoteException( "IOException occured during devices discovery", ex );
        }

        if ( devices == null && fail ) throw new IllegalStateException( "No UPNP IGD (urn:schemas-upnp-org:device:InternetGatewayDevice:1) available, unable to create object" );
        if ( devices != null && devices.length > 1 ) {
          String deviceUDN = System.getProperty( "net.sbbi.upnp.remote.deviceUDN" );
          if ( deviceUDN == null ) {
            String UDNs = "";
            for ( int i = 0; i < devices.length; i++ ) {
              UPNPRootDevice dv = devices[i];
              UDNs += dv.getUDN();
              if ( i < devices.length ) {
                UDNs += ", ";
              }
            }
            throw new RemoteException( "Found multiple IDG UPNP devices UDN's :" + UDNs + ", " +
                                       "please set the net.sbbi.upnp.remote.deviceUDN system " +
                                       "property with one of the following identifier to define " +
                                       "which UPNP device need to be used with remote objects" );
    
          }
          StringBuffer foundUDN = new StringBuffer();
          for ( int i = 0; i < devices.length; i++ ) {
            UPNPRootDevice dv = devices[i];
            if ( dv.getUDN().equals( deviceUDN ) ) {
              rootIGDDevice = dv;
              break;
            }
            foundUDN.append( dv.getUDN() );
            if ( i < devices.length ) foundUDN.append( ", " );
          }
          if ( rootIGDDevice == null ) throw new RemoteException( "No UPNP device matching UDN :" + deviceUDN + ", found UDN(s) are :" + foundUDN );

        } else if ( devices != null ) {
          rootIGDDevice = devices[0];
        }
        
        if ( rootIGDDevice != null ) {
          wanConnDevice = rootIGDDevice.getChildDevice( "urn:schemas-upnp-org:device:WANConnectionDevice:1" );
          if ( wanConnDevice == null && fail ) throw new RemoteException( "Your UPNP device does not implements urn:schemas-upnp-org:device:WANConnectionDevice:1 required specs for NAT transversal" );
        }
      }
    }
  }
  
  private class UnicastObjectShutdownHook extends Thread {
  
    private UnicastRemoteObject object;
   
    private UnicastObjectShutdownHook( UnicastRemoteObject object ) {
      this.object = object;
    }
   
    public void run() {
      object.closePort();
    } 
  }

}
