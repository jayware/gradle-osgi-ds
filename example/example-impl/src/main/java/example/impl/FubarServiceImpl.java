package example.impl;


import example.api.Fubar;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;


@Component(service = Fubar.class, name = "MySuperService", immediate = true)
public class FubarServiceImpl
implements Fubar
{
    @Activate
    public void turnOn()
    {
        System.out.println("FubarService activated!");
    }

    @Override
    public void sayHello()
    {
        System.out.println("Hello World!");
    }
}
