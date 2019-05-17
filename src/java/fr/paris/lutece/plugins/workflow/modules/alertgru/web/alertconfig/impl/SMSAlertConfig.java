/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.alertgru.web.alertconfig.impl;

import fr.paris.lutece.plugins.workflow.modules.alertgru.business.AlertGruTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.alertgru.web.AbstractAlertConfigValidator;
import fr.paris.lutece.plugins.workflow.modules.alertgru.web.IAlertConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * This class represents a configuration for the SMS alert
 *
 */
public class SMSAlertConfig implements IAlertConfig
{
    private static final String NAME = "sms";

    // PARAMETERS
    private static final String PARAMETER_MESSAGE = "message_sms";

    private final HttpServletRequest _request;
    protected final AlertGruTaskConfig _config;
    private final String _strMessage;

    /**
     * Constructor
     * 
     * @param request
     *            the request used by this configuration
     * @param config
     *            the configuration of the task
     */
    public SMSAlertConfig(HttpServletRequest request, AlertGruTaskConfig config )
    {
        _request = request;
        _config = config;
        _strMessage = request.getParameter( PARAMETER_MESSAGE );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName( )
    {
        return NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isActive( )
    {
        return _config.isActiveOngletSMS( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setActive( boolean bActive )
    {
        _config.setActiveOngletSMS( bActive );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractAlertConfigValidator getValidator( )
    {
        return new Validator( _request );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addConfig( )
    {
        _config.setMessageSMS( _strMessage );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeConfig( )
    {
        _config.setMessageSMS( null );
    }

    /**
     * This class represents a validator for the SMS alert configuration
     *
     */
    private final class Validator extends AbstractAlertConfigValidator
    {
        // MESSAGES
        private static final String MESSAGE_FIELD_MESSAGE_MANDATORY = "module.workflow.alertgru.message.field.sms";

        /**
         * Constructor
         * 
         * @param request
         *            the request used by this validator
         */
        private Validator( HttpServletRequest request )
        {
            super( request );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected List<String> validateFieldsWithoutMarker( )
        {
            List<String> listErrors = new ArrayList<>( );

            if ( !isMandatoryFieldValid( _strMessage ) )
            {
                listErrors.add( getErrorMessageForMandatoryField( MESSAGE_FIELD_MESSAGE_MANDATORY ) );
            }

            return listErrors;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected boolean areFieldsWithMarkersValid( Map<String, Object> model )
        {
            return areMarkersValid( _strMessage, model );
        }

    }

}
