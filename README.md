
# Altcha Spring boot

A simple captcha library for Spring boot based on the open source project [Altcha](https://altcha.org/)

Compatible with:

    - Spring boot 3.5.5
    - Java 17+

## Installation

For now you have to download the jar and install manually in your Maven project.

1. Add the library to your Maven pom.xml file:

```bash
mvn install:install-file   
    -Dfile=altcha-spring-boot-1.0.0.jar   
    -DgroupId=com.nandaleio.altcha   
    -DartifactId=altcha-spring-boot   
    -Dversion=1.0.0   
    -Dpackaging=jar
```

2. Add in your pom.xml as dependency:

```xml
<dependency>
    <groupId>com.nandaleio</groupId>
    <artifactId>altcha-spring-boot</artifactId>
    <version>1.0.0</version>
</dependency>
```

3. Finally run `mvn clean install`.


## Usage

### Basic Setup

Configure the properties in your `application.properties` or `application.yml` (only `hmac-key` is required)

```properties
altcha-spring-boot.hmac-key=your-secret-key
altcha-spring-boot.max-number=100000
altcha-spring-boot.expiration-in-seconds=1200
altcha-spring-boot.api-endpoint=/api/captcha
```


| Property | Default | Description |
|----------|---------|-------------|
| `altcha-spring-boot.hmac-key` | - | Secret key for HMAC signing (required) |
| `altcha-spring-boot.max-number` | 100000 | Maximum number for challenge generation |
| `altcha-spring-boot.expiration-in-seconds` | 1200 | Challenge expiration time in seconds |
| `altcha-spring-boot.api-endpoint` | `/api/captcha` | Endpoint for challenge generation |

### Protecting Endpoints

Use the `@RequireAltcha` annotation on controllers or methods that need captcha protection:

```java
@RestController
@RequireAltcha // <-- either HERE
public class MyController {
    
    @PostMapping("/protected-endpoint")
    @RequireAltcha  // <-- or HERE
    public ResponseEntity<String> protectedMethod() {
        return ResponseEntity.ok("Success! Captcha validated.");
    }
}
```

### Frontend Integration

The library automatically exposes a captcha endpoint that generates challenges. Your frontend can fetch challenges from the configured endpoint (default: `/api/captcha`) and submit the captcha payload with form submissions.

Include in the `head` of your HTML page
```html
<script async defer src="{YOUR BASE HREF}/altcha.min.js" type="module"></script>
```

Then add the component inside the form you want to protect against the bots:
```html
<altcha-widget challengeurl='{YOUR BASE HREF/api/captha}'></altcha-widget>
```

For more information about the configration of the frontend widget please refer to the [Altcha  widget documentation](https://altcha.org/docs/v2/widget-integration/) 

### Validating the form

The form require the challenge result to be included either in :

- the query params
- the form data 
- the header `ALTCHA-PAYLOAD`

---

## Roadmap

- [ ] More configration (eg: the header to store the challenge result)
- [ ] Handle more request type
- [ ] Release to Nexus
- [x] ~~Logging~~