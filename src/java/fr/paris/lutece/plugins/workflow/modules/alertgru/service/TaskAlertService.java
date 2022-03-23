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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
import fr.paris.lutece.plugins.librarynotifygru.services.NotificationService;
import fr.paris.lutece.plugins.workflow.modules.alertgru.business.AlertGruTaskConfig;
import fr.paris.lutece.plugins.workflow.modules.alertgru.business.UpdateTaskStateResourceQueue;
import fr.paris.lutece.plugins.workflow.modules.alertgru.business.UpdateTaskStateResourceQueueHome;
import fr.paris.lutece.plugins.workflow.modules.alertgru.business.history.AlertGruHistory;
import fr.paris.lutece.plugins.workflow.modules.alertgru.service.cache.AlertGruCacheService;
import fr.paris.lutece.plugins.workflow.modules.alertgru.utils.constants.Constants;
import fr.paris.lutece.plugins.workflow.service.provider.MarkerProviderService;
import fr.paris.lutece.plugins.workflow.service.provider.ProviderManagerUtil;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.resource.IResourceHistoryDAO;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceWorkflow;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.provider.AbstractProviderManager;
import fr.paris.lutece.plugins.workflowcore.service.provider.IMarkerProvider;
import fr.paris.lutece.plugins.workflowcore.service.provider.IProvider;
import fr.paris.lutece.plugins.workflowcore.service.provider.InfoMarker;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.resource.ResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.ResourceWorkflowService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.task.TaskService;
import fr.paris.lutece.portal.business.event.ResourceEvent;
import fr.paris.lutece.portal.business.mailinglist.Recipient;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.util.sql.TransactionManager;

public enum TaskAlertService
{

    INSNACE;
    private static final int TAILLE_LOT = AppPropertiesService.getPropertyInt( "workflow-notifygru.selectSize", 100 );
    /** The _task alert gru config service. */
    // SERVICES
    private ITaskConfigService _taskAlertGruConfigService;

    private IResourceWorkflowService _resourceWorkflowService;

    /** The _task notify gru history service. */
    private IAlertGruHistoryService _taskAlertGruHistoryService;

    /** Lib-NotifyGru sender service */
    private NotificationService _alertGruSenderService;

    /** The task service **/
    private ITaskService _taskService;

    /** The resource workflow history service **/
    private IResourceHistoryService _resourceHistoryService;

    /** resource history dao implementation **/
    private IResourceHistoryDAO _resourceHistoryDAO;

    /**
     * Constructor
     */
    private TaskAlertService( )
    {

        initService( );
    }

    /**
     * Send gru alert service
     */
    public void sendAlert( )
    {
        List<Integer> listIdResourceQueues = UpdateTaskStateResourceQueueHome.findAllIdQueueActived( );

        IntStream.range( 0, ( listIdResourceQueues.size( ) + TAILLE_LOT - 1 ) / TAILLE_LOT )
                .mapToObj( i -> listIdResourceQueues.subList( i * TAILLE_LOT, Math.min( listIdResourceQueues.size( ), ( i + 1 ) * TAILLE_LOT ) ) )
                .forEach( batch -> {
                    try
                    {
                        sendAlert( listIdResourceQueues );

                    }
                    catch( Exception e )
                    {
                        AppLogService.error( e.getMessage( ), e );
                    }
                } );

    }

    /**
     * Get the alert reference date
     * 
     * @param nIdTask
     *            the id task
     * @param resourceHistory
     *            the resource history
     * @param request
     *            the request
     * @return Timestamp alert reference
     */
    public Optional<Timestamp> getReferenceDateAlert( int nIdTask, ResourceHistory resourceHistory, HttpServletRequest request )
    {

        /* Task Config form cache, it can't be null due to getNotifyGruConfigFromCache algorithm */
        AlertGruTaskConfig config = AlertGruCacheService.getInstance( ).getAlertGruConfigFromCache( _taskAlertGruConfigService, nIdTask );
        if ( config.getMarkerAlert( ).equals( Constants.MARK_DEFAULT_MARKER ) )
        {
            return Optional.ofNullable( resourceHistory.getCreationDate( ) );
        }
        String strProviderManagerId = ProviderManagerUtil.fetchProviderManagerId( config.getIdSpringProvider( ) );
        String strProviderId = ProviderManagerUtil.fetchProviderId( config.getIdSpringProvider( ) );
        AbstractProviderManager providerManager = ProviderManagerUtil.fetchProviderManager( strProviderManagerId );

        if ( providerManager == null )
        {
            AppLogService.error( "Task id {}  : Unable to retrieve the provider manager {} ", nIdTask, strProviderManagerId );
            return Optional.empty( );
        }

        IProvider provider = providerManager.createProvider( strProviderId, resourceHistory, request );

        if ( provider == null )
        {
            AppLogService.error( "Task id {} : Unable to retrieve the provider", nIdTask, config.getIdSpringProvider( ) );
            return Optional.empty( );
        }
        Collection<InfoMarker> infoMarker = provider.provideMarkerValues( );

        return getReferenceDateAlert( resourceHistory.getCreationDate( ), infoMarker, config.getMarkerAlert( ) );

    }

