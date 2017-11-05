primogenitor
===

[![Travis](https://img.shields.io/travis/io7m/primogenitor.png?style=flat-square)](https://travis-ci.org/io7m/primogenitor)
[![Maven Central](https://img.shields.io/maven-central/v/com.io7m.primogenitor/com.io7m.primogenitor.png?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.io7m.primogenitor%22)

The [io7m](http://io7m.com) root [POM](https://maven.apache.org/pom.html).

![primogenitor](./src/site/resources/primogenitor.jpg?raw=true)

Note: To build this project, you must use:

```
$ mvn -Denforcer.skip=true clean package
```

The reason for this is that this POM file is intended to be the
root POM for [io7m](http://io7m.com) projects and uses the [Maven
Enforcer](https://maven.apache.org/enforcer/maven-enforcer-plugin/)
plugin to require that descendant projects define values for certain
properties that this root POM leaves empty. Because there is no way
in Maven to have a plugin applied only to descendants, the root POM
actually cannot pass its own checks! Using the `enforcer.skip` property
allows the root POM to be installed and deployed to repositories.

