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
package fr.paris.lutece.plugins.workflow.modules.alertgru.business;

import fr.paris.lutece.plugins.workflow.modules.alertgru.service.AlertGruPlugin;
import fr.paris.lutece.plugins.workflow.modules.alertgru.service.cache.AlertGruCacheService;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.util.sql.DAOUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class AlertGruTaskConfigDAO implements ITaskConfigDAO<AlertGruTaskConfig>
{

    private static final String SQL_QUERY_INSERT_NOTIF = "INSERT INTO workflow_task_alert_gru_cf( "
            + "id_task,id_spring_provider,marker_provider_ids,demand_status,crm_status_id,set_onglet,message_guichet,status_text_guichet,sender_name_guichet,"
            + "subject_guichet,demand_max_step_guichet,demand_user_current_step_guichet,is_active_onglet_guichet,"
            + "status_text_agent,message_agent,is_active_onglet_agent,subject_email, message_email,"
            + "sender_name_email,recipients_cc_email,recipients_cci_email,is_active_onglet_email,"
            + "message_sms,billing_account_sms,billing_group_sms,is_active_onglet_sms,"
            + "id_mailing_list_broadcast,email_broadcast,sender_name_broadcast,subject_broadcast,message_broadcast,"
            + "recipients_cc_broadcast,recipients_cci_broadcast,"
            + "is_active_onglet_broadcast ,  days_to_alert, id_state_after, alert_subject, marker_alert, alert_after_before) "
            + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_task, id_spring_provider,marker_provider_ids,demand_status,crm_status_id, set_onglet,"
            + "message_guichet,status_text_guichet,sender_name_guichet,"
            + "subject_guichet,demand_max_step_guichet,demand_user_current_step_guichet,is_active_onglet_guichet,"
            + "status_text_agent,message_agent,is_active_onglet_agent," + "subject_email,message_email,sender_name_email,recipients_cc_email,"
            + "recipients_cci_email,is_active_onglet_email,message_sms,billing_account_sms,billing_group_sms,is_active_onglet_sms,"
            + "id_mailing_list_broadcast,email_broadcast,sender_name_broadcast,subject_broadcast,message_broadcast,"
            + "recipients_cc_broadcast,recipients_cci_broadcast,is_active_onglet_broadcast,  days_to_alert, id_state_after, alert_subject, marker_alert, alert_after_before "
            + "FROM workflow_task_alert_gru_cf notif WHERE id_task = ?";

    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task_alert_gru_cf "
            + " SET id_task = ?, id_spring_provider = ?, marker_provider_ids = ?, demand_status = ?,crm_status_id = ?, set_onglet = ?,"
            + " message_guichet = ?, status_text_guichet = ?, sender_name_guichet = ?, "
            + "subject_guichet = ? ,demand_max_step_guichet = ? ,demand_user_current_step_guichet = ? ," + " is_active_onglet_guichet = ? ,"
            + "status_text_agent =? , message_agent = ? ,is_active_onglet_agent = ? , " + " subject_email = ?, message_email = ?, sender_name_email = ?,"
            + "recipients_cc_email = ?, recipients_cci_email = ?, " + " is_active_onglet_email= ?,"
            + "message_sms = ?, billing_account_sms = ?, billing_group_sms = ?, " + "is_active_onglet_sms = ?,  "
            + "id_mailing_list_broadcast = ?, email_broadcast = ?, sender_name_broadcast = ?, subject_broadcast = ?, message_broadcast = ?,"
            + " recipients_cc_broadcast = ?,recipients_cci_broadcast = ?, " + " is_active_onglet_broadcast = ? ," + " days_to_alert = ?,id_state_after = ?, "
            + " alert_subject = ?, " + " marker_alert = ?, " + " alert_after_before = ? " + " WHERE id_task = ? ";

    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_task_alert_gru_cf WHERE id_task = ? ";

    @Override
    public void insert( AlertGruTaskConfig config )
    {
        AlertGruCacheService.getInstance( ).removeGruConfigFromCache( config.getIdTask( ) );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_NOTIF, AlertGruPlugin.getPlugin( ) ) )
        {
            configToData( config, daoUtil );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param alertGruTaskConfig
     */
    @Override
    public void store( AlertGruTaskConfig alertGruTaskConfig )
    {
        // remove cache
        AlertGruCacheService.getInstance( ).removeGruConfigFromCache( alertGruTaskConfig.getIdTask( ) );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, AlertGruPlugin.getPlugin( ) ) )
        {
            int nPos = configToData( alertGruTaskConfig, daoUtil );

            daoUtil.setInt( ++nPos, alertGruTaskConfig.getIdTask( ) );
            daoUtil.executeUpdate( );
        }

    }

    /**
     * {@inheritDoc}
     *
     * @param nIdTask
     * @return
     */
    @Override
    public AlertGruTaskConfig load( int nIdTask )
    {
        AlertGruTaskConfig config = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, AlertGruPlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nIdTask );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                int nPos = 0;
                config = new AlertGruTaskConfig( );
                config.setIdTask( daoUtil.getInt( ++nPos ) );

                config.setIdSpringProvider( daoUtil.getString( ++nPos ) );

                String strMarkerProviderIds = daoUtil.getString( ++nPos );
                List<String> listMarkerProviderIds = new ArrayList<>( );

                if ( !StringUtils.isBlank( strMarkerProviderIds ) )
                {
                    Collections.addAll( listMarkerProviderIds, StringUtils.split( strMarkerProviderIds, ',' ) );
                }

                config.setMarkerProviders( listMarkerProviderIds );

                config.setDemandStatus( daoUtil.getInt( ++nPos ) );
                config.setCrmStatusId( daoUtil.getInt( ++nPos ) );
                config.setSetOnglet( daoUtil.getInt( ++nPos ) );

                config.setMessageGuichet( daoUtil.getString( ++nPos ) );
                config.setStatustextGuichet( daoUtil.getString( ++nPos ) );
                config.setSenderNameGuichet( daoUtil.getString( ++nPos ) );
                config.setSubjectGuichet( daoUtil.getString( ++nPos ) );
                config.setDemandMaxStepGuichet( daoUtil.getInt( ++nPos ) );
                config.setDemandUserCurrentStepGuichet( daoUtil.getInt( ++nPos ) );
                config.setActiveOngletGuichet( daoUtil.getBoolean( ++nPos ) );

                config.setStatustextAgent( daoUtil.getString( ++nPos ) );
                config.setMessageAgent( daoUtil.getString( ++nPos ) );
                config.setActiveOngletAgent( daoUtil.getBoolean( ++nPos ) );

                config.setSubjectEmail( daoUtil.getString( ++nPos ) );
                config.setMessageEmail( daoUtil.getString( ++nPos ) );
                config.setSenderNameEmail( daoUtil.getString( ++nPos ) );
                config.setRecipientsCcEmail( daoUtil.getString( ++nPos ) );
                config.setRecipientsCciEmail( daoUtil.getString( ++nPos ) );
                config.setActiveOngletEmail( daoUtil.getBoolean( ++nPos ) );

                config.setMessageSMS( daoUtil.getString( ++nPos ) );
                config.setBillingAccountSMS( daoUtil.getString( ++nPos ) );
                config.setBillingGroupSMS( daoUtil.getString( ++nPos ) );
                config.setActiveOngletSMS( daoUtil.getBoolean( ++nPos ) );

                config.setIdMailingListBroadcast( daoUtil.getInt( ++nPos ) );
                config.setEmailBroadcast( daoUtil.getString( ++nPos ) );
                config.setSenderNameBroadcast( daoUtil.getString( ++nPos ) );
                config.setSubjectBroadcast( daoUtil.getString( ++nPos ) );
                config.setMessageBroadcast( daoUtil.getString( ++nPos ) );
                config.setRecipientsCcBroadcast( daoUtil.getString( ++nPos ) );
                config.setRecipientsCciBroadcast( daoUtil.getString( ++nPos ) );
                config.setActiveOngletBroadcast( daoUtil.getBoolean( ++nPos ) );

                config.setDaysToAlert( daoUtil.getInt( ++nPos ) );
                config.setIdStateAfter( daoUtil.getInt( ++nPos ) );
                config.setAlertSubject( daoUtil.getString( ++nPos ) );
                config.setMarkerAlert( daoUtil.getString( ++nPos ) );
                config.setAlertAfterBefore( daoUtil.getString( ++nPos ) );
            }
        }
        return config;
    }

    @Override
    public void delete( int i )
    {
        // remove cache
        AlertGruCacheService.getInstance( ).removeGruConfigFromCache( i );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, AlertGruPlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, i );
            daoUtil.executeUpdate( );
        }

    }

    /**
     * Inserts the data of the specified configuration into the specified {@code DAOUtil} object
     *
     * @param config
     *            the configuration containing the data
     * @param daoUtil
     *            the {@DAOUtil} object
     * @return the last position of the data
     */
    private int configToData( AlertGruTaskConfig config, DAOUtil daoUtil )
    {
        int nPos = 0;

        daoUtil.setInt( ++nPos, config.getIdTask( ) );
        daoUtil.setString( ++nPos, config.getIdSpringProvider( ) );
        daoUtil.setString( ++nPos, StringUtils.join( config.getMarkerProviders( ), ';' ) );
        daoUtil.setInt( ++nPos, config.getDemandStatus( ) );
        daoUtil.setInt( ++nPos, config.getCrmStatusId( ) );
        daoUtil.setInt( ++nPos, config.getSetOnglet( ) );

        daoUtil.setString( ++nPos, config.getMessageGuichet( ) );
        daoUtil.setString( ++nPos, config.getStatustextGuichet( ) );
        daoUtil.setString( ++nPos, config.getSenderNameGuichet( ) );
        daoUtil.setString( ++nPos, config.getSubjectGuichet( ) );
        daoUtil.setInt( ++nPos, config.getDemandMaxStepGuichet( ) );
        daoUtil.setInt( ++nPos, config.getDemandUserCurrentStepGuichet( ) );
        daoUtil.setBoolean( ++nPos, config.isActiveOngletGuichet( ) );

        daoUtil.setString( ++nPos, config.getStatustextAgent( ) );
        daoUtil.setString( ++nPos, config.getMessageAgent( ) );
        daoUtil.setBoolean( ++nPos, config.isActiveOngletAgent( ) );

        daoUtil.setString( ++nPos, config.getSubjectEmail( ) );
        daoUtil.setString( ++nPos, config.getMessageEmail( ) );
        daoUtil.setString( ++nPos, config.getSenderNameEmail( ) );
        daoUtil.setString( ++nPos, config.getRecipientsCcEmail( ) );
        daoUtil.setString( ++nPos, config.getRecipientsCciEmail( ) );
        daoUtil.setBoolean( ++nPos, config.isActiveOngletEmail( ) );

        daoUtil.setString( ++nPos, config.getMessageSMS( ) );
        daoUtil.setString( ++nPos, config.getBillingAccountSMS( ) );
        daoUtil.setString( ++nPos, config.getBillingGroupSMS( ) );
        daoUtil.setBoolean( ++nPos, config.isActiveOngletSMS( ) );

        daoUtil.setInt( ++nPos, config.getIdMailingListBroadcast( ) );
        daoUtil.setString( ++nPos, config.getEmailBroadcast( ) );
        daoUtil.setString( ++nPos, config.getSenderNameBroadcast( ) );
        daoUtil.setString( ++nPos, config.getSubjectBroadcast( ) );
        daoUtil.setString( ++nPos, config.getMessageBroadcast( ) );
        daoUtil.setString( ++nPos, config.getRecipientsCcBroadcast( ) );
        daoUtil.setString( ++nPos, config.getRecipientsCciBroadcast( ) );
        daoUtil.setBoolean( ++nPos, config.isActiveOngletBroadcast( ) );

        daoUtil.setInt( ++nPos, config.getDaysToAlert( ) );
        daoUtil.setInt( ++nPos, config.getIdStateAfter( ) );
        daoUtil.setString( ++nPos, config.getAlertSubject( ) );
        daoUtil.setString( ++nPos, config.getMarkerAlert( ) );
        daoUtil.setString( ++nPos, config.getAlertAfterBefore( ) );

        return nPos;
    }

}
