# Spring Boot 日記API

## 技術要素

| feature         | version | description            |
|-----------------|---------|------------------------|
| Java            | 21      | プログラミング言語              |
| Spring Boot     | 3.4.0   | フルスタックフレームワーク          |
| Spring Boot JPA | 3.4.0   | データベース操作のライブラリ/フレームワーク |
| MySQL           | 8.0.40  | リレーショナルデータベース          |
| Postman         | 11.23.1 | API開発ツール               |

## 環境構築手順

### git clone手順

```bash
# プロジェクトを配置するディレクトリに移動しておく
git clone git@github.com:Genki1019/springboot-diary.git
cd springboot-diary
```

### MySQL起動手順

```bash
# Windows
net start mysql

# macOS/Linux
sudo systemctl start mysql
```

### アプリケーション起動手順

```bash
./mvnw spring-boot:run
```

## URL設計

| URL                 | Method | Description   | Status Code    |
|---------------------|--------|---------------|----------------|
| /diary/             | POST   | 日記登録API（1件）   | 201 Created    |
| /diary/{日記ID}       | GET    | 日記取得API（1件）   | 200 OK         |
| /diary?title=value  | GET    | 日記取得API（複数件）  | 200 OK         |
| /diary/{日記ID}/image | GET    | 日記画像取得API（1件） | 200 OK         |
| /diary/{日記ID}       | PUT    | 日記更新API（1件）   | 200 OK         |
| /diary/{日記ID}       | DELETE | 日記削除API（1件）   | 204 No Content |

## DB設計

| type    | database name | table name |
|---------|---------------|------------|
| logical |               | 日記テーブル     |
| logical | spring_dev    | diary      |

| logical | physical   | type          | UN | NN | PK | UQ | ZF | AI | default                                               |
|---------|------------|---------------|----|----|----|----|----|----|-------------------------------------------------------|
| ID      | id         | bigint        | o  | o  | o  |    |    | o  |                                                       |
| タイトル    | title      | varchar(100)  |    | o  |    | 1  |    |    |                                                       |
| 本文      | content    | varchar(1000) |    | o  |    |    |    |    |                                                       |
| 画像パス    | image_path | text          |    | x  |    |    |    |    |                                                       |
| 作成日時    | created_at | datetime      |    | o  |    |    |    |    | default current_timestamp                             |
| 更新日時    | updated_at | datetime      |    | o  |    |    |    |    | default current_timestamp on update current_timestamp |