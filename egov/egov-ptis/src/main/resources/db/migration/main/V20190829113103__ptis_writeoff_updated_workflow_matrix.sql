---WriteOff

delete from eg_wf_matrix where objecttype = 'WriteOff';


--Revenue Officer
INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Revenue Officer Approved', NULL, 'Assistant Commissioner Approval Pending', 'Assistant Commissioner', 'NULL', 'Assistant Commissioner Approved', 'Digital Signature Pending', 'Zonal Commissioner,Deputy Commissioner,Additional Commissioner,Commissioner','Assistant Commissioner Approved', 'Forward,Approve,Reject', NULL, NULL, '2015-04-01', '2099-04-01');


INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Revenue Officer Approved', NULL, 'Zonal Commissioner Approval Pending', 'Zonal Commissioner', 'NULL', 'Zonal Commissioner Approved', 'Digital Signature Pending', 'Deputy Commissioner,Additional Commissioner,Commissioner','Assistant Commissioner Approved', 'Forward,Approve,Reject', NULL, NULL, '2015-04-01', '2099-04-01');


INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Revenue Officer Approved', NULL, 'Deputy Commissioner Approval Pending', 'Deputy Commissioner', 'NULL', 'Deputy Commissioner Approved', 'Digital Signature Pending', 'Additional Commissioner,Commissioner','Deputy Commissioner Approved', 'Forward,Approve,Reject', NULL, NULL, '2015-04-01', '2099-04-01');


INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Revenue Officer Approved', NULL, 'Additional Commissioner Approval Pending', 'Additional Commissioner', 'NULL', 'Additional Commissioner Approved', 'Digital Signature Pending', 'Commissioner','Additional Commissioner Approved', 'Forward,Approve,Reject', NULL, NULL, '2015-04-01', '2099-04-01');


INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Revenue Officer Approved', NULL, 'Commissioner Approval Pending', 'Commissioner', 'NULL', 'Commissioner Approved', 'Digital Signature Pending', 'Commissioner','Commissioner Approved', 'Approve,Reject', NULL, NULL, '2015-04-01', '2099-04-01');




--Assistant Commissioner
INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Assistant Commissioner Approved', NULL, 'Zonal Commissioner Approval Pending', 'Zonal Commissioner', 'NULL', 'Zonal Commissioner Approved', 'Digital Signature Pending', 'Deputy Commissioner,Additional Commissioner,Commissioner','Assistant Commissioner Approved', 'Forward,Approve,Reject', NULL, NULL, '2015-04-01', '2099-04-01');


INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Assistant Commissioner Approved', NULL, 'Digital Signature Pending', 'Zonal Commissioner', 'NULL', 'Zonal Commissioner Approved', NULL, 'Deputy Commissioner,Additional Commissioner,Commissioner',NULL, 'Forward,Preview,Sign', NULL, NULL, '2015-04-01', '2099-04-01');


INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Assistant Commissioner Approved', NULL, 'Deputy Commissioner Approval Pending', 'Deputy Commissioner', 'NULL', 'Deputy Commissioner Approved', 'Digital Signature Pending', 'Additional Commissioner,Commissioner','Deputy Commissioner Approved', 'Forward,Approve,Reject', NULL, NULL, '2015-04-01', '2099-04-01');


INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Assistant Commissioner Approved', NULL, 'Digital Signature Pending', 'Deputy Commissioner', 'NULL', 'Deputy Commissioner Approved', NULL, 'Additional Commissioner,Commissioner',NULL, 'Forward,Preview,Sign', NULL, NULL, '2015-04-01', '2099-04-01');


INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Assistant Commissioner Approved', NULL, 'Additional Commissioner Approval Pending', 'Additional Commissioner', 'NULL', 'Additional Commissioner Approved', 'Digital Signature Pending', 'Commissioner','Additional Commissioner Approved', 'Forward,Approve,Reject', NULL, NULL, '2015-04-01', '2099-04-01');


INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Assistant Commissioner Approved', NULL, 'Digital Signature Pending', 'Assistant Commissioner', 'NULL', 'Assistant Commissioner Approved', NULL, 'Zonal Commissioner,Deputy Commissioner,Additional Commissioner,Commissioner',NULL, 'Forward,Preview,Sign', NULL, NULL, '2015-04-01', '2099-04-01');


INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Assistant Commissioner Approved', NULL, 'Commissioner Approval Pending', 'Commissioner', 'NULL', 'Commissioner Approved', 'Digital Signature Pending', 'Commissioner','Commissioner Approved', 'Forward,Approve,Reject', NULL, NULL, '2015-04-01', '2099-04-01');


INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Assistant Commissioner Approved', NULL, 'Digital Signature Pending', 'Additional Commissioner', 'NULL', 'Additional Commissioner Approved', NULL, 'Commissioner',NULL, 'Forward,Preview,Sign', NULL, NULL, '2015-04-01', '2099-04-01');



----Zonal Commissioner
INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Zonal Commissioner Approved', NULL, 'Digital Signature Pending', 'Deputy Commissioner', 'NULL', 'Deputy Commissioner Approved', NULL, 'Additional Commissioner,Commissioner',NULL, 'Forward,Preview,Sign', NULL, NULL, '2015-04-01', '2099-04-01');


INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Zonal Commissioner Approved', NULL, 'Deputy Commissioner Approval Pending', 'Deputy Commissioner', 'NULL', 'Deputy Commissioner Approved', 'Digital Signature Pending', 'Additional Commissioner,Commissioner',NULL, 'Forward,Approve,Reject', NULL, NULL, '2015-04-01', '2099-04-01');


INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Zonal Commissioner Approved', NULL, 'Digital Signature Pending', 'Additional Commissioner', 'NULL', 'Additional Commissioner Approved', NULL, 'Commissioner',NULL, 'Forward,Preview,Sign', NULL, NULL, '2015-04-01', '2099-04-01');


INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Zonal Commissioner Approved', NULL, 'Additional Commissioner Approval Pending', 'Additional Commissioner', 'NULL', 'Additional Commissioner Approved', 'Digital Signature Pending', 'Commissioner',NULL, 'Forward,Approve,Reject', NULL, NULL, '2015-04-01', '2099-04-01');


INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Zonal Commissioner Approved', NULL, 'Commissioner Approval Pending', 'Commissioner', 'NULL', 'Commissioner Approved', 'Digital Signature Pending', 'Commissioner',NULL, 'Forward,Approve,Reject', NULL, NULL, '2015-04-01', '2099-04-01');


INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Zonal Commissioner Approved', NULL, 'Digital Signature Pending', 'Commissioner', 'NULL', 'Commissioner Approved', NULL, 'Commissioner',NULL, 'Forward,Preview,Sign', NULL, NULL, '2015-04-01', '2099-04-01');


INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Zonal Commissioner Approved', NULL, 'Digital Signature Pending', 'Zonal Commissioner', 'NULL', 'Zonal Commissioner Approved', NULL, 'Deputy Commissioner,Additional Commissioner,Commissioner',NULL, 'Forward,Preview,Sign', NULL, NULL, '2015-04-01', '2099-04-01');



----Deputy Commissioner
INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Deputy Commissioner Approved', NULL, 'Additional Commissioner Approval Pending', 'Additional Commissioner', 'NULL', 'Additional Commissioner Approved', 'Digital Signature Pending', 'Commissioner','Additional Commissioner Approved', 'Forward,Approve,Reject', NULL, NULL, '2015-04-01', '2099-04-01');


INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Deputy Commissioner Approved', NULL, 'Digital Signature Pending', 'Additional Commissioner', 'NULL', 'Additional Commissioner Approved', NULL, 'Commissioner',NULL, 'Forward,Preview,Sign', NULL, NULL, '2015-04-01', '2099-04-01');


INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Deputy Commissioner Approved', NULL, 'Digital Signature Pending', 'Deputy Commissioner', 'NULL', 'Deputy Commissioner Approved', NULL, 'Additional Commissioner,Commissioner',NULL, 'Forward,Preview,Sign', NULL, NULL, '2015-04-01', '2099-04-01');


INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Deputy Commissioner Approved', NULL, 'Commissioner Approval Pending', 'Commissioner', 'NULL', 'Commissioner Approved', 'Digital Signature Pending', 'Commissioner','Commissioner Approved', 'Approve,Reject', NULL, NULL, '2015-04-01', '2099-04-01');




--Additional Commissioner
INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Additional Commissioner Approved', NULL, 'Commissioner Approval Pending', 'Commissioner', 'NULL', 'Commissioner Approved', 'Digital Signature Pending', 'Commissioner','Commissioner Approved', 'Approve,Reject', NULL, NULL, '2015-04-01', '2099-04-01');


INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Additional Commissioner Approved', NULL, 'Digital Signature Pending', 'Additional Commissioner', 'NULL', 'Additional Commissioner Approved', NULL, 'Commissioner',NULL, 'Forward,Preview,Sign', NULL, NULL, '2015-04-01', '2099-04-01');



--Commissioner
INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Commissioner Approved', NULL, 'Digital Signature Pending', 'Commissioner', 'NULL', 'Digitally Signed', 'Notice Print Pending', NULL, NULL, 'Preview,Sign', NULL, NULL, '2015-04-01', '2099-04-01');


INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Digitally Signed', NULL, 'Notice Print Pending', 'Senior Assistant,Junior Assistant', 'NULL', 'END', 'END', NULL, NULL, 'Generate Notice', NULL, NULL, '2015-04-01', '2099-04-01');


--Reject and common
INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Rejected', NULL, 'Revenue Officer Approval Pending', 'Revenue Officer', NULL, 'Revenue Officer Approved', NULL, 'Assistant Commissioner,Zonal Commissioner,Deputy Commissioner,Additional Commissioner,Commissioner', 'Revenue Officer Approved', 'Forward,Reject', NULL, NULL, now(), '2099-04-01');

INSERT INTO eg_wf_matrix (id, department, objecttype, currentstate, currentstatus, pendingactions, currentdesignation, additionalrule, nextstate, nextaction, nextdesignation, nextstatus, validactions, fromqty, toqty, fromdate, todate) VALUES (NEXTVAL('seq_eg_wf_matrix'), 'ANY', 'WriteOff', 'Created', NULL, NULL, NULL, NULL, 'Revenue Officer Approved', NULL, 'Assistant Commissioner,Zonal Commissioner,Deputy Commissioner,Additional Commissioner,Commissioner', 'Revenue Officer Approved', 'Forward', NULL, NULL, now(), '2099-04-01');


