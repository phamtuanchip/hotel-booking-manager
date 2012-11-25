package cz.fi.muni.pa165.hotelbookingmanagerweb;

import cz.fi.muni.pa165.hotelbookingmanager.service.interfaces.ClientService;
import cz.fi.muni.pa165.hotelbookingmanager.transferobjects.ClientTO;
import cz.fi.muni.pa165.hotelbookingmanagerhelper.ClientServiceImpl;
import java.util.List;
import java.util.Set;
import net.sourceforge.stripes.action.*;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;

/**
 *
 * @author Andrej Galád
 */
@UrlBinding("/clients/{$event}/{client.id}")
public class ClientsActionBean implements ActionBean {

    private ActionBeanContext context;

    private ClientService clientService = new ClientServiceImpl();
    private CountryPicker countryPicker = new CountryPicker();
    
    @ValidateNestedProperties(value = {
            @Validate(on = {"add", "save"}, field = "firstName", required = true, minlength = 2, maxlength = 50),
            @Validate(on = {"add", "save"}, field = "lastName", required = true, minlength = 2, maxlength = 50),
            @Validate(on = {"add", "save"}, field = "contact.phone", required = true, mask = "\\d*", maxlength = 30),
            @Validate(on = {"add", "save"}, field = "contact.email", required = true, mask = "[\\w\\-\\.\\+]+@[a-zA-Z0-9\\.\\-]+\\.[a-zA-z]+", minlength = 6, maxlength = 50),
            @Validate(on = {"add", "save"}, field = "contact.address", required = true, maxlength = 30),
            @Validate(on = {"add", "save"}, field = "contact.city", required = true, minlength = 2, maxlength = 50)
    })
    private ClientTO client;

    public ClientTO getClient() {
        return client;
    }

    public void setClient(ClientTO client) {
        this.client = client;
    }

    @Override
    public void setContext(ActionBeanContext context) {
        this.context = context;
    }

    public List<ClientTO> getClients() {
        return clientService.findAllClients();
    }
    
    public Set<String> getCountries() {
        return countryPicker.getCountriesName();
    }

    @Override
    public ActionBeanContext getContext() {
        return context;
    }

    @DefaultHandler
    public Resolution all() {
        return new ForwardResolution("/client.jsp");
    }

    public Resolution addClient() {
        clientService.createClient(client);
        return new RedirectResolution(this.getClass(), "all");
    }

    @Before(stages = LifecycleStage.BindingAndValidation, on = {"edit", "save"})
    public void loadHotelFromDatabase() {
        String ids = context.getRequest().getParameter("client.id");
        if (ids == null) return;
        client = clientService.findClient(Long.parseLong(ids));
    }

    public Resolution edit() {
        return new ForwardResolution("/editClient.jsp");
    }

    public Resolution save() {
        clientService.updateClient(client);
        return new RedirectResolution(this.getClass(), "all");
    }

    public Resolution delete() {
        clientService.deleteClient(client);
        return new RedirectResolution(this.getClass(), "all");
    }
}
