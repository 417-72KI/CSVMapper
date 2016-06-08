# CSVMapper

##概要

CSVMapperはCSVファイルをJavaオブジェクトとして扱えるようにマッピングするライブラリです。

##機能

* CSVファイルの取り込み
* CSVファイルへの出力

## 更新履歴

2016/06/08 Ver 1.0.0 公開

## 使い方

### Gradle

* build.gradleに下記のブロックを追加
```maven
repositories {
    maven {
        url 'https://github.com/417-72KI/CSVMapper/raw/master/repos'
    }
}
```
```maven
dependencies {
    compile 'jp.natsukishina.csvmapper:csv-mapper:1.0.0'
}
```

## TODO

* CSVMappableの仕様の見直し
  (Annotationsで列番号を指定できるようにする？)
