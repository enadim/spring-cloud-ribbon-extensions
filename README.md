# Spring Cloud Ribbon Extensions
Must To Have!! ribbon routing extensions

Experimental working version designed for dev environment.
Do not use in production unless excessive testing or simple direct usage without the context propagation feature.
Integration tests for stomp & jms are not yet done.

[![Build Status](https://travis-ci.org/enadim/spring-cloud-ribbon-extensions.svg?branch=develop)](https://travis-ci.org/enadim/spring-cloud-ribbon-extensions)
[![codecov](https://codecov.io/gh/enadim/spring-cloud-ribbon-extensions/branch/develop/graph/badge.svg)](https://codecov.io/gh/enadim/spring-cloud-ribbon-extensions)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/bf7e3455f2894da19b1e250173c9ace1)](https://www.codacy.com/app/enadim/spring-cloud-ribbon-extensions?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=enadim/spring-cloud-ribbon-extensions&amp;utm_campaign=Badge_Grade)

![CoL](https://tokei.rs/b1/github/enadim/spring-cloud-ribbon-extensions)
[![Quality Gate](https://sonarcloud.io/api/badges/measure?key=enadim:spring-cloud-ribbon-extensions:develop&metric=reliability_rating)](https://sonarcloud.io/component_measures?id=enadim%3Aspring-cloud-ribbon-extensions%3Adevelop&metric=reliability_rating)
[![Quality Gate](https://sonarcloud.io/api/badges/measure?key=enadim:spring-cloud-ribbon-extensions:develop&metric=security_rating)](https://sonarcloud.io/component_measures?id=enadim%3Aspring-cloud-ribbon-extensions%3Adevelop&metric=security_rating)
[![Quality Gate](https://sonarcloud.io/api/badges/measure?key=enadim:spring-cloud-ribbon-extensions:develop&metric=sqale_rating)](https://sonarcloud.io/component_measures?id=enadim%3Aspring-cloud-ribbon-extensions%3Adevelop&metric=sqale_rating)
[![Quality Gate](https://sonarcloud.io/api/badges/measure?key=enadim:spring-cloud-ribbon-extensions:develop&metric=coverage)](https://sonarcloud.io/component_measures?id=enadim%3Aspring-cloud-ribbon-extensions%3Adevelop&metric=Coverage)
[![Quality Gate](https://sonarcloud.io/api/badges/measure?key=enadim:spring-cloud-ribbon-extensions:develop&metric=duplicated_lines_density)](https://sonarcloud.io/component_measures?id=enadim%3Aspring-cloud-ribbon-extensions%3Adevelop&metric=Duplications)

[![Maven Central](https://img.shields.io/maven-central/v/enadim/spring-cloud-ribbon-extensions.svg)](http://search.maven.org/#artifactdetails%7Ccom.github.enadim%7Cspring-cloud-ribbon-extensions%7C1.2.1-SNAPSHOT%7C)
[![Javadocs](http://www.javadoc.io/badge/com.github.enadim/spring-cloud-ribbon-extensions.svg)](http://www.javadoc.io/doc/com.github.enadim/spring-cloud-ribbon-extensions)

[![GitHub license](https://img.shields.io/github/license/enadim/spring-cloud-ribbon-extensions.svg)](https://github.com/enadim/spring-cloud-ribbon-extensions/develop/LICENSE)
[![GitHub issues](https://img.shields.io/github/issues/enadim/spring-cloud-ribbon-extensions.svg)](https://github.com/enadim/spring-cloud-ribbon-extensions/issues)
[![GitHub network/members](https://img.shields.io/github/forks/enadim/spring-cloud-ribbon-extensions.svg)](https://github.com/enadim/spring-cloud-ribbon-extensions/network/members)

### Requirements
* spring cloud ribbon.
* spring cloud eureka.
* optional spring cloud feign.
* optional spring cloud zuul.
* optional spring cloud hystrix
* optional stomp.
* optional jms.


## Features

### Routing Rule
#### Favorite Zone Routing.
use @EnableRibbonFavoriteZone to enable routing to a favorite zone: see the tests for concrete usage and configurations.

Designed for development environment in order of testing a new beta version of a micro-service without impacting the users.

#### Strict Metadata Routing
use @EnableRibbonStrictMetadataMatcher to enable routing to servers that have the same context metadata: see the tests for concrete usage and configurations.

Designed to target a specific service that holds a point to point connection with an external system (like FIX,...)


#### Dynamic Metadata Attribute Routing
use @EnableDynamicMatcher to enable routing against a dynamic key: see the tests for concrete usage and configurations.

Designed to target a specific service that holds multiple a point to point connections with many external systems (like FIX,...)

### Context Propagation
use @EnableRibbonContextPropagation to enable desired http headers flowing between all your rest micro-services, jms messages, stomp messages: see the tests for concrete usage and configurations.

Designed to propagate the technical routing information across multiple transport http, jms, stomp ...

You should use only lower case key names (http header limitation).

### Combine Favorite Zone Routing & Context Propagation
Eureka! Let's enter a world of easy development with micro-service architecture.
* Developers are able to deploy & debug their own micro-service and get back any request they have initiated disregarding the entry point and without being annoyed by the requests they have not initiated).
* Deploying in multi region-and let our clients (that have no knowledge of eureka, ribbon, zuul) choose the zone they want to target.
* And other things that I have not thought about...

## Setup
The artifact will never have a third party dependency: please check the compatibility with your dependencies.

maven
```xml
<dependency>
  <groupId>com.github.enadim</groupId>
  <artifactId>spring-cloud-ribbon-extensions</artifactId>
  <version>1.2.1-SNAPSHOT</version>
</dependency>
```

gradle
```gradle
dependencies {
    compile 'com.github.enadim:spring-cloud-ribbon-extensions:1.2.1-SNAPSHOT'
}
```

## License

[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
