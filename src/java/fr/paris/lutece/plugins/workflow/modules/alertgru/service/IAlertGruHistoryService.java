package fr.paris.lutece.plugins.workflow.modules.alertgru.service;

import fr.paris.lutece.plugins.workflow.modules.alertgru.business.history.AlertGruHistory;
import fr.paris.lutece.portal.service.plugin.Plugin;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * IAlertGruHistoryService
 *
 */
public interface IAlertGruHistoryService {
    /**
     * Creation of an instance of Alert GRU
     *
     * @param notifyGru
     *            The instance of comment value which contains the informations to store
     * @param plugin
     *            the plugin
     */
    @Transactional( AlertGruPlugin.BEAN_TRANSACTION_MANAGER )
    void create( AlertGruHistory notifyGru, Plugin plugin );

    /**
     * Remove Alert GRU value by history
     *
     * @param nIdHistory
     *            the history key
     * @param nIdTask
     *            The task key
     * @param plugin
     *            the Plugin
     */
    @Transactional( AlertGruPlugin.BEAN_TRANSACTION_MANAGER )
    void removeByHistory( int nIdHistory, int nIdTask, Plugin plugin );

    /**
     * Remove Alert GRU value by task
     *
     * @param nIdTask
     *            The task key
     * @param plugin
     *            the Plugin
     */
    @Transactional( AlertGruPlugin.BEAN_TRANSACTION_MANAGER )
    void removeByTask( int nIdTask, Plugin plugin );

    /**
     * Load the Alert GRU Object
     *
     * @param nIdHistory
     *            the history id
     * @param nIdTask
     *            the task id
     * @param plugin
     *            the plugin
     * @return the Config Object
     */
    AlertGruHistory findByPrimaryKey(int nIdHistory, int nIdTask, Plugin plugin );
}
