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
package fr.paris.lutece.plugins.workflow.modules.alertgru.service.cache;

import fr.paris.lutece.plugins.workflow.modules.alertgru.business.AlertGruTaskConfig;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.portal.service.cache.AbstractCacheableService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * The Class NotifyGruCacheService.
 */
public final class AlertGruCacheService extends AbstractCacheableService
{
    /** The Constant CACHE_NAME. */
    private static final String CACHE_NAME = "workflow.alertGruConfigCacheService";

    /** The Constant BEAN_SERVICE. */
    private static final String BEAN_SERVICE = "workflow-alertgru.alertGruCacheService";

    /** The _singleton. */
    private static AlertGruCacheService _singleton;

    /**
     * Instantiates a new notify gru cache service.
     */
    private AlertGruCacheService( )
    {
        initCache( );
    }

    /**
     * Gets the single instance of NotifyGruCacheService.
     *
     * @return single instance of NotifyGruCacheService
     */
    public static AlertGruCacheService getInstance( )
    {
        if ( _singleton == null )
        {
            _singleton = SpringContextService.getBean( BEAN_SERVICE );
        }

        return _singleton;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.portal.service.util.LuteceService#getName()
     */
    @Override
    public String getName( )
    {
        return CACHE_NAME;
    }

    /**
     * Gets the notify gru config from cache.
     *
     * @param taskAlertGruConfigService
     *            the task notify gru config service
     * @param nidTask
     *            the nid task
     * @return the notify gru config from cache
     */
    public AlertGruTaskConfig getAlertGruConfigFromCache( ITaskConfigService taskAlertGruConfigService, int nidTask )
    {
        AlertGruTaskConfig config = (AlertGruTaskConfig) getFromCache( getCacheKey( nidTask ) );

        if ( config == null )
        {
            config = taskAlertGruConfigService.findByPrimaryKey( nidTask );

            if ( config == null )
            {
                // no config stored yet for this task => adding an empty one in cache
                config = new AlertGruTaskConfig( );
            }

            putInCache( getCacheKey( nidTask ), config );
        }

        return config;
    }

    /**
     * Removes the gru config from cache.
     *
     * @param nidTask
     *            the nid task
     */
    public void removeGruConfigFromCache( int nidTask )
    {
        removeKey( getCacheKey( nidTask ) );
    }

    /**
     * Gets the cache key.
     *
     * @param nidTask
     *            the nid task
     * @return the cache key
     */
    private String getCacheKey( int nidTask )
    {
        StringBuilder sbKey = new StringBuilder( );
        sbKey.append( "[WORKFLOWALERTGRU-" ).append( nidTask ).append( "-CACHE]" );

        return sbKey.toString( );
    }
}
