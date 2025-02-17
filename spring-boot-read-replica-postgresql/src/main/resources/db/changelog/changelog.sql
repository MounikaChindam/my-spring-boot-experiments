--liquibase formatted sql

--changeset jlong:1
--preconditions onFail:MARK_RAN onError:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(*)   from information_schema.tables where table_name = 'articles' ;

--  HALT, CONTINUE, MARK_RAN, WARN

create table   articles
(
    id       serial primary key,
    title    varchar(255) not null,
    authored timestamp    not null
);

--changeset jlong:2
--preconditions onFail:MARK_RAN, onError:MARK_RAN,
--precondition-sql-check expectedResult:0 SELECT COUNT(*) from information_schema.tables where table_name = 'comments' ;

create table   comments
(
    id         serial primary key,
    comment    varchar(255) not null,
    article_id serial,
    constraint article_fk foreign key (article_id) references articles (id)
);
--rollback drop table comments, articles;

--changeset jlong:3
--preconditions onFail:MARK_RAN, onError:MARK_RAN,
--precondition-sql-check expectedResult:0 SELECT count(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'articles' and column_name = 'published';
alter table articles add column published timestamp null;
--rollback alter table articles drop column published;

--changeset raja:4
--preconditions onFail:MARK_RAN, onError:MARK_RAN,
CREATE SEQUENCE IF NOT EXISTS articles_seq
    INCREMENT 50
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1
    OWNED BY articles.id;
--rollback DROP SEQUENCE IF EXISTS articles_seq;

--changeset raja:5
--preconditions onFail:MARK_RAN, onError:MARK_RAN,
CREATE SEQUENCE IF NOT EXISTS comment_seq
    INCREMENT 50
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1
    OWNED BY comments.id;
--rollback DROP SEQUENCE IF EXISTS comment_seq;

--changeset jlong:6
--preconditions onFail:MARK_RAN, onError:MARK_RAN,
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM articles;
insert into articles( title, authored, published) values( 'Waiter! There is a bug in my JSoup!' , now(), null);
insert into articles( title, authored, published) values( 'Beat the Queue with Apache Kafka' , now(), null);
insert into articles( title, authored, published) values( 'How I Stopped Worrying and Learned to Devops My SQL' , now(), null);
insert into comments (comment, article_id)  values ('first!' , (select max(id) from articles));
insert into comments (comment, article_id)  values ('first!' , (select min(id) from articles));
insert into comments (comment, article_id)  values ('i came here to say that.' , (select min(id) from articles));
--rollback truncate comments, articles;

--changeset raja:7
--preconditions onFail:MARK_RAN, onError:MARK_RAN,
GRANT SELECT ON ALL TABLES IN SCHEMA public TO repl_user;