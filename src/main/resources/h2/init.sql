drop table if exists core_custom_query;
drop table if exists core_custom_query_text;
drop table if exists core_custom_query_log;
drop table if exists core_custom_query_log_text;

create table core_custom_query
(
	sql_id bigint NOT NULL AUTO_INCREMENT,
	title varchar(128),
	remark varchar(512),
	create_time datetime,
	update_time datetime,
	PRIMARY KEY (sql_id)
);
create table core_custom_query_text
(
	text_id bigint NOT NULL AUTO_INCREMENT,
    sql_id bigint,
    text_type varchar(64),
	text_detail text,
	PRIMARY KEY (text_id)
);
create table core_custom_query_log
(
	log_id bigint NOT NULL AUTO_INCREMENT,
	sql_id bigint,
	create_by varchar(64),
	create_time datetime,
	result text,
	PRIMARY KEY (log_id)
);
create table core_custom_query_log_text
(
	text_id bigint NOT NULL AUTO_INCREMENT,
	log_id bigint,
	sql_text text,
	params text,
	result text,
	PRIMARY KEY (text_id)
);