var pairs =
{
"php":{"code":1}
,"code":{"samples":1}
,"samples":{"<php":1}
,"<php":{"$ch":1,"$url":1}
,"$ch":{"=curl_init":1}
,"=curl_init":{"curl_setopt":1}
,"curl_setopt":{"($ch":1}
,"($ch":{"curlopt_url":1,"curlopt_header":1,"curlopt_verbose":1,"curlopt_put":1,"curlopt_returntransfer":1,"curlopt_infile":1,"curlopt_infilesize":1,"curlinfo_http_code)":1}
,"curlopt_url":{"http:\u002F\u002Flocalhost:8080\u002Fehcache\u002Frest\u002Fsamplecache2\u002F3":1,"$url)":1}
,"http:\u002F\u002Flocalhost:8080\u002Fehcache\u002Frest\u002Fsamplecache2\u002F3":{"curl_setopt":1,"$localfile":1,"<content-length":1}
,"curlopt_header":{"curl_exec":1}
,"curl_exec":{"($ch)":1}
,"($ch)":{"curl_close":1,"server":1,"$error":1,"$http_code":1,"fclose":1}
,"curl_close":{"($ch)":1}
,"server":{"responds":1}
,"responds":{"hello":1,"connect":1}
,"hello":{"ingo":1}
,"ingo":{"put":1}
,"put":{"<php":1,"\u002Fehcache\u002Frest\u002Fsamplecache2\u002F3":1}
,"$url":{"http:\u002F\u002Flocalhost:8080\u002Fehcache\u002Frest\u002Fsamplecache2\u002F3":1}
,"$localfile":{"localfile.txt":1}
,"localfile.txt":{"$fp":1}
,"$fp":{"=fopen":1}
,"=fopen":{"($localfile":1}
,"($localfile":{"$ch":1}
,"curlopt_verbose":{"curl_setopt":1}
,"$url)":{"curl_setopt":1}
,"curlopt_put":{"curl_setopt":1}
,"curlopt_returntransfer":{"curl_setopt":1}
,"curlopt_infile":{"$fp)":1}
,"$fp)":{"curl_setopt":1}
,"curlopt_infilesize":{"filesize":1}
,"filesize":{"($localfile))":1}
,"($localfile))":{"$http_result":1}
,"$http_result":{"=curl_exec":1}
,"=curl_exec":{"($ch)":1}
,"$error":{"=curl_error":1}
,"=curl_error":{"($ch)":1}
,"$http_code":{"=curl_getinfo":1,"print":1}
,"=curl_getinfo":{"($ch":1}
,"curlinfo_http_code)":{"curl_close":1}
,"fclose":{"($fp)":1}
,"($fp)":{"print":1}
,"print":{"$http_code":1,"<br":1}
,"<br":{"<br":1,"\u002F>$http_result":1,"\u002F>$error":1}
,"\u002F>$http_result":{"($error)":1}
,"($error)":{"{print":1}
,"{print":{"<br":1}
,"\u002F>$error":{"server":1}
,"connect":{"localhost":1}
,"localhost":{"port":1,":1)":1,"left":1}
,"port":{"8080":1}
,"8080":{"trying":1,"put":1}
,"trying":{"*connected":1}
,"*connected":{"connected":1}
,"connected":{"localhost":1}
,":1)":{"port":1}
,"\u002Fehcache\u002Frest\u002Fsamplecache2\u002F3":{"http\u002F1.1":1}
,"http\u002F1.1":{"host":1}
,"host":{"localhost:8080":1,"localhost":1}
,"localhost:8080":{"accept":1}
,"accept":{"*\u002F*content-length":1}
,"*\u002F*content-length":{"expect":1}
,"expect":{"100-continue":1}
,"100-continue":{"<http\u002F1.1":1}
,"<http\u002F1.1":{"100":1,"201":1}
,"100":{"continue":1}
,"continue":{"<http\u002F1.1":1}
,"201":{"created":1}
,"created":{"<location":1}
,"<location":{"http:\u002F\u002Flocalhost:8080\u002Fehcache\u002Frest\u002Fsamplecache2\u002F3":1}
,"<content-length":{"<server":1}
,"<server":{"jetty":1}
,"jetty":{"(6.1.10)":1}
,"(6.1.10)":{"connection":1}
,"connection":{"host":1}
,"left":{"intact":1}
,"intact":{"closing":1}
,"closing":{"connection":1}
}
;Search.control.loadWordPairs(pairs);
