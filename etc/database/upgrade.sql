alter table tw_permission drop column permission_orphanedquotebundle;

alter table tw_userstate add column userstate_allquotebundle text;

create unique index fki_userstate_allquotebundle on tw_userstate using btree (userstate_allquotebundle);
