# Max utils 使用文档

## 概括

- 对于Max 广告的集成，方便使用，下面时使用该库的步骤

### 1.添加远依赖

~~~
allprojects {
    repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
~~~

~~~
dependencies {
    implementation 'com.github.zuozhuang0629:maxUtils:1.0.0'
}
~~~

### 2. 使用方法

1. 在使用具体广告前，我们应该先进行初始化

~~~
MaxUtils.get().initSdk(this) {
            return@initSdk "max用户key"
}
~~~

2. 如果使用插屏广告，我们需要先对插屏广告进行一次初始化

~~~
 MaxUtils.get().initMaxInter("插屏的广告id", activity)
~~~

3. banner广告的显示

~~~
 MaxUtils.get().showMaxBanner(context, "banner广告id", 需要显示的banner容器)
~~~

4. native广告的显示

~~~
  MaxUtils.get().showMaxNative(context, "native广告id", 需要显示native广告的容器)
~~~

5. inter广告的显示

~~~
 MaxUtils.get().showInter(开关显示的结果, "inter广告的id") {
    //  返回是否显示成功
 }
~~~


