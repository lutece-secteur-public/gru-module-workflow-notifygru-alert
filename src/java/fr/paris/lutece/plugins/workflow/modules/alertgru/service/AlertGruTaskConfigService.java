package fr.paris.lutece.plugins.workflow.modules.alertgru.service;

import fr.paris.lutece.plugins.workflow.modules.alertgru.business.AlertGruTaskConfig;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfig;
import fr.paris.lutece.plugins.workflowcore.service.config.TaskConfigService;
import org.springframework.transaction.annotation.Transactional;

public class AlertGruTaskConfigService extends TaskConfigService {

    public static final String BEAN_SERVICE = "workflow-alertgru.taskAlertGruConfigService";

    /**
     * {@inheritDoc}
     *
     * @param config
     */
    @Override
    @Transactional( AlertGruPlugin.BEAN_TRANSACTION_MANAGER )
    public void create( ITaskConfig config )
    {
        super.create( config );
    }


    /**
     * {@inheritDoc}
     *
     * @param <T>
     * @param nIdTask
     * @return
     */
    @SuppressWarnings( "unchecked" )
    @Override
    public <T> T findByPrimaryKey( int nIdTask )
    {
        AlertGruTaskConfig config = super.findByPrimaryKey( nIdTask );

        return (T) config;
    }

    /**
     * {@inheritDoc}
     *
     * @param config
     */
    @Override
    @Transactional( AlertGruPlugin.BEAN_TRANSACTION_MANAGER )
    public void update( ITaskConfig config )
    {
        super.update( config );
    }


    /**
     * {@inheritDoc}
     *
     * @param nIdTask
     */
    @Override
    @Transactional( AlertGruPlugin.BEAN_TRANSACTION_MANAGER )
    public void remove( int nIdTask )
    {
        super.remove( nIdTask );
    }
}
