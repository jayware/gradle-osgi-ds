package example.impl;

import example.api.AnotherService;
import org.osgi.service.component.annotations.Component;


@Component(service = AnotherService.class, name = "AnotherServiceImplementation", immediate = true)
public class AnotherServiceImpl
implements AnotherService
{
    @Override
    public void doSomething()
    {
        System.out.println("Another service did something!");
    }
}
