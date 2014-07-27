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
package net.sbbi.upnp.messages;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sbbi.upnp.services.ServiceActionArgument;

/**
 * An action respons container Object
 * @author <a href="mailto:superbonbon@sbbi.net">SuperBonBon</a>
 * @version 1.0
 */
public class ActionResponse {

  private Map outArguments = new HashMap();
  private Map outArgumentsVals = new HashMap();

  protected ActionResponse() {
  }

  public ServiceActionArgument getOutActionArgument( String actionArgumentName ) {
    return (ServiceActionArgument)outArguments.get( actionArgumentName );
  }

  public String getOutActionArgumentValue( String actionArgumentName ) {
    return (String)outArgumentsVals.get( actionArgumentName );
  }
  
  public Set getOutActionArgumentNames() {
    return outArguments.keySet();
  }

  /**
   * Adds a result to the response, adding an existing result ServiceActionArgument
   * will override the ServiceActionArgument value
   * @param arg the service action argument
   * @param value the arg value
   */
  protected void addResult( ServiceActionArgument arg, String value ) {
    outArguments.put( arg.getName(), arg );
    outArgumentsVals.put( arg.getName(), value );
  }

  public String toString() {
    StringBuffer rtrVal = new StringBuffer();
    for ( Iterator i = outArguments.keySet().iterator(); i.hasNext(); ) {
      String name = (String)i.next();
      String value = (String)outArgumentsVals.get( name );
      rtrVal.append( name ).append( "=" ).append( value );
      if ( i.hasNext() ) rtrVal.append( "\n" );
    }
    return rtrVal.toString();
  }

}
