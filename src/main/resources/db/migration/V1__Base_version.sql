create table items (
  id bigserial not null primary key,
  title varchar(256) default '' not null,
  text  varchar(16384) default '' not null,
  link  varchar(256) default '' not null
);
