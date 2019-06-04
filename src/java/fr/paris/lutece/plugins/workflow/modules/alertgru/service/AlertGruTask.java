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
import fr.paris.lutece.plugins.workflow.modules.alertgru.service.cache.AlertGruCacheService;
import fr.paris.lutece.plugins.workflow.modules.alertgru.utils.constants.Constants;
import fr.paris.lutece.plugins.workflow.service.provider.MarkerProviderService;
import fr.paris.lutece.plugins.workflow.service.provider.ProviderManagerUtil;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
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
import fr.paris.lutece.plugins.workflowcore.service.workflow.IWorkflowService;
import fr.paris.lutece.portal.business.mailinglist.Recipient;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.html.HtmlTemplate;
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
import org.apache.commons.lang.StringUtils;

public class AlertGruTask extends SimpleTask {
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
    IWorkflowService _workflowService;


    @Inject
    private IResourceWorkflowService _resourceWorkflowService;



    @Override
    public void processTask(int nIdResourceHistory, HttpServletRequest request, Locale locale) {


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
    private MyDashboardNotification buildMyDashboardNotification(AlertGruTaskConfig config, Map<String, Object> model )
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
    private BackofficeNotification buildBackofficeNotification(AlertGruTaskConfig config, Map<String, Object> model )
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
    private <T extends SMSNotification> T buildSMSNotification(AlertGruTaskConfig config, IProvider provider, Map<String, Object> model )
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
    private BroadcastNotification buildBroadcastNotification(AlertGruTaskConfig config, Map<String, Object> model )
    {
        BroadcastNotification broadcastNotification = new BroadcastNotification( );

        List<String> listRecipientBroadcast = new ArrayList<String>( );

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
    private Collection<InfoMarker> findMarkers(ResourceHistory resourceHistory, IProvider provider, List<String> listMarkerProviderIds,
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
        @SuppressWarnings( "deprecation" )
        HtmlTemplate template = AppTemplateService.getTemplateFromStringFtl( strMessage, Locale.FRENCH, model );

        return template.getHtml( );
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
    public String getTitle(Locale locale) {
        return "Alert notification GRU";
    }




    public void sendAlert(int idResource, String resourceType,  int actionId, int workflowId) {

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

        if ( task != null ) {
            /* Task Config form cache, it can't be null due to getAlertGruConfigFromCache algorithm */
            AlertGruTaskConfig config = AlertGruCacheService.getInstance().getAlertGruConfigFromCache(_taskAlertGruConfigService, task.getId());
            String strProviderManagerId = ProviderManagerUtil.fetchProviderManagerId(config.getIdSpringProvider());
            String strProviderId = ProviderManagerUtil.fetchProviderId(config.getIdSpringProvider());
            AbstractProviderManager providerManager = ProviderManagerUtil.fetchProviderManager(strProviderManagerId);

            int daysAlert = config.getDaysToAlert();
            String  alertAfterBefore = config.getAlertAfterBefore();
            String markerAlert = config.getMarkerAlert();

            if (daysAlert != 0) {
                if (providerManager != null) {
                    ResourceHistory resourceHistory = _resourceHistoryService.getLastHistoryResource(idResource, resourceType, workflowId);
                    int nIdResourceHistory = resourceHistory.getId();
                    IProvider provider = providerManager.createProvider(strProviderId, resourceHistory, null);

                    if (provider != null) {
                    Map<String, Object> model = markersToModel(findMarkers(resourceHistory, provider, config.getMarkerProviders(), null));

                    //Day timestamp
                    Timestamp timestampNow = new Timestamp(Calendar.getInstance().getTimeInMillis());

                    //Resource or State timestamp
                    Timestamp dateAlert = computeDateAlert(resourceHistory.getCreationDate(),model, markerAlert,  daysAlert, alertAfterBefore);
                    if (dateAlert.getTime() <= timestampNow.getTime()) {
                            AlertGruHistory alertGruHistory = new AlertGruHistory();
                            alertGruHistory.setIdTask(task.getId());
                            alertGruHistory.setIdResourceHistory(nIdResourceHistory);

                            Notification notificationObject = buildNotification(config, provider);
                            EmailNotification emailNotification = null;
                            boolean bNotifEmpty = true;
                            if (config.isActiveOngletEmail() && StringUtils.isNotBlank(provider.provideCustomerEmail())) {
                                emailNotification = buildEmailNotification(config, provider, model);
                                notificationObject.setEmailNotification(emailNotification);
                                alertGruHistory.setEmail(NotificationToHistory.populateEmail(config, emailNotification));
                                bNotifEmpty = false;
                            }

                            MyDashboardNotification myDashBoardNotification = null;

                            if (config.isActiveOngletGuichet() && StringUtils.isNotBlank(provider.provideCustomerConnectionId())) {
                                myDashBoardNotification = buildMyDashboardNotification(config, model);
                                notificationObject.setMyDashboardNotification(myDashBoardNotification);
                                alertGruHistory.setGuichet(NotificationToHistory.populateGuichet(config, myDashBoardNotification));
                                bNotifEmpty = false;
                            }

                            SMSNotification smsNotification = null;

                            if (config.isActiveOngletSMS() && StringUtils.isNotBlank(provider.provideCustomerMobilePhone())) {
                                smsNotification = buildSMSNotification(config, provider, model);
                                notificationObject.setSmsNotification(smsNotification);
                                alertGruHistory.setSMS(NotificationToHistory.populateSMS(config, smsNotification));
                                bNotifEmpty = false;
                            }

                            BackofficeNotification backofficeNotification = null;

                            if (config.isActiveOngletAgent()) {
                                backofficeNotification = buildBackofficeNotification(config, model);
                                notificationObject.setBackofficeNotification(backofficeNotification);
                                alertGruHistory.setAgent(NotificationToHistory.populateAgent(config, backofficeNotification));
                                bNotifEmpty = false;
                            }

                            BroadcastNotification broadcastNotification = null;

                            if (config.isActiveOngletBroadcast()) {
                                broadcastNotification = buildBroadcastNotification(config, model);

                                if (!broadcastNotification.getRecipient().isEmpty()) {
                                    notificationObject.addBroadcastEmail(broadcastNotification);
                                    alertGruHistory.setBroadCast(NotificationToHistory.populateBroadcast(config, broadcastNotification));
                                    bNotifEmpty = false;
                                }
                            }

                            // crm status id
                            alertGruHistory.setCrmStatusId(config.getCrmStatusId());

                            if (!bNotifEmpty) {
                                try {
                                    _alertGruSenderService.send(notificationObject);
                                    _taskAlertGruHistoryService.create(alertGruHistory, WorkflowUtils.getPlugin());

                                    // Create Resource History
                                    ResourceHistory resourceHistoryState = new ResourceHistory();
                                    resourceHistoryState.setIdResource(idResource);
                                    resourceHistoryState.setResourceType(resourceType);
                                    resourceHistoryState.setAction(_actionService.findByPrimaryKey(actionId));
                                    resourceHistoryState.setWorkFlow(_actionService.findByPrimaryKey(actionId).getWorkflow());
                                    resourceHistoryState.setCreationDate(WorkflowUtils.getCurrentTimestamp());
                                    resourceHistoryState.setUserAccessCode(Constants.USER_AUTO);
                                    _resourceHistoryService.create(resourceHistoryState);
                                    // Update Resource
                                    ResourceWorkflow resourceWorkflow = _resourceWorkflowService.findByPrimaryKey(idResource,
                                            resourceType, workflowId);
                                    State state = _stateService.findByPrimaryKey(config.getIdStateAfter());
                                    resourceWorkflow.setState(state);
                                    _resourceWorkflowService.update(resourceWorkflow);
                                    Locale locale = I18nService.getDefaultLocale();
                                    _workflowService.doProcessAutomaticReflexiveActions(idResource, resourceType, config.getIdStateAfter(), null, locale);
                                } catch (AppException | NotifyGruException e) {
                                    AppLogService.error("Unable to send the notification");
                                }
                            }
                        } else {
                            AppLogService.error("Task id " + task.getId() + " : Unable to retrieve the provider '" + config.getIdSpringProvider() + "'");
                        }
                    }

                } else {
                    AppLogService.error("Task id " + task.getId() + " : Unable to retrieve the provider manager '" + strProviderManagerId + "'");
                }
            } else {
                AppLogService.info("Task id " + task.getId() + " : days defined to 0.");
            }
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
    private Timestamp computeDateAlert(Timestamp dateResource, Map<String,Object> modelMarker,String markerAlert, int daysAlert, String alertAfterBefore ){
        Timestamp dateInit;
        if(markerAlert.equals(Constants.MARK_DEFAULT_MARKER)) {
            //dateResource = resourceHistory.getCreationDate();
            dateInit = dateResource;
        } else {
            SimpleDateFormat dateFormat_tmp = new SimpleDateFormat(
                    "dd/MM/yyyy");
            Date date_tmp = null;
            try {
                date_tmp = dateFormat_tmp.parse(String.valueOf(modelMarker.get(markerAlert)));
            } catch (ParseException e) {
                AppLogService.info("Marker " + markerAlert + " : Unable to parse date.");
            }
            dateInit =  new Timestamp(date_tmp.getTime());
        }
        //Date de la ressource + jour configuré en admin
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateInit);
        if(Constants.MARK_ALERT_AFTER.equals(alertAfterBefore)){
            cal.add(Calendar.DAY_OF_WEEK, daysAlert);
        } else {
            cal.roll(Calendar.DAY_OF_WEEK, daysAlert);
        }
        return new Timestamp(cal.getTime().getTime());
    }
}
