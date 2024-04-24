create table person (
    id bigint not null auto_increment,
    name varchar(255) not null,
    constraint pk_person primary key (id)
);

create table person_parents (
    parent_id bigint not null,
    person_id bigint not null,
    constraint pk_person_parents primary key (parent_id, person_id)
);

alter table person_parents add constraint fk_personparent_on_parent foreign key (parent_id) references person (id);
alter table person_parents add constraint fk_personparent_on_person foreign key (person_id) references person (id);