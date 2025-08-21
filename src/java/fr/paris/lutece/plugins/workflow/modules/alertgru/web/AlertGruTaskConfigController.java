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
package fr.paris.lutece.plugins.workflow.modules.alertgru.web;

import fr.paris.lutece.plugins.workflow.modules.alertgru.business.AlertGruTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.alertgru.service.AlertGruService;
import fr.paris.lutece.plugins.workflow.modules.alertgru.service.AlertGruTaskConfigService;
import fr.paris.lutece.plugins.workflow.modules.alertgru.service.IAlertGruService;
import fr.paris.lutece.plugins.workflow.modules.alertgru.service.ServiceConfigTaskForm;
import fr.paris.lutece.plugins.workflow.modules.alertgru.utils.constants.Constants;
import fr.paris.lutece.plugins.workflow.modules.alertgru.web.alertconfig.impl.AgentAlertConfig;
import fr.paris.lutece.plugins.workflow.modules.alertgru.web.alertconfig.impl.BillingAccountBasedSMSAlertConfig;
import fr.paris.lutece.plugins.workflow.modules.alertgru.web.alertconfig.impl.BroadcastAlertConfig;
import fr.paris.lutece.plugins.workflow.modules.alertgru.web.alertconfig.impl.EmailAlertConfig;
import fr.paris.lutece.plugins.workflow.modules.alertgru.web.alertconfig.impl.GuichetAlertConfig;
import fr.paris.lutece.plugins.workflow.modules.alertgru.web.alertconfig.impl.SMSAlertConfig;
import fr.paris.lutece.plugins.workflow.service.provider.MarkerProviderService;
import fr.paris.lutece.plugins.workflow.service.provider.ProviderManagerUtil;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.provider.IMarkerProvider;
import fr.paris.lutece.plugins.workflowcore.service.provider.IProviderManager;
import fr.paris.lutece.plugins.workflowcore.service.provider.InfoMarker;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.util.CdiHelper;
import fr.paris.lutece.portal.util.mvc.utils.MVCMessage;
import fr.paris.lutece.util.ErrorMessage;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

public class AlertGruTaskConfigController
{

    // Templates
    private static final String TEMPLATE_FIRST_STEP = "admin/plugins/workflow/modules/alertgru/task_alert_gru_config_first_step.html";
    private static final String TEMPLATE_SECOND_STEP = "admin/plugins/workflow/modules/alertgru/task_alert_gru_config_second_step.html";

    // Marks
    private static final String MARK_SELECT_PROVIDER = "list_provider";
    private static final String MARK_LIST_MARKER_PROVIDER = "list_marker_provider";
    private static final String MARK_LIST_ALERT_CONFIG = "list_alert_config";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_WEBAPP_URL = "webapp_url";

    // Services
    private static final ITaskConfigService _taskAlertGruConfigService = CdiHelper.getReference( ITaskConfigService.class, AlertGruTaskConfigService.BEAN_SERVICE );
    private static final IAlertGruService _alertGruService = CdiHelper.getReference( IAlertGruService.class, AlertGruService.BEAN_SERVICE );

    private final ITask _task;
    private AlertGruTaskConfig _config;

    // Parameters
    private static final String PARAMETER_APPLY = "apply";
    private static final String PARAMETER_APPLY_MARKER = "apply_marker";

    // Actions
    private static final String ACTION_FIRST_STEP_SAVE = "saveFirstStep";
    private static final String ACTION_SECOND_STEP_SAVE = "saveSecondStep";
    private static final String ACTION_ADVANCED_CONFIG_SAVE = "saveAdvancedConfig";
    private static final String ACTION_ADVANCED_CONFIG_CANCEL = "cancelAdvancedConfig";
    private static final String ACTION_ALERT_CONFIG_ADD = "AddAlertConfig";
    private static final String ACTION_ALERT_CONFIG_REMOVE_PREFIX = "RemoveAlertConfig_";
    private static final String ACTION_ALERT_CONFIG_MARKERS = "loadMarkers";

    // Sessions
    private static final String SESSION_ID_PROVIDER = "alertProviderSession";

    /**
     * Constructor
     *
     * @param task
     *            the task associated to the configuration
     */
    public AlertGruTaskConfigController( ITask task )
    {
        _task = task;

        findConfig( );
    }

