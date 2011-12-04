package de.jaxenter.eesummit.caroline.gui.beans.employee;


import de.jaxenter.eesummit.caroline.backend.api.CustomerService;
import de.jaxenter.eesummit.caroline.entities.Customer;
import de.jaxenter.eesummit.caroline.gui.viewconfig.EmployeePages;
import org.apache.myfaces.extensions.cdi.core.api.config.view.ViewConfig;
import org.apache.myfaces.extensions.cdi.jsf.api.Jsf;
import org.apache.myfaces.extensions.cdi.message.api.Message;
import org.apache.myfaces.extensions.cdi.message.api.MessageContext;
import org.apache.myfaces.extensions.cdi.message.api.payload.MessageSeverity;

import javax.faces.bean.ViewScoped;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Backing bean for creating and editing customers.
 *
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a> 
 */
@ViewScoped
@Named("editCustomer")
public class EditCustomerView implements Serializable
{
    private Customer customer = new Customer();
    private Long customerId = null;
    private boolean edit = false;

    private @Inject CustomerService customerSvc;
    private @Inject @Jsf MessageContext messageContext;


    /**
     * This gets called from the view event.
     * see editCustomer.xhtml
     */
    public void init(ComponentSystemEvent ev)
    {
        if (customerId != null)
        {
            customer = customerSvc.getById(customerId);
            if (customer == null)
            {
                Message msg = messageContext.message().text("{customer_doesnt_exist}").
                        namedArgument("customerId", customerId).
                        payload(MessageSeverity.ERROR).add();
            }
            edit = true;
        }
    }

    public Customer getCustomer()
    {
        return customer;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    public Long getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(Long customerId)
    {
        this.customerId = customerId;
    }

    public Class<? extends ViewConfig> saveCustomer()
    {
        if (!edit)
        {
            customerSvc.createCustomer(customer);
        }
        else
        {
            customerSvc.save(customer);
        }

        return EmployeePages.EditCustomer.class;
    }

    public boolean isEdit()
    {
        return edit;
    }
}
