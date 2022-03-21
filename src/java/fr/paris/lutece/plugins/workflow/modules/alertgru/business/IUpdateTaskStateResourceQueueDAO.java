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

import java.util.List;
import java.util.Optional;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * IUpdateTaskStateResourceQueueDAO Interface
 */

public interface IUpdateTaskStateResourceQueueDAO
{

    /**
     * Insert a new record in the table.
     * 
     * @param updateTaskStateResourceQueue
     *            instance of the updateTaskStateResourceQueue object to inssert
     * @param plugin
     *            The plugin 
     */

    void insert( UpdateTaskStateResourceQueue updateTaskStateResourceQueue, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param updateTaskStateResourceQueue
     *            the reference of the updateTaskStateResourceQueue
     * @param plugin
     *            The plugin 
     */

    void store( UpdateTaskStateResourceQueue updateTaskStateResourceQueue, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nIdResourceQueues
     *            int identifier of the UpdateTaskStateResourceQueue to delete
     * @param plugin
     *            The plugin    
     */
    void deleteByPrimaryKey( int nIdResourceQueues, Plugin plugin );
    
    /**
     * Delete a record from the table
     * 
     * @param nIdUpdateTaskStateResourceQueue
     *            int identifier of the UpdateTaskStateResourceQueue to delete
     * @param plugin
     *            The plugin            
     */
    void deleteByIdTask( int nIdTask, Plugin plugin );
   
    /**
     * Delete a record from the table
     * 
     * @param IdResourceHistory
     *            the resource history id of the resource workflow 
     * @param plugin
     *            The plugin            
     */
    void deleteByResourceHistory( int nIdResourceHistory,  Plugin plugin );
    
    /**
     * Delete a record from the table
     * 
     * @param nIdResource The nIdResource of the updateTaskStateResourceQueue
     * @param strResourceType  The resource type of the updateTaskStateResourceQueue
     * @param plugin the plugin       
     */
    void deleteByResource( int nIdResource, String strResourceType, Plugin plugin );
    
    
    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Load the data from the table
     * 
     * @param nKey
     *            The identifier of the updateTaskStateResourceQueue
     * @param plugin
     *            The plugin 
     * @return The instance of the updateTaskStateResourceQueue
     */
    Optional<UpdateTaskStateResourceQueue> load( int nKey, Plugin plugin );

    /**
     * Load the data from the table
     * 
     * @param nIdResource
     *            The nIdResource of the updateTaskStateResourceQueue
     * @param nIdTask
     *            The nIdTask of the updateTaskStateResourceQueue
     * @param plugin
     *            The plugin 
     * @return The instance of the updateTaskStateResourceQueue
     */
    Optional<UpdateTaskStateResourceQueue> load( int nIdResource, int nIdTask, Plugin plugin );
    /**
     * Load the data from the table by resource
     * @param nIdResource The nIdResource of the updateTaskStateResourceQueue
     * @param strResourceType  The resource type of the updateTaskStateResourceQueue
     * @param plugin the plugin
     * @return The instance of the updateTaskStateResourceQueue
     */
    Optional<UpdateTaskStateResourceQueue> load( int nIdResource, String strResourceType, Plugin plugin );

    /**
     * Load the data of all the updateTaskStateResourceQueue objects and returns them as a collection
     * @param plugin
     *            The plugin 
     * @return the List which contains the data of all the updateTaskStateResourceQueue objects
     */

    List<UpdateTaskStateResourceQueue> selectAllActived( Plugin plugin );

    /**
     * Load the data of all the updateTaskStateResourceQueue objects and returns them as a collection
     * @param plugin
     *            The plugin 
     * @return the List which contains the data of all the updateTaskStateResourceQueue objects
     */

    List<UpdateTaskStateResourceQueue> selectAllDisabled( Plugin plugin );

    
    /**
     * Load the id of all the updateTaskStateResourceQueue  and returns them as a collection
     * @param plugin
     *            The plugin 
     * @return the List which contains the id of all the updateTaskStateResourceQueue objects
     */
    List <Integer> selectAllIdQueueActived( Plugin plugin );
    
    /**
     * Load the data from the table
     * 
     * @param listIdResourceQueues
     *            The identifier list of the updateTaskStateResourceQueue
     * @param plugin
     *            The plugin 
     * @return The instance list of the updateTaskStateResourceQueue
     */
    List<UpdateTaskStateResourceQueue> selectByPrimaryKeyList( List<Integer> listIdResourceQueues, Plugin plugin );
}
