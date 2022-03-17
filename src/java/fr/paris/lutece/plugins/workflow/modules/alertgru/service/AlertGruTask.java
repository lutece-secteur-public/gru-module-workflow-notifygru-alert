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

import fr.paris.lutece.plugins.grubusiness.business.customer.Customer;
import fr.paris.lutece.plugins.grubusiness.business.demand.Demand;
import fr.paris.lutece.plugins.grubusiness.business.notification.BackofficeNotification;
import fr.paris.lutece.plugins.grubusiness.business.notification.BillingAccountBasedSMSNotification;
import fr.paris.lutece.plugins.grubusiness.business.notification.BroadcastNotification;
import fr.paris.lutece.plugins.grubusiness.business.notification.EmailAddress;
import fr.paris.lutece.plugins.grubusiness.business.notification.EmailNotification;
import fr.paris.lutece.plugins.grubusiness.business.notification.MyDashboardNotification;
import fr.paris.lutece.plugins.grubusiness.business.notification.Notification;
import fr.paris.lutece.plugins.grubusiness.business.notification.SMSNotification;
import fr.paris.lutece.plugins.librarynotifygru.exception.NotifyGruException;
import fr.paris.lutece.plugins.librarynotifygru.services.NotificationService;
import fr.paris.lutece.plugins.workflow.modules.alertgru.business.history.AlertGruHistory;
import fr.paris.lutece.plugins.workflow.modules.alertgru.business.AlertGruTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.alertgru.business.UpdateTaskStateResourceQueue;
import fr.paris.lutece.plugins.workflow.modules.alertgru.service.cache.AlertGruCacheService;
import fr.paris.lutece.plugins.workflow.modules.alertgru.utils.constants.Constants;
import fr.paris.lutece.plugins.workflow.service.provider.MarkerProviderService;
import fr.paris.lutece.plugins.workflow.service.provider.ProviderManagerUtil;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.provider.AbstractProviderManager;
import fr.paris.lutece.plugins.workflowcore.service.provider.IMarkerProvider;
import fr.paris.lutece.plugins.workflowcore.service.provider.IProvider;
import fr.paris.lutece.plugins.workflowcore.service.provider.InfoMarker;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;

