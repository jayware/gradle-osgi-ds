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
| Group ID              | Artifact ID                                                                                            | Version |
| :-------------------: | :----------------------------------------------------------------------------------------------------: | :-----: |
| org.jayware           | <a href="https://jcenter.bintray.com/org/jayware/gradle-osgi-ds/">gradle-osgi-ds</a>     | 0.1.0   |

### Usage
```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath "org.jayware:gradle-osgi-ds:0.1.0"
    }
}

apply plugin: 'osgi-ds'
```

## Contributions
All contributions are welcome: ideas, patches, documentation, bug reports, complaints.
