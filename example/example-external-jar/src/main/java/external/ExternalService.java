package external;

import org.osgi.service.component.annotations.Component;


@Component(service = ExternalServiceApi.class, name = "ExternalService", immediate = true)
public class ExternalService implements ExternalServiceApi
{
    public String external()
    {
        return "I'm an external service.";
    }
}