import fr.paris.lutece.portal.business.mailinglist.Recipient;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

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

    /** Lib-NotifyGru sender service */
    @Inject
    @Named( "workflow-notifygru.lib-notifygru.notificationService" )
    private NotificationService _alertGruSenderService;

    @Inject
    private IResourceHistoryService _resourceHistoryService;

    @Inject
    private ITaskService _taskService;

    @Inject
    private IActionService _actionService;

    @Inject
    private IStateService _stateService;

    @Inject
    private IResourceWorkflowService _resourceWorkflowService;
    private UpdateTaskStateResourceQueueService _updateResourceQueueService;

    @Override
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );

        ResourceWorkflow resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( resourceHistory.getIdResource( ), resourceHistory.getResourceType( ),
                resourceHistory.getWorkflow( ).getId( ) );

            UpdateTaskStateResourceQueue updateResourceQueue = new UpdateTaskStateResourceQueue( );
            updateResourceQueue.setIdResource( resourceHistory.getIdResource( ) );
            updateResourceQueue.setIdResourceHistory( nIdResourceHistory );
            updateResourceQueue.setIdTask( getId( ) );
            updateResourceQueue.setIdExternalParent( resourceWorkflow.getExternalParentId( ) );
            updateResourceQueue.setIdWorkflow( resourceWorkflow.getWorkflow( ).getId( ) );
            updateResourceQueue.setResourceType( resourceHistory.getResourceType( ) );
            updateResourceQueue.setStatus( false );

            _updateResourceQueueService.create( updateResourceQueue );
    }

    /**
     * Builds an {@link Notification} object
     *
     * @param config
     *            the task configuration
     * @param provider
     *            the provider
     * @return the {@link Notification} object
     */
    private static Notification buildNotification( AlertGruTaskConfig config, IProvider provider )
    {
        Notification notification = new Notification( );

        Demand demand = new Demand( );

        demand.setStatusId( config.getDemandStatus( ) );
        demand.setReference( provider.provideDemandReference( ) );
        notification.setDate( System.currentTimeMillis( ) );
        demand.setId( provider.provideDemandId( ) );
        demand.setTypeId( provider.provideDemandTypeId( ) );
        demand.setSubtypeId( provider.provideDemandSubtypeId( ) );
        demand.setMaxSteps( config.getDemandMaxStepGuichet( ) );
        demand.setCurrentStep( config.getDemandUserCurrentStepGuichet( ) );

        Customer customer = new Customer( );
        customer.setId( provider.provideCustomerId( ) );
        customer.setConnectionId( provider.provideCustomerConnectionId( ) );
        customer.setEmail( provider.provideCustomerEmail( ) );
        demand.setCustomer( customer );
        notification.setDemand( demand );

        return notification;
    }

    /**
     * Builds an {@link MyDashboardNotification} object
     *
     * @param config
     *            the task configuration
     * @param model
     *            the model
     * @return the {@link MyDashboardNotification} object
     */
    private MyDashboardNotification buildMyDashboardNotification( AlertGruTaskConfig config, Map<String, Object> model )
    {
        MyDashboardNotification userDashBoard = new MyDashboardNotification( );

        userDashBoard.setStatusId( config.getCrmStatusId( ) );
        userDashBoard.setStatusText( replaceMarkers( config.getStatustextGuichet( ), model ) );
        userDashBoard.setSenderName( config.getSenderNameGuichet( ) );
        userDashBoard.setSubject( replaceMarkers( config.getSubjectGuichet( ), model ) );
        userDashBoard.setMessage( replaceMarkers( config.getMessageGuichet( ), model ) );
        userDashBoard.setData( StringUtils.EMPTY );

        return userDashBoard;
    }

    /**
     * Builds an {@link BackofficeNotification} object
     *
     * @param config
     *            the task configuration
     * @param model
     *            the model
     * @return the {@link BackofficeNotification} object
     */
    private BackofficeNotification buildBackofficeNotification( AlertGruTaskConfig config, Map<String, Object> model )
    {
        BackofficeNotification userAgent = new BackofficeNotification( );

        userAgent.setStatusText( replaceMarkers( config.getStatustextAgent( ), model ) );
        userAgent.setMessage( replaceMarkers( config.getMessageAgent( ), model ) );

        return userAgent;
    }

    /**
     * Builds an {@link SMSNotification} object
     *
     * @param config
     *            the task configuration
     * @param provider
     *            the provider
     * @param model
     *            the model
     * @return the {@link SMSNotification} object
     */
    private <T extends SMSNotification> T buildSMSNotification( AlertGruTaskConfig config, IProvider provider, Map<String, Object> model )
    {
        if ( !config.isBillingAccountBasedSmsNotification( ) )
        {
            SMSNotification userSMS = new BillingAccountBasedSMSNotification( );
            userSMS.setMessage( replaceMarkers( config.getMessageSMS( ), model ) );
            userSMS.setPhoneNumber( provider.provideCustomerMobilePhone( ) );
            userSMS.setSenderName( provider.provideSmsSender( ) );
            return (T) userSMS;
        }
        else
        {
            BillingAccountBasedSMSNotification billingAccountUserSMS = new BillingAccountBasedSMSNotification( );
            billingAccountUserSMS.setMessage( replaceMarkers( config.getMessageSMS( ), model ) );
            billingAccountUserSMS.setPhoneNumber( provider.provideCustomerMobilePhone( ) );
            billingAccountUserSMS.setSenderName( provider.provideSmsSender( ) );
            billingAccountUserSMS.setBillingAccount( config.getBillingAccountSMS( ) );
            billingAccountUserSMS.setBillingGroup( config.getBillingGroupSMS( ) );
            return (T) billingAccountUserSMS;
        }
    }

    /**
     * Builds an {@link EmailNotification} object
     *
     * @param config
     *            the task configuration
     * @param provider
     *            the provider
     * @param model
     *            the model
     * @return the {@link EmailNotification} object
     */
    private EmailNotification buildEmailNotification( AlertGruTaskConfig config, IProvider provider, Map<String, Object> model )
    {
        EmailNotification userEmailNotification = new EmailNotification( );

        userEmailNotification.setSenderName( config.getSenderNameEmail( ) );
        userEmailNotification.setSenderEmail( MailService.getNoReplyEmail( ) );
        userEmailNotification.setRecipient( provider.provideCustomerEmail( ) );
        userEmailNotification.setSubject( replaceMarkers( config.getSubjectEmail( ), model ) );
        userEmailNotification.setMessage( replaceMarkers( config.getMessageEmail( ), model ) );
        userEmailNotification.setCc( config.getRecipientsCcEmail( ) );
        userEmailNotification.setBcc( config.getRecipientsCciEmail( ) );

        return userEmailNotification;
    }

    /**
     * Builds an {@link BroadcastNotification} object
     *
     * @param config
     *            the task configuration
     * @param model
     *            the model
     * @return the {@link BroadcastNotification} object
     */
    private BroadcastNotification buildBroadcastNotification( AlertGruTaskConfig config, Map<String, Object> model )
    {
        BroadcastNotification broadcastNotification = new BroadcastNotification( );

        List<String> listRecipientBroadcast = new ArrayList<>( );

        if ( StringUtils.isNotEmpty( config.getEmailBroadcast( ) ) )
        {
            String strRecipientBroadcast = replaceMarkers( config.getEmailBroadcast( ), model );
            if ( StringUtils.isNotEmpty( strRecipientBroadcast ) )
            {
                listRecipientBroadcast.addAll( Arrays.asList( strRecipientBroadcast.split( Constants.SEMICOLON ) ) );
            }
        }

        if ( config.getIdMailingListBroadcast( ) > 0 )
        {
            Collection<Recipient> listRecipients = AdminMailingListService.getRecipients( config.getIdMailingListBroadcast( ) );

            for ( Recipient recipient : listRecipients )
            {
                listRecipientBroadcast.add( recipient.getEmail( ) );
            }
        }

        String strRecipientCcBroadcast = StringUtils.EMPTY;

        if ( StringUtils.isNotEmpty( config.getRecipientsCcBroadcast( ) ) )
        {
            strRecipientCcBroadcast = replaceMarkers( config.getRecipientsCcBroadcast( ), model );
        }

        broadcastNotification.setSenderName( config.getSenderNameBroadcast( ) );
        broadcastNotification.setSenderEmail( MailService.getNoReplyEmail( ) );
        // we split a StringBuilder we can
        broadcastNotification.setRecipient( EmailAddress.buildEmailAddresses( listRecipientBroadcast.toArray( new String [ ] { } ) ) );
        broadcastNotification.setSubject( replaceMarkers( config.getSubjectBroadcast( ), model ) );
        broadcastNotification.setMessage( replaceMarkers( config.getMessageBroadcast( ), model ) );
        broadcastNotification.setCc( EmailAddress.buildEmailAddresses( strRecipientCcBroadcast.split( Constants.SEMICOLON ) ) );
        broadcastNotification.setBcc( EmailAddress.buildEmailAddresses( config.getRecipientsCciBroadcast( ).split( Constants.SEMICOLON ) ) );

        return broadcastNotification;
    }

    /**
     * Finds the AlertGru markers
     *
     * @param resourceHistory
     *            the resource history
     * @param provider
     *            the provider
     * @param listMarkerProviderIds
     *            the list of marker provider ids
     * @param request
     *            the request
     * @return the AlertGru markers
     */
    private Collection<InfoMarker> findMarkers( ResourceHistory resourceHistory, IProvider provider, List<String> listMarkerProviderIds,
            HttpServletRequest request )
    {
        Collection<InfoMarker> collectionMarkers = provider.provideMarkerValues( );

        for ( String strMarkerProviderId : listMarkerProviderIds )
        {
            IMarkerProvider markerProvider = MarkerProviderService.getInstance( ).find( strMarkerProviderId );

            if ( markerProvider != null )
            {
                collectionMarkers.addAll( markerProvider.provideMarkerValues( resourceHistory, this, request ) );
            }
        }

        return collectionMarkers;
    }

    /**
     * Replaces the markers in the specified message
     *
     * @param strMessage
     *            the message
     * @param model
     *            the model
     * @return the message with markers replaced
     */
    private static String replaceMarkers( String strMessage, Map<String, Object> model )
    {
        for ( Map.Entry<String, Object> entry : model.entrySet( ) ) 
        {
            strMessage = strMessage.replaceAll( "\\$\\{" + entry.getKey( ) + "!{0,1}}", entry.getValue( ) == null ? "" : entry.getValue( ).toString( )  );
        } 
        return strMessage;
    }

    /**
     * Converts the specified collection of AlertGru markers into a model
     *
     * @param collectionAlertGruMarkers
     *            the collection to convert
     * @return the model
     */
    private Map<String, Object> markersToModel( Collection<InfoMarker> collectionAlertGruMarkers )
    {
        Map<String, Object> model = new HashMap<>( );

        for ( InfoMarker alertGruMarker : collectionAlertGruMarkers )
        {
            model.put( alertGruMarker.getMarker( ), alertGruMarker.getValue( ) );
        }

        return model;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask#doRemoveConfig()
     */
    @Override
    public void doRemoveConfig( )
    {
        _taskAlertGruConfigService.remove( this.getId( ) );
        _taskAlertGruHistoryService.removeByTask( this.getId( ), WorkflowUtils.getPlugin( ) );
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

    private ITask findTaskAlert( int actionId )
    {
        ITask task = null;
        List<ITask> listActionTasks = _taskService.getListTaskByIdAction( actionId, Locale.getDefault( ) );
        for ( ITask tsk : listActionTasks )
        {
            if ( tsk.getTaskType( ) != null && tsk.getTaskType( ).getBeanName( ) != null
                    && tsk.getTaskType( ).getBeanName( ).equals( "workflow-alertgru.taskAlertGru" ) )
            {
                task = tsk;
            }
        }
        return task;
    }

    public void sendAlert( int idResource, String resourceType, int actionId, int workflowId )
    {

        ITask task = findTaskAlert( actionId );
        if ( task == null )
        {
            return;
        }

        /* Task Config form cache, it can't be null due to getAlertGruConfigFromCache algorithm */
        AlertGruTaskConfig config = AlertGruCacheService.getInstance( ).getAlertGruConfigFromCache( _taskAlertGruConfigService, task.getId( ) );
        int daysAlert = config.getDaysToAlert( );
        String alertAfterBefore = config.getAlertAfterBefore( );
        String markerAlert = config.getMarkerAlert( );

        if ( daysAlert == 0 )
        {
            AppLogService.info( "Task id " + task.getId( ) + " : days defined to 0." );
            return;
        }

        String strProviderManagerId = ProviderManagerUtil.fetchProviderManagerId( config.getIdSpringProvider( ) );
        String strProviderId = ProviderManagerUtil.fetchProviderId( config.getIdSpringProvider( ) );
        AbstractProviderManager providerManager = ProviderManagerUtil.fetchProviderManager( strProviderManagerId );
        if ( providerManager == null )
        {
            AppLogService.error( "Task id " + task.getId( ) + " : Unable to retrieve the provider manager '" + strProviderManagerId + "'" );
            return;
        }
        ResourceHistory resourceHistory = _resourceHistoryService.getLastHistoryResource( idResource, resourceType, workflowId );
        IProvider provider = providerManager.createProvider( strProviderId, resourceHistory, null );
        if ( provider == null )
        {
            AppLogService.error( "Task id " + task.getId( ) + " : Unable to retrieve the provider '" + config.getIdSpringProvider( ) + "'" );
            return;
        }

        Map<String, Object> model = markersToModel( findMarkers( resourceHistory, provider, config.getMarkerProviders( ), null ) );

        // Day timestamp
        Timestamp timestampNow = new Timestamp( Calendar.getInstance( ).getTimeInMillis( ) );

        // Resource or State timestamp
        Timestamp dateAlert = computeDateAlert( resourceHistory.getCreationDate( ), model, markerAlert, daysAlert, alertAfterBefore, timestampNow );

        if ( dateAlert == null || dateAlert.getTime( ) > timestampNow.getTime( ) )
        {
            return;
        }

        AlertGruHistory alertGruHistory = new AlertGruHistory( );
        alertGruHistory.setIdTask( task.getId( ) );

        Notification notificationObject = buildNotification( config, provider );
        boolean bNotifEmpty = buildNotificationContent( provider, notificationObject, resourceHistory, alertGruHistory, config );

        // crm status id
        alertGruHistory.setCrmStatusId( config.getCrmStatusId( ) );

        if ( !bNotifEmpty )
        {
            doSendAlert( idResource, resourceType, actionId, workflowId, notificationObject, alertGruHistory, config );
        }
    }

    private boolean buildNotificationContent( IProvider provider, Notification notificationObject, ResourceHistory resourceHistory,
            AlertGruHistory alertGruHistory, AlertGruTaskConfig config )
    {
        Map<String, Object> model = markersToModel( findMarkers( resourceHistory, provider, config.getMarkerProviders( ), null ) );

        boolean bNotifEmpty = true;
        if ( config.isActiveOngletEmail( ) && StringUtils.isNotBlank( provider.provideCustomerEmail( ) ) )
        {
            EmailNotification emailNotification = buildEmailNotification( config, provider, model );
            notificationObject.setEmailNotification( emailNotification );
            alertGruHistory.setEmail( NotificationToHistory.populateEmail( config, emailNotification ) );
            bNotifEmpty = false;
        }

        if ( config.isActiveOngletGuichet( ) && StringUtils.isNotBlank( provider.provideCustomerConnectionId( ) ) )
        {
            MyDashboardNotification myDashBoardNotification = buildMyDashboardNotification( config, model );
            notificationObject.setMyDashboardNotification( myDashBoardNotification );
            alertGruHistory.setGuichet( NotificationToHistory.populateGuichet( config, myDashBoardNotification ) );
            bNotifEmpty = false;
        }

        if ( config.isActiveOngletSMS( ) && StringUtils.isNotBlank( provider.provideCustomerMobilePhone( ) ) )
        {
            SMSNotification smsNotification = buildSMSNotification( config, provider, model );
            notificationObject.setSmsNotification( smsNotification );
            alertGruHistory.setSMS( NotificationToHistory.populateSMS( config, smsNotification ) );
            bNotifEmpty = false;
        }

        if ( config.isActiveOngletAgent( ) )
        {
            BackofficeNotification backofficeNotification = buildBackofficeNotification( config, model );
            notificationObject.setBackofficeNotification( backofficeNotification );
            alertGruHistory.setAgent( NotificationToHistory.populateAgent( config, backofficeNotification ) );
            bNotifEmpty = false;
        }

        if ( config.isActiveOngletBroadcast( ) )
        {
            BroadcastNotification broadcastNotification = buildBroadcastNotification( config, model );
            if ( !broadcastNotification.getRecipient( ).isEmpty( ) )
            {
                notificationObject.addBroadcastEmail( broadcastNotification );
                alertGruHistory.setBroadCast( NotificationToHistory.populateBroadcast( config, broadcastNotification ) );
                bNotifEmpty = false;
            }
        }
        return bNotifEmpty;
    }

    private void doSendAlert( int idResource, String resourceType, int actionId, int workflowId, Notification notificationObject,
            AlertGruHistory alertGruHistory, AlertGruTaskConfig config )
    {
        try
        {
            _alertGruSenderService.send( notificationObject );

            // Create Resource History
            ResourceHistory resourceHistoryState = new ResourceHistory( );
            resourceHistoryState.setIdResource( idResource );
            resourceHistoryState.setResourceType( resourceType );
            resourceHistoryState.setAction( _actionService.findByPrimaryKey( actionId ) );
            resourceHistoryState.setWorkFlow( _actionService.findByPrimaryKey( actionId ).getWorkflow( ) );
            resourceHistoryState.setCreationDate( WorkflowUtils.getCurrentTimestamp( ) );
            resourceHistoryState.setUserAccessCode( Constants.USER_AUTO );
            _resourceHistoryService.create( resourceHistoryState );

            // Create Alert gru History
            alertGruHistory.setIdResourceHistory( resourceHistoryState.getId( ) );
            _taskAlertGruHistoryService.create( alertGruHistory, WorkflowUtils.getPlugin( ) );

            // Update Resource
            ResourceWorkflow resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( idResource, resourceType, workflowId );
            State state = _stateService.findByPrimaryKey( config.getIdStateAfter( ) );
            resourceWorkflow.setState( state );
            _resourceWorkflowService.update( resourceWorkflow );
            Locale locale = I18nService.getDefaultLocale( );
            WorkflowService.getInstance( ).doProcessAutomaticReflexiveActions( idResource, resourceType, config.getIdStateAfter( ), null, locale, null );

        }
        catch( AppException | NotifyGruException e )
        {
            AppLogService.error( "Unable to send the notification" );
        }
    }

    /**
     * Calculate date of alert
     *
     * @param dateResource
     *            the date of resource
     * @param dateMarker
     *            the provider
     * @param markerAlert
     *            the provider
     * @param daysAlert
     *            the model
     * @param alertAfterBefore
     *            the direction : after or before
     * @return the Timestamp date
     */
    private Timestamp computeDateAlert( Timestamp dateResource, Map<String, Object> modelMarker, String markerAlert, int daysAlert, String alertAfterBefore,
            Timestamp timestampNow )
    {

        Timestamp dateInit;
        if ( markerAlert.equals( Constants.MARK_DEFAULT_MARKER ) )
        {
            dateInit = dateResource;
        }
        else
        {
            SimpleDateFormat dateFormatTmp = new SimpleDateFormat( Constants.PROPERTIE_DATE_FORMAT );
            Date dateTmp = null;
            try
            {

                dateTmp = dateFormatTmp.parse( String.valueOf( modelMarker.get( markerAlert ) ) );

            }
            catch( ParseException e )
            {

                AppLogService.error( "Marker " + markerAlert + " : Unable to parse date." );
                return null;
            }
            dateInit = new Timestamp( dateTmp.getTime( ) );
        }
        // Resource date + day configured in admin
        Calendar cal = Calendar.getInstance( );
        cal.setTime( dateInit );
        if ( Constants.MARK_ALERT_AFTER.equals( alertAfterBefore ) )
        {

            cal.add( Calendar.DAY_OF_WEEK, daysAlert );

        }
        else
            if ( dateInit.after( timestampNow ) )
            {

                cal.add( Calendar.DAY_OF_WEEK, daysAlert * -1 );

            }
            else
            {

                return null;
            }

        return new Timestamp( cal.getTime( ).getTime( ) );
    }
}
