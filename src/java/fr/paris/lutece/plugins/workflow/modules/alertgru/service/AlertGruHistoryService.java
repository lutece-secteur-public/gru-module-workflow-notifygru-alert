package fr.paris.lutece.plugins.workflow.modules.alertgru.service;

import fr.paris.lutece.plugins.workflow.modules.alertgru.business.history.AlertGruHistory;
import fr.paris.lutece.plugins.workflow.modules.alertgru.business.history.AlertGruHistoryDAO;
import fr.paris.lutece.plugins.workflow.modules.alertgru.business.history.IAlertGruHistoryDAO;
import fr.paris.lutece.portal.service.plugin.Plugin;
import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.transaction.annotation.Transactional;

public class AlertGruHistoryService implements IAlertGruHistoryService {
    /**
     * The name of the bean of this service
     */
    public static final String BEAN_SERVICE = "workflow-alertgru.alertGruHistoryService";
    @Inject
    @Named(AlertGruHistoryDAO.BEAN )
    private IAlertGruHistoryDAO _dao;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( AlertGruPlugin.BEAN_TRANSACTION_MANAGER )
    public void create( AlertGruHistory alertGru, Plugin plugin )
    {
        _dao.insert( alertGru, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( AlertGruPlugin.BEAN_TRANSACTION_MANAGER )
    public void removeByHistory( int nIdHistory, int nIdTask, Plugin plugin )
    {
        _dao.deleteByHistory( nIdHistory, nIdTask, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( AlertGruPlugin.BEAN_TRANSACTION_MANAGER )
    public void removeByTask( int nIdTask, Plugin plugin )
    {
        _dao.deleteByTask( nIdTask, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AlertGruHistory findByPrimaryKey( int nIdHistory, int nIdTask, Plugin plugin )
    {
        return _dao.load( nIdHistory, nIdTask, plugin );
    }
}