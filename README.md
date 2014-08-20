JBeatnik
========

Sample
------

    curl -i http://localhost:9000/manage/health
    
    curl -vu jbeatnikapp:myOAuthSecret 'http://localhost:9000/oauth/token?username=admin&password=admin&grant_type=password'    
    curl -vu jbeatnikapp:myOAuthSecret 'http://localhost:9000/oauth/token?grant_type=refresh_token&refresh_token=<refresh_token>'
    
    curl -i -H "Authorization: Bearer <access_token>" http://localhost:9000/api/user
    curl -i -H "Authorization: Bearer <access_token>" http://localhost:9000/api/me
    curl -i -H "Authorization: Bearer <access_token>" http://localhost:9000/manage/metrics
    curl -i -H "Authorization: Bearer <access_token>" http://localhost:9000/oauth/logout

Docs
----
* http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle
* http://docs.spring.io/spring-security/site/docs/4.0.0.M1/reference/htmlsingle
* http://www.baeldung.com/2011/10/31/securing-a-restful-web-service-with-spring-security-3-1-part-3
* https://github.com/jhipster
