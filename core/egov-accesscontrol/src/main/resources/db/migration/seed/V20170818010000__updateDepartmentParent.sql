update service set parentmodule =(select id from service where code='ADMIN' and tenantid='default') where code='DEPT' and tenantid='default';