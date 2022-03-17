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
package fr.paris.lutece.plugins.workflow.modules.alertgru.service;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import fr.paris.lutece.plugins.workflow.modules.alertgru.business.IUpdateTaskStateResourceQueueDAO;
import fr.paris.lutece.plugins.workflow.modules.alertgru.business.UpdateTaskStateResourceQueue;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.resource.ResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.ResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.task.TaskService;

/**
 * 
 * UpdateTaskStateResourceQueueService
 *
 */
public class UpdateTaskStateResourceQueueService implements IUpdateTaskStateResourceQueueService
{
    @Inject
    @Named( ResourceWorkflowService.BEAN_SERVICE )
    private IResourceWorkflowService _resourceWorkflowService;
    @Inject
    @Named( ResourceHistoryService.BEAN_SERVICE )
    private IResourceHistoryService _resourceHistoryService;
    @Inject
    @Named( ActionService.BEAN_SERVICE )
    private IActionService _actionService;
    @Inject
    @Named( StateService.BEAN_SERVICE )
    private IStateService _stateService;
    @Inject
    @Named( TaskService.BEAN_SERVICE )
    private ITaskService _taskService;
    @Inject
    private IUpdateTaskStateResourceQueueDAO _updateResourceQueueDAO;

    /**
     * {@inheritDoc}
     */
    @Override
    public void create( UpdateTaskStateResourceQueue updateResourceQueue )
    {
        if ( updateResourceQueue != null )
        {
            _updateResourceQueueDAO.insert( updateResourceQueue );
        }
    }

    @Override
    public void update( UpdateTaskStateResourceQueue updateResourceQueue )
    {
        if ( updateResourceQueue != null )
        {
            _updateResourceQueueDAO.store( updateResourceQueue );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UpdateTaskStateResourceQueue> findAllActived( )
    {
        return _updateResourceQueueDAO.selectAllActived( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UpdateTaskStateResourceQueue> findAllDisabled( )
    {
        return _updateResourceQueueDAO.selectAllDisabled( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UpdateTaskStateResourceQueue find( int nIdResourceHistory, int nIdTask )
    {
        return _updateResourceQueueDAO.find( nIdResourceHistory, nIdTask );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdResourceHistory, int nIdTask )
    {
        _updateResourceQueueDAO.delete( nIdResourceHistory, nIdTask );
    }

    /**
     * {@inheritDoc}
     */
	@Override
	public void deleteByIdTask(int nIdTask) 
	{
		_updateResourceQueueDAO.deleteByIdTask( nIdTask );	
	}

}
