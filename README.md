<p>
    <h1>Gradle plugin for OSGi Declarative Services</h1>
    <table>
        <thead>
            <tr>
                <th align="center" colspan="3">master</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td align="center">
                    <a href="https://travis-ci.org/jayware/gradle-osgi-ds">
                        <img src="https://img.shields.io/travis/jayware/gradle-osgi-ds/master.svg?style=flat-square" alt="Build Status">
                    </a>
                </td>
                <td align="center">
                    <a href="https://www.versioneye.com/user/projects/56e719fc96f80c003cade71a?child=summary">
                        <img src="https://www.versioneye.com/user/projects/56e719fc96f80c003cade71a/badge.svg?style=flat" alt="Dependency Status" />
                    </a>
                </td>
            </tr>
        </tbody>
    </table>
</p>

## Description
Easy to use gradle plugin to generate Declarative Services XML files based on the [OSGi Service Component Annotations](http://wiki.osgi.org/wiki/Declarative_Services).

### Maven coordinates
| Group ID              | Artifact ID                                                                              | Version |
| :-------------------: | :--------------------------------------------------------------------------------------: | :-----: |
| org.jayware           | <a href="https://jcenter.bintray.com/org/jayware/gradle-osgi-ds/">gradle-osgi-ds</a>     | 0.5.1   |

### Usage

#### Example Gradle script
```groovy
plugins {
    id 'org.jayware.osgi-ds' version '0.5.1'
}

apply plugin: 'java'
apply plugin: 'osgi'

apply plugin: 'osgi-ds'


dependencies {
    compileOnly 'org.osgi:org.osgi.service.component.annotations:1.3.0'
}

jar {
    manifest {
        instruction "Service-Component", "OSGI-INF/*.xml"
    }
}
```

#### Example OSGi Service

```java
package example.api;

public interface Fubar
{
    void sayHello();
}
```

```java
package example.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component(service = Fubar.class, name = "MySuperService", immediate = true)
public class FubarServiceImpl
implements Fubar
{
    @Activate
    public void turnOn()
    {
        System.out.println("FubarService activated");
    }

    @Override
    public void sayHello()
    {
        System.out.println("Hello World!");
    }
}
```

#### Resulting DS file
```xml
<?xml version="1.0" encoding="UTF-8"?>
<scr:component xmlns:scr="http://www.osgi.org/xmlns/scr/v1.1.0" immediate="true" name="MySuperService" activate="turnOn">
    <implementation class="example.impl.FubarServiceImpl"/>
    <service servicefactory="false">
        <provide interface="example.api.Fubar"/>
    </service>
</scr:component>
```

## Contributions
All contributions are welcome: ideas, patches, documentation, bug reports, complaints.
