package fr.paris.lutece.plugins.workflow.modules.alertgru.service;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.workflow.modules.alertgru.business.UpdateTaskStateResourceQueueHome;
import fr.paris.lutece.portal.business.event.EventRessourceListener;
import fr.paris.lutece.portal.business.event.ResourceEvent;
import fr.paris.lutece.portal.service.util.AppLogService;

public class AlertRessourceListener implements EventRessourceListener {
	
	@Override
	public String getName()
	{
		return AlertGruPlugin.PLUGIN_NAME;
	}

	@Override
	public void addedResource(ResourceEvent event) {
		//do nothing

	}

	@Override
	public void deletedResource(ResourceEvent event) {
		try {			
			if( StringUtils.isNumeric( event.getIdResource( ) )  ) {
				
				UpdateTaskStateResourceQueueHome.removeByResource( Integer.parseInt( event.getIdResource( ) ), event.getTypeResource( ));
			}
		}catch(Exception e) {
			AppLogService.error( e.getMessage( ),e);
		}
	}

	@Override
	public void updatedResource(ResourceEvent event) {
		try {			
			TaskAlertService.INSNACE.updateResourceQueue( event );
		
		}catch(Exception e) {
			AppLogService.error( e.getMessage( ),e);
		}
	}
	

}
