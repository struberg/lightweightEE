package de.jaxenter.eesummit.caroline.gui.msg;

import org.apache.deltaspike.core.api.message.MessageBundle;

/**
 * Typesafe Messages for displaying JSF messages
 */
@MessageBundle
public interface CarolineMessages
{
    String customerDoesNotExist(Long customerId);

    String loginRequired();
    String loginRequiredAdmin();
    String loginRequiredCustomer();
    String loginRequiredEmployee();

    String unknownUser();

    String nothingFound();
    String userPreferencesStored();
}
