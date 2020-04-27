# Zephyr/Zodiac
A pair of Springboot services demonstrating Hashi Corp.'s Consul.
Consul is a set of tools that enable services to be run in a _service _mesh_, allowing for flexible and scalable
operations.

The example focuses on three of those features, using the Springboot 'cloud' framework:
  * Service registration and discovery
  * Health monitoring
  * Distribution/dynamic configuration
  
Not discussed here, but important ingredients of a full service mesh implementation:
  * Secure proxys, service talk http to proxy (sidecar) which translates to TLS for inter-service comms
  * Multi DCs: transparently communication between not just nodes, but also datacenters
  
## Example Architecture
Two services are deployed in the example, one of which (`zodiac-service`) relies on an api exposed by the second
(`zephyr-service`).  When a call is made to `zodiac-service`'s API, it satisfies it by finding a URL for `zephyr-service` 
and calling the endpoint.

For convenience, `zodiac-service` runs on a fixed localhost port, 8080, while `zephyr-service` has random port assigned
when started.

Both services leverages the Springboot cloud framework to register themselves, and discover other services. They also 
provide endpoints that are utilized by Consul's health checker and proxy algorithms.

Additionally, `zephyr-service` uses Consul KV store to access configuration data dynamically to satisfy the API call.



```$bash
 consul agent -dev

 curl localhost:8500/v1/agent/services

 curl localhost:8500/v1/agent/checks

 consul kv put

 curl localhost:8500/v1/agent/checks

curl --request PUT --data 'America/Los_Angeles' localhost:8500/v1/kv/config/zephyr-service/service/timezone

curl localhost:8500/v1/kv/zephyr-service

```

## Distributed Configuration
With the Zephyr service started, use either the `consul` CLI, it's HTTP interface, or via the consul agent web page
 (e.g, http://localhost:8500/ui/dc1/services) to set key `/config/zephyr-service/zephyr/response` to the required value.

This will override the default value from `resources/zephyr-application.yml`:
```yaml
service:
  timezone: 'America/Los_Angeles'
```
forcing the service's APi controller to be reloaded, with the new value:
```java
@RestController
@RefreshScope
public class ApiController {

    @Value("${service.timezone}")
    private String value;

    @GetMapping("/call")
    public String call() {
        return value;
    }
}
```