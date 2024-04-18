# APIの概要

idとnameのCRUD処理をするAPIを作成しました。MyBatisでCRUD処理と、指定したIDがない時・バリデーションエラー用の例外処理を実装しています。Spring Testを使った単体テスト、Database Riderを使ったDBテスト、MocbMvcを使った結合テスト、Github ActionsでのCIを実装しています。DockerでMySQLコンテナを立ち上げて使います。スクリーンショットはAWSのEC2とRDSを接続してデプロイしたときのものを使用しています。

---
## 構成要件
* Java 17
* Spring Framework 3.0.1
* MyBatis
* MySQL 8.0.32
* Docker Desktop 4.17.1
* JUnit 5.9.1
* Mockito
* Database Rider
* MockMvc
* GitHub Actions
---
# 起動手順
```  
docker compose up  
```  
で起動  
```  
docker compose down  
``` 
で停止  

---
# DBテーブル

|カラム名（論理名）|カラム名（物理名）|型・桁|Nullable|その他コメント|
|---|---|---|---|---|
|ID|id|int|NO|primary key, auto_increment|
|名前|name|varchar(20)|NO|null、空文字、全て半角・全角スペース、タブは受け付けない  

---

# URL設計
| 機能     | メソッド名 | HTTPリクエストの種類 | URL          | 
|-------------| ------------ |-----------------|-----------------|
| レコード一覧取得     | findAll() |GET|http\://localhost:8080/users      |
| 指定したIDのレコードを取得 | findById(int id) |GET|  http\://localhost:8080/users/{id}  |
| レコードの新規登録 | createUser(CreateForm form) |POST| http\://localhost:8080/users |
| 指定したレコードのnameの更新 | updateName(int id, UpdateForm form) |PATCH| http\://localhost:8080/users/{id} |
| レコードの削除 | deleteUser(int id) |DELETE| http\://localhost:8080/users/{id} |

---
# スクリーンショットと例外処理


*起動時

![スクリーンショット (346)](https://github.com/Horikawa-1/CRUD-API/assets/111167638/80c61180-9b4d-40dc-a190-0218567b71c0)


* GETでhttp\://localhost:8080/usersでUserControllerクラスのgetUsersメソッドより、データベースの全レコードが返されます。

![スクリーンショット (330)](https://github.com/Horikawa-1/CRUD-API/assets/111167638/3fae3b3d-b1fa-4182-9b05-fc97ee1110ee)


* GETでhttp\://localhost:8080/users/{id}でgetUserByIdメソッドより、指定したIDのレコードが返されます。以下IDが2の例です。

![スクリーンショット (331)](https://github.com/Horikawa-1/CRUD-API/assets/111167638/ca8383a7-f9ec-4567-980d-0ccfb93129cc)


* getUserByIdメソッドで指定されたIDがテーブルになかった場合、例外処理で 「"message": "IDが{指定されたID}のレコードはありません。"」、ステータスコード404が返されます。以下、IDが4の例です。

![スクリーンショット (332)](https://github.com/Horikawa-1/CRUD-API/assets/111167638/124ed02a-dd80-4111-b353-331ecd0b9852)


* POSTでhttp\://localhost:8080/usersでCreateUserメソッドより、入力されたnameの値が新しくレコードに追加されます。ただし、nameの値がnull、空文字、21字以上の時はエラーになります。以下IDが4のレコードを追加したスクリーンショットです。

![スクリーンショット (333)](https://github.com/Horikawa-1/CRUD-API/assets/111167638/ba1b4e98-19a5-4565-b978-dcbc092631b8)


![スクリーンショット (334)](https://github.com/Horikawa-1/CRUD-API/assets/111167638/f6ef3e8e-4a8c-4aa3-8eaf-9eb17c745d0b)


* 以下nameの値がnull、空白、19,20,21字の場合のスクリーンショットです。

![スクリーンショット (335)](https://github.com/Horikawa-1/CRUD-API/assets/111167638/b57a0bda-45ac-4402-ae3e-18b169ba3628)


![スクリーンショット (336)](https://github.com/Horikawa-1/CRUD-API/assets/111167638/8a5965dd-47fe-4e22-9f79-a69775e6ed92)


![スクリーンショット (337)](https://github.com/Horikawa-1/CRUD-API/assets/111167638/697248e0-7090-4cb7-81fd-091555e58309)


![スクリーンショット (338)](https://github.com/Horikawa-1/CRUD-API/assets/111167638/b0a8142a-dc75-4aaf-be15-802ade1594c1)


![スクリーンショット (339)](https://github.com/Horikawa-1/CRUD-API/assets/111167638/57fda7b7-04a9-418d-9fed-8669c2863e62)


* PATCHでhttp\://localhost:8080/users/{id}でupdateNameメソッドより、指定したIDのレコードのnameを更新できます。以下IDが6のレコードを更新する例です。

![スクリーンショット (341)](https://github.com/Horikawa-1/CRUD-API/assets/111167638/ab338ebc-2e48-4c64-9361-c6d4309d4220)


![スクリーンショット (342)](https://github.com/Horikawa-1/CRUD-API/assets/111167638/eebf191a-c066-4794-91c6-31c91a834208)


![スクリーンショット (343)](https://github.com/Horikawa-1/CRUD-API/assets/111167638/af73620d-48de-4e80-9e36-c4be33a8d330)


* DELETEでhttp\://localhost:8080/users/{id}でdeleteUserメソッドより、指定したIDのレコードを消去できます。以下IDが5のレコードを消去する例です。

![スクリーンショット (344)](https://github.com/Horikawa-1/CRUD-API/assets/111167638/044c5548-85a5-4386-9901-219c065cf0b0)


![スクリーンショット (345)](https://github.com/Horikawa-1/CRUD-API/assets/111167638/a710ef08-2131-445e-aec3-c103c7ab43b4)


* 単体テストの結果です。

![スクリーンショット (320)](https://github.com/Horikawa-1/last-homework-test-code-2/assets/111167638/8f303191-9861-4444-b641-ad708880514c)

* DBテストの結果です。

![スクリーンショット (321)](https://github.com/Horikawa-1/last-homework-test-code-2/assets/111167638/14f7e8c7-a5ed-4f5e-9cd6-27b78f695a85)

* 結合テストの結果です。

![スクリーンショット (323)](https://github.com/Horikawa-1/last-homework-test-code-2/assets/111167638/7515cdee-407d-4fb3-92af-0a08cc995a99)

* GitHub Actionsの結果です。

![スクリーンショット (317)](https://github.com/Horikawa-1/last-homework-test-code-2/assets/111167638/b2d9ff80-a521-47ae-8f68-1fc762fa6e46)