    /**
     * Finds the configuration
     */
    private void findConfig( )
    {
        _config = _taskAlertGruConfigService.findByPrimaryKey( _task.getId( ) );

        if ( _config == null )
        {
            // no config stored yet for this task, setting a empty one
            _config = new AlertGruTaskConfig( );
        }
    }

    /**
     * Initializes the alert configurations
     *
     * @param request
     *            the request used to initialize the configurations
     * @return the list of configurations
     */
    private List<IAlertConfig> initAlertConfigs( HttpServletRequest request )
    {
        List<IAlertConfig> listAlertConfig = new ArrayList<>( );

        listAlertConfig.add( new AgentAlertConfig( request, _config ) );
        listAlertConfig.add( new BroadcastAlertConfig( request, _config ) );
        listAlertConfig.add( new EmailAlertConfig( request, _config ) );
        listAlertConfig.add( new GuichetAlertConfig( request, _config ) );
        if ( _config.isBillingAccountBasedSmsNotification( ) )
        {
            listAlertConfig.add( new BillingAccountBasedSMSAlertConfig( request, _config ) );
        }
        else
        {
            listAlertConfig.add( new SMSAlertConfig( request, _config ) );
        }

        return listAlertConfig;
    }

    /**
     * Builds the view depending on the specified request and locale
     *
     * @param request
     *            the request the locale
     * @return the {@link HtmlTemplate} representing the view
     */
    public HtmlTemplate buildView( HttpServletRequest request )
    {
        HtmlTemplate template = null;
        View view = new View( request );

        if ( _config.getIdSpringProvider( ) == null || _config.getMarkerAlert( ) == null )
        {
            template = view.buildFirstStep( );
        }
        else
        {
            template = view.buildSecondStep( );
        }

        return template;
    }

    /**
     * Performs an action triggered by the user
     *
     * @param request
     *            the request containing the action
     * @return the URL of the error page if there is an error during the action, {@code null} otherwise
     */
    public String performAction( HttpServletRequest request )
    {
        String strErrorUrl = null;
        String strAction = findAction( request );
        Action action = new Action( request, strAction );

        if ( ACTION_ALERT_CONFIG_MARKERS.equals( strAction ) )
        {
            strErrorUrl = action.loadMarkers( );
        }

        if ( ACTION_FIRST_STEP_SAVE.equals( strAction ) )
        {
            strErrorUrl = action.saveFirstStep( );
        }

        if ( ACTION_SECOND_STEP_SAVE.equals( strAction ) )
        {
            strErrorUrl = action.saveSecondStep( );
        }

        if ( strAction.startsWith( ACTION_ALERT_CONFIG_REMOVE_PREFIX ) )
        {
            strErrorUrl = action.removeAlertConf( );
        }

        if ( ACTION_ALERT_CONFIG_ADD.equals( strAction ) )
        {
            strErrorUrl = action.addAlertConf( );
        }

        if ( ACTION_ADVANCED_CONFIG_CANCEL.equals( strAction ) )
        {
            strErrorUrl = action.cancel( );
        }

        if ( ACTION_ADVANCED_CONFIG_SAVE.equals( strAction ) )
        {
            strErrorUrl = action.saveAdvancedConfig( );
        }

        return strErrorUrl;
    }

    /**
     * Finds the action contained in the specified request
     *
     * @param request
     *            the request
     * @return the action
     */
    private String findAction( HttpServletRequest request )
    {
        String strAction;
        if ( !StringUtils.isBlank( request.getParameter( PARAMETER_APPLY_MARKER ) ) )
        {
            strAction = request.getParameter( PARAMETER_APPLY_MARKER );
        }
        else
        {
            strAction = request.getParameter( PARAMETER_APPLY );
        }
        if ( StringUtils.isBlank( strAction ) )
        {
            strAction = ACTION_SECOND_STEP_SAVE;
        }

        return strAction;
    }

    /**
     * This class represents a view of the task configuration
     *
     */
    private final class View
    {
        private final HttpServletRequest _request;
        private final List<IAlertConfig> _listAlertConfig;
        private final Map<String, Object> _model;

