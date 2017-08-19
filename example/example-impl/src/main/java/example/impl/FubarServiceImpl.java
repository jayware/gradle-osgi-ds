package example.impl;


import example.api.AnotherService;
import example.api.Fubar;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


@Component(service = Fubar.class, name = "MySuperService", immediate = true)
public class FubarServiceImpl
implements Fubar
{
    private AnotherService anotherService;

    @Activate
    public void turnOn()
    {
        System.out.println("FubarService activated!");
    }

    @Override
    public void sayHello()
    {
        System.out.println("Hello World!");

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
}
