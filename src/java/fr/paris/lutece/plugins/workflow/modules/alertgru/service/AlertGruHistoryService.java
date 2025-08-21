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

import fr.paris.lutece.plugins.workflow.modules.alertgru.business.history.AlertGruHistory;
import fr.paris.lutece.plugins.workflow.modules.alertgru.business.history.AlertGruHistoryDAO;
import fr.paris.lutece.plugins.workflow.modules.alertgru.business.history.IAlertGruHistoryDAO;
import fr.paris.lutece.portal.service.plugin.Plugin;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Named( AlertGruHistoryService.BEAN_SERVICE )
public class AlertGruHistoryService implements IAlertGruHistoryService
{
    /**
     * The name of the bean of this service
     */
    public static final String BEAN_SERVICE = "workflow-alertgru.alertGruHistoryService";
    @Inject
    @Named( AlertGruHistoryDAO.BEAN )
    private IAlertGruHistoryDAO _dao;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void create( AlertGruHistory alertGru, Plugin plugin )
    {
        _dao.insert( alertGru, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void removeByHistory( int nIdHistory, int nIdTask, Plugin plugin )
    {
        _dao.deleteByHistory( nIdHistory, nIdTask, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void removeByTask( int nIdTask, Plugin plugin )
    {
        _dao.deleteByTask( nIdTask, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AlertGruHistory findByPrimaryKey( int nIdHistory, int nIdTask, Plugin plugin )
    {
        return _dao.load( nIdHistory, nIdTask, plugin );
    }
}
