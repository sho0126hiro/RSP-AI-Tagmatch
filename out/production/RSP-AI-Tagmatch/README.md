# 特別演習課題

実行環境: Java11 (or Java8)

##  ディレクトリ構成
（*.classは省略）
```
├── ButtleManager.java
├── README.md
├── Runner.java 
├── agents // エージェントの定義
│   └── SampleAgent.java
├── common // 共通プログラム
│   ├── RSPEnum.java
│   ├── Result.java
│   ├── TagTeamAction.java
│   └── Team.java
├── run.sh // linux shellで実行するためのファイル
└── teams // 各チーム内のエージェントを統括するプログラム
    └── SampleTeam.java
```

## 注意
チームを自作する場合は
`teams/<チーム名(イニシャルなど)>Team.java`
のようにファイルを追加し、

エージェントを自作する場合は
`agents/<チーム名(イニシャルなど)>/hogehogeagent.java`
のように，パッケージに入れてください

## ファイルの仕様
*Note 詳細はファイル

実装するのは，`agents/hogehogeAgent.java`と`teams/hogehogeTeam.java`のメソッドのみ

### `Runner.java`
実行のためのファイル
（static main()からはstatic関数・変数しか呼び出せず，いろいろ不便なので）

### `ButtleManager.java`
`Runner.java`から呼び出される，主に対戦の管理をするクラス

やること
- チームの総当り戦
- 各50000回の対戦
- じゃんけんの勝敗判定
- （天の声）

### `common/`: 共通プログラム
- `TagTeamAction.java`  
「次の手」を定義するクラス
（共通化のためにこの形式に統一してください）
- `Team.java`  
「じゃんけんエージェントのチーム」を定義するインターフェース
次のようにclassの定義時にimplementsすることでTeamを実装してください．  
Team.javaに書いてあるメソッドは必ずクラスに実装しないといけません．
```java
public class HogehogeTeam implements Team{}
```

- `Result.java`  
対戦結果を定義するクラス
（共通化のためにこの形式に統一してください）
- `RSPEnum.java`  
グー・チョキ・パーを定義するEnum
（共通化のためにこの形式に統一してください）
RSPEnum.ROCKで，「グー」

### `teams/`: チームを格納するためのファイル
チームからエージェントを呼び出す．

`teams/hogehogeTeam.jav`aでは，例えば次のようなことをする
- エージェント間通信
- 環境保持など
- 対戦結果からチーム全体の振舞を学習
- チームの次の行動を決定する

### `agents/`: エージェントを格納するためのファイル
チームから呼び出されるエージェント単体を実装

`agents/hogehogeAgent.java`では，例えば次のようなことをする
- エージェント単体での学習

## サンプルプログラム

- [Teamのサンプル - ](./teams/SampleTeam.java)
`./teams/SampleTeam.java`
- [Agentのサンプル - ](./agents/SampleAgent.java)
`./agents/SampleAgent.java`
