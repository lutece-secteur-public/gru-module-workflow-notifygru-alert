-- liquibase formatted sql
-- changeset workflow-alertgru:create_db_workflow_alertgru.sql
-- preconditions onFail:MARK_RAN onError:WARN

DROP TABLE IF EXISTS workflow_task_alert_gru_cf;
DROP TABLE IF EXISTS workflow_task_alert_gru_history;


/*==============================================================*/
/* Table structure for table workflow_task_alert_gru_cf					*/
/*==============================================================*/


  
  
CREATE TABLE workflow_task_alert_gru_cf(
  id_task INT NOT NULL,
 id_spring_provider VARCHAR(255) DEFAULT NULL,
 marker_provider_ids VARCHAR(1000) NULL,
 demand_status INT DEFAULT  NULL, 
 crm_status_id INT DEFAULT  1, 
 set_onglet SMALLINT  NULL DEFAULT NULL,

  message_guichet TEXT COLLATE utf8_unicode_ci DEFAULT NULL,  
  status_text_guichet VARCHAR(255) DEFAULT  NULL,  
  sender_name_guichet VARCHAR(255) DEFAULT  NULL,  
  subject_guichet VARCHAR(255) DEFAULT  NULL,  
  demand_max_step_guichet SMALLINT NULL DEFAULT 1,
  demand_user_current_step_guichet SMALLINT NULL DEFAULT 1,
  is_active_onglet_guichet SMALLINT NULL DEFAULT 0,

  status_text_agent  VARCHAR(255) DEFAULT  NULL,  
 message_agent text COLLATE utf8_unicode_ci,  
  is_active_onglet_agent SMALLINT NOT NULL DEFAULT 0,

 subject_email VARCHAR(255) DEFAULT   NULL,
 message_email text COLLATE utf8_unicode_ci,  
  sender_name_email VARCHAR(255) DEFAULT   NULL,
  recipients_cc_email VARCHAR(255) DEFAULT   NULL,
  recipients_cci_email VARCHAR(255) DEFAULT   NULL,
  is_active_onglet_email SMALLINT  NULL DEFAULT 0,

  message_sms text COLLATE utf8_unicode_ci,
  billing_account_sms VARCHAR(255) DEFAULT   NULL,  
  billing_group_sms VARCHAR(255) DEFAULT   NULL,  
  is_active_onglet_sms SMALLINT NOT NULL DEFAULT 0,

 id_mailing_list_broadcast INT DEFAULT NULL,
 email_broadcast VARCHAR(255) DEFAULT NULL,
   sender_name_broadcast VARCHAR(255) DEFAULT   NULL,
subject_broadcast VARCHAR(255) DEFAULT   NULL,
 message_broadcast text COLLATE utf8_unicode_ci,  
  recipients_cc_broadcast VARCHAR(255) DEFAULT   NULL,
  recipients_cci_broadcast VARCHAR(255) DEFAULT   NULL,
  is_active_onglet_broadcast SMALLINT  NULL DEFAULT 0,
  days_to_alert INT DEFAULT  NULL,
  id_state_after INT DEFAULT  NULL,
  alert_subject VARCHAR(255) DEFAULT   NULL,
  marker_alert VARCHAR(255) DEFAULT   NULL,
  alert_after_before VARCHAR(50) DEFAULT   NULL,
  PRIMARY KEY  (id_task)
  );


CREATE TABLE workflow_task_alert_gru_history(
  id_history INT NOT NULL,
  id_task INT DEFAULT NULL,
   crm_status_id INT DEFAULT  1, 

  message_guichet TEXT COLLATE utf8_unicode_ci DEFAULT NULL,  
  status_text_guichet VARCHAR(255) DEFAULT  NULL,  
  sender_name_guichet VARCHAR(255) DEFAULT  NULL,  
  subject_guichet VARCHAR(255) DEFAULT  NULL,  
  demand_max_step_guichet SMALLINT NULL DEFAULT 1,
  demand_user_current_step_guichet SMALLINT NULL DEFAULT 1,
  is_active_onglet_guichet SMALLINT NULL DEFAULT 0,

  status_text_agent  VARCHAR(255) DEFAULT  NULL,  
 message_agent text COLLATE utf8_unicode_ci,  
  is_active_onglet_agent SMALLINT NOT NULL DEFAULT 0,

 subject_email VARCHAR(255) DEFAULT   NULL,
 message_email text COLLATE utf8_unicode_ci,  
  sender_name_email VARCHAR(255) DEFAULT   NULL,
  recipients_cc_email VARCHAR(255) DEFAULT   NULL,
  recipients_cci_email VARCHAR(255) DEFAULT   NULL,
  is_active_onglet_email SMALLINT  NULL DEFAULT 0,

  message_sms text COLLATE utf8_unicode_ci,
  billing_account_sms VARCHAR(255) DEFAULT   NULL, 
  billing_group_sms VARCHAR(255) DEFAULT   NULL,  
  is_active_onglet_sms SMALLINT NOT NULL DEFAULT 0,

 id_mailing_list_broadcast INT DEFAULT NULL,
 email_broadcast VARCHAR(255) DEFAULT NULL,
   sender_name_broadcast VARCHAR(255) DEFAULT   NULL,
subject_broadcast VARCHAR(255) DEFAULT   NULL,
 message_broadcast text COLLATE utf8_unicode_ci,  
  recipients_cc_broadcast VARCHAR(255) DEFAULT   NULL,
  recipients_cci_broadcast VARCHAR(255) DEFAULT   NULL,
  is_active_onglet_broadcast SMALLINT  NULL DEFAULT 0,


  PRIMARY KEY  (id_history, id_task)
  );

  --
-- Table structure for table notifygru_alert_update_resource_state_queue
--
DROP TABLE IF EXISTS notifygru_alert_update_resource_state_queue;
CREATE TABLE notifygru_alert_update_resource_state_queue (
  id_resource_queue INT AUTO_INCREMENT,
  id_resource INT NOT NULL,
  id_resource_history INT NOT NULL,
  id_task INT NOT NULL,
  resource_type VARCHAR(255) DEFAULT '' NOT NULL,
  id_external_parent INT DEFAULT 0 NOT NULL,
  id_workflow INT NOT NULL,
  status INT DEFAULT 0 NOT NULL,
  creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  alert_reference_date TIMESTAMP NOT NULL,
  id_state INT NOT NULL,
  
  PRIMARY KEY ( id_resource_queue )
);