        /**
         * Constructor
         *
         * @param request
         *            the request used by the view
         */
        private View( HttpServletRequest request )
        {
            _request = request;
            _model = new HashMap<>( );

            _listAlertConfig = initAlertConfigs( request );
        }

        /**
         * Builds the first step of the configuration
         *
         * @return the {@link HtmlTemplate} representing the view
         */
        private HtmlTemplate buildFirstStep( )
        {
            fillModelWithConfig( );
            fillModelWithGlobalConfig( );
            if ( _config.getIdSpringProvider( ) != null )
            {
                manageAlertGruMarkersInModel( );
            }

            return AppTemplateService.getTemplate( TEMPLATE_FIRST_STEP, _request.getLocale( ), _model );
        }

        /**
         * Builds the second step of the configuration
         *
         * @return the {@link HtmlTemplate} representing the view
         */
        private HtmlTemplate buildSecondStep( )
        {
            fillModelWithConfig( );
            fillModelWithRichTextEditorConfig( );
            fillModelWithAlertConfigs( );
            fillModelWithMailSenderName( );
            fillModelWithMailingList( );
            fillModelWithGlobalConfig( );
            manageAlertGruMarkersInModel( );

            return AppTemplateService.getTemplate( TEMPLATE_SECOND_STEP, _request.getLocale( ), _model );
        }

        /**
         * Fills the model with the task configuration
         */
        private void fillModelWithConfig( )
        {
            if ( _request.getSession( ).getAttribute( SESSION_ID_PROVIDER ) != null )
            {
                _config.setIdSpringProvider( (String) _request.getSession( ).getAttribute( SESSION_ID_PROVIDER ) );
            }
            _model.put( Constants.MARK_CONFIG, _config );
        }

        /**
         * Fills the model with the configuration of the rich text editor
         */
        private void fillModelWithRichTextEditorConfig( )
        {
            _model.put( MARK_LOCALE, _request.getLocale( ) );
            _model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( _request ) );
        }

        /**
         * Fills the model the alert configurations (as reference list)
         */
        private void fillModelWithAlertConfigs( )
        {
            ReferenceList listAlertConfig = ServiceConfigTaskForm.buildReferenceListOfInactiveAlertConfigs( _listAlertConfig, _request.getLocale( ) );

            _model.put( MARK_LIST_ALERT_CONFIG, listAlertConfig );
        }

        /**
         * Fills the model with the global configuration of the task
         */
        private void fillModelWithGlobalConfig( )
        {
            fillModelWithProviders( );
            fillModelWithMarkerProviders( );
            fillModelWithAlertInfos( );
        }

        /**
         * Fills the model with the providers
         */
        private void fillModelWithProviders( )
        {
            _model.put( MARK_SELECT_PROVIDER, ServiceConfigTaskForm.getProviderReferenceList( _task ) );
        }

        /**
         * Fills the model with the marker providers
         */
        private void fillModelWithMarkerProviders( )
        {
            _model.put( MARK_LIST_MARKER_PROVIDER, MarkerProviderService.getInstance( ).getMarkerProviders( ) );
        }

        /**
         * Fills the model with the mailing list
         */
        private void fillModelWithMailingList( )
        {
            _model.put( Constants.MARK_MAILING_LIST, _alertGruService.getMailingList( _request ) );
        }

        /**
         * Fills the model with the mail sender name
         */
        private void fillModelWithMailSenderName( )
        {
            String strDefaultSenderName = AppPropertiesService.getProperty( Constants.PROPERTY_NOTIFY_MAIL_DEFAULT_SENDER_NAME );

            _model.put( Constants.MARK_DEFAULT_SENDER_NAME, strDefaultSenderName );
        }

        private void fillModelWithAlertInfos( )
        {
            _model.put( Constants.MARK_STATE_LIST, _alertGruService.getListStates( _task.getAction( ).getId( ) ) );

        }