    /**
     * Update resource queue when an event is triggered
     * 
     * @param event
     *            the resource event (event of the Listener)
     */
    public void updateResourceQueue( ResourceEvent event )
    {

        if ( StringUtils.isNumeric( event.getIdResource( ) ) )
        {
            UpdateTaskStateResourceQueue resourceInQueue = UpdateTaskStateResourceQueueHome
                    .find( Integer.parseInt( event.getIdResource( ) ), event.getTypeResource( ) ).orElse( null );
            if ( resourceInQueue != null )
            {
                ResourceWorkflow resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( resourceInQueue.getIdResource( ),
                        resourceInQueue.getResourceType( ), resourceInQueue.getIdWorkflow( ) );
                if ( resourceWorkflow != null && resourceInQueue.getIdState( ) == resourceWorkflow.getState( ).getId( ) )
                {
                    updateResourceQueue( resourceInQueue, resourceWorkflow );
                }
                else
                {
                    UpdateTaskStateResourceQueueHome.removeByPrimaryKey( resourceInQueue.getIdResourceQueue( ) );
                }

            }
        }
    }

    /**
     * update resource if event is triggered
     */
    private void updateResourceQueue( UpdateTaskStateResourceQueue resourceInQueue, ResourceWorkflow resourceWorkflow )
    {

        List<ResourceHistory> listResourceHistoryWorkflow = _resourceHistoryDAO.selectByResource( resourceInQueue.getIdResource( ),
                resourceInQueue.getResourceType( ), resourceInQueue.getIdWorkflow( ) );
        if ( CollectionUtils.isNotEmpty( listResourceHistoryWorkflow ) )
        {
            ResourceHistory lastResourceWorkflow = listResourceHistoryWorkflow.get( 0 );
            if ( lastResourceWorkflow.getId( ) != resourceInQueue.getIdResourceHistory( )
                    && resourceInQueue.getIdState( ) == resourceWorkflow.getState( ).getId( ) )
            {
                Optional<Timestamp> referenceDate = TaskAlertService.INSNACE.getReferenceDateAlert( resourceInQueue.getIdTask( ), lastResourceWorkflow, null );
                if ( referenceDate.isPresent( ) )
                {
                    resourceInQueue.setAlertReferenceDate( referenceDate.get( ) );
                    UpdateTaskStateResourceQueueHome.update( resourceInQueue );

                }
                else
                {
                    UpdateTaskStateResourceQueueHome.removeByPrimaryKey( resourceInQueue.getIdResourceQueue( ) );
                }
            }
        }
    }

    private void sendAlert( List<Integer> listIdResourceQueues )
    {

        UpdateTaskStateResourceQueueHome.findByPrimaryKeyList( listIdResourceQueues ).stream( )
                .collect( Collectors.groupingBy( UpdateTaskStateResourceQueue::getIdTask ) ).forEach( ( key, value ) -> {
                    try
                    {
                        sendAlert( _taskService.findByPrimaryKey( key, Locale.getDefault( ) ),
                                AlertGruCacheService.getInstance( ).getAlertGruConfigFromCache( _taskAlertGruConfigService, key ), value );
                    }
                    catch( Exception e )
                    {
                        AppLogService.error( e.getMessage( ), e );
                    }
                } );

    }

    /**
     * Send gru alert
     */
    private void sendAlert( ITask task, AlertGruTaskConfig config, List<UpdateTaskStateResourceQueue> listResourceQueues )
    {

        if ( task == null )
        {

            UpdateTaskStateResourceQueueHome.removeByIdTask( listResourceQueues.get( 0 ).getIdTask( ) );
            return;
        }
        if ( config.getDaysToAlert( ) == 0 )
        {
            AppLogService.error( "Task id {} : days defined to 0.", config.getIdTask( ) );
            return;
        }

        AbstractProviderManager providerManager = ProviderManagerUtil
                .fetchProviderManager( ProviderManagerUtil.fetchProviderManagerId( config.getIdSpringProvider( ) ) );
        if ( providerManager == null )
        {
            AppLogService.error( "Task id {} : Unable to retrieve the provider manager {} ", config.getIdSpringProvider( ) );
            return;
        }

        LocalDateTime now = LocalDateTime.now( );
        String strIdProv = ProviderManagerUtil.fetchProviderId( config.getIdSpringProvider( ) );
        listResourceQueues.forEach( resource -> {
            try
            {

                doSendAlert( task, config, resource, now, providerManager, strIdProv );

            }
            catch( Exception e )
            {
                AppLogService.error( e.getMessage( ), e );
            }
        } );
    }

