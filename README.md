# skinny-di-test
SkinnyFramework + Google Guiceを使用したDIのサンプル

# 設定

1. PostgreSQL(9.4)をインストール
2. 接続情報を `src/main/resources/application.conf` に記述(testとdevelopmentの箇所)
3. testとdevelopmentに指定した接続先のdatabaseに対して、 `db/er.sql` を実行
4. `./skinny test` で実行するとDBを使用したテストが実行できます
5. `./skinny run` で実行した後、ブラウザで http://localhost:8080/ にアクセスするとTOP画面を表示します(レコード追加します)

# IntelliJで実行する場合
テストクラスはScalaTestで書かれています。IntelliJで実行する場合、ScalaTestのデフォルト設定として「VM parameters」に

`-Dskinny.env=test`

を設定して実行して下さい
