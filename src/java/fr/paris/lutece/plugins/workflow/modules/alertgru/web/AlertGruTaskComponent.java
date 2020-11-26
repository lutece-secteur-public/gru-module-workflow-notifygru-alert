/*
 * Copyright (c) 2002-2020, City of Paris
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
package fr.paris.lutece.plugins.workflow.modules.alertgru.web;

import fr.paris.lutece.plugins.workflow.modules.alertgru.business.AlertGruTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.alertgru.business.history.AlertGruHistory;
import fr.paris.lutece.plugins.workflow.modules.alertgru.service.AlertGruHistoryService;
import fr.paris.lutece.plugins.workflow.modules.alertgru.service.AlertGruTaskConfigService;
import fr.paris.lutece.plugins.workflow.modules.alertgru.service.IAlertGruHistoryService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflow.web.task.NoFormTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

public class AlertGruTaskComponent extends NoFormTaskComponent
{

    // SERVICES
    @Inject
    @Named( AlertGruTaskConfigService.BEAN_SERVICE )
    private ITaskConfigService _taskAlertGruConfigService;

    @Inject
    @Named( AlertGruHistoryService.BEAN_SERVICE )
    private IAlertGruHistoryService _taskAlertGruHistoryService;

    // MARKS
    private static final String MARK_CONFIG = "config";
    private static final String MARK_ALERT_HISTORY = "information_history";

    // TEMPLATES
    private static final String TEMPLATE_TASK_ALERT_INFORMATION = "admin/plugins/workflow/modules/alertgru/task_alert_information.html";

    @Override
    public String getDisplayConfigForm( HttpServletRequest httpServletRequest, Locale locale, ITask iTask )
    {
        HtmlTemplate template = new AlertGruTaskConfigController( iTask ).buildView( httpServletRequest );

        return template.getHtml( );
    }

    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest httpServletRequest, Locale locale, ITask iTask )
    {
        AlertGruHistory alertGruTaskHistory = _taskAlertGruHistoryService.findByPrimaryKey( nIdHistory, iTask.getId( ), WorkflowUtils.getPlugin( ) );

        Map<String, Object> model = new HashMap<>( );
        AlertGruTaskConfig config = _taskAlertGruConfigService.findByPrimaryKey( iTask.getId( ) );
        model.put( MARK_CONFIG, config );
        model.put( MARK_ALERT_HISTORY, alertGruTaskHistory );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_ALERT_INFORMATION, locale, model );
        return template.getHtml( );

    }

    @Override
    public String getTaskInformationXml( int i, HttpServletRequest httpServletRequest, Locale locale, ITask iTask )
    {
        return null;
    }

    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
        return new AlertGruTaskConfigController( task ).performAction( request );
    }
}
