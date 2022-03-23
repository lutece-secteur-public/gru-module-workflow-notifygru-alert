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

import fr.paris.lutece.plugins.workflow.modules.alertgru.service.AlertGruPlugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * 
 * UpdateTaskStateResourceQueueService
 *
 */
public class UpdateTaskStateResourceQueueHome
{

    private static IUpdateTaskStateResourceQueueDAO _updateResourceQueueDAO = SpringContextService
            .getBean( "workflow-alertgru.updateTaskStateResourceQueueDAO" );

    private UpdateTaskStateResourceQueueHome( )
    {

    }

    public static void create( UpdateTaskStateResourceQueue updateResourceQueue )
    {
        _updateResourceQueueDAO.insert( updateResourceQueue, AlertGruPlugin.getPlugin( ) );
    }

    public static void update( UpdateTaskStateResourceQueue updateResourceQueue )
    {
        _updateResourceQueueDAO.store( updateResourceQueue, AlertGruPlugin.getPlugin( ) );

    }

    /**
     * Load the data of all the updateTaskStateResourceQueue objects and returns them as a collection
     * 
     * @return the List which contains the data of all the updateTaskStateResourceQueue objects
     */
    public static List<UpdateTaskStateResourceQueue> findAllActived( )
    {
        return _updateResourceQueueDAO.selectAllActived( AlertGruPlugin.getPlugin( ) );
    }

    /**
     * Load the data of all the updateTaskStateResourceQueue objects and returns them as a collection
     * 
     * @return the List which contains the data of all the updateTaskStateResourceQueue objects
     */
    public static List<UpdateTaskStateResourceQueue> findAllDisabled( )
    {
        return _updateResourceQueueDAO.selectAllDisabled( AlertGruPlugin.getPlugin( ) );
    }

    /**
     * Load the data from the table
     * 
     * @param nIdResourceHistory
     *            The nIdResourceHistory of the updateTaskStateResourceQueue
     * @param nIdTask
     *            The nIdTask of the updateTaskStateResourceQueue
     * @return The instance of the updateTaskStateResourceQueue
     */
    public static Optional<UpdateTaskStateResourceQueue> find( int nIdResourceHistory, int nIdTask )
    {
        return _updateResourceQueueDAO.load( nIdResourceHistory, nIdTask, AlertGruPlugin.getPlugin( ) );
    }

    public static void removeByPrimaryKey( int nIdResourceQueues )
    {
        _updateResourceQueueDAO.deleteByPrimaryKey( nIdResourceQueues, AlertGruPlugin.getPlugin( ) );
    }

    /**
     * Delete a record from the table
     * 
     * @param nIdUpdateTaskStateResourceQueue
     */
    public static void removeByIdTask( int nIdTask )
    {
        _updateResourceQueueDAO.deleteByIdTask( nIdTask, AlertGruPlugin.getPlugin( ) );
    }

    /**
     * Remove a record from the table
     * 
     * @param IdResourceHistory
     *            the resource history id of the resource workflow
     * @param plugin
     *            The plugin
     */
    public static void removeByResourceHistory( int nIdResourceHistory )
    {
        _updateResourceQueueDAO.deleteByResourceHistory( nIdResourceHistory, AlertGruPlugin.getPlugin( ) );
    }

    /**
     * Remove a record from the table
     * 
     * @param nIdResource
     *            The nIdResource of the updateTaskStateResourceQueue
     * @param strResourceType
     *            The resource type of the updateTaskStateResourceQueue
     * @param plugin
     *            the plugin
     */
    public static void removeByResource( int nIdResource, String strResourceType )
    {
        _updateResourceQueueDAO.deleteByResource( nIdResource, strResourceType, AlertGruPlugin.getPlugin( ) );
    }

    /**
     * Load the id of all the updateTaskStateResourceQueue and returns them as a collection
     * 
     * @return the List which contains the id of all the updateTaskStateResourceQueue objects
     */
    public static List<Integer> findAllIdQueueActived( )
    {

        return _updateResourceQueueDAO.selectAllIdQueueActived( AlertGruPlugin.getPlugin( ) );
    }

    /**
     * Load the data from the table
     * 
     * @param listIdResourceQueues
     *            The identifier list of the updateTaskStateResourceQueue
     * @return The instance list of the updateTaskStateResourceQueue
     */
    public static List<UpdateTaskStateResourceQueue> findByPrimaryKeyList( List<Integer> listIdResourceQueues )
    {
        return _updateResourceQueueDAO.selectByPrimaryKeyList( listIdResourceQueues, AlertGruPlugin.getPlugin( ) );
    }

    /**
     * find instance of the updateTaskStateResourceQueue by resource
     * 
     * @param nIdResource
     *            The nIdResource of the updateTaskStateResourceQueue
     * @param strResourceType
     *            The resource type of the updateTaskStateResourceQueue
     * @param plugin
     *            the plugin
     * @return The instance of the updateTaskStateResourceQueue
     */
    public static Optional<UpdateTaskStateResourceQueue> find( int nIdResource, String strResourceType )
    {
        return _updateResourceQueueDAO.load( nIdResource, strResourceType, AlertGruPlugin.getPlugin( ) );
    }

}
