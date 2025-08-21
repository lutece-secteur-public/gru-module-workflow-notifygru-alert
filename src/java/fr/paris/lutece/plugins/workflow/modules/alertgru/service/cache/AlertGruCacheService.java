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
package fr.paris.lutece.plugins.workflow.modules.alertgru.service.cache;

import fr.paris.lutece.plugins.workflow.modules.alertgru.business.AlertGruTaskConfig;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.portal.service.cache.AbstractCacheableService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * The Class NotifyGruCacheService.
 */
@ApplicationScoped
public class AlertGruCacheService extends AbstractCacheableService<String, AlertGruTaskConfig>
{
    /** The Constant CACHE_NAME. */
    private static final String CACHE_NAME = "workflow.alertGruConfigCacheService";

    @PostConstruct
    public void init( )
    {
        initCache( CACHE_NAME, String.class, AlertGruTaskConfig.class );
    }

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
    	AlertGruTaskConfig config = null;	
    	if ( isCacheEnable( ) && isCacheAvailable( ) )
    	{
    		config = get( getCacheKey( nidTask ) );
    	}

        if ( config == null )
        {
            config = taskAlertGruConfigService.findByPrimaryKey( nidTask );

            if ( config == null )
            {
                // no config stored yet for this task => adding an empty one in cache
                config = new AlertGruTaskConfig( );
            }

            if ( isCacheEnable( ) && isCacheAvailable( ) )
        	{
            	put( getCacheKey( nidTask ), config );
        	}            
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
    	if ( isCacheEnable( ) && isCacheAvailable( ) )
    	{
            remove( getCacheKey( nidTask ) );
    	}
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

    /**
     * Checks whether the cache instance is initialized and currently open.
     *
     * @return true if the cache is not null and not closed, false otherwise.
     */
    private boolean isCacheAvailable( )
    {
        return _cache != null && !_cache.isClosed( );
    }
}
