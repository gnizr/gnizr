DELETE FROM tag_prpt;
INSERT INTO tag_prpt(id,name,ns_prefix,description,prpt_type,cardinality) VALUES 
 (1,'related','skos','SKOS related','default',-1),
 (2,'broader','skos','SKOS broader','default',-1),
 (3,'narrower','skos','SKOS narrower','default',-1),
 (4,'type','rdf','RDF type','default',-1),
 (5,'lat/lng','geo','Geo latitude and longitude','spatial',1),
 (6,'format','dc','Dublin Core dc:format','default',1),
 (7,'created','dc','Dublin Core dc:created','temporal',1),
 (8,'for','gn','gnizr links-for-you','system',-1),
 (9,'subscribe','gn','gnizr do feed subscribe','system',1),
 (10,'wikiped','gn','reference wikipedia','reference',-1),
 (11,'icd10','gn','reference ICD10','reference',-1);