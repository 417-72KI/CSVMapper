# CSVMapper

##概要

CSVMapperはCSVファイルをJavaオブジェクトとして扱えるようにマッピングするライブラリです。

##機能

* CSVファイルの取り込み
* CSVファイルへの出力

## 更新履歴

### 2016/06/10 Ver 1.0.0 公開

## 使い方

### Gradle

* repositoriesブロック内に下記を追加
```groovy
repositories {
    maven {
        url 'https://github.com/417-72KI/CSVMapper/raw/master/repos'
    }
}
```
* dependenciesブロック内に下記を追加
```groovy
dependencies {
    compile 'jp.natsukishina:csv-mapper:2.0.0'
}
```

### Maven

* repositoriesブロック内に下記を追加

```xml
<repository>
	<id>CSVMapper</id>
	<url>https://github.com/417-72KI/CSVMapper/raw/master/repos/</url>
</repository>
```
* dependenciesブロック内に下記を追加
```xml
<dependency>
	<groupId>jp.natsukishina</groupId>
	<artifactId>csv-mapper</artifactId>
	<version>2.0.0</version>
</dependency>
```


## TODO

* CSVMappableの仕様の見直し
  (Annotationsで列番号を指定できるようにする？)

## JavaDoc

[Ver 1.0.0](http://417-72ki.github.io/CSVMapper/javadoc/1.0.0/)

## ライセンス

Copyright &copy; 2016 417.72KI
Licensed under the [MIT License][mit].

[MIT]: http://www.opensource.org/licenses/mit-license.php
