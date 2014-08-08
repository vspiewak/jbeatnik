JBeatnik
========

Sample
------
curl -i -X POST -d username=admin -d password=admin -c /tmp/cookies.txt http://localhost:9000/auth/login
curl -i -X GET -b /tmp/cookies.txt http://localhost:9000/user
curl -i -X GET -b /tmp/cookies.txt http://localhost:9000/auth/logout

Docs
----
http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle
http://docs.spring.io/spring-security/site/docs/4.0.0.M1/reference/htmlsingle
http://www.baeldung.com/2011/10/31/securing-a-restful-web-service-with-spring-security-3-1-part-3
https://github.com/jhipster
