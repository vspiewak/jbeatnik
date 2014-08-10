JBeatnik
========

Sample
------

    curl -i http://localhost:9000/manage/health
    
    curl -i -X POST -H "Authorization: Basic amJlYXRuaWthcHA6bXlPQXV0aFNlY3JldA==" 'http://localhost:9000/oauth/token?username=admin&password=admin&grant_type=password'
    curl -H "Authorization: Basic amJlYXRuaWthcHA6bXlPQXV0aFNlY3JldA==" 'http://localhost:9000/oauth/token?grant_type=refresh_token&refresh_token=fe4bef09-f093-4879-b307-470b20e9f2bf'
    curl -i -H "Authorization: Bearer 5f89a812-fdc3-40f1-afd3-20bfd9b25edb" http://localhost:9000/user
    curl -i -H "Authorization: Bearer 5f89a812-fdc3-40f1-afd3-20bfd9b25edb" http://localhost:9000/oauth/logout

Docs
----
* http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle
* http://docs.spring.io/spring-security/site/docs/4.0.0.M1/reference/htmlsingle
* http://www.baeldung.com/2011/10/31/securing-a-restful-web-service-with-spring-security-3-1-part-3
* https://github.com/jhipster
