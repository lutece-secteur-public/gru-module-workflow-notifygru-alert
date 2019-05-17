package fr.paris.lutece.plugins.workflow.modules.alertgru.business.history;

import fr.paris.lutece.plugins.workflow.modules.alertgru.business.history.BroadcastHistory;
import fr.paris.lutece.plugins.workflow.modules.alertgru.business.history.EmailHistory;
import fr.paris.lutece.plugins.workflow.modules.alertgru.business.history.GuichetHistory;
import fr.paris.lutece.plugins.workflow.modules.alertgru.business.history.SMSHistory;
import fr.paris.lutece.plugins.workflow.modules.alertgru.business.history.AgentHistory;

/**
 * This is the business class for the object AlertGruHistory
 */
public class AlertGruHistory {
    // Variables declarations
    private int _nIdTask;
    private int _nIdResourceHistory;

    /** The _ncrm status id. */
    private int _nCrmStatusId;
    private BroadcastHistory _oBroadCast;
    private EmailHistory _oEmail;
    private GuichetHistory _oGuichet;
    private SMSHistory _oSMS;
    private AgentHistory _oAgent;


    /**
     * Simple Constructor
     */
    public AlertGruHistory( )
    {
        _oBroadCast = new BroadcastHistory( );
        _oEmail = new EmailHistory( );
        _oGuichet = new GuichetHistory( );
        _oSMS = new SMSHistory( );
        _oAgent = new AgentHistory( );
    }

    /**
     * Gets the crm status id.
     *
     * @return the _ncrmStatusId
     */
    public int getCrmStatusId( )
    {
        return _nCrmStatusId;
    }

    /**
     * Sets the crm status id.
     *
     * @param crmStatusId
     *            the new crm status id
     */
    public void setCrmStatusId( int crmStatusId )
    {
        this._nCrmStatusId = crmStatusId;
    }

    /**
     * Returns the IdTask
     *
     * @return The IdTask
     */
    public int getIdTask( )
    {
        return _nIdTask;
    }

    /**
     * Sets the IdTask
     *
     * @param nIdTask
     *            The IdTask
     */
    public void setIdTask( int nIdTask )
    {
        _nIdTask = nIdTask;
    }

    /**
     * Returns the IdResourceHistory
     *
     * @return The IdResourceHistory
     */
    public int getIdResourceHistory( )
    {
        return _nIdResourceHistory;
    }

    /**
     * Sets the IdResourceHistory
     *
     * @param nIdResourceHistory
     *            The IdResourceHistory
     */
    public void setIdResourceHistory( int nIdResourceHistory )
    {
        _nIdResourceHistory = nIdResourceHistory;
    }

    /**
     * Returns the oBroadCast
     *
     * @return The oBroadCast
     */
    public BroadcastHistory getBroadCast( )
    {
        return _oBroadCast;
    }

    /**
     * Sets the oBroadCast
     *
     * @param oBroadCast
     *            The oBroadCast
     */
    public void setBroadCast( BroadcastHistory oBroadCast )
    {
        _oBroadCast = oBroadCast;
    }

    /**
     * Returns the oEmail
     *
     * @return The oEmail
     */
    public EmailHistory getEmail( )
    {
        return _oEmail;
    }

    /**
     * Sets the oEmail
     *
     * @param oEmail
     *            The oEmail
     */
    public void setEmail( EmailHistory oEmail )
    {
        _oEmail = oEmail;
    }

    /**
     * Returns the oGuichet
     *
     * @return The oGuichet
     */
    public GuichetHistory getGuichet( )
    {
        return _oGuichet;
    }

    /**
     * Sets the oGuichet
     *
     * @param oGuichet
     *            The oGuichet
     */
    public void setGuichet( GuichetHistory oGuichet )
    {
        _oGuichet = oGuichet;
    }

    /**
     * Returns the oSMS
     *
     * @return The oSMS
     */
    public SMSHistory getSMS( )
    {
        return _oSMS;
    }

    /**
     * Sets the oSMS
     *
     * @param oSMS
     *            The oSMS
     */
    public void setSMS( SMSHistory oSMS )
    {
        _oSMS = oSMS;
    }

    /**
     * Returns the oAgent
     *
     * @return The oAgent
     */
    public AgentHistory getAgent( )
    {
        return _oAgent;
    }

    /**
     * Sets the oAgent
     *
     * @param oAgent
     *            The oAgent
     */
    public void setAgent( AgentHistory oAgent )
    {
        _oAgent = oAgent;
    }
}
