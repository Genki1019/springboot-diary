create table if not exists diary
(
  id bigint unsigned not null auto_increment comment 'ID',
  title varchar(100) not null comment 'タイトル',
  content varchar(1000) not null comment '本文',
  image_path text comment '画像パス',
  created_at datetime not null default current_timestamp comment '作成日時',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新日時',
  primary key (id),
  unique key (title)
) engine = innodb
  charset utf8mb4
  collate utf8mb4_bin comment '日記テーブル';