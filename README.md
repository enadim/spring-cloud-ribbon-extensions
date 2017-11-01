# Spring Cloud Ribbon Extensions
[![Build Status](https://travis-ci.org/enadim/spring-cloud-ribbon-extensions.svg?branch=master)](https://travis-ci.org/enadim/spring-cloud-ribbon-extensions)[![codecov](https://codecov.io/gh/enadim/spring-cloud-ribbon-extensions/branch/master/graph/badge.svg)](https://codecov.io/gh/enadim/spring-cloud-ribbon-extensions)


### Requirements
* spring cloud eureka.
* spring cloud ribbon.
* optional spring cloud zuul.
* optional spring cloud hystrix

## Features

### Favorite Zone Routing.
use @EnableFavoriteZone to enable routing to a favorite zone.

### Strict Metadata Routing
use @EnableStrictMetadataMatcher to enable routing to servers that have the same context metadata.

### Context Propagation
use @EnableContextPropagation to enable desired http headers flowing between all your micro-services.

### Combine Favorite Zone Routing & Context Propagation
Eureka!
* We are able to test our micro-service without running all the application services only by adding our zone to http request header.
* We are able to deploy in multi region and let our clients (that have no knowledge of eureka, ribbon, zuul) choose the nearest zone.

## Setup

maven
```xml
<dependency>
  <groupId>com.github.enadim</groupId>
  <artifactId>spring-cloud-ribbon-extensions</artifactId>
  <version>1.0.0</version>
</dependency>
```

gradle
```gradle
dependencies {
    compile 'com.github.enadim:spring-cloud-ribbon-extensions:1.0.0'
}
```

## License

[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)