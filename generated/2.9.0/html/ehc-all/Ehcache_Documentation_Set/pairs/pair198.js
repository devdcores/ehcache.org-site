var pairs =
{
"monitoring":{"cache":1,"mbeanserver":1}
,"cache":{"server":1}
,"server":{"cache":1,"registers":1,"vendor":1}
,"registers":{"ehcache":1}
,"ehcache":{"mbeans":1}
,"mbeans":{"platform":1}
,"platform":{"mbeanserver":1}
,"mbeanserver":{"remote":1,"responsibility":1}
,"remote":{"monitoring":1}
,"responsibility":{"web":1}
,"web":{"container":1}
,"container":{"application":1}
,"application":{"server":1}
,"vendor":{"example":1}
,"example":{"instructions":1}
,"instructions":{"tomcat":1}
,"tomcat":{"product":1}
,"product":{"documentation":1}
,"documentation":{"web":1}
}
;Search.control.loadWordPairs(pairs);
