# CSVMapper

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
    compile 'jp.natsukishina.csvmapper:csv-mapper:0.0.1'
}
```
* CSVConvertableの実装クラスを作成する