        /**
         * Manages the AlertyGru markers in the model
         */
        private void manageAlertGruMarkersInModel( )
        {
            String strIdSpringProvider = _request.getSession( ).getAttribute( SESSION_ID_PROVIDER ) != null
                    ? (String) _request.getSession( ).getAttribute( SESSION_ID_PROVIDER )
                    : _config.getIdSpringProvider( );
            String strProviderManagerId = ProviderManagerUtil.fetchProviderManagerId( strIdSpringProvider );
            String strProviderId = ProviderManagerUtil.fetchProviderId( strIdSpringProvider );
            IProviderManager providerManager = ProviderManagerUtil.retrieveProviderManager( strProviderManagerId );

            if ( providerManager != null )
            {
                _model.put( Constants.MARK_NOTIFYGRU_MARKERS, findMarkers( providerManager, strProviderId, _config.getMarkerProviders( ) ) );
            }
            else
            {
                List<ErrorMessage> listErrorMessages = new ArrayList<>( );
                listErrorMessages.add( new MVCMessage( I18nService.getLocalizedString( Constants.MESSAGE_ERROR_PROVIDER_NOT_FOUND, _request.getLocale( ) ) ) );
                _model.put( Constants.MARK_MESSAGES_ERROR, listErrorMessages );
            }
        }

