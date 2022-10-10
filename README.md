# url随机分发器

<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-1-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->

## 简介

本项目是为了将多个url进行合并后，通过一个url进行随机获取的目标而创建的。

## 环境

JDK17及以上版本

## 运行

## 开发

需要Gradle和JDK17

若使用项目中的脚本 则只需要JDK17

### API地址
[postman](https://documenter.getpostman.com/view/7743596/2s83ziMP4T)

### JDK17安装

MacOS:
```shell
brew cask install homebrew/cask-versions/zulu17
```
Windows:
```Shell
# scoop 方式
scoop install zulu17-jdk
# winget 方式
winget install Azul.Zulu.17.JDK
```

Linux:
```shell
# sdkman
sdk install java 17.36.19-zulu
# debian/ubuntu 
apt install zulu17-jdk
```

### 运行项目

通过gradle运行,该过程会自动下载依赖

使用安装好的gradle:
```shell
gradle :run
```

使用自带脚本:

Windows:
```shell
gradlew.bat :run
```

MacOS/Linux:
```shell
gradlew :run
```

默认地址为:[http://localhost:8080](http://localhost:8080)



## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center"><a href="https://github.com/masklkdf"><img src="https://avatars.githubusercontent.com/u/51392537?v=4?s=100" width="100px;" alt="likeit"/><br /><sub><b>likeit</b></sub></a><br /><a href="https://github.com/yunkuangao/url-random/commits?author=masklkdf" title="Code">💻</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
