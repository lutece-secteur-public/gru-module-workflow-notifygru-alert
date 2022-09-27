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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

/**
 * This class provides Data Access methods for UpdateTaskStateResourceQueue objects
 */
public final class UpdateTaskStateResourceQueueDAO implements IUpdateTaskStateResourceQueueDAO
{

    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_resource_queue, id_resource, id_task, resource_type, id_external_parent, id_workflow, id_resource_history, status, creation_date, alert_reference_date, id_state FROM notifygru_alert_update_resource_state_queue WHERE id_resource = ?";
    private static final String SQL_QUERY_SELECT_BY_ID_TASK_AND_ID_RESOURCE = "SELECT id_resource_queue, id_resource, id_task, resource_type, id_external_parent, id_workflow, id_resource_history, status, creation_date, alert_reference_date, id_state FROM notifygru_alert_update_resource_state_queue WHERE id_resource = ? AND id_task = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO notifygru_alert_update_resource_state_queue ( id_resource, id_task, resource_type, id_external_parent, id_workflow, id_resource_history, status, creation_date, alert_reference_date, id_state ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM notifygru_alert_update_resource_state_queue WHERE id_resource_queue = ? ";
    private static final String SQL_QUERY_DELETE_BY_RESOURCE_HISTORY = "DELETE FROM notifygru_alert_update_resource_state_queue WHERE id_resource_history = ? ";
    private static final String SQL_QUERY_DELETE_BY_RESOURCE = "DELETE FROM notifygru_alert_update_resource_state_queue WHERE id_resource = ?  AND resource_type = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE notifygru_alert_update_resource_state_queue SET id_resource = ?, id_task = ?, resource_type = ?, id_external_parent = ?, id_workflow = ?, id_resource_history = ?, status = ?, creation_date = ?, alert_reference_date= ?, id_state= ? WHERE id_resource_queue = ? ";
    private static final String SQL_QUERY_SELECTALL_ACTIVED = "SELECT id_resource_queue, id_resource, id_task, resource_type, id_external_parent, id_workflow, id_resource_history, status, creation_date, alert_reference_date, id_state FROM notifygru_alert_update_resource_state_queue WHERE status = 1";
    private static final String SQL_QUERY_SELECTALL_DISABLED = "SELECT id_resource_queue, id_resource, id_task, resource_type, id_external_parent, id_workflow, id_resource_history, status, creation_date, alert_reference_date, id_state FROM notifygru_alert_update_resource_state_queue WHERE status = 0";
    private static final String SQL_QUERY_DELETE_BY_ID_TASK = "DELETE FROM notifygru_alert_update_resource_state_queue WHERE id_task = ? ";
    private static final String SQL_QUERY_SELECTALL_ID_ACTIVED = "SELECT id_resource_queue FROM notifygru_alert_update_resource_state_queue WHERE status= 1 ";
    private static final String SQL_QUERY_SELECTALL_ACTIVED_BY_LIST = "SELECT id_resource_queue, id_resource, id_task, resource_type, id_external_parent, id_workflow, id_resource_history, status, creation_date, alert_reference_date, id_state FROM notifygru_alert_update_resource_state_queue WHERE  id_resource_queue IN ( ";
    private static final String SQL_QUERY_SELECT_BY_RESOURCE = "SELECT id_resource_queue, id_resource, id_task, resource_type, id_external_parent, id_workflow, id_resource_history, status, creation_date, alert_reference_date, id_state FROM notifygru_alert_update_resource_state_queue WHERE id_resource = ? AND resource_type = ? ";
    private static final String SQL_QUERY_SELECT_BY_ID_TASK_AND_ID_RESOURCE_AND_RESSOURCE_TYPE= SQL_QUERY_SELECT_BY_ID_TASK_AND_ID_RESOURCE+" AND resource_type = ? ";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( UpdateTaskStateResourceQueue updateTaskStateResourceQueue, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 0;
            daoUtil.setInt( ++nIndex, updateTaskStateResourceQueue.getIdResource( ) );
            daoUtil.setInt( ++nIndex, updateTaskStateResourceQueue.getIdTask( ) );
            daoUtil.setString( ++nIndex, updateTaskStateResourceQueue.getResourceType( ) );
            daoUtil.setInt( ++nIndex, updateTaskStateResourceQueue.getIdExternalParent( ) );
            daoUtil.setInt( ++nIndex, updateTaskStateResourceQueue.getIdWorkflow( ) );
            daoUtil.setInt( ++nIndex, updateTaskStateResourceQueue.getIdResourceHistory( ) );
            daoUtil.setBoolean( ++nIndex, updateTaskStateResourceQueue.getStatus( ) );
            daoUtil.setTimestamp( ++nIndex, updateTaskStateResourceQueue.getCreationDate( ) );
            daoUtil.setTimestamp( ++nIndex, updateTaskStateResourceQueue.getAlertReferenceDate( ) );
            daoUtil.setInt( ++nIndex, updateTaskStateResourceQueue.getIdState( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                updateTaskStateResourceQueue.setIdResourceQueue( daoUtil.getGeneratedKeyInt( 1 ) );
            }

        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<UpdateTaskStateResourceQueue> load( int nId, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nId );
            daoUtil.executeQuery( );

            UpdateTaskStateResourceQueue updateTaskStateResourceQueue = null;

            if ( daoUtil.next( ) )
            {
                updateTaskStateResourceQueue = buildUpdateTaskStateResourceQueue( daoUtil );
            }

            return Optional.ofNullable( updateTaskStateResourceQueue );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteByPrimaryKey( int nIdResourceQueue, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdResourceQueue );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteByResourceHistory( int IdResourceHistory, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_RESOURCE_HISTORY, plugin ) )
        {
            daoUtil.setInt( 1, IdResourceHistory );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void deleteByResource( int nIdResource, String strResourceType, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_RESOURCE, plugin ) )
        {
            daoUtil.setInt( 1, nIdResource );
            daoUtil.setString( 2, strResourceType );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteByIdTask( int nIdTask, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ID_TASK, plugin ) )
        {
            daoUtil.setInt( 1, nIdTask );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( UpdateTaskStateResourceQueue updateTaskStateResourceQueue, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 0;

            daoUtil.setInt( ++nIndex, updateTaskStateResourceQueue.getIdResource( ) );
            daoUtil.setInt( ++nIndex, updateTaskStateResourceQueue.getIdTask( ) );
            daoUtil.setString( ++nIndex, updateTaskStateResourceQueue.getResourceType( ) );
            daoUtil.setInt( ++nIndex, updateTaskStateResourceQueue.getIdExternalParent( ) );
            daoUtil.setInt( ++nIndex, updateTaskStateResourceQueue.getIdWorkflow( ) );
            daoUtil.setInt( ++nIndex, updateTaskStateResourceQueue.getIdResourceHistory( ) );
            daoUtil.setBoolean( ++nIndex, updateTaskStateResourceQueue.getStatus( ) );
            daoUtil.setTimestamp( ++nIndex, updateTaskStateResourceQueue.getCreationDate( ) );
            daoUtil.setTimestamp( ++nIndex, updateTaskStateResourceQueue.getAlertReferenceDate( ) );
            daoUtil.setInt( ++nIndex, updateTaskStateResourceQueue.getIdState( ) );
            daoUtil.setInt( ++nIndex, updateTaskStateResourceQueue.getIdResourceQueue( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<UpdateTaskStateResourceQueue> selectAllActived( Plugin plugin )
    {
        List<UpdateTaskStateResourceQueue> listUpdateResourceQueues = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ACTIVED, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listUpdateResourceQueues.add( buildUpdateTaskStateResourceQueue( daoUtil ) );
            }

            return listUpdateResourceQueues;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<UpdateTaskStateResourceQueue> selectByPrimaryKeyList( List<Integer> listIdResourceQueues, Plugin plugin )
    {
        List<UpdateTaskStateResourceQueue> listUpdateResourceQueues = new ArrayList<>( );
        StringBuilder sbSql = new StringBuilder( SQL_QUERY_SELECTALL_ACTIVED_BY_LIST );
        if ( CollectionUtils.isNotEmpty( listIdResourceQueues ) )
        {
            sbSql.append( listIdResourceQueues.stream( ).map( s -> "?" ).collect( Collectors.joining( "," ) ) );
            sbSql.append( ")" );
        }

        try ( DAOUtil daoUtil = new DAOUtil( sbSql.toString( ), plugin ) )
        {
            int nIndex = 0;
            for ( int id : listIdResourceQueues )
            {
                daoUtil.setInt( ++nIndex, id );
            }
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {

                listUpdateResourceQueues.add( buildUpdateTaskStateResourceQueue( daoUtil ) );
            }

            return listUpdateResourceQueues;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<UpdateTaskStateResourceQueue> selectAllDisabled( Plugin plugin )
    {
        List<UpdateTaskStateResourceQueue> listUpdateResourceQueues = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_DISABLED, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listUpdateResourceQueues.add( buildUpdateTaskStateResourceQueue( daoUtil ) );
            }
            return listUpdateResourceQueues;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectAllIdQueueActived( Plugin plugin )
    {
        List<Integer> listIdResourceQueues = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID_ACTIVED, plugin ) )
        {
            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {

                listIdResourceQueues.add( daoUtil.getInt( 1 ) );
            }
            return listIdResourceQueues;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<UpdateTaskStateResourceQueue> load( int nIdResource, int nIdTask, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_TASK_AND_ID_RESOURCE, plugin ) )
        {
            daoUtil.setInt( 1, nIdResource );
            daoUtil.setInt( 2, nIdTask );
            daoUtil.executeQuery( );

            UpdateTaskStateResourceQueue updateTaskStateResourceQueue = null;

            if ( daoUtil.next( ) )
            {
                updateTaskStateResourceQueue = buildUpdateTaskStateResourceQueue( daoUtil );
            }

            return Optional.ofNullable( updateTaskStateResourceQueue );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<UpdateTaskStateResourceQueue> load( int nIdResource, String strResourceType, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_RESOURCE, plugin ) )
        {
            daoUtil.setInt( 1, nIdResource );
            daoUtil.setString( 2, strResourceType );
            daoUtil.executeQuery( );

            UpdateTaskStateResourceQueue updateTaskStateResourceQueue = null;

            if ( daoUtil.next( ) )
            {
                updateTaskStateResourceQueue = buildUpdateTaskStateResourceQueue( daoUtil );

            }

            return Optional.ofNullable( updateTaskStateResourceQueue );
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<UpdateTaskStateResourceQueue> load( int nIdResource, int nIdTask, String strResourceType, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_TASK_AND_ID_RESOURCE_AND_RESSOURCE_TYPE, plugin ) )
        {
            daoUtil.setInt( 1, nIdResource );
            daoUtil.setInt( 2, nIdTask );
            daoUtil.setString( 3, strResourceType );
            daoUtil.executeQuery( );
            
            UpdateTaskStateResourceQueue updateTaskStateResourceQueue = null;
            
            if ( daoUtil.next( ) )
            {
                updateTaskStateResourceQueue = buildUpdateTaskStateResourceQueue( daoUtil );
                
            }
            
            return Optional.ofNullable( updateTaskStateResourceQueue );
        }
    }

    /**
     * Build UpdateTaskStateResourceQueue from daoUtil
     * 
     * @return UpdateTaskStateResourceQueue object builded
     */
    private UpdateTaskStateResourceQueue buildUpdateTaskStateResourceQueue( DAOUtil daoUtil )
    {

        int nIndex = 0;
        UpdateTaskStateResourceQueue updateTaskStateResourceQueue = new UpdateTaskStateResourceQueue( );
        updateTaskStateResourceQueue.setIdResourceQueue( daoUtil.getInt( ++nIndex ) );
        updateTaskStateResourceQueue.setIdResource( daoUtil.getInt( ++nIndex ) );
        updateTaskStateResourceQueue.setIdTask( daoUtil.getInt( ++nIndex ) );
        updateTaskStateResourceQueue.setResourceType( daoUtil.getString( ++nIndex ) );
        updateTaskStateResourceQueue.setIdExternalParent( daoUtil.getInt( ++nIndex ) );
        updateTaskStateResourceQueue.setIdWorkflow( daoUtil.getInt( ++nIndex ) );
        updateTaskStateResourceQueue.setIdResourceHistory( daoUtil.getInt( ++nIndex ) );
        updateTaskStateResourceQueue.setStatus( daoUtil.getBoolean( ++nIndex ) );
        updateTaskStateResourceQueue.setCreationDate( daoUtil.getTimestamp( ++nIndex ) );
        updateTaskStateResourceQueue.setAlertReferenceDate( daoUtil.getTimestamp( ++nIndex ) );
        updateTaskStateResourceQueue.setIdState( daoUtil.getInt( ++nIndex ) );

        return updateTaskStateResourceQueue;
    }

}