        /**
         * Finds the AlertGru markers
         *
         * @param providerManager
         *            the provider manager
         * @param strProviderId
         *            the provider id
         * @param listMarkerProviderIds
         *            the list of marker provider ids
         * @return the AlertGru markers
         */
        private Collection<InfoMarker> findMarkers( IProviderManager providerManager, String strProviderId, List<String> listMarkerProviderIds )
        {
            Collection<InfoMarker> collectionMarkers = providerManager.getProviderDescription( strProviderId ).getMarkerDescriptions( );

            for ( String strMarkerProviderId : listMarkerProviderIds )
            {
                IMarkerProvider markerProvider = MarkerProviderService.getInstance( ).find( strMarkerProviderId );

                if ( markerProvider != null )
                {
                    collectionMarkers.addAll( markerProvider.provideMarkerDescriptions( ) );
                }
            }

            return collectionMarkers;
        }

    }

    /**
     * This class represents an action in the task configuration
     *
     *
     *
     *
     *
     *
     */
    private final class Action
    {
        private static final String VALUE_CHECKBOX = "on";
        private static final String PARAMETER_ADDED_ALERT_CONFIG = "added_alert_config";
        private static final String PARAMETER_REMOVE_SEPARATOR = "_";
        private static final int PARAMETER_REMOVE_ALERT_CONFIG_NAME_PART = 1;

        private final HttpServletRequest _request;
        private final List<IAlertConfig> _listAlertConfig;
        private final IProviderManager _providerManager;
        private final String _strAction;

        /**
         * Constructor
         *
         * @param request
         *            the request used to perform the action
         * @param strAction
         *            the action to perform
         */
        private Action( HttpServletRequest request, String strAction )
        {
            _request = request;
            _strAction = strAction;

            String strProviderManagerId = ProviderManagerUtil.fetchProviderManagerId( _config.getIdSpringProvider( ) );
            _providerManager = ProviderManagerUtil.retrieveProviderManager( strProviderManagerId );

            _listAlertConfig = initAlertConfigs( request );

        }

        /**
         * Load markers from chosen provider
         *
         * @return the URL of the error page if there is an error during the action, {@code null} otherwise
         */
        private String loadMarkers( )
        {
            fillConfigWithGlobalConfig( );
            putProviderInSession( );
            return null;
        }

        private void putProviderInSession( )
        {
            _request.getSession( ).setAttribute( SESSION_ID_PROVIDER, _request.getParameter( Constants.PARAMETER_SELECT_PROVIDER ) );
        }

        /**
         * Saves the first step of the task configuration
         *
         * @return the URL of the error page if there is an error during the action, {@code null} otherwise
         */
        private String saveFirstStep( )
        {
            String strErrorUrl = validateGlobalConfig( );

            if ( !StringUtils.isEmpty( strErrorUrl ) )
            {
                return strErrorUrl;
            }

            fillConfigWithGlobalConfig( );
            putProviderInSession( );
            saveConfig( );

            return null;
        }

        /**
         * Saves the second step of the task configuration
         *
         * @return the URL of the error page if there is an error during the action, {@code null} otherwise
         */
        private String saveSecondStep( )
        {
            String strResult = validateSecondStepConfig( );

            if ( !StringUtils.isEmpty( strResult ) )
            {
                return strResult;
            }

            saveAlertConfigs( );
            saveConfig( );

            return null;

        }

        /**
         * Saves the advanced configuration of the task
         *
         * @return the URL of the error page if there is an error during the action, {@code null} otherwise
         */
        private String saveAdvancedConfig( )
        {
            String strErrorUrl = validateGlobalConfig( );

            if ( !StringUtils.isEmpty( strErrorUrl ) )
            {
                return strErrorUrl;
            }
            fillConfigWithGlobalConfig( );
            fillConfigWithCrmStatus( );
            putProviderInSession( );
            saveConfig( );
            return null;
        }

        /**
         * Cancels the action
         *
         * @return {@code null}
         */
        private String cancel( )
        {
            _request.getSession( ).removeAttribute( SESSION_ID_PROVIDER );
            return null;
        }

        /**
         * Adds an alert configuration in the task
         *
         * @return the URL of the error page if there is an error during the action, {@code null} otherwise
         */
        private String addAlertConf( )
        {
            String strErrorUrl = validateAlertConfigs( );

            if ( !StringUtils.isEmpty( strErrorUrl ) )
            {
                return strErrorUrl;
            }

            String strNameOfAddedAlertConfig = findNameOfAddedAlertConfig( );
            IAlertConfig alertConfigAdded = findAlertConfigByName( strNameOfAddedAlertConfig );

            alertConfigAdded.setActive( true );

            fillConfigWithCurrentAlertConfig( alertConfigAdded );

            saveAlertConfigs( );
            saveConfig( );

            return null;
        }

        /**
         * Removes a alert configuration from the task
         *
         * @return the URL of the error page if there is an error during the action, {@code null} otherwise
         */
        private String removeAlertConf( )
        {
            String strNameOfRemovedAlertConfig = findNameOfRemovedAlertConfig( );
            IAlertConfig alertConfigRemoved = findAlertConfigByName( strNameOfRemovedAlertConfig );

            alertConfigRemoved.setActive( false );
            alertConfigRemoved.removeConfig( );

            saveConfig( );

            return null;
        }

        /**
         * Finds the name of the added alert configuration
         *
         * @return the name of the added alert configuration
         */
        private String findNameOfAddedAlertConfig( )
        {
            return _request.getParameter( PARAMETER_ADDED_ALERT_CONFIG );
        }

        /**
         * Finds the name of the removed alert configuration
         *
         * @return the name of the removed alert configuration
         */
        private String findNameOfRemovedAlertConfig( )
        {
            return _strAction.split( PARAMETER_REMOVE_SEPARATOR ) [PARAMETER_REMOVE_ALERT_CONFIG_NAME_PART];
        }

        /**
         * Finds a alert configuration by its name
         *
         * @param strName
         *            the name of the alert configuration to find
         * @return the found alert configuration, or {@code null} if not found
         */
        private IAlertConfig findAlertConfigByName( String strName )
        {
            IAlertConfig alertConfigFound = null;

            for ( IAlertConfig alertConfig : _listAlertConfig )
            {
                if ( alertConfig.getName( ).equals( strName ) )
                {
                    alertConfigFound = alertConfig;
                    break;
                }
            }

            return alertConfigFound;
        }

        /**
         * Validates the global configuration of the task
         *
         * @return the URL of the error page if there is a validation error, {@code null} otherwise
         */
        private String validateGlobalConfig( )
        {
            String errUrl;
            errUrl = validateFieldIsNotNull( Constants.PARAMETER_SUBJECT_ALERT, Constants.MESSAGE_MANDATORY_SUBJECT );
            if ( StringUtils.isEmpty( errUrl ) )
            {
                errUrl = validateFieldIsNotNull( Constants.PARAMETER_SELECT_PROVIDER, Constants.MESSAGE_MANDATORY_PROVIDER );
                if ( StringUtils.isEmpty( errUrl ) )
                {
                    errUrl = validateFieldIsNotNull( Constants.PARAMETER_MARKER_ALERT, Constants.MESSAGE_MANDATORY_MARKER );
                }
                if ( StringUtils.isEmpty( errUrl ) )
                {
                    errUrl = validateFieldIsNotNull( Constants.PARAMETER_ALERT_AFTER_BEFORE, Constants.MESSAGE_MANDATORY_ALERT_AFTER );
                    if ( StringUtils.isEmpty( errUrl ) )
                    {
                        errUrl = validateFieldIsNotNull( Constants.PARAMETER_STATE_AFTER, Constants.MESSAGE_MANDATORY_STATE_AFTER );
                    }
                }
            }
            return errUrl;
        }

        /**
         * Validates that the field is not {@code null}
         *
         * @return the URL of the error page if there is a validation error, {@code null} otherwise
         */
        private String validateFieldIsNotNull( String paramater, String errMessage )
        {
            String strErrorUrl = null;

            if ( StringUtils.isEmpty( _request.getParameter( paramater ) ) )
            {
                strErrorUrl = buildUrlForMissingField( errMessage );
            }

            return strErrorUrl;
        }

        /**
         * Validates the second step of the task configuration
         *
         * @return the URL of the error page if there is a validation error, {@code null} otherwise
         */
        private String validateSecondStepConfig( )
        {
            String strErrorUrl = validateOneAlertConfigIsActive( );

            if ( !StringUtils.isEmpty( strErrorUrl ) )
            {
                return strErrorUrl;
            }

            strErrorUrl = validateAlertConfigs( );

            if ( !StringUtils.isEmpty( strErrorUrl ) )
            {
                return strErrorUrl;
            }

            return null;
        }

        /**
         * Validates that at least one alert configuration is active
         *
         * @return the URL of the error page if there is a validation error, {@code null} otherwise
         */
        private String validateOneAlertConfigIsActive( )
        {
            String strErrorUrl = null;

            if ( filterActiveAlertConfigs( ).isEmpty( ) )
            {
                strErrorUrl = buildUrlForNoAlertConfig( );
            }

            return strErrorUrl;
        }

        /**
         * Validates the alert configurations
         *
         * @return the URL of the error page if there is a validation error, {@code null} otherwise
         */
        private String validateAlertConfigs( )
        {
            return validateAlertConfigs( buildModelForValidation( ) );
        }

        /**
         * Validates the alert configurations with the specified model
         *
         * @param model
         *            the model
         * @return the URL of the error page if there is a validation error, {@code null} otherwise
         */
        private String validateAlertConfigs( Map<String, Object> model )
        {
            String strErrorUrl = null;

            for ( IAlertConfig alertConfig : filterActiveAlertConfigs( ) )
            {
                strErrorUrl = alertConfig.getValidator( ).validate( model );

                if ( !StringUtils.isEmpty( strErrorUrl ) )
                {
                    break;
                }
            }

            return strErrorUrl;
        }

        /**
         * Filters the active alert configurations
         *
         * @return the list of active alert configurations
         */
        private List<IAlertConfig> filterActiveAlertConfigs( )
        {
            List<IAlertConfig> listActiveAlertConfigs = new ArrayList<>( );

            for ( IAlertConfig alertConfig : _listAlertConfig )
            {
                if ( alertConfig.isActive( ) )
                {
                    listActiveAlertConfigs.add( alertConfig );
                }
            }

            return listActiveAlertConfigs;
        }

        /**
         * Builds the URL for missing field
         *
         * @return the URL
         */
        private String buildUrlForMissingField( String errMessage )
        {
            return AdminMessageService.getMessageUrl( _request, errMessage, AdminMessage.TYPE_STOP );
        }

        /**
         * Builds the URL when there is no alert configuration
         *
         * @return the URL
         */
        private String buildUrlForNoAlertConfig( )
        {
            return AdminMessageService.getMessageUrl( _request, Constants.MESSAGE_MANDATORY_ONGLET, AdminMessage.TYPE_STOP );
        }

        /**
         * Builds the model used to validate the task
         *
         * @return the model
         */
        private Map<String, Object> buildModelForValidation( )
        {
            String strProviderId = ProviderManagerUtil.fetchProviderId( _config.getIdSpringProvider( ) );

            return markersToModel( _providerManager.getProviderDescription( strProviderId ).getMarkerDescriptions( ) );
        }

        /**
         * Converts the specified collection of AlertGru markers into a model
         *
         * @param collectionAlertMarkers
         *            the collection to convert
         * @return the model
         */
        private Map<String, Object> markersToModel( Collection<InfoMarker> collectionAlertMarkers )
        {
            Map<String, Object> model = new HashMap<>( );

            for ( InfoMarker alertMarker : collectionAlertMarkers )
            {
                model.put( alertMarker.getMarker( ), alertMarker.getDescription( ) );
            }

            return model;
        }

        /**
         * Fills the configuration with the global configuration of the task
         */
        private void fillConfigWithGlobalConfig( )
        {
            fillConfigWithProvider( );
            fillConfigWithMarkerProviders( );
            fillConfigWithDemandStatus( );
            fillConfigWithAlertInfos( );
            fillConfigWithMarkerAlert( );
        }

        /**
         * Fills the configuration with the provider
         */
        private void fillConfigWithProvider( )
        {
            _config.setIdSpringProvider( _request.getParameter( Constants.PARAMETER_SELECT_PROVIDER ) );
        }

        /**
         * Fills the configuration with the marker providers
         */
        private void fillConfigWithMarkerProviders( )
        {
            String [ ] listMarkerProviders = _request.getParameterValues( Constants.PARAMETER_MARKER_PROVIDERS );

            if ( listMarkerProviders != null )
            {
                _config.setMarkerProviders( Arrays.asList( listMarkerProviders ) );
            }
            else
            {
                _config.setMarkerProviders( null );
            }
        }

        /**
         * Fills the configuration with the demand status
         */
        private void fillConfigWithDemandStatus( )
        {
            int nDemandStatus = ( VALUE_CHECKBOX.equals( _request.getParameter( Constants.PARAMETER_DEMAND_STATUS ) ) ) ? 1 : 0;

            _config.setDemandStatus( nDemandStatus );
        }

        /**
         * Fills the configuration with alert information
         */
        private void fillConfigWithAlertInfos( )
        {
            _config.setDaysToAlert( StringUtils.isNotBlank( _request.getParameter( Constants.PARAMETER_DAYS_TO_ALERT ) )
                    ? Integer.parseInt( _request.getParameter( Constants.PARAMETER_DAYS_TO_ALERT ) )
                    : 0 );
            _config.setAlertSubject( _request.getParameter( Constants.PARAMETER_SUBJECT_ALERT ) );
            _config.setIdStateAfter( StringUtils.isNotBlank( _request.getParameter( Constants.PARAMETER_STATE_AFTER ) )
                    ? Integer.parseInt( _request.getParameter( Constants.PARAMETER_STATE_AFTER ) )
                    : 0 );
            _config.setAlertAfterBefore( _request.getParameter( Constants.PARAMETER_ALERT_AFTER_BEFORE ) );
        }

        /**
         * Fills the configuration with the marker
         */
        private void fillConfigWithMarkerAlert( )
        {
            _config.setMarkerAlert( _request.getParameter( Constants.PARAMETER_MARKER_ALERT ) );
        }

        /**
         * Fills the configuration with the CRM status
         */
        private void fillConfigWithCrmStatus( )
        {
            String strCrmStatusId = _request.getParameter( Constants.PARAMETER_CRM_STATUS_ID );
            int nCrmStatusId = ( ( StringUtils.equals( strCrmStatusId, "1" ) ) || ( StringUtils.equals( strCrmStatusId, "0" ) ) )
                    ? Integer.parseInt( strCrmStatusId )
                    : 1;

            _config.setCrmStatusId( nCrmStatusId );
        }

        /**
         * Fills the configuration with the current alert configuration
         *
         * @param alertConfig
         *            the current alert configuration
         */
        private void fillConfigWithCurrentAlertConfig( IAlertConfig alertConfig )
        {
            int nCurrentTabIndex = ServiceConfigTaskForm.getTabIndex( alertConfig.getName( ) );
            _config.setSetOnglet( nCurrentTabIndex );
        }

        /**
         * Saves the alert configurations
         */
        private void saveAlertConfigs( )
        {
            for ( IAlertConfig alertConfig : filterActiveAlertConfigs( ) )
            {
                alertConfig.addConfig( );
            }
        }

        /**
         * Saves the configuration of the task
         */
        private void saveConfig( )
        {
            boolean bCreate = false;

            if ( _config.getIdTask( ) == 0 )
            {
                _config.setIdTask( _task.getId( ) );
                bCreate = true;
            }

            if ( bCreate )
            {
                _taskAlertGruConfigService.create( _config );
            }
            else
            {
                _taskAlertGruConfigService.update( _config );
            }
        }
    }

}
