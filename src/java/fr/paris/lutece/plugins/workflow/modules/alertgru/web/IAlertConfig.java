package fr.paris.lutece.plugins.workflow.modules.alertgru.web;

public interface IAlertConfig {
    /**
     * Gives the name of the alert configuration
     *
     * @return the name
     */
    String getName( );

    /**
     * Tests if this alert configuration is active or not
     *
     * @return {@code true} if the alert configuration is active, {@code false} otherwise
     */
    boolean isActive( );

    /**
     * Activates or deactivates this alert configuration depending to the specified boolean
     *
     * @param bActive
     *            {@code true} if this alert configuration must be activated, {@code false} if it must be deactivated
     */
    void setActive( boolean bActive );

    /**
     * Adds this alert configuration
     */
    void addConfig( );

    /**
     * Removes this alert configuration
     */
    void removeConfig( );

    /**
     * Gives the validator for this alert configuration
     *
     * @return the validator
     */
     AbstractAlertConfigValidator getValidator( );
}
