package example.impl;


import example.api.AnotherService;
import example.api.Fubar;
import external.ExternalServiceApi;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


@Component(service = Fubar.class, name = "MySuperService", immediate = true)
public class FubarServiceImpl
implements Fubar
{
    private AnotherService anotherService;
    private ExternalServiceApi externalService;

    @Activate
    public void turnOn()
    {
        System.out.println("FubarService activated!");
    }

    @Override
    public void sayHello()
    {
        System.out.println("Hello World!");
        System.out.println(externalService.external());

        anotherService.doSomething();
    }

    @Reference
    public void bind(AnotherService service)
    {
        anotherService = service;
    }

    public void unbind(AnotherService service)
    {
        anotherService = null;
    }

    @Reference
    public void bind(ExternalServiceApi service)
    {
        externalService = service;
    }

    public void unbind(ExternalServiceApi service)
    {
        externalService = null;
    }
}
