create table lottery_missions
(
    id                bigint auto_increment
        primary key,
    max_daily_count   int                                                             not null,
    max_reward_amount bigint                                                          not null,
    type              enum ('ATTENDANCE', 'KAKAO_SHARE', 'VISIT_COUPANG', 'WATCH_AD') not null unique
);

create table lottery_rounds
(
    id                bigint auto_increment
        primary key,
    bonus_number      int          null,
    drw_no_date       datetime(6)  not null,
    first_win_amount  bigint       null,
    first_win_count   int          null,
    numbers           varchar(255) null,
    round             int          not null unique,
    total_sell_amount bigint       null
);

create index lottery_rounds_round
    on lottery_rounds (round desc, drw_no_date desc);

create table shedlock
(
    name       varchar(64)  not null
        primary key,
    lock_until timestamp    not null,
    locked_at  timestamp    not null,
    locked_by  varchar(255) null
);

create table users
(
    id           varchar(255) not null
        primary key,
    email        varchar(50)  not null,
    phone_number varchar(20)  not null unique,
    user_name    varchar(50)  not null
);

create index users_id_full_index
    on users (id, phone_number, email, user_name);

create table lottery_mission_coins
(
    id          bigint auto_increment
        primary key,
    amount      bigint       not null,
    amount_type              enum ('MINUS', 'PLUS') not null,
        user_id     varchar(255) not null,
    created_at         datetime(6)                            not null
);

create index lottery_mission_coins_full_index
    on lottery_mission_coins (user_id, created_at desc, amount, amount_type, id);

create table lottery_mission_records
(
    id                 bigint auto_increment
        primary key,
    completed_at       datetime(6)                            null,
    created_at         datetime(6)                            not null,
    status             enum ('COMPLETED', 'COMPLETE_WAITING') not null,
    lottery_mission_id bigint                                 not null,
    user_id            varchar(255)                           not null
);

create index lottery_mission_records_user_id_completed_at_index
    on lottery_mission_records (user_id asc, lottery_mission_id asc, completed_at desc, created_at desc);

create index lottery_mission_records_user_id_created_at_index
    on lottery_mission_records (user_id asc, lottery_mission_id asc, created_at desc, completed_at desc);

create table lottery_user_draws
(
    id                          bigint auto_increment
        primary key,
    created_at                  datetime(6)  not null,
    is_rewarded                 bit          not null,
    numbers                     varchar(255) null,
    lottery_round               bigint       null,
    user_id                     varchar(255) not null
);

create index lottery_user_draws_lottery_round
    on lottery_user_draws (user_id, lottery_round desc, created_at desc)
