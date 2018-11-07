create table sources (
  id bigserial not null primary key,
  description varchar(256) default '' not null,
  rss_url varchar(256) default '' not null
);

insert into sources values (1, 'tjournal', 'https://tjournal.ru/rss/all');
insert into sources values (2, 'meduza', 'https://meduza.io/rss/all');

alter table items rename column text to description;
alter table items add column pub_date timestamp not null default CURRENT_DATE;
alter table items add column source_id bigserial, add constraint fk_sources foreign key (source_id) references sources (id);
alter table items add column html_content varchar(65536) not null default '';