    /**
     * Send gru alert
     */
    private void doSendAlert( ITask task, AlertGruTaskConfig config, UpdateTaskStateResourceQueue updateTaskStateResourceQueue, LocalDateTime now,
            AbstractProviderManager providerManager, String strProviderId )
    {

        if ( !isAlertDay( updateTaskStateResourceQueue.getAlertReferenceDate( ), config.getDaysToAlert( ), config.getAlertAfterBefore( ), now,
                updateTaskStateResourceQueue.getIdResourceQueue( ) ) )
        {
            return;
        }
        ResourceHistory resourceHistory = buildResourceHistory( updateTaskStateResourceQueue, task );

        IProvider provider = providerManager.createProvider( strProviderId, resourceHistory, null );
        if ( provider == null )
        {
            AppLogService.error( "Task id {} : Unable to retrieve the provider manager {} ", updateTaskStateResourceQueue.getIdTask( ),
                    config.getIdSpringProvider( ) );
            return;
        }

        AlertGruHistory alertGruHistory = new AlertGruHistory( );
        alertGruHistory.setIdTask( updateTaskStateResourceQueue.getIdTask( ) );

        Notification notificationObject = buildNotification( config, provider );
        boolean bNotifEmpty = buildNotificationContent( task, provider, notificationObject, resourceHistory, alertGruHistory, config );

        // crm status id
        alertGruHistory.setCrmStatusId( config.getCrmStatusId( ) );

        if ( !bNotifEmpty )
        {
            doSendAlert( resourceHistory, notificationObject, alertGruHistory, config, updateTaskStateResourceQueue );
        }
    }

