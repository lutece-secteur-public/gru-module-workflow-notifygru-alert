/*
 * Copyright (c) 2002-2022, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.alertgru.business;

import java.sql.Timestamp;

/**
 * This is the business class for the object UpdateTaskStateResourceQueue
 */
public class UpdateTaskStateResourceQueue
{
    // Variables declarations
    private int _nIdResource;
    private int _nIdTask;
    private String _strResourceType;
    private int _nIdExternalParent;
    private int _nIdResourceHistory;
    private int _nIdWorkflow;
    private boolean _bStatus;
    private int _nIdResourceQueue;
    private Timestamp _tCreationDate;
    private Timestamp  _tAlertReferenceDate;
    private int _nIdState;
    
    
    
    /**
     * Returns the IdResourceQueue
     * @return The IdResourceQueue
     */ 
     public int getIdResourceQueue()
     {
         return _nIdResourceQueue;
     }
 
    /**
     * Sets the IdResourceQueue
     * @param nIdResourceQueue The IdResourceQueue
     */ 
     public void setIdResourceQueue( int nIdResourceQueue )
     {
         _nIdResourceQueue = nIdResourceQueue;
     }
    /**
     * Returns the IdResourceHistory
     * 
     * @return The IdResourceHistory
     */
    public int getIdResource( )
    {
        return _nIdResource;
    }

    /**
     * Sets the IdResource
     * 
     * @param nIdResource
     *            The IdResource
     */
    public void setIdResource( int nIdResource )
    {
        _nIdResource = nIdResource;
    }

    /**
     * Returns the IdTask
     * 
     * @return The IdTask
     */
    public int getIdTask( )
    {
        return _nIdTask;
    }

    /**
     * Sets the IdTask
     * 
     * @param nIdTask
     *            The IdTask
     */
    public void setIdTask( int nIdTask )
    {
        _nIdTask = nIdTask;
    }

    /**
     * Returns the ResourceType
     * 
     * @return The ResourceType
     */
    public String getResourceType( )
    {
        return _strResourceType;
    }

    /**
     * Sets the ResourceType
     * 
     * @param strResourceType
     *            The ResourceType
     */
    public void setResourceType( String strResourceType )
    {
        _strResourceType = strResourceType;
    }

    
    /**
     * Returns the IdExternalParent
     * 
     * @return The IdExternalParent
     */
    public int getIdExternalParent( )
    {
        return _nIdExternalParent;
    }

    /**
     * Sets the IdExternalParent
     * 
     * @param nIdExternalParent
     *            The IdExternalParent
     */
    
    public void setIdExternalParent( int nIdExternalParent )
    {
        _nIdExternalParent = nIdExternalParent;
    }

    /**
     * Returns the IdWorkflow
     * 
     * @return The IdWorkflow
     */
    public int getIdWorkflow( )
    {
        return _nIdWorkflow;
    }

    /**
     * Sets the IdWorkflow
     * 
     * @param nIdWorkflow
     *            The IdWorkflow
     */
    public void setIdWorkflow( int nIdWorkflow )
    {
        _nIdWorkflow = nIdWorkflow;
    }
    
    /**
    * Returns the IdResourceHistory
    * 
    * @return The IdResourceHistory
    */
    public int getIdResourceHistory( )
    {
        return _nIdResourceHistory;
    }

    /**
    * Sets the IdResourceHistory
    * 
    * @param nIdResourceHistory
    *            The IdResourceHistory
    */
    public void setIdResourceHistory( int nIdResourceHistory )
    {
        _nIdResourceHistory = nIdResourceHistory;
    }
            
    /**
     * Returns the Status
     * 
     * @return The Status
     */
    public boolean getStatus( )
    {
        return _bStatus;
    }

    /**
     * Sets the Status
     * 
     * @param bStatus
     *            The Status
     */
    public void setStatus( boolean bStatus )
    {
        _bStatus = bStatus;
    }
    /**
    *
    * @return the creation date
    */
   public Timestamp getCreationDate( )
   {
       return _tCreationDate;
   }

   /**
    * set the creation date
    * 
    * @param dateCreation
    *            the creation date
    */
   public void setCreationDate( Timestamp dateCreation )
   {
       _tCreationDate = dateCreation;
   }
   
  /**
   * @return the AlertReferenceDate date
   */
  public Timestamp getAlertReferenceDate( )
  {
      return _tAlertReferenceDate;
  }

  /**
   * set the alert Reference Date 
   * 
   * @param alertReferenceDate
   *            the alert Reference Date 
   */
  public void setAlertReferenceDate( Timestamp alertReferenceDate )
  {
      _tAlertReferenceDate = alertReferenceDate;
  }
  /**
   * Returns the IdState
   * @return The IdState
   */ 
   public int getIdState()
   {
       return _nIdState;
   }

  /**
   * Sets the IdState
   * @param nIdState The IdState
   */ 
   public void setIdState( int nIdState )
   {
       _nIdState = nIdState;
   }
}
