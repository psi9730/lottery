create table lottery_missions
(
    id                bigint auto_increment
        primary key,
    max_daily_count   int     not null,
    max_reward_amount bigint  not null,
    type              tinyint not null unique
);

create table users
(
    id           varchar(255) not null
        primary key,
    email        varchar(50) not null,
    user_name         varchar(50) not null,
    phone_number varchar(20) not null unique
);

create table lottery_mission_coins
(
    id          bigint auto_increment
        primary key,
    amount      bigint       not null,
    amount_type tinyint      not null,
    user_id     varchar(255) not null
);

create index lottery_mission_coins_user_id_amount_index
    on lottery_mission_coins (user_id, amount);

create table lottery_mission_records
(
    id                 bigint auto_increment
        primary key,
    completed_at       bigint       null,
    created_at         bigint       null,
    status             tinyint      null,
    lottery_mission_id bigint       not null,
    user_id            varchar(255) not null,
    check (`status` between 0 and 1)
);

create index lottery_mission_records_user_id_completed_at_index
    on lottery_mission_records (user_id asc, lottery_mission_id asc, completed_at desc, created_at desc);

create index lottery_mission_records_user_id_created_at_index
    on lottery_mission_records (user_id asc, lottery_mission_id asc, created_at desc, completed_at desc);

create index users_id_full_index
    on users (id, phone_number, email, user_name);