    /**
     * Send gru alery
     */
    private void doSendAlert( ResourceHistory resourceHistoryState, Notification notificationObject, AlertGruHistory alertGruHistory, AlertGruTaskConfig config,
            UpdateTaskStateResourceQueue updateTaskStateResourceQueue )
    {
        TransactionManager.beginTransaction( null );
        try
        {

            _alertGruSenderService.send( notificationObject );

            // Create Resource History
            resourceHistoryState.setId( 0 );
            resourceHistoryState.setCreationDate( WorkflowUtils.getCurrentTimestamp( ) );
            resourceHistoryState.setUserAccessCode( Constants.USER_AUTO );
            _resourceHistoryService.create( resourceHistoryState );

            // Create Alert gru History+
            alertGruHistory.setIdResourceHistory( resourceHistoryState.getId( ) );
            _taskAlertGruHistoryService.create( alertGruHistory, WorkflowUtils.getPlugin( ) );

            // Update Resource
            ResourceWorkflow resourceWorkflow = _resourceWorkflowService.findByPrimaryKey( resourceHistoryState.getIdResource( ),
                    resourceHistoryState.getResourceType( ), resourceHistoryState.getWorkflow( ).getId( ) );
            State state = new State( );
            state.setId( config.getIdStateAfter( ) );
            resourceWorkflow.setState( state );
            _resourceWorkflowService.update( resourceWorkflow );

            WorkflowService.getInstance( ).doProcessAutomaticReflexiveActions( resourceHistoryState.getIdResource( ), resourceHistoryState.getResourceType( ),
                    config.getIdStateAfter( ), updateTaskStateResourceQueue.getIdExternalParent( ), I18nService.getDefaultLocale( ), null );

            UpdateTaskStateResourceQueueHome.removeByPrimaryKey( updateTaskStateResourceQueue.getIdResourceQueue( ) );
            TransactionManager.commitTransaction( null );

        }
        catch( Exception e )
        {
            TransactionManager.rollBack( null );
            AppLogService.error( "Unable to send the notification", e );
        }
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
    private Collection<InfoMarker> findMarkers( ITask task, ResourceHistory resourceHistory, IProvider provider, List<String> listMarkerProviderIds,
            HttpServletRequest request )
    {
        Collection<InfoMarker> collectionMarkers = provider.provideMarkerValues( );

        for ( String strMarkerProviderId : listMarkerProviderIds )
        {
            IMarkerProvider markerProvider = MarkerProviderService.getInstance( ).find( strMarkerProviderId );

            if ( markerProvider != null )
            {
                collectionMarkers.addAll( markerProvider.provideMarkerValues( resourceHistory, task, request ) );
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
            strMessage = strMessage.replaceAll( "\\$\\{" + entry.getKey( ) + "!{0,1}}", entry.getValue( ) == null ? "" : entry.getValue( ).toString( ) );
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

    /**
     * Build notification content
     * 
     * @return true if the notification is builded
     */
    private boolean buildNotificationContent( ITask task, IProvider provider, Notification notificationObject, ResourceHistory resourceHistory,
            AlertGruHistory alertGruHistory, AlertGruTaskConfig config )
    {
        Map<String, Object> model = markersToModel( findMarkers( task, resourceHistory, provider, config.getMarkerProviders( ), null ) );

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
    private boolean isAlertDay( Timestamp alertReferenceDate, int daysToAlert, String alertAfterBefore, LocalDateTime now, int nIdResourceQueues )
    {
        LocalDateTime refDate = alertReferenceDate.toLocalDateTime( );
        if ( Constants.MARK_ALERT_AFTER.equals( alertAfterBefore ) )
        {
            Duration duration = Duration.between( refDate, now );

            if ( !duration.minusDays( daysToAlert ).isNegative( ) || duration.minusDays( daysToAlert ).isZero( ) )
            {

                return true;
            }
        }
        else
        {
            Duration duration = Duration.between( now, refDate );
            if ( duration.isNegative( ) )
            {
                UpdateTaskStateResourceQueueHome.removeByPrimaryKey( nIdResourceQueues );
            }
            else
                if ( duration.minusDays( daysToAlert ).isNegative( ) || duration.minusDays( daysToAlert ).isZero( ) )
                {

                    return true;
                }
        }
        return false;
    }

    /**
     * Build ResourceHistory
     * 
     * @return ResourceHistory builded
     */
    private ResourceHistory buildResourceHistory( UpdateTaskStateResourceQueue resource, ITask task )
    {
        ResourceHistory resourceHistory = new ResourceHistory( );
        resourceHistory.setId( resource.getIdResourceHistory( ) );
        resourceHistory.setIdResource( resource.getIdResource( ) );
        resourceHistory.setResourceType( resource.getResourceType( ) );

        Workflow workflow = new Workflow( );
        workflow.setId( resource.getIdWorkflow( ) );
        resourceHistory.setWorkFlow( workflow );
        resourceHistory.setAction( task.getAction( ) );
        resourceHistory.setCreationDate( resource.getCreationDate( ) );
        resourceHistory.setUserAccessCode( Constants.USER_AUTO );

        return resourceHistory;
    }

    /**
     * init TaskAlertService
     */
    private void initService( )
    {

        if ( _taskAlertGruConfigService == null )
        {
            _taskAlertGruConfigService = SpringContextService.getBean( AlertGruTaskConfigService.BEAN_SERVICE );
        }
        if ( _resourceWorkflowService == null )
        {
            _resourceWorkflowService = SpringContextService.getBean( ResourceWorkflowService.BEAN_SERVICE );
        }
        if ( _taskAlertGruHistoryService == null )
        {
            _taskAlertGruHistoryService = SpringContextService.getBean( "workflow-alertgru.alertGruHistoryService" );
        }
        if ( _alertGruSenderService == null )
        {
            _alertGruSenderService = SpringContextService.getBean( "workflow-notifygru.lib-notifygru.notificationService" );
        }
        if ( _taskService == null )
        {
            _taskService = SpringContextService.getBean( TaskService.BEAN_SERVICE );
        }
        if ( _resourceHistoryService == null )
        {
            _resourceHistoryService = SpringContextService.getBean( ResourceHistoryService.BEAN_SERVICE );
        }
        if ( _resourceHistoryDAO == null )
        {

            _resourceHistoryDAO = SpringContextService.getBean( "workflow.resourceHistoryDAO" );
        }
    }

    /**
     * calculate and return the alert refernce date
     */
    private Optional<Timestamp> getReferenceDateAlert( Timestamp dateResource, Collection<InfoMarker> infoMarker, final String markerAlert )
    {
        if ( markerAlert.equals( Constants.MARK_DEFAULT_MARKER ) )
        {
            return Optional.ofNullable( dateResource );
        }
        else
        {
            try
            {
                Optional<InfoMarker> alertDate = infoMarker.stream( ).filter( m -> m.getMarker( ).equals( markerAlert ) ).findAny( );
                if ( alertDate.isPresent( ) )
                {
                    return Optional.ofNullable( new Timestamp(
                            ( new SimpleDateFormat( Constants.PROPERTIE_DATE_FORMAT ) ).parse( String.valueOf( alertDate.get( ).getValue( ) ) ).getTime( ) ) );
                }
            }
            catch( ParseException e )
            {
                AppLogService.error( "Marker {} : Unable to parse date. ", markerAlert, e );
            }
        }

        return Optional.empty( );
    }
}
