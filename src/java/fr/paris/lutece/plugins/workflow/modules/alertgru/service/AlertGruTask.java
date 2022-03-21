/*
 * Copyright (c) 2002-2021, City of Paris
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

import fr.paris.lutece.plugins.workflow.modules.alertgru.business.UpdateTaskStateResourceQueue;
import fr.paris.lutece.plugins.workflow.modules.alertgru.business.UpdateTaskStateResourceQueueHome;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

public class AlertGruTask extends SimpleTask
{
    /** The _task alert gru config service. */
    // SERVICES
    @Inject
    @Named( AlertGruTaskConfigService.BEAN_SERVICE )
    private ITaskConfigService _taskAlertGruConfigService;
    /** The _task notify gru history service. */
    @Inject
    @Named( "workflow-alertgru.alertGruHistoryService" )
    private IAlertGruHistoryService _taskAlertGruHistoryService;
    @Inject
    private IResourceHistoryService _resourceHistoryService;
    @Inject
    private IResourceWorkflowService _resourceWorkflowService;

    @Override
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {      
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );

        Optional<Timestamp>  referenceDate=TaskAlertService.INSNACE.getReferenceDateAlert(this.getId(), resourceHistory, request);        
        if(referenceDate.isPresent( )) {
        	ResourceWorkflow resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( resourceHistory.getIdResource( ), resourceHistory.getResourceType( ),
                      resourceHistory.getWorkflow( ).getId( ) );
                  
        	UpdateTaskStateResourceQueue updateResourceQueue = new UpdateTaskStateResourceQueue( );
            updateResourceQueue.setIdResource( resourceHistory.getIdResource( ) );
            updateResourceQueue.setIdResourceHistory( nIdResourceHistory );
            updateResourceQueue.setIdTask( getId( ) );
            updateResourceQueue.setIdExternalParent( resourceWorkflow.getExternalParentId( ) );
            updateResourceQueue.setIdWorkflow( resourceWorkflow.getWorkflow( ).getId( ) );
            updateResourceQueue.setResourceType( resourceHistory.getResourceType( ) );
            updateResourceQueue.setStatus( true );
            updateResourceQueue.setCreationDate( resourceHistory.getCreationDate( ) );
            updateResourceQueue.setAlertReferenceDate(referenceDate.get( ));
            updateResourceQueue.setIdState(resourceWorkflow.getState().getId( ));
            
        	Optional<UpdateTaskStateResourceQueue> resourceQueue =UpdateTaskStateResourceQueueHome.find( resourceHistory.getIdResource( ), resourceHistory.getResourceType( ) );
        	if (resourceQueue.isPresent()) {
        		
        		updateResourceQueue.setIdResourceQueue(resourceQueue.get().getIdResourceQueue( ));
        		UpdateTaskStateResourceQueueHome.update(updateResourceQueue);
        	}else {
        		
                UpdateTaskStateResourceQueueHome.create( updateResourceQueue );
        	}    
        }
    }
        
    /**
     * (non-Javadoc)
     *
     * @see fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask#doRemoveConfig()
     */
    @Override
    public void doRemoveConfig( )
    {
        _taskAlertGruConfigService.remove( this.getId( ) );
        _taskAlertGruHistoryService.removeByTask( this.getId( ), WorkflowUtils.getPlugin( ) );
        UpdateTaskStateResourceQueueHome.removeByIdTask( this.getId( ) );
    }

    /**
     * {@inheritDoc}
     *
     * @param nIdHistory
     */
    @Override
    public void doRemoveTaskInformation( int nIdHistory )
    {
        _taskAlertGruHistoryService.removeByHistory( nIdHistory, this.getId( ), WorkflowUtils.getPlugin( ) );
        UpdateTaskStateResourceQueueHome.removeByResourceHistory( nIdHistory );
    }

    /**
     * {@inheritDoc}
     *
     * @param locale
     * @return
     */
    @Override
    public String getTitle( Locale locale )
    {
        return "Alert notification GRU";
    }

    
    
   
}
