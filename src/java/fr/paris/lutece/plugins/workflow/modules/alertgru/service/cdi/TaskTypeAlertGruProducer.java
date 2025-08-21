/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.plugins.workflow.modules.alertgru.service.cdi;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.paris.lutece.plugins.workflowcore.business.task.ITaskType;
import fr.paris.lutece.plugins.workflowcore.business.task.TaskType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

@ApplicationScoped
public class TaskTypeAlertGruProducer 
{
	@Produces
    @ApplicationScoped
    @Named( "workflow-alertgru.taskTypeAlertGru" )
    public ITaskType produceTaskTypeComment( 
    		@ConfigProperty( name = "workflow-alertgru.taskTypeAlertGru.key" ) String key,
            @ConfigProperty( name = "workflow-alertgru.taskTypeAlertGru.titleI18nKey" ) String titleI18nKey,
            @ConfigProperty( name = "workflow-alertgru.taskTypeAlertGru.beanName" ) String beanName,
            @ConfigProperty( name = "workflow-alertgru.taskTypeAlertGru.configBeanName" ) String configBeanName,
            @ConfigProperty( name = "workflow-alertgru.taskTypeAlertGru.configRequired", defaultValue = "false" ) boolean configRequired,
            @ConfigProperty( name = "workflow-alertgru.taskTypeAlertGru.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
            @ConfigProperty( name = "workflow-alertgru.taskTypeAlertGru.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
    {
		TaskType taskType = new TaskType( );
        taskType.setKey( key );
        taskType.setTitleI18nKey( titleI18nKey );
        taskType.setBeanName( beanName );
        taskType.setConfigBeanName( configBeanName );
        taskType.setConfigRequired( configRequired );
        taskType.setFormTaskRequired( formTaskRequired );
        taskType.setTaskForAutomaticAction( taskForAutomaticAction );
        return taskType;
    }
}
