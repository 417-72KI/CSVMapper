# CSVMapper

## 更新履歴

2016/06/08 Ver 1.0.0 公開

## 使い方

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
* CSVMappableの実装クラスを作成する

