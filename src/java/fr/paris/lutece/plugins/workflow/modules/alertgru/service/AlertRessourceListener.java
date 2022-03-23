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

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.workflow.modules.alertgru.business.UpdateTaskStateResourceQueueHome;
import fr.paris.lutece.portal.business.event.EventRessourceListener;
import fr.paris.lutece.portal.business.event.ResourceEvent;
import fr.paris.lutece.portal.service.util.AppLogService;

public class AlertRessourceListener implements EventRessourceListener
{

    @Override
    public String getName( )
    {
        return AlertGruPlugin.PLUGIN_NAME;
    }

    @Override
    public void addedResource( ResourceEvent event )
    {
        // do nothing

    }

    @Override
    public void deletedResource( ResourceEvent event )
    {
        try
        {
            if ( StringUtils.isNumeric( event.getIdResource( ) ) )
            {

                UpdateTaskStateResourceQueueHome.removeByResource( Integer.parseInt( event.getIdResource( ) ), event.getTypeResource( ) );
            }
        }
        catch( Exception e )
        {
            AppLogService.error( e.getMessage( ), e );
        }
    }

    @Override
    public void updatedResource( ResourceEvent event )
    {
        try
        {
            TaskAlertService.INSNACE.updateResourceQueue( event );

        }
        catch( Exception e )
        {
            AppLogService.error( e.getMessage( ), e );
        }
    }

}
